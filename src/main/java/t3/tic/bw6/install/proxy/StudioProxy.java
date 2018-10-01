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
package t3.tic.bw6.install.proxy;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import t3.plugin.annotations.Parameter;
import t3.tic.bw6.BW6CommonMojo;
import t3.tic.bw6.BW6MojoInformation;
import t3.tic.bw6.Messages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Generic class to install or uninstall of the TIBCO Business Studio proxy
 * which is a prerequisite for this plugin.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
public abstract class StudioProxy extends BW6CommonMojo {

    /**
     * <p>
     * The <strong>groupId</strong> of the Maven artifact of the Eclipse update
     * site with the TIBCO Business Studio proxy feature.
     * </p>
     */
    @Parameter (property = BW6MojoInformation.Studio.studioProxyGroupId, defaultValue = BW6MojoInformation.Studio.studioProxyGroupId_default, required = true)
    protected String proxyGroupId;

    /**
     * <p>
     * The <strong>artifactId</strong> of the Maven artifact of the Eclipse
     * update site with the TIBCO Business Studio proxy feature.
     * </p>
     */
    @Parameter (property = BW6MojoInformation.Studio.studioProxyArtifactId, defaultValue = BW6MojoInformation.Studio.studioProxyArtifactId_default, required = true)
    protected String proxyArtifactId;

    /**
     * <p>
     * The revision <strong>version</strong> (aka third digit of the version) of
     * the Maven artifact of the Eclipse update site with the TIBCO Business
     * Studio proxy feature.
     * </p>
     */
    @Parameter (property = BW6MojoInformation.Studio.studioProxyVersion, defaultValue = BW6MojoInformation.Studio.studioProxyVersion_default, required = true)
    protected Integer proxyVersion;

    /**
     * <p>
     * The name of the TIBCO Business Studio proxy Eclipse feature name. 
     * </p>
     */
    @Parameter (property = BW6MojoInformation.Studio.studioProxyFeature, defaultValue = BW6MojoInformation.Studio.studioProxyFeature_default, required = true)
    protected String proxyFeature;

    /**
     * <p>
     * Timeout for the execution the installation.
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

    public abstract List<String> getArguments();
    protected abstract String getFailureMessage();
    protected abstract String getInfoMessage();

    private void retrieveStudioProxyArtifact(File outputDirectory) throws MojoExecutionException {
        try {
            copyDependency(proxyGroupId, proxyArtifactId, businessStudioVersion + "." + proxyVersion, "zip", outputDirectory, "repo.zip");
        } catch (MojoExecutionException e) {
            // TODO: explain the problem about the proxy dependency not found 
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File outputDirectory;
        try {
            outputDirectory = Files.createTempDirectory("unpack").toFile();
        } catch (IOException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }

        retrieveStudioProxyArtifact(outputDirectory);

        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add("--launcher.suppressErrors");
        arguments.add("-nosplash");
        arguments.add("-application");
        arguments.add("org.eclipse.equinox.p2.director");
        arguments.add("-install");
        arguments.add(businessStudioHome.getAbsolutePath());
        arguments.add("-repository");
        arguments.add("jar:file:///" + new File(outputDirectory, "repo.zip").getAbsolutePath().replace("\\", "/") + "!/");
        arguments.addAll(getArguments());

        getLog().info(getInfoMessage());

        try {
            executeBusinessStudio(arguments, businessStudioHome, getFailureMessage());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

}
