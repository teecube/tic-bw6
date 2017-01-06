/**
 * (C) Copyright 2016-2017 teecube
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

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.UnknownRepositoryLayoutException;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.building.StringModelSource;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import t3.CommonMojo;
import t3.POMManager;
import t3.plugin.PluginBuilder;
import t3.plugin.PluginConfigurator;
import t3.tic.bw6.util.ManifestManager;

/**
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@SuppressWarnings("deprecation")
public class BW6PackagingConvertor {

	private Logger logger;
	private MavenSession session;
	private MavenProject mavenProject;
	private ArtifactRepositoryFactory artifactRepositoryFactory;
	private ArtifactResolver artifactResolver;
	private ArtifactHandler artifactHandler;
	private BuildPluginManager pluginManager;
	private ProjectBuilder projectBuilder;
	private ProjectBuildingRequest projectBuildingRequest;

	private List<Capability> builtinCapabilities; // builtin such as palettes
	private List<Capability> customCapabilities; // custom such as shared modules
	private File bw6P2Repository = null; // BW6 p2 repository (generated by 'mvn bw6:p2maven-install')
	private File mavenP2Repository = null; // project-specific p2 repository (populated with Maven dependencies)

	private CommonMojo propertiesManager; // for Maven properties helpers
	private Pattern capabilityPattern = Pattern.compile(BW6Constants.capabilityPattern);
	private Pattern capabilityFilterPattern = Pattern.compile(BW6Constants.capabilityFilterPattern);
	private Pattern providedCapabilityPattern = Pattern.compile(BW6Constants.providedCapabilityPattern);
	private HashMap<String, List<Dependency>> projectDependencies;

	public BW6PackagingConvertor(Logger logger, CommonMojo propertiesManager, MavenSession session) {
		this.logger = logger;
		this.propertiesManager = propertiesManager;
		this.session = session;

		builtinCapabilities = new ArrayList<Capability>();
		customCapabilities = new ArrayList<Capability>();
	}

	/**
	 * <p>
	 * This will fetch all capabilities.
	 *  <ul>
	 *   <li>the ones found in the hardcoded list of BW6 capabilities (palettes
	 *   and OSGi plugins included in BW6 runtime) will be added in
	 *   builtinCapabilities</li>
	 *   <li>the ones defined as shared module and found in the Maven model will
	 *   be added in customCapabilities</li>
	 *   <li>the ones defined as shared module and not found in the Maven model
	 *   will throw an exception</li>
	 *  </ul>
	 * </p>
	 *
	 * @param capabilities the list of capabilities for the given module.
	 *
	 * @throws MojoExecutionException 
	 */
	protected void processRequiredCapabilites(List<String> capabilities) throws MojoExecutionException {
		if (capabilities == null || capabilities.equals("")) return;

		for (String capability : capabilities) {
			String id = BW6Constants.capabilities.get(capability); // try to get plugin from hardcoded values 

			if (id == null || id.equals("")) { // otherwise try to check if it is an available shared module
				Capability c = processCustomCapability(capability);
				if (c == null) {
					logger.info("The shared module is not found in the Maven reactor, try to resolve it in Maven repositories");

					throw new MojoExecutionException("Capability 'com.tibco.bw.module' not found!");
				} else if (c.getId().isEmpty()) {
					continue;
				}

				//customCapabilities.add(c);
			} else {
				Capability c = new Capability(id, "6.0.0", "builtin"); // TODO: externalize in configuration ?
				builtinCapabilities.add(c);
			}
		}
	}

	/**
	 * <p>
	 * A custom capability is a capability not found in built-in capabilities.
	 * <br />
	 * It is most likely a shared module being used (starting with
	 * 'com.tibco.bw.module').<br />
	 * We need to translate this capability into a Maven dependency (if found
	 * in current reactor or repositories).
	 * </p>
	 *
	 * @param capability
	 * @throws MojoExecutionException 
	 */

	private Capability processCustomCapability(String capability) throws MojoExecutionException {
		Matcher m1 = capabilityPattern.matcher(capability);
		if (m1.matches()) {
			String capability_ = m1.group(1);
			String filter = m1.group(2);
			switch (capability_) {
			case "com.tibco.bw.module":
				Matcher m2 = capabilityFilterPattern.matcher(filter);

				if (m2.matches()) {
					String sharedModuleName = m2.group(1);
					String sharedModuleVersion = m2.group(2);

					logger.info("Found shared module capability: " + sharedModuleName + " version " + sharedModuleVersion);
					return findSharedModuleWithCapability(sharedModuleName, sharedModuleVersion);
				}
				break;

			default:
				logger.debug("Unhandeld capability: " + capability);
				break;
			}
		} else {
			logger.debug("Unparsable capability: " + capability);
		}

		return new Capability("", "0.0.0", ""); // not found so don't throw anything :)
	}

	public static List<String> getCapabilites(String capabilities) {
		if (capabilities == null) return null;

		return new LinkedList<String>(Arrays.asList(capabilities.split("\\s*,\\s*")));
	}

	private Capability findSharedModuleWithCapability(String name, String version) throws MojoExecutionException {
		// first have a look in Maven reactor
		for (MavenProject mavenProject : this.session.getProjects()) {
			if (mavenProject.getOriginalModel().getPackaging().equals(BW6CommonMojo.BW6_SHARED_MODULE_PACKAGING)) {
				List<String> capabilities;
				try {
					capabilities = getProvidedCapabilities(mavenProject);
				} catch (IOException | BundleException e) {
					throw new MojoExecutionException(e.getLocalizedMessage(), e);
				}

				logger.info(mavenProject.getBasedir().getAbsolutePath());
				logger.info(capabilities.toString());
				for (String capability : capabilities) {
					Matcher m1 = providedCapabilityPattern.matcher(capability);
					if (m1.matches()) {
						String capabilityId = m1.group(1);
						String capabilityVersionRange = m1.group(2);
						if (capabilityId.equals(name) && capabilityVersionRange.equals(version)) {
							// The shared module is in the Maven reactor => it will be built during Maven build
							// For OSGi validation we remove it from required capabilities (it will be readded at the end of Tycho initialization)
							// When it's build it will be added to the temporary p2 repository
							File manifestSource = new File(this.mavenProject.getBasedir(), "META-INF/MANIFEST.MF");
							try {
								ManifestManager.removeRequiredCapability(manifestSource, capabilityId);  
							} catch (IOException e) {
								throw new MojoExecutionException(e.getLocalizedMessage(), e);
							}
							addArtifactInMavenP2Repository(mavenProject.getArtifact());
							return new Capability(capabilityId, capabilityVersionRange, "shared"); // return the capability so it can be added in the customCapabilities list
						}
					}
				}
			}
		}

		// not found in Maven reactor
		// it must be resolved from a Maven repository and converted into an OSGi bundle (eclipse-plugin) put in a temporary p2 repository

		for (Dependency dependency : this.mavenProject.getDependencies()) {
			if (name.equals(dependency.getArtifactId()) && BW6CommonMojo.BW6_SHARED_MODULE_PACKAGING.equals(dependency.getType())) {
				logger.info("Found '" + name + "' in Maven dependencies.");
				logger.info("trying to resolve...");

				Artifact artifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getScope(), dependency.getType(), dependency.getClassifier(), artifactHandler);
				System.out.println(artifact.getArtifactId());

				ArtifactResolutionRequest request = new ArtifactResolutionRequest();
				request.setArtifact(artifact);

				ArtifactRepository localRepository = session.getRequest().getLocalRepository();
				List<ArtifactRepository> remoteRepositories  = session.getRequest().getRemoteRepositories();
				request.setLocalRepository(localRepository);
				request.setRemoteRepositories(remoteRepositories);
				artifactResolver.resolve(request);

				if (artifact.getFile() != null) {
					if (!artifact.getFile().exists()) {
						artifact.setFile(new File(artifact.getFile().getAbsolutePath() + ".jar"));
					}

					// found as a Maven dependency => add it in the project p2 repository
					if (artifact.getFile().exists() && installArtifactInP2(artifact, getProjectP2Repository())) {
						Capability c = new Capability(dependency.getArtifactId(), dependency.getVersion().replace("-SNAPSHOT", ""), "shared");
						c.setFile(artifact.getFile());
						customCapabilities.add(c); // add only capabilities not in reactor
						return c;
					}
				}
			}
		}

		// here add the unresolved capabilities that will be displayed later

		return null; 
	}

	private boolean installArtifactInP2(Artifact artifact, File p2Repository) throws MojoExecutionException {
		boolean result = false;

		if (artifact == null || p2Repository == null || !p2Repository.exists() || !p2Repository.isDirectory()) return result;

		File tempSource = null;
		try {
			tempSource = Files.createTempDirectory("repo").toFile();

			new File(tempSource, "features").mkdir();
			new File(tempSource, "plugins").mkdir();

			FileUtils.copyFileToDirectory(artifact.getFile(), new File(tempSource, "plugins"));
			ArrayList<Element> configuration = new ArrayList<Element>();
			configuration.add(new Element("artifactRepositoryLocation", p2Repository.getAbsolutePath()));
			configuration.add(new Element("metadataRepositoryLocation", p2Repository.getAbsolutePath()));
			configuration.add(new Element("sourceLocation", tempSource.getAbsolutePath()));

			executeMojo(
				plugin(
						groupId("org.eclipse.tycho.extras"),
						artifactId("tycho-p2-extras-plugin"),
						version("0.25.0")
						),
				goal("publish-features-and-bundles"),
				configuration(
						configuration.toArray(new Element[0])
						),
				executionEnvironment(mavenProject, session, pluginManager)
			);

			result = true; // successfully added in p2 repository
		} catch (IOException e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		} finally {
			if (tempSource != null && tempSource.exists()) {
				FileUtils.deleteQuietly(tempSource); // keep it clean but don't worry if it's not
			}
		}

		return result;
	}

	private void addArtifactInMavenP2Repository(Artifact artifact) throws MojoExecutionException {
		File mavenP2Repository = getProjectP2Repository();

		File artifactFile = new File(mavenP2Repository, artifact.getArtifactId()+artifact.getVersion()+BW6CommonMojo.JAR_EXTENSION);
        if (!artifactFile.exists()) {
            try {
				new FileOutputStream(artifactFile).close();
			} catch (IOException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			}
        }
	}

	/**
	 * Used to retrieve BW6 requirements from this plugin (self) configuration
	 *
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private List<BW6Requirement> getBW6Requirements(MavenProject mavenProject) throws XmlPullParserException, IOException {
		List<BW6Requirement> bw6Requirements = new ArrayList<BW6Requirement>();

		List<Plugin> plugins = mavenProject.getBuild().getPlugins();
		for (Plugin plugin : plugins) {
			if (BW6LifecycleParticipant.pluginArtifactId.equals(plugin.getArtifactId())) {
				Object pluginConfiguration = plugin.getConfiguration();
				String config = null;
				if (pluginConfiguration != null) {
					config = plugin.getConfiguration().toString();
				}
				if (config == null) {
					continue;
				}

				Xpp3Dom dom = Xpp3DomBuilder.build(new ByteArrayInputStream(config.getBytes()), "UTF-8"); // FIXME: encoding

				for (Xpp3Dom child : dom.getChildren()) {
					if ("dependencies".equals(child.getName())) {
						for (Xpp3Dom requirementNode : child.getChildren()) {
							if ("requirement".equals(requirementNode.getName())) {
								Xpp3Dom type = requirementNode.getChild("type");
								Xpp3Dom id = requirementNode.getChild("id");
								Xpp3Dom versionRange = requirementNode.getChild("versionRange");
								if (type != null && id != null && versionRange != null) {
									BW6Requirement requirement = new BW6Requirement();
									requirement.setType(type.getValue());
									requirement.setId(id.getValue());
									requirement.setVersionRange(versionRange.getValue());
									bw6Requirements.add(requirement);
								} else {
									logger.warn("wrong requirement");
								}
							}
						}
					}
				}

			}
		}

		logger.debug("BW6 requirements:");
		for (BW6Requirement bw6Requirement : bw6Requirements) {
			logger.debug(bw6Requirement.getId());
		}

		return bw6Requirements;
	}

	protected class Capability {
		private File file;
		private String id;
		private String type;
		private String versionRange;

		public Capability(String id, String versionRange, String type) {
			this.id = id;
			this.type = type;
			this.versionRange = versionRange;
		}

		public File getFile() {
			return file;
		}
		public void setFile(File file) {
			this.file = file;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getVersionRange() {
			return versionRange;
		}
		public void setVersionRange(String versionRange) {
			this.versionRange = versionRange;
		}

	}

	private List<BW6Requirement> getBW6Requirements(List<Capability> capabilities) {
		List<BW6Requirement> bw6Requirements = new ArrayList<BW6Requirement>();

		for (Capability capability : capabilities) {
			BW6Requirement bw6Requirement = new BW6Requirement();
			bw6Requirement.setId(capability.getId());
			bw6Requirement.setType("eclipse-plugin"); // TODO: externalize in static string
			bw6Requirement.setVersionRange(capability.getVersionRange());

			bw6Requirements.add(bw6Requirement);
		}

		return bw6Requirements;
	}

	private List<Element> getRequirementsConfiguration(List<BW6Requirement> bw6Requirements) {
		List<Element> configuration = new ArrayList<Element>();

		ArrayList<Element> requirements = new ArrayList<Element>();
		for (BW6Requirement bw6Requirement : bw6Requirements) {
			requirements.add(
				element(
					"requirement",
					element("type", bw6Requirement.getType()),
					element("id", bw6Requirement.getId()),
					element("versionRange", bw6Requirement.getVersionRange())
				)
			);
		}

		/*
		 * &lt;dependency-resolution&gt;
		 *   &lt;extraRequirements&gt;
		 *     &lt;requirement&gt;
		 *       &lt;type&gt;eclipse-plugin&lt;/type&gt;
		 *       &lt;id&gt;com.tibco.bw.core.model&lt;/id&gt;
		 *       &lt;versionRange&gt;6.0.0&lt;/versionRange&gt;
		 *     &lt;/requirement&gt;
		 *   &lt;/extraRequirements&gt;
		 * &lt;/dependency-resolution&gt;
		 */
		configuration.add(
			element("dependency-resolution",
				element("extraRequirements",
					requirements.toArray(new Element[0])
				)
			)
		);

		return configuration;
	}

	private void updateTychoTargetPlatformPlugin() throws XmlPullParserException, IOException {
		if (mavenProject == null) return;

		List<BW6Requirement> bw6Requirements = SetUniqueList.setUniqueList(new ArrayList<BW6Requirement>());
		bw6Requirements.addAll(getBW6Requirements(mavenProject)); // retrieve from configuration of this plugin
		bw6Requirements.addAll(getBW6Requirements(builtinCapabilities));
		bw6Requirements.addAll(getBW6Requirements(customCapabilities));

		// the target-platform-configuration plugin exists because it is part of the lifecycle (see 'plexus/components.xml')
		Plugin tychoTargetPlatformPlugin = mavenProject.getPlugin("org.eclipse.tycho:target-platform-configuration");
		PluginBuilder pluginBuilder = new PluginBuilder(tychoTargetPlatformPlugin);

		List<Element> requirementsConfiguration = getRequirementsConfiguration(bw6Requirements);

		pluginBuilder.addConfiguration(configuration(requirementsConfiguration.toArray(new Element[0])));
	}

	public static File getManifest(MavenProject mavenProject) {
		if (mavenProject == null) return null;

		return getManifest(mavenProject.getFile().getParentFile()); // can use mavenProject.getBasedir() ?
	}

	public static File getManifest(File basedir) {
		if (basedir == null || !basedir.exists() || !basedir.isDirectory()) return null;

		File manifest = new File(basedir, "META-INF" + File.separator + "MANIFEST.MF");

		return manifest;
	}

	private List<String> getRequiredCapabilities(MavenProject mavenProject) throws MojoExecutionException, FileNotFoundException, IOException, BundleException {
		Map<String,String> headers = new HashMap<String,String>();
		ManifestElement.parseBundleManifest(new FileInputStream(getManifest(mavenProject)), headers);

		List<String> capabilities = getCapabilites(headers.get("Require-Capability"));

		return capabilities;
	}

	private List<String> getProvidedCapabilities(MavenProject mavenProject) throws FileNotFoundException, IOException, BundleException {
		Map<String,String> headers = new HashMap<String,String>();
		ManifestElement.parseBundleManifest(new FileInputStream(getManifest(mavenProject)), headers);

		List<String> capabilities = getCapabilites(headers.get("Provide-Capability"));

		return capabilities;
	}

	private void addP2Repository(File p2Repository, String p2PropertyName) throws UnknownRepositoryLayoutException  {
		if (p2Repository == null || !p2Repository.exists() || !p2Repository.isDirectory()) return;
		if (p2Repository.list().length <= 0) {
			logger.debug("p2 repository '" + p2Repository.getAbsolutePath() + "' is empty. Skipping.");
			return;
		}
		if (artifactRepositoryFactory != null) {
			ArtifactRepository artifactRepository = artifactRepositoryFactory.createArtifactRepository(
			p2PropertyName,
			"file:///" + p2Repository,
			"p2",
			new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN),
			new ArtifactRepositoryPolicy(false, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN));

			mavenProject.getRemoteArtifactRepositories().add(artifactRepository);
			session.getRequest().getRemoteRepositories().add(artifactRepository);
		}
	}

	private void addBW6P2Repository() throws UnknownRepositoryLayoutException  {
		addP2Repository(getBW6P2Repository(), BW6MojoInformation.BW6.bw6P2Repository);
	}

	private void addProjectP2Repository() throws UnknownRepositoryLayoutException {
		addP2Repository(getProjectP2Repository(), BW6MojoInformation.BW6.projectP2Repository);
	}

	private File getP2Repository(String p2PropertyName) {
		String value;
		if (propertiesManager.propertyExistsInSettings(p2PropertyName)) {
			value = propertiesManager.getPropertyValue(p2PropertyName, true);
		} else {
			value = propertiesManager.getPropertyValue(p2PropertyName);
		}
		String p2Repository = propertiesManager.replaceProperties(propertiesManager.replaceProperties(value));

		File result = new File(p2Repository);

		if (p2Repository != null && p2Repository.contains("${")) {
			return result;
		}

		if (!result.exists()) {
			result.mkdirs();
		}

		return result;
	}

	private File getBW6P2Repository() {
		if (bw6P2Repository != null) {
			return bw6P2Repository;
		}

		bw6P2Repository = getP2Repository(BW6MojoInformation.BW6.bw6P2Repository);
		return bw6P2Repository;
	}

	private File getProjectP2Repository() {
		if (mavenP2Repository != null) {
			return mavenP2Repository;
		}

		mavenP2Repository = getP2Repository(BW6MojoInformation.BW6.projectP2Repository);
		return mavenP2Repository;
	}

	private MavenProject prepareBW6Module() throws MojoExecutionException, IOException, XmlPullParserException, BundleException, UnknownRepositoryLayoutException {
		mavenProject.setPackaging("eclipse-plugin"); // change packaging of the POM to "eclipse-plugin" (used by tycho plugins)

		PluginConfigurator.updatePluginsConfiguration(mavenProject, session, true, BW6PackagingConvertor.class, logger, BW6LifecycleParticipant.pluginKey);

		processRequiredCapabilites(getRequiredCapabilities(mavenProject)); // will update builtinCapabilities and customCapabilities

		updateTychoTargetPlatformPlugin();
		updateProjectDependencies();
		addBW6P2Repository();
		addProjectP2Repository(); // Maven p2 repository is filled by dependencies of Maven model such as shared module (with bw6-shared-module packaging)

		return mavenProject;
	}

	private void updateProjectDependencies() {
		List<Dependency> dependencies = new ArrayList<Dependency>();
		for (Capability capability : customCapabilities) {
			if ("shared".equals(capability.type)) {
				Dependency d = new Dependency();
				d.setGroupId("OSGi");
				d.setArtifactId(capability.id);
				d.setVersion(capability.versionRange);
				d.setType(BW6CommonMojo.BW6_SHARED_MODULE_PACKAGING);

				File f = capability.getFile();
				if (f != null) {
					d.setScope("system");
					d.setSystemPath(f.getAbsolutePath());
				}

				dependencies.add(d);
			}
		}

		projectDependencies.put(mavenProject.getGroupId()+":"+mavenProject.getArtifactId()+":"+mavenProject.getVersion()+":"+mavenProject.getPackaging(), dependencies);
	}

	public MavenProject prepareBW6AppModule() throws MojoExecutionException, UnknownRepositoryLayoutException, IOException, XmlPullParserException, BundleException {
		return prepareBW6Module();
	}

	public MavenProject prepareBW6SharedModule() throws MojoExecutionException, UnknownRepositoryLayoutException, IOException, XmlPullParserException, BundleException {
		return prepareBW6Module();
	}

	public boolean hasBW6ModuleOrDependency() {
		if (mavenProject == null) return false;

		List<String> modules = mavenProject.getModel().getModules();
		for (String module : modules) {
			try {
				Model model = POMManager.getModelOfModule(mavenProject, module);

				if (BW6CommonMojo.BW6_APP_MODULE_PACKAGING.equals(model.getPackaging())) {
					return true;
				}
			} catch (IOException | XmlPullParserException e) {
				logger.debug(e.getLocalizedMessage());
			}
		}
		return false;
	}

	private List<Model> getBW6Modules() {
		List<Model> result = new ArrayList<Model>();
		if (mavenProject == null) return result;

		List<String> modules = mavenProject.getModel().getModules();
		for (String module : modules) {
			try {
				Model model = POMManager.getModelOfModule(mavenProject, module);

				if (model == null) {
					logger.warn("Module '" + module + "' is declared in POM '" + mavenProject.getFile().getAbsolutePath() + "' but not found. Skipping.");
					continue;
				}

				if (BW6CommonMojo.BW6_APP_MODULE_PACKAGING.equals(model.getPackaging()) || // TODO: add OSGi ?
					BW6CommonMojo.BW6_SHARED_MODULE_PACKAGING.equals(model.getPackaging())) {
					result.add(model);
				}
			} catch (IOException | XmlPullParserException e) {
				logger.debug(e.getLocalizedMessage());
			}
		}

		return result;
	}

	/**
	 * <p>
	 * A BW6 application can be defined by a "pom" packaging and have
	 * &lt;modules&gt;.
	 * However, the internal lifecycle to use is the one of "bw6-application"
	 * packaging.
	 * </p>
	 * @return
	 * @throws MojoExecutionException
	 * @throws UnknownRepositoryLayoutException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws BundleException
	 */
	public MavenProject prepareBW6Application() throws MojoExecutionException, UnknownRepositoryLayoutException, IOException, XmlPullParserException, BundleException {
//		PluginConfigurator.updatePluginsConfiguration(mavenProject, true, BW6PackagingConvertor.class, logger);

		mavenProject.setPackaging(BW6CommonMojo.BW6_APPLICATION_PACKAGING); // change packaging of the POM from "pom" to "bw6-application"

		// all BW6 modules become dependencies
		List<Model> bw6Modules = getBW6Modules();
		for (Model bw6Module : bw6Modules) {
			Dependency dependency = new Dependency();
			dependency.setGroupId(bw6Module.getGroupId());
			dependency.setArtifactId(bw6Module.getArtifactId());
			dependency.setVersion(bw6Module.getVersion());
			dependency.setType(bw6Module.getPackaging());

			mavenProject.getModel().addDependency(dependency);

			String key = bw6Module.getGroupId()+":"+bw6Module.getArtifactId()+":"+bw6Module.getVersion()+":eclipse-plugin";
			List<Dependency> transitiveDependencies = projectDependencies.get(key);
			if (transitiveDependencies != null) {
				for (Dependency d : transitiveDependencies) {
					System.out.println(d.getArtifactId());
					mavenProject.getModel().addDependency(d);
				}
			} else {
				this.logger.debug("No transitive dependency found in current project");
			}
		}

		// hack: save <modules> and remove them to allow Model validation
		List<String> modules = new ArrayList<String>(mavenProject.getModel().getModules());
		mavenProject.getModel().getModules().clear();

		// convert this hacked Model as a StringModelSource
		MavenXpp3Writer writer = new MavenXpp3Writer();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		writer.write(baos, mavenProject.getModel());
		String model = baos.toString();
		// TODO : use StringSource instead
		StringModelSource modelSource = new StringModelSource(model);

		// recreate a MavenProject from the hacked model
		// this is useful to have the correct <executions> in this <plugin>
		ProjectBuildingResult projectBuildingResult = null;
		try {
			projectBuildingResult = projectBuilder.build(modelSource, projectBuildingRequest);
		} catch (ProjectBuildingException e) {
			e.printStackTrace();
		}
		MavenProject newMavenProject = projectBuildingResult.getProject();
		newMavenProject.setFile(mavenProject.getFile());

		// hack: put back the <modules> in the Model
		newMavenProject.getModel().getModules().addAll(modules);

		mavenProject = newMavenProject;

		return newMavenProject;
	}

	public void setArtifactRepositoryRepository(ArtifactRepositoryFactory artifactRepositoryFactory) {
		this.artifactRepositoryFactory = artifactRepositoryFactory;
	}

	public void setArtifactResolver(ArtifactResolver artifactResolver) {
		this.artifactResolver = artifactResolver;
	}
	
	public void setArtifactHandler(ArtifactHandler artifactHandler) {
		this.artifactHandler = artifactHandler;
	}

	public void setMavenProject(MavenProject mavenProject) {
		this.mavenProject = mavenProject;
		this.builtinCapabilities.clear();
		this.customCapabilities.clear();
	}
	
	public void setPluginManager(BuildPluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	public void setProjectBuilder(ProjectBuilder projectBuilder) {
		this.projectBuilder = projectBuilder;
	}

	public void setProjectBuildingRequest(ProjectBuildingRequest projectBuildingRequest) {
		this.projectBuildingRequest = projectBuildingRequest;
	}

	public void setProjectDependencies(HashMap<String, List<Dependency>> projectDependencies) {
		this.projectDependencies = projectDependencies;
	}


}
