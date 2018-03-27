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
package t3.tic.bw6.install.proxy;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import t3.plugin.annotations.Mojo;
import t3.tic.bw6.Messages;

/**
 * <p>
 * This goals uninstalls the TIBCO Business Studio proxy.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="studio-proxy-uninstall", requiresProject = false)
public class StudioProyxUninstallMojo extends StudioProxy {

	@Override
	public List<String> getArguments() {
		List<String> arguments = new ArrayList<>();
		arguments.add("-uninstallIUs");
		arguments.add(proxyFeature);

		return arguments;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute(); // must exist because of standalone goal initialization
	}

	@Override
	protected String getFailureMessage() {
		return Messages.Studio.PROXY_UNINSTALL_FAILED;
	}

	@Override
	protected String getInfoMessage() {
		return Messages.Studio.PROXY_UNINSTALL;
	}

}
