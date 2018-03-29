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
package t3.tic.bw6.studio;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.twdata.maven.mojoexecutor.MojoExecutor.*;
import t3.plugin.annotations.Mojo;
import t3.plugin.annotations.Parameter;
import t3.tic.bw6.BW6MojoInformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * <p>
 * This goals launches the TIBCO Business Studio of TIBCO BusinessWorks 6
 * installation with a workspace found following these rules (in this order):
 *  <ol>
 *   <li>looking in directory specified by
 *   <strong>tibco.bw6.studio.workspace.location</strong> property.</li>
 *   <li>looking in current directory, if it does not contain a POM.</li>
 *   <li>looking in parent directory, if current directory contains a POM.</li>
 *  </ol>
 * If no workspace is found, a new one will be tentatively created if
 * <strong>tibco.bw6.studio.workspace.createIfNotExists</strong> property is set
 * to <em>true</em>. Creation is delegated to
 * <a href="./workspace-mojo.html">bw6:workspace</a> goal.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="studio", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = false)
public class StudioLauncherMojo extends StudioCommon {

    /**
     * <p>
     * Whether to create a workspace on-the-fly if no workspace is found.
     * </p>
     */
    @Parameter (property = BW6MojoInformation.Studio.workspaceCreateIfNotExists, defaultValue = BW6MojoInformation.Studio.workspaceCreateIfNotExists_default)
    protected boolean createIfNotExists;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File workspace = getWorkspaceLocation();

        if (!workspaceExists(workspace)) {
            getLog().info("No workspace found at '" + workspace.getAbsolutePath() + "'");

            if (createIfNotExists) {
                getLog().info("Creating one...");
                workspace = createWorkspace();
            } else {
                getLog().error("Unable to create workspace");
                throw new MojoExecutionException("Unable to create workspace.");
            }
        } else {
            getLog().info("Using workspace found at '" + workspace.getAbsolutePath() + "'");
        }

        ArrayList<String> arguments = new ArrayList<String>();
        if (workspace != null && workspace.exists() && workspace.isDirectory()) {
            arguments.add("-data");
            arguments.add(workspace.getAbsolutePath());
        }

        try {
            executeBusinessStudio(arguments, projectBasedir, "fail to launch Studio", true, true);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    private File createWorkspace() throws MojoExecutionException {
        WorkspaceMojo.workspace = null;

        getLog().info("");
        getLog().info(">>> " + pluginDescriptor.getArtifactId() + ":" + pluginDescriptor.getVersion() + ":workspace (" + "default-cli" + ") @ " + project.getArtifactId() + " >>>");

        ArrayList<Element> configuration = new ArrayList<Element>();

        executeMojo(
            plugin(
                groupId(pluginDescriptor.getGroupId()),
                artifactId(pluginDescriptor.getArtifactId()),
                version(pluginDescriptor.getVersion())
            ),
            goal("workspace"),
            configuration(
                configuration.toArray(new Element[0])
            ),
            getEnvironment()
        );

        getLog().info("");
        getLog().info("<<< " + pluginDescriptor.getArtifactId() + ":" + pluginDescriptor.getVersion() + ":workspace (" + "default-cli" + ") @ " + project.getArtifactId() + " <<<");
        getLog().info("");

        return WorkspaceMojo.workspace;
    }

}
