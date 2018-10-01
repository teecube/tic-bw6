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
package t3.tic.bw6.install;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import t3.Messages;
import t3.plugin.annotations.Mojo;
import t3.plugin.annotations.Parameter;
import t3.tic.bw6.BW6CommonMojo;
import t3.tic.bw6.BW6MojoInformation;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * <p>
 * This goals creates a p2 repository for Maven builds.<br />
 * It is composed of plugins (including palettes) found in these directories:
 *  <ul>
 *   <li>${tibco.home}/bw/${tibco.bw6.version}/system/lib</li>
 *   <li>${tibco.home}/bw/${tibco.bw6.version}/system/palettes</li>
 *   <li>${tibco.home}/bw/${tibco.bw6.version}/system/shared</li>
 *   <li>${tibco.home}/eclipse-platform/bundlepool/1.0/org.eclipse.equinox.p2.touchpoint.eclipse</li>
 *  </ul>
 * The p2 repository is created in ${tibco.bw6.p2repository} directory
 * (default is: '${tibco.home}/bw/${tibco.bw6.version}/maven/p2repo').
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="p2maven-install", requiresProject = false)
public class BW6P2MavenInstallMojo extends BW6CommonMojo {

    /**
     * <p>
     * Timeout for the execution of the installation.
     * </p>
     * <p>
     * Default value is: 1800 seconds (30 minutes)
     * </p>
     */
    @Parameter (property = BW6MojoInformation.BW6.bwInstallTimeOut, defaultValue = BW6MojoInformation.BW6.bwInstallTimeOut_default)
    protected int timeOut;

    @Override
    protected int getTimeOut() {
        return timeOut;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Initializing a p2 repository for Maven build... (This might take some minutes)");

        try {
            // create a temporary p2 repository with its plugins directory
            File tempRepo = Files.createTempDirectory("tempRepo").toFile();
            File tempRepoPlugins = new File(tempRepo, "plugins");
            getLog().debug("Temporary p2 repository: " + tempRepo.getAbsolutePath());
            getLog().debug("Temporary p2 plugins: " + tempRepoPlugins.getAbsolutePath());

            // add plugins from these directories
            File systemLib = new File(this.tibcoBW6Home, "system/lib");
            getLog().debug("Adding plugins from: " + systemLib.getAbsolutePath());
            FileUtils.copyDirectory(systemLib, tempRepoPlugins);

            File systemPalettes = new File(this.tibcoBW6Home, "system/palettes");
            getLog().debug("Adding plugins from: " + systemPalettes.getAbsolutePath());
            FileUtils.copyDirectory(systemPalettes, tempRepoPlugins);

            File systemShared = new File(this.tibcoBW6Home, "system/shared");
            getLog().debug("Adding plugins from: " + systemShared.getAbsolutePath());
            FileUtils.copyDirectory(systemShared, tempRepoPlugins);

            // clean up '${tibco.home}/bw/${tibco.bw6.version}/maven/p2repo' directory if exists
            if (tibcoBW6P2Repository.exists()) {
                FileUtils.deleteDirectory(tibcoBW6P2Repository);
            }
            // (then) create it
            tibcoBW6P2Repository.mkdirs();

            publishP2(new File(tibcoHOME, "eclipse-platform/bundlepool/1.0/org.eclipse.equinox.p2.touchpoint.eclipse"), tibcoBW6P2Repository);
            publishP2(tempRepo, tibcoBW6P2Repository);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }

    }

    private File getLauncherJar() throws MojoExecutionException {
        FileFilter launcherFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().startsWith("org.eclipse.equinox.launcher_") && file.getName().endsWith(".jar");
            }
        };

        File pluginsDirectory = new File(tibcoHOME, "eclipse-platform/bundlepool/1.0/org.eclipse.equinox.p2.touchpoint.eclipse/plugins"); // exists because enforced in "plugins-configuration/goals/bw6_p2maven-install.xml"
        File[] launchers = pluginsDirectory.listFiles(launcherFilter);

        if (launchers.length > 0) {
            return launchers[0];
        } else {
            throw new MojoExecutionException("Unable to find 'org.eclipse.equinox.launcher_*.jar' file in '"+ pluginsDirectory.getAbsolutePath() + "'.", new FileNotFoundException());
        }
    }

    private void publishP2(File source, File destination) throws MojoExecutionException {
        if (source == null || destination == null || !source.exists() || !destination.exists() || !source.isDirectory() || !destination.isDirectory()) return;

        getLog().info(Messages.MESSAGE_SPACE);
        getLog().info("Publishing from '" + source.getAbsolutePath() + "' to p2 repository '" + destination.getAbsolutePath() + "'.");

        File launcherJar = getLauncherJar();

        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add("-jar");
        arguments.add(launcherJar.getAbsolutePath());
        arguments.add("-install");
        arguments.add(businessStudioHome.getAbsolutePath());
        arguments.add("-application");
        arguments.add("org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher");
        arguments.add("-metadataRepository");
        arguments.add("file://" + destination.getAbsolutePath());
        arguments.add("-artifactRepository");
        arguments.add("file://" + destination.getAbsolutePath());
        arguments.add("-source");
        arguments.add(source.getAbsolutePath());
        arguments.add("-append");
        arguments.add("-publishArtifacts");

        try {
            executeBinary(new File(tibcoJRE64Home, "bin/java"), arguments, new File(tibcoJRE64Home, "bin"), "failed");
        } catch (IOException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }

    }

}
