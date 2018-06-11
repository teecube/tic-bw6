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
package t3.tic.bw6.project.module;

import t3.plugin.annotations.GlobalParameter;
import t3.tic.bw6.BW6MojoInformation;
import t3.tic.bw6.project.BW6ProjectCommonMojo;

import java.io.File;
import java.util.List;

/**
 * <p>
 * A BW6 module can be:
 * </p>
 *  <ul>
 *   <li>an app module</li>
 *   <li>an shared module</li>
 *  </ul>
 * <p>
 *  They both have in common:
 * </p>
 *  <ul>
 *   <li>a build.properties file</li>
 *  </ul>
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
public abstract class BW6ModuleCommonMojo extends BW6ProjectCommonMojo {

    @GlobalParameter (property = BW6MojoInformation.BW6Module.buildProperties, defaultValue = BW6MojoInformation.BW6Module.buildProperties_default, category = BW6MojoInformation.BW6Module.category, required = true)
    protected File buildProperties;

    @GlobalParameter (property = BW6MojoInformation.BW6Module.buildPropertiesSource, defaultValue = BW6MojoInformation.BW6Module.buildPropertiesSource_default, category = BW6MojoInformation.BW6Module.category, required = true)
    protected File buildPropertiesSource;

    @GlobalParameter (property = BW6MojoInformation.BW6Module.diagramsRelativePath, defaultValue = BW6MojoInformation.BW6Module.diagramsRelativePath_default, category = BW6MojoInformation.BW6Module.category)
    protected String diagramsRelativePath;

    @GlobalParameter (property = BW6MojoInformation.BW6Module.diagramsDirectory, defaultValue = BW6MojoInformation.BW6Module.diagramsDirectory_default, category = BW6MojoInformation.BW6Module.category)
    protected File diagramsDirectory;

    /**
     * @return a list of all processes files with ".bwp" extension in the module project.
     */
    protected List<File> getAllProcessesFiles() {
        return getAllBWPFiles(this.projectBasedir);
    }

    protected String getPackage(File bwpProcessFile) {
        if (bwpProcessFile == null || !bwpProcessFile.exists()) return null;

        String path = bwpProcessFile.getAbsolutePath();
        path = path.substring(path.indexOf("Processes") + "Processes".length() + 1, path.lastIndexOf(File.separator));
        path = path.replace("/", ".");
        return path;
    }

}
