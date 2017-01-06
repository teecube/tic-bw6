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
package t3.tic.bw6.project.module;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;

import t3.plugin.annotations.Mojo;
import t3.plugin.annotations.Parameter;
import t3.tic.bw6.BW6MojoInformation;
import t3.tic.bw6.util.ManifestManager;
import t3.tic.bw6.util.ManifestManager.ManifestVersionUpdate;

/**
 * <p>
 * This goals prepares the META-INF folder of a TIBCO BusinessWorks 6 app module
 * or shared module.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="prepare-module-meta", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class PrepareModuleMetaMojo extends BW6ModuleCommonMojo {

	/**
	 * <p>
	 * Whether the version set in the Manifest file
	 * (by default <i>META-INF/MANIFEST.MF</i>) of the project is the reference
	 * or not (by default it is not). If it is not, the version of the
	 * <i>POM</i> is the reference.
	 * </p>
	 * <p>
	 * This plugin will always synchronize the two versions (the one in the
	 * <i>POM</i> with Maven syntax and the one in <i>META-INF/MANIFEST.MF</i>
	 * file with OSGi syntax).
	 * The reference version remains the same while the other is set to  the
	 * same value as the reference version. 
	 * </p>
	 */
	@Parameter (property = BW6MojoInformation.BW6Module.manifestVersionIsMaster, defaultValue = BW6MojoInformation.BW6Module.manifestVersionIsMaster_default)
	private boolean manifestVersionIsMaster;

	// TODO: externalize messages
	private void syncManifestVersion() throws MojoExecutionException {
		try {
			String pomVersion = this.project.getVersion();
			if (!manifestVersionIsMaster) {
				ManifestVersionUpdate manifestUpdate = ManifestManager.updatedManifestVersion(this.manifestSource, pomVersion);
				if (manifestUpdate.updated) {
					getLog().info("Updating Manifest version from '" + manifestUpdate.oldVersion + "' to '" + manifestUpdate.newVersion + "' (POM version is '" + pomVersion + "').");
				} else {
					getLog().info("Manifest version '" + manifestUpdate.oldVersion + "' is in sync with POM version '" + pomVersion + "': not updating.");
				}
			} else {
				String manifestVersion = ManifestManager.getManifestVersion(this.manifestSource);
				getLog().info("The Bundle-Version of the manifest won't be updated because the '" + BW6MojoInformation.BW6Module.manifestVersionIsMaster + "' property is set.");
				if (!ManifestManager.convertVersionFromMavenToOSGi(pomVersion).equals(manifestVersion)) {
					getLog().error("Manifest version '" + manifestVersion + "' is not in sync with POM version '" + pomVersion + "'.");
					// TODO: update POM version if specified
					throw new MojoFailureException("The POM version must be in sync with the manifest version.");
				}
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		syncManifestVersion();
	}

}
