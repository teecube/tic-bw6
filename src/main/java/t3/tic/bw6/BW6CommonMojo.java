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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

import t3.AdvancedMavenLifecycleParticipant;
import t3.CommonTIBCOMojo;
import t3.plugin.annotations.GlobalParameter;

/**
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
public abstract class BW6CommonMojo extends CommonTIBCOMojo {

	/* BW */
	@GlobalParameter (property = BW6MojoInformation.BW6.bwVersion, description = BW6MojoInformation.BW6.bwVersion_description, category = BW6MojoInformation.BW6.category, required = true, valueGuessedByDefault = false)
	protected String tibcoBW6Version;

	@GlobalParameter (property = BW6MojoInformation.BW6.bw6P2Repository, defaultValue = BW6MojoInformation.BW6.bw6P2Repository_default, description = BW6MojoInformation.BW6.bw6P2Repository_description, category = BW6MojoInformation.BW6.category)
	protected File tibcoBW6P2Repository;

	@GlobalParameter (property = BW6MojoInformation.BW6.projectP2Repository, defaultValue = BW6MojoInformation.BW6.projectP2Repository_default, description = BW6MojoInformation.BW6.projectP2Repository_description, category = BW6MojoInformation.BW6.category)
	protected File tibcoMavenP2Repository;

	@GlobalParameter (property = BW6MojoInformation.BW6.bwHome, defaultValue = BW6MojoInformation.BW6.bwHome_default, description = BW6MojoInformation.BW6.bwHome_description, category = BW6MojoInformation.BW6.category)
	protected File tibcoBW6Home;

	@GlobalParameter (property = BW6MojoInformation.BW6.bwBin, defaultValue = BW6MojoInformation.BW6.bwBin_default, description = BW6MojoInformation.BW6.bwBin_description, category = BW6MojoInformation.BW6.category)
	protected File tibcoBW6Bin;

	@GlobalParameter (property = BW6MojoInformation.BW6.bwAdmin, defaultValue = BW6MojoInformation.BW6.bwAdmin_default, description = BW6MojoInformation.BW6.bwAdmin_description, category = BW6MojoInformation.BW6.category)
	protected File tibcoBW6Admin;

	@GlobalParameter (property = BW6MojoInformation.BW6.bwAdminTRA, defaultValue = BW6MojoInformation.BW6.bwAdminTRA_default, description = BW6MojoInformation.BW6.bwAdminTRA_description, category = BW6MojoInformation.BW6.category)
	protected File tibcoBW6AdminTRA;

	@GlobalParameter (property = BW6MojoInformation.BW6.bwInstall, defaultValue = BW6MojoInformation.BW6.bwInstall_default, description = BW6MojoInformation.BW6.bwInstall_description, category = BW6MojoInformation.BW6.category)
	protected File tibcoBW6Install;

	@GlobalParameter (property = BW6MojoInformation.BW6.bwInstallTRA, defaultValue = BW6MojoInformation.BW6.bwInstallTRA_default, description = BW6MojoInformation.BW6.bwInstallTRA_description, category = BW6MojoInformation.BW6.category)
	protected File tibcoBW6InstallTRA;

	@GlobalParameter (property = BW6MojoInformation.Studio.studioVersion, description = BW6MojoInformation.Studio.studioVersion_description, required = true, category = BW6MojoInformation.Studio.category)
	protected String businessStudioVersion;

	@GlobalParameter (property = BW6MojoInformation.Studio.studioHome, defaultValue = BW6MojoInformation.Studio.studioHome_default, description = BW6MojoInformation.Studio.studioHome_description, required = true, category = BW6MojoInformation.Studio.category)
	protected File businessStudioHome;

	@GlobalParameter (property = BW6MojoInformation.Studio.studio, defaultValue = BW6MojoInformation.Studio.studio_default, description = BW6MojoInformation.Studio.studio_description, required = true, category = BW6MojoInformation.Studio.category)
	protected File businessStudio;

	public final static String BW6_APPLICATION_PACKAGING = "bw6-application";
	public final static String BW6_APP_MODULE_PACKAGING = "bw6-app-module";
	public final static String BW6_SHARED_MODULE_PACKAGING = "bw6-shared-module";

	public final static String JAR_EXTENSION = ".jar";

	@Override
	protected AdvancedMavenLifecycleParticipant getLifecycleParticipant() throws MojoExecutionException {
		return new BW6LifecycleParticipant();
	}

	protected int executeBusinessStudio(List<String> arguments, File workingDirectory, String errorMessage, boolean fork, boolean synchronous) throws MojoExecutionException, IOException {
		return executeBinary(businessStudio, arguments, workingDirectory, errorMessage, fork, synchronous);
	}

	/**
	 * <p>
	 * Default behaviour is synchronous and no fork.
	 * </p>
	 */
	protected int executeBusinessStudio(ArrayList<String> arguments, File workingDirectory, String errorMessage) throws IOException, MojoExecutionException {
		return executeBusinessStudio(arguments, workingDirectory, errorMessage, false, true);
	}
}
