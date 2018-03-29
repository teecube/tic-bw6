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

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import t3.Utils;
import t3.plugin.annotations.Mojo;
import t3.plugin.annotations.Parameter;
import t3.tic.bw6.BW6MojoInformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This goals configures an Eclipse workspace ready to be used by TIBCO Business
 * Studio in a location found following these rules (in this order):
 *  <ol>
 *   <li>using directory specified by
 *   <strong>tibco.bw6.studio.workspace.location</strong> property.</li>
 *   <li>using current directory, if it does not contain a POM.</li>
 *   <li>using parent directory, if current directory contains a POM.</li>
 *  </ol>
 * Concerning projects imported in the workspace, there are several behaviours:
 *  <ul>
 *   <li><strong>if the current directory has no POM</strong><br />
 *     It will scan for POMs in sub-directories with "bw6-app-module",
 *     "bw6-shared-module" or "pom" packagings (for "pom" packaging, only POMs
 *     having modules with "bw6-app-module" or "bw6-shared-module" packagings
 *     will be kept).<br /><br />
 *     This is best used when a directory holds a lot of unrelated projects.
 *   </li>
 *   <li><strong>if the current directory has a POM</strong><br />
 *     Only the current POM and its modules will be added to the workspace.
 *     <br /><br />
 *     This is best used when a directory holds a BW6 application and its
 *     BW6 modules.
 *   </li>
 *  </ul>
 * Workspaces created with this goal can be launched with
 * <a href="./studio-mojo.html">bw6:studio</a> goal. The latter can directly
 * create workspaces on-the-fly using this goal.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="workspace", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = false)
public class WorkspaceMojo extends StudioCommon {

    /**
     * <p>
     * Whether to overwrite an existing workspace.
     * </p>
     */
    @Parameter (property = BW6MojoInformation.Studio.workspaceOverwriteExisting, defaultValue = BW6MojoInformation.Studio.workspaceOverwriteExisting_default)
    protected boolean overwriteExisting;

    /**
     * <p>Whether the location of the workspace is available or not.</p>
     * <p>Available means:
     *  <ul>
     *   <li>either the directory does not contain any workspace</li>
     *   <li>or it is allowed to overwrite any existing workspace in this
     *   directory</li>
     *  </ul>
     * </p>
     */
    protected boolean checkWorkspaceIsAvailable(File workspace) {
        return !workspaceExists(workspace) || overwriteExisting;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        workspace = getWorkspaceLocation();

        if (!checkWorkspaceIsAvailable(workspace)) {
            throw new MojoExecutionException("Workspace already exists and unable to delete it");
        }

        if (workspaceExists(workspace) && overwriteExisting) {
            getLog().info("Deleting workspace at '" + workspace.getAbsolutePath() + "'");
            try {
                FileUtils.deleteDirectory(new File(workspace, ".bsProject"));
                FileUtils.deleteDirectory(new File(workspace, ".com.tibco.bw.rad"));
                FileUtils.deleteDirectory(new File(workspace, ".metadata"));
            } catch (IOException e) {
                throw new MojoExecutionException(e.getLocalizedMessage(), e);
            }
        }

        List<File> projects = null;
        try {
            projects = getProjects();
        } catch (IOException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add("--launcher.suppressErrors");
        arguments.add("-nosplash");
        arguments.add("-application");
        arguments.add("tic-bw6-studio-proxy-plugin.App");
        for (File project : projects) {
            arguments.add("-import");
            arguments.add(project.getParentFile().getAbsolutePath());
        }
        arguments.add("-data");
        arguments.add(workspace.getAbsolutePath());
//        arguments.add("-ignoreExisting");

        try {
            executeBusinessStudio(arguments, getSessionBasedir(), "fail to create workspace");
        } catch (IOException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }

        if (!workspace.exists()) { // ???
            workspace.mkdirs();
        }
    }

    private List<File> getProjects() throws IOException {
        FileSet restriction = new FileSet();
        restriction.setDirectory(workspace.getAbsolutePath());
        restriction.addInclude("**/.project");
        restriction.addExclude("**/.bsProject/.project");
        restriction.addExclude("**/.com.tibco.bw.rad/.project");
        List<File> projects = Utils.toFileList(restriction);

        return projects;
    }

}
