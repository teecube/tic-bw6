/**
 * (C) Copyright 2016-2018 teecube
 * (https://teecu.be) and others.
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import t3.plugin.annotations.Mojo;
import t3.plugin.annotations.Parameter;
import t3.tic.bw6.BW6MojoInformation;
import t3.tic.bw6.osgi.OSGIEnabled;
import t3.tic.bw6.project.module.diagrams.DiagramGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This goals updates the content of the generated JAR file for a BW6 module (shared module or app module).
 *
 * For instance, it will try to embed SVG diagrams for processes.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="update-bw6-module", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true)
public class UpdateModuleMojo extends BW6ModuleCommonMojo implements OSGIEnabled {

    @Parameter (property = BW6MojoInformation.BW6Module.skipUpdate, defaultValue = BW6MojoInformation.BW6Module.skipUpdate_default)
    private boolean updateBW6ModuleSkip;

    private ClassLoader osgiClassLoader;

    private File getDiagram(File bwpProcessFile) {
        diagramsDirectory.mkdirs();
        return new File(diagramsDirectory, getPackage(bwpProcessFile) + "." + FilenameUtils.removeExtension(bwpProcessFile.getName()) + ".bwd"); // TODO : externalize
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (updateBW6ModuleSkip) {
            skipGoal(BW6MojoInformation.BW6Module.skipUpdate);
            return;
        }

        super.execute();

        List<File> processesFiles = getAllProcessesFiles();
        try {
            if (!processesFiles.isEmpty()) {
                osgiClassLoader = this.getOSGIClassLoader();
                DiagramGenerator diagramGenerator = new DiagramGenerator(osgiClassLoader, getLog());
                for (File processFile : processesFiles) {
                    diagramGenerator.generateProcessDiagram(processFile, getDiagram(processFile));
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }

    }

    @Override
    public List<File> getOSGIClasspathElements() throws MojoExecutionException {
        List<File> bundles = new ArrayList<File>();

        try {
            List<File> designLocations = getDesignLocations();
            for (File designLocation : designLocations) {
                File pluginsDirectory = new File(designLocation, "plugins");
                if (pluginsDirectory.exists() && pluginsDirectory.isDirectory()) {
                    bundles.addAll(FileUtils.listFiles(new File(designLocation, "plugins"), new String[]{"jar"}, false));
                }
            }

            bundles.addAll(FileUtils.listFiles(new File(eclipsePlatformHome, "org.eclipse.equinox.p2.touchpoint.eclipse/plugins"), new String[] {"jar"}, false));
            bundles.addAll(FileUtils.listFiles(new File(tibcoBW6Home, "system/shared"), new String[] {"jar"}, true));

            getLog().debug("Found " + bundles.size() + " OSGi bundles");
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }

        return bundles;
    }

}
