/**
 * (C) Copyright 2016-2016 teecube
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
package t3.tic.bw6.install.proxy;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import t3.plugin.annotations.Mojo;
import t3.tic.bw6.Messages;

/**
 * <p>
 * This goals installs the TIBCO Business Studio proxy which is a prerequisite
 * for this plugin.
 * </p>
 * <p>
 * The TIBCO Business Studio proxy is an Eclipse feature packaged in an Eclipse
 * update site as a Maven artifact and downloaded from Maven repositories.<br />
 * The coordinates of this artifact are defined as follows:
 *  <ul>
 *   <li><strong>groupId</strong> is set with
 *    <i>tibco.bw6.studio.proxyGroupId</i> parameter and defaults to
 *    <i>io.teecube.tic.tic-bw6-studio</i>. This should never be changed.
 *   </li>
 *   <li><strong>artifactId</strong> is set with
 *    <i>tibco.bw6.studio.proxyArtifactId</i> parameter and defaults to
 *    <i>tic-bw6-studio-proxy-site</i>. This should never be changed.
 *   </li>
 *   <li><strong>version</strong> is guessed by concatenation of
 *    <i>tibco.bw6.studio.version</i> parameter (with a major and minor version)
 *    and <i>tic-bw6-studio-proxy-site</i> parameter (a revision version).
 *    This should never be changed.
 *   </li>
 *  </ul>
 * </p>
 * 
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="studio-proxy-install", requiresProject = false)
public class StudioProyxInstallMojo extends StudioProxy {

	@Override
	public List<String> getArguments() {
		List<String> arguments = new ArrayList<>();
		arguments.add("-installIUs");
		arguments.add(proxyFeature);

		return arguments;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute(); // must exist because of standalone goal initialization
	}

	@Override
	protected String getFailureMessage() {
		return Messages.Studio.PROXY_INSTALL_FAILED;
	}

	@Override
	protected String getInfoMessage() {
		return Messages.Studio.PROXY_INSTALL;
	}

}
