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
package t3.tic.bw6.project.application;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import t3.CommonMojoInformation;
import t3.plugin.annotations.GlobalParameter;
import t3.plugin.annotations.Mojo;
import t3.tic.bw6.BW6Artifact;
import t3.tic.bw6.BW6MojoInformation;
import t3.tic.bw6.Messages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This goals packages the TIBCO BusinessWorks 6 EAR.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true)
public class PackageBW6Mojo extends BW6ApplicationCommonMojo implements BW6Artifact {

    private JarArchiver jarArchiver;
    private MavenArchiver mavenArchiver;
    private MavenArchiveConfiguration archiveConfiguration;

// TODO: mutualize "artifact" information
    @GlobalParameter (property = "project.build.classifier", description = CommonMojoInformation.classifier_description, category = CommonMojoInformation.mavenCategory, defaultValue = "")
    protected String classifier;

    @GlobalParameter (property = "project.build.finalName", description = CommonMojoInformation.finalName_description, category = CommonMojoInformation.mavenCategory, required = true, defaultValue = "")
    protected String finalName;

    @Override
    public String getArtifactFileExtension() {
        return ".ear"; // TODO: externalize
    }

    /**
     * Retrieves the full path of the artifact that will be created.
     *
     * @param basedir, the directory where the artifact will be created
     * @param finalName, the name of the artifact, without file extension
     * @param classifier
     * @return a {@link File} object with the path of the artifact
     */
    public File getArtifactFile(File basedir, String finalName,    String classifier) {
        if (classifier == null) {
            classifier = "";
        } else if (classifier.trim().length() > 0 && !classifier.startsWith("-")) {
            classifier = "-" + classifier;
        }

        return new File(basedir, finalName + classifier + getArtifactFileExtension());
    }

    /**
     * @return the Maven artefact as a {@link File}
     */
    public File getOutputFile() {
        return getArtifactFile(directory, finalName, classifier);
    }
//

    private void addApplication() throws ArchiverException, ManifestException, IOException, DependencyResolutionRequiredException, MojoExecutionException {
        getLog().info("");

        jarArchiver.addDirectory(metaInf, metaInf.getName() + File.separator); // add "target/META-INF/", already prepared by "prepare-application-meta"

        mavenArchiver.setArchiver(jarArchiver);
        mavenArchiver.setOutputFile(getOutputFile());

        // Set the MANIFEST.MF to the JAR Archiver
        jarArchiver.setManifest(manifest);

        // Set the MANIFEST.MF to the Archive Configuration
        archiveConfiguration.setManifestFile(manifest);
        archiveConfiguration.setAddMavenDescriptor(true);

        // create the Archive
        mavenArchiver.createArchive(session, project, archiveConfiguration);
    }

    private void addModules() throws MojoExecutionException, FileNotFoundException, IOException {
        List<File> modulesJARs = getModulesJARs();

        Map<String, String> symbolicNames = getModulesSymbolicNames();
        Map<String, String> versions = getModulesVersions();

        String symbolicName = null;
        String version = null;

        for (File moduleJAR : modulesJARs) {
            // add the JAR file of the module to the EAR file
            for (String moduleName : versions.keySet()) {
                if (moduleName.equals(moduleJAR.getName())) {                    
                    symbolicName = symbolicNames.get(moduleName);
                    version = versions.get(moduleName);
                    break;
                }
            }
            if (symbolicName != null && version != null) {
                String moduleName = symbolicName + "_" + version + JAR_EXTENSION;
                getLog().info(Messages.APPLICATION_ADDING_MODULE + moduleName);
                jarArchiver.addFile(moduleJAR, moduleName);
            }

            String diagramsRelativePath = getPropertyValue(BW6MojoInformation.BW6Module.diagramsRelativePath);
            File diagramsDirectory = new File(moduleJAR.getParentFile(), diagramsRelativePath);
            if (diagramsDirectory.exists() && diagramsDirectory.isDirectory()) {
                jarArchiver.addDirectory(diagramsDirectory, "resources/" + symbolicName + "/" + diagramsDirectory.getName() + "/");
                getLog().info(Messages.APPLICATION_ADDING_DIAGRAMS + diagramsDirectory.getAbsolutePath());
            }
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(Messages.APPLICATION_PACKAGING);

        jarArchiver = new JarArchiver();
        mavenArchiver = new MavenArchiver();
        archiveConfiguration = new MavenArchiveConfiguration();

        try {
            addModules();
            addApplication();
        } catch (ArchiverException | ManifestException | IOException | DependencyResolutionRequiredException e) {
            e.printStackTrace();
        }
    }

}
