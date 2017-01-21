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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;

import t3.AdvancedMavenLifecycleParticipant;
import t3.CommonTIBCOMojo;
import t3.plugin.annotations.GlobalParameter;
import t3.tic.bw6.osgi.OSGIEnabled;

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

	@GlobalParameter (property = BW6MojoInformation.BW6.eclipsePlatformVersion, defaultValue = BW6MojoInformation.BW6.eclipsePlatformVersion_default, required = true, category = BW6MojoInformation.BW6.category)
	protected String eclipsePlatformVersion;

	@GlobalParameter (property = BW6MojoInformation.BW6.eclipsePlatform, defaultValue = BW6MojoInformation.BW6.eclipsePlatform_default, required = true, category = BW6MojoInformation.BW6.category)
	protected File eclipsePlatformHome;

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

	/* OSGI environment loader (independently of Eclipse platform) */

	protected static Map<String, ClassLoader> osgiEnvironments = new HashMap<String, ClassLoader>();

	private ClassLoader loadOSGIEnvironment(ArrayList<String> osgiDefaultClasspathElements) throws MalformedURLException, DependencyResolutionRequiredException {
		for (ListIterator<String> iterator = osgiDefaultClasspathElements.listIterator(); iterator.hasNext();) {
			String element = iterator.next();
			String osgiDefaultClasspathElement = this.replaceProperties(element);

			File actualFile = new File(osgiDefaultClasspathElement);
			String relativePath = "";
			File parent = actualFile;
			do {
				relativePath = parent.getName() + (relativePath.isEmpty() ? "" : "/" + relativePath);
				parent = parent.getParentFile();
			} while (parent != null && parent.getName().contains("*") && parent.getParentFile() != null);

			if (parent == actualFile || parent == null) {
				iterator.remove();
				getLog().warn("OSGI environment loading - bundle not found : '" + element + "'");
				continue;
			};

			while (relativePath.contains("/")) {
				IOFileFilter wildcardFilter = new WildcardFileFilter(relativePath.substring(0, relativePath.indexOf("/")));
				Collection<File> r = FileUtils.listFilesAndDirs(parent, wildcardFilter, wildcardFilter);
				parent = r.toArray(new File[0])[1];
				relativePath = relativePath.substring(relativePath.indexOf("/")+1);
			}
			Collection<File> r = FileUtils.listFiles(parent, new WildcardFileFilter(relativePath), TrueFileFilter.TRUE);
			if (r.size() <= 0) {
				iterator.remove();
				getLog().warn("OSGI environment loading - bundle not found : '" + element + "'");
				continue;
			}
			actualFile = r.toArray(new File[0])[0];
			osgiDefaultClasspathElement = actualFile.getAbsolutePath();
			iterator.set(osgiDefaultClasspathElement);
		}

		List<URL> osgiClasspathElements = new ArrayList<URL>();

		for (String osgiDefaultClasspathElement : osgiDefaultClasspathElements) {
			File osgiDefaultClasspathElementFile = new File(osgiDefaultClasspathElement);
			if (osgiDefaultClasspathElementFile.exists()) {
				osgiClasspathElements.add(osgiDefaultClasspathElementFile.toURI().toURL());
			}
		}

		URLClassLoader osgiClassloader = new URLClassLoader(osgiClasspathElements.toArray(new URL[0]), this.getClass().getClassLoader());
		return osgiClassloader;
	}

	protected ClassLoader getOSGIClassLoader() throws MojoExecutionException {
		if (!(this instanceof OSGIEnabled)) {
			throw new MojoExecutionException("This class does not support OSGI environment loading", new UnsupportedOperationException());
		}

		ClassLoader osgiEnvironmentForCurrentClass = osgiEnvironments.get(this.getClass().getCanonicalName());
		if (osgiEnvironmentForCurrentClass != null) {
			return osgiEnvironmentForCurrentClass;
		}

		try {
			osgiEnvironmentForCurrentClass = loadOSGIEnvironment(((OSGIEnabled) this).getOSGIClasspathElements());
		} catch (MalformedURLException | DependencyResolutionRequiredException e) {
			throw new MojoExecutionException("Unable to load OSGI environment", e);
		}

		osgiEnvironments.put(this.getClass().getCanonicalName(), osgiEnvironmentForCurrentClass);
		return osgiEnvironmentForCurrentClass;
	}

}
