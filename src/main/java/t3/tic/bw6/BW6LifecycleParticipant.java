/**
 * (C) Copyright 2016-2018 teecube
 * (http://teecu.be) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package t3.tic.bw6;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.tycho.core.maven.TychoMavenLifecycleParticipant;
import org.eclipse.tycho.core.osgitools.BundleReader;
import org.eclipse.tycho.resolver.TychoResolver;
import t3.AdvancedMavenLifecycleParticipant;
import t3.CommonMavenLifecycleParticipant;
import t3.CommonTIBCOMojo;
import t3.utils.POMManager;
import t3.plugin.PluginConfigurator;
import t3.plugin.PluginManager;
import t3.plugin.PropertiesEnforcer;
import t3.site.GenerateGlobalParametersDocMojo;
import t3.tic.bw6.util.ManifestManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = "TICBW6LifecycleParticipant")
public class BW6LifecycleParticipant extends CommonMavenLifecycleParticipant implements AdvancedMavenLifecycleParticipant {

    public final static String pluginGroupId = "io.teecube.tic";
    public final static String pluginArtifactId = "tic-bw6";

    private Map<File, ByteArrayOutputStream> history;

    private Boolean displayLoadedMessage = false;
    private Boolean p2ResolveEnabled = true;

    private HashMap<String, List<Dependency>> projectDependencies;

    @Requirement
    private BundleReader bundleReader;

    @Requirement
    private TychoResolver resolver;

    @Requirement
    private PlexusContainer plexus;

    @Requirement
    private Logger log;

    @Override
    protected String getPluginGroupId() {
        return pluginGroupId;
    }

    @Override
    protected String getPluginArtifactId() {
        return pluginArtifactId;
    }

    @Override
    protected String loadedMessage() {
        return displayLoadedMessage ? Messages.LOADED : null;
    }

    @Override
    protected void initProjects(MavenSession session) throws MavenExecutionException {
        if ("standalone-pom".equals(session.getCurrentProject().getArtifactId())) {
            p2ResolveEnabled = false;
        }

        PluginManager.registerCustomPluginManager(pluginManager, new BW6MojosFactory()); // to inject Global Parameters in Mojos
        PropertiesEnforcer.setCustomProperty(session, "sampleProfileCommandLine", GenerateGlobalParametersDocMojo.standaloneGenerator(session.getCurrentProject(), this.getClass()).getFullSampleProfileForCommandLine(BW6LifecycleParticipant.pluginArtifactId, "| "));

        setStudioVersion(session);
        CommonTIBCOMojo.setJreVersions(session, propertiesManager);

        List<MavenProject> projects = prepareProjects(session.getProjects(), session.getProjectBuildingRequest(), session);
        session.setProjects(projects);
        PluginConfigurator.propertiesManager.setProject(session.getCurrentProject());

        customizeGoalsExecutions(session);

        if (!skipRules(session)) {
            PropertiesEnforcer.enforceProperties(session, pluginManager, logger, new ArrayList<String>(), BW6LifecycleParticipant.class, getPluginKey()); // check that all mandatory properties are correct
        }

        if (!containsNonOSGIGoal(session) && p2ResolveEnabled) {
            displayLoadedMessage = true;
            logger.info(Messages.RESOLVING_BW6_DEPENDENCIES);
            logger.info(Messages.MESSAGE_SPACE);
            TychoMavenLifecycleParticipant tychoMavenLifecycleParticipant = new StandaloneTychoMavenLifecycleParticipant(bundleReader, resolver, plexus, log);
            tychoMavenLifecycleParticipant.afterProjectsRead(session);
            logger.info(Messages.MESSAGE_SPACE);
            logger.info(Messages.RESOLVED_BW6_DEPENDENCIES);
            logger.info(Messages.MESSAGE_SPACE);

            session.getUserProperties().put("tycho.mode", "maven"); // to avoid duplicate call of TychoMavenLifecycleParticipant.afterProjectsRead()
        }

        restoreManifests(); // the "prepare-module-meta" goal will do the version replacement if configured to do so (mandatory to have a valid format for the version to resolve dependencies)
    }

    protected class StandaloneTychoMavenLifecycleParticipant extends TychoMavenLifecycleParticipant {
        public StandaloneTychoMavenLifecycleParticipant(BundleReader bundleReader, TychoResolver resolver, PlexusContainer plexus, Logger log) throws MavenExecutionException {
            try {
                // inject bundleReader
                Field bundleReaderField = TychoMavenLifecycleParticipant.class.getDeclaredField("bundleReader");
                bundleReaderField.setAccessible(true);
                bundleReaderField.set(this, bundleReader);
                // inject resolver
                Field resolverField = TychoMavenLifecycleParticipant.class.getDeclaredField("resolver");
                resolverField.setAccessible(true);
                resolverField.set(this, resolver);
                // inject plexus
                Field plexusField = TychoMavenLifecycleParticipant.class.getDeclaredField("plexus");
                plexusField.setAccessible(true);
                plexusField.set(this, plexus);
                // inject log
                Field logField = TychoMavenLifecycleParticipant.class.getDeclaredField("log");
                logField.setAccessible(true);
                logField.set(this, log);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new MavenExecutionException(e.getLocalizedMessage(), e);
            }
        }
    }

    private void setStudioVersion(MavenSession session) {
        String bw6Version = propertiesManager.getPropertyValue(BW6MojoInformation.BW6.bwVersion);
        String studioVersion = null;
        if (bw6Version != null && !bw6Version.isEmpty()) {
            switch (bw6Version) {
            case "6.1":
            case "6.2":
                studioVersion = "3.6";
                break;
            case "6.3":
            case "6.4":
                studioVersion = "4.0";
                break;

            default:
                studioVersion = "4.0"; // assume next minor versions will have the same Studio version
                break;
            }
        }

        if (bw6Version != null) {
            PropertiesEnforcer.setCustomProperty(session, BW6MojoInformation.BW6.bwVersion, bw6Version);
        }
        if (studioVersion != null) {
            PropertiesEnforcer.setCustomProperty(session, BW6MojoInformation.Studio.studioVersion, studioVersion);
        }
    }

    private boolean containsNonOSGIGoal(MavenSession session) {
        for (String goal : session.getRequest().getGoals()) {
            if (goal.endsWith(":add-module") ||
                goal.endsWith(":remove-module") ||
                goal.endsWith(":p2maven-install") ||
                goal.endsWith(":studio-proxy-install") ||
                goal.startsWith("archetype:") ||
                goal.startsWith("toe:")) {
                return true;
            }
        }
        return false;
    }

    private boolean skipRules(MavenSession session) {
        for (String goal : session.getRequest().getGoals()) {
            if (goal.startsWith("toe:") ||
                goal.startsWith("io.teecube.toe:") ||
                goal.startsWith("archetype:")) {
                return true;
            }
        }
        return false;
    }

    private void customizeGoalsExecutions(MavenSession session) {
        if (containsNonOSGIGoal(session)) {
            session.getRequest().getUserProperties().put("bw6MavenSkip", "true");
        }
        if (session.getRequest().getGoals().contains("bw6:p2maven-install")) {
            session.getRequest().getUserProperties().put("bw6MavenSkip", "true");
            p2ResolveEnabled = false;
        }
        if (session.getRequest().getGoals().contains("bw6:studio-proxy-install")) {
//            session.getRequest().getUserProperties().put("bw6MavenSkip", "true");
            p2ResolveEnabled = false;
        }
        if (session.getRequest().getGoals().contains("bw6:studio") || session.getRequest().getGoals().contains("bw6:workspace")) {
            p2ResolveEnabled = false;
        }
    }

    /**
     * <p>
     *
     * </p>
     *
     * @param projects
     * @param projectBuildingRequest
     * @param session
     * @throws MavenExecutionException
     */
    private List<MavenProject> prepareProjects(List<MavenProject> projects, ProjectBuildingRequest projectBuildingRequest, MavenSession session) throws MavenExecutionException {
        List<MavenProject> result = new ArrayList<MavenProject>();

        if (projects == null) {
            logger.warn("No projects to convert.");
            return result;
        }

        projectDependencies = new HashMap<String, List<Dependency>>();

        BW6PackagingConvertor convertor = new BW6PackagingConvertor(logger, propertiesManager, session);
        convertor.setArtifactRepositoryRepository(artifactRepositoryFactory);
        convertor.setArtifactResolver(artifactResolver);
        convertor.setArtifactHandler(artifactHandler);
        convertor.setPluginManager(pluginManager);
        convertor.setProjectBuilder(projectBuilder);
        convertor.setProjectBuildingRequest(projectBuildingRequest);
        convertor.setProjectDependencies(projectDependencies);

        history = new HashMap<File, ByteArrayOutputStream>();
        for (MavenProject mavenProject : projects) {
            PluginConfigurator.addPluginsParameterInModel(mavenProject, BW6LifecycleParticipant.class, logger);

            if (containsNonOSGIGoal(session)) {
                result.add(mavenProject);
                logger.debug("Skipping conversion for : " + mavenProject.getName());
                continue;
            }

            convertor.setMavenProject(mavenProject);
            try {
                switch (mavenProject.getPackaging()) {
                case BW6CommonMojo.BW6_APP_MODULE_PACKAGING:
                    forceManifestForValidation(mavenProject);
                    if (!containsNonOSGIGoal(session)) {
                        checkForBadParentDefinition(mavenProject);
                    }
                    result.add(convertor.prepareBW6AppModule());
                    break;
                case BW6CommonMojo.BW6_SHARED_MODULE_PACKAGING:
                    forceManifestForValidation(mavenProject);
                    if (!containsNonOSGIGoal(session)) {
                        checkForBadParentDefinition(mavenProject);
                    }
                    result.add(convertor.prepareBW6SharedModule());
                    break;
                case "pom":
                    if (convertor.hasBW6ModuleOrDependency()) {
                        forceManifestForValidation(mavenProject);

                        // a BW6 application has a "pom" packaging and at least one module or dependency with "bw6-app-module" packaging
                        result.add(convertor.prepareBW6Application());
                    } else {
                        // TODO: warn user
                        result.add(mavenProject);
                    }
                    break;
                default:
                    result.add(mavenProject);
                    logger.debug("No conversion for : " + mavenProject.getName());
                    break;
                }
            } catch (Exception e) {
                throw new MavenExecutionException(e.getLocalizedMessage(), e);
            }
        }

        return result;
    }

    private void checkForBadParentDefinition(MavenProject mavenProject) throws MavenExecutionException {
        if (mavenProject == null || mavenProject.getParent() == null) return;

        MavenProject parent = mavenProject.getParent();

        for (String module : parent.getOriginalModel().getModules()) {
            try {
                Model moduleModel = POMManager.getModelOfModule(parent, module);

                if (hasBadParentDefinition(parent, module)) {
                    logger.error("The project with coordinates '" + moduleModel.getGroupId() + ":" + moduleModel.getArtifactId() + ":" + moduleModel.getVersion() + ":" + moduleModel.getPackaging() + "' inherits from its BW6 application project with coordinates '" + parent.getGroupId() + ":" + parent.getArtifactId() + ":" + parent.getVersion() + ":" + parent.getPackaging() + "'");
                    logger.error("Remove parent definition or make BW6 module inherit from another POM (it can be BW6 application parent POM)");
                    throw new MavenExecutionException("A BW6 module cannot inherit from its BW6 application.", new Exception());
                }
            } catch (IOException | XmlPullParserException e) {
                throw new MavenExecutionException(e.getLocalizedMessage(), e);
            }
        }
    }

    public static boolean hasBadParentDefinition(MavenProject parent, String module) throws IOException, XmlPullParserException {
        Model moduleModel = POMManager.getModelOfModule(parent, module);

        if (moduleModel != null && moduleModel.getParent() == null) return false; // no parent so can't be bad

        return (moduleModel != null &&
                parent.getGroupId().equals(moduleModel.getParent().getGroupId()) &&
                parent.getArtifactId().equals(moduleModel.getParent().getArtifactId()) &&
                parent.getVersion().equals(moduleModel.getParent().getVersion())
               );
    }

    /**
     * <p>
     * Updates MANIFEST.MF file during the loading of OSGi requirements.
     * <ul>
     *  <li>Bundle-Version</li>
     *  <li></li>
     * </ul>
     * </p>
     *
     * @param mavenProject
     * @throws MavenExecutionException
     * @throws MojoExecutionException
     * @throws MojoFailureException
     * @throws BW6ExecutionException
     */
    private void forceManifestForValidation(MavenProject mavenProject) throws MavenExecutionException {
        File manifestSource = new File(mavenProject.getBasedir(), "META-INF/MANIFEST.MF");
        try {
            ByteArrayOutputStream oldFileContent = new ByteArrayOutputStream();
            FileUtils.copyFile(manifestSource, oldFileContent);
            history.put(manifestSource, oldFileContent);

            ManifestManager.updatedManifestVersion(manifestSource, mavenProject.getVersion());
        } catch (IOException e) {
            throw new MavenExecutionException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>
     * After loading of OSGi requirements, MANIFEST.MF files are reverted to
     * their original versions.
     * </p>
     *
     * @throws MavenExecutionException
     * @throws MojoExecutionException
     * @throws MojoFailureException
     * @throws BW6ExecutionException
     */
    private void restoreManifests() {
        for (File file : history.keySet()) {
            ByteArrayOutputStream originalFileContent = history.get(file);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                originalFileContent.writeTo(fos);
            } catch (IOException e) {

            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }

    }

}