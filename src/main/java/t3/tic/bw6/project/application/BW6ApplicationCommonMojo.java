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
package t3.tic.bw6.project.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import t3.plugin.annotations.GlobalParameter;
import t3.tic.bw6.BW6MojoInformation;
import t3.tic.bw6.Messages;
import t3.tic.bw6.project.BW6ProjectCommonMojo;
import t3.tic.bw6.util.BW6Utils;

/**
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
public abstract class BW6ApplicationCommonMojo extends BW6ProjectCommonMojo {

	@GlobalParameter (property = BW6MojoInformation.BW6Application.tibcoXML, defaultValue = BW6MojoInformation.BW6Application.tibcoXML_default, category = BW6MojoInformation.BW6Application.category, required = true)
	protected File tibcoXML;

	@GlobalParameter (property = BW6MojoInformation.BW6Application.tibcoXMLSource, defaultValue = BW6MojoInformation.BW6Application.tibcoXMLSource_default, category = BW6MojoInformation.BW6Application.category, required = true)
	protected File tibcoXMLSource;

	@Requirement
	protected ArtifactHandler artifactHandler;

	@Requirement
	@Component(role = ArtifactResolver.class)
	protected ArtifactResolver resolver;

	protected String getUnqualifiedVersion() {
		return project.getProperties().getProperty("unqualifiedVersion");
	}

	protected List<File> getModulesJARs() throws MojoExecutionException {
		List<String> resolved = new ArrayList<String>();
		List<File> result = new ArrayList<File>();

		ArtifactResolutionRequest request = new ArtifactResolutionRequest();
		request.setLocalRepository(session.getRequest().getLocalRepository());
		// TODO: manage remote repositories
		request.setRemoteRepositories(session.getRequest().getRemoteRepositories());
		//request.setRemoteRepositories(remoteRepositories);

		for (Object a : project.getDependencyArtifacts()) { // dependencyArtifacts contains modules from Maven reactor
			Artifact artifact = (Artifact) a;
			if (BW6Utils.isBW6(artifact)) {
				if (!"system".equals(artifact.getScope()) || artifact.getFile() == null || !artifact.getFile().exists()) {
					request.setArtifact(artifact);
					ArtifactResolutionResult r = resolver.resolve(request);
					if (r.isSuccess()) {
						getLog().debug("Successfully resolved artifact '" + artifact.getGroupId() + ":" + artifact.getArtifactId() + "'");
					}
					
					if (artifact.getFile() == null || !artifact.getFile().exists()) {
						throw new MojoExecutionException(Messages.DEPENDENCY_RESOLUTION_FAILED, new FileNotFoundException());
					}
					getLog().debug(artifact.getFile().getAbsolutePath());
				}

				result.add(artifact.getFile());
				resolved.add(artifact.getArtifactId()+"-"+artifact.getVersion().replace("-SNAPSHOT", ".qualifier"));
			}
		}

		return result;
	}

	private Manifest getModuleManifest(File moduleFile) throws IOException {
		JarInputStream jarStream = new JarInputStream(new FileInputStream(moduleFile));
		Manifest moduleManifest = jarStream.getManifest();
		jarStream.close();

		return moduleManifest;
	}

	private String getManifesSymbolicName(Manifest manifest) throws FileNotFoundException, IOException {
		return manifest.getMainAttributes().getValue("Bundle-SymbolicName");
	}

	private String getManifestVersion(Manifest manifest) throws FileNotFoundException, IOException {
		return manifest.getMainAttributes().getValue("Bundle-Version");
	}

	protected Map<String, String> getModulesVersions() throws MojoExecutionException {
		Map<String, String> result = new HashMap<String, String>();

		List<File> modulesJARs = getModulesJARs();

		for (File moduleJAR : modulesJARs) {
            String version;
            String symbolicName;
			try {
				Manifest manifest_ = getModuleManifest(moduleJAR);
				version = getManifestVersion(manifest_);
				symbolicName = getManifesSymbolicName(manifest_);

				result.put(moduleJAR.getName(), version);
				result.put(symbolicName, version);
			} catch (IOException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			}

		}

		return result;
	}

	protected Map<String, String> getModulesSymbolicNames() throws MojoExecutionException {
		Map<String, String> result = new HashMap<String, String>();

		List<File> modulesJARs = getModulesJARs();

		for (File moduleJAR : modulesJARs) {
			String symbolicName;
			try {
				Manifest manifest_ = getModuleManifest(moduleJAR);
				symbolicName = getManifesSymbolicName(manifest_);
				
				result.put(moduleJAR.getName(), symbolicName);
			} catch (IOException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			}
			
		}

		return result;
	}

	protected File getPackageUnitFile() {
		return new File(metaInfSource, "TIBCO.xml");
	}
}
