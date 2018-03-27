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
package t3.tic.bw6.project.application;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;

import t3.plugin.annotations.Mojo;
import t3.tic.bw6.util.BW6Utils;
import t3.tic.bw6.util.ManifestManager;

/**
 * <p>
 * This goals prepares the META-INF folder of a TIBCO BusinessWorks 6
 * application.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="prepare-application-meta", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class PrepareApplicationMetaMojo extends BW6ApplicationCommonMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		FileFilter filters = new FileFilter() {
			@Override
			public boolean accept(File file) {
				String name = file.getName().toUpperCase();
				String extension = FilenameUtils.getExtension(file.getAbsolutePath());

				return "MANIFEST.MF".equals(name) ||
						"TIBCO.XML".equals(name) ||
						"substvar".equals(extension);
			}
		};
		try {
			FileUtils.copyDirectory(metaInfSource, metaInf, filters);

			Map<String, String> versions = getModulesVersions();
			if (versions.size() > 0) {
				String version = (String) versions.values().toArray()[0];
				ManifestManager.updatedManifestVersion(manifest, version);
				BW6Utils.updateTIBCOXMLVersion(tibcoXML, versions);
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}
	}

}
