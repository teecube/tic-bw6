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

import t3.plugin.annotations.Parameter;
import t3.tic.bw6.BW6CommonMojo;
import t3.tic.bw6.BW6MojoInformation;

import java.io.File;

/**
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
public class StudioCommon extends BW6CommonMojo {

    /**
     * <p>
     * Location of an Eclipse workspace for TIBCO BusinessStudio.
     * </p>
     */
    @Parameter (property = BW6MojoInformation.Studio.workspaceLocation, defaultValue = "", required = false)
    protected File workspaceLocation;

    private File sessionBasedir = null;

    public static File workspace;

    protected File getWorkspaceLocation() {
        if (workspaceLocation != null && workspaceLocation.exists() && workspaceLocation.isDirectory()) {
            return workspaceLocation;
        }

        File pomInSessionBasedir = new File(getSessionBasedir(), "pom.xml");

        if (!pomInSessionBasedir.exists()) { // no POM
            workspaceLocation = getSessionBasedir(); // create in session basedir
        } else {
            workspaceLocation = getSessionBasedir().getParentFile(); // create in parent directory
        }

        return workspaceLocation;
    }

    protected File getSessionBasedir() {
        if (sessionBasedir == null) {
            sessionBasedir  = new File(session.getRequest().getBaseDirectory());
        }

        return sessionBasedir;
    }

    protected boolean workspaceExists(File workspace) {
        return new File(workspace, ".bsProject").exists() ||
               new File(workspace, ".com.tibco.bw.rad").exists() ||
               new File(workspace, ".metadata").exists();
    }

}
