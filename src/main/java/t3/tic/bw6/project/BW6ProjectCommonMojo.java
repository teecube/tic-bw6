/**
 * (C) Copyright 2016-2016 teecube
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
package t3.tic.bw6.project;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;
import t3.plugin.annotations.GlobalParameter;
import t3.tic.bw6.BW6CommonMojo;
import t3.tic.bw6.BW6MojoInformation;

/**
 * <p>
 *  There are three kinds of BW6 projects:
 * </p>
 *  <ul>
 *   <li>BW6 app module</li>
 *   <li>BW6 shared module</li>
 *   <li>BW6 application</li>
 *  </ul>
 * <p>
 *  They all have in common:
 * </p>
 *  <ul>
 *   <li>a META-INF folder</li>
 *   <li>a MANIFEST.MF file in the META-INF folder</li>
 *   <li>a .config file</li>
 *   <li>a .project file</li>
 *  </ul>
 * <p>
 * This abstract Mojo (which can be inherited by concrete ones) defines all
 * these common objects as Mojo parameters.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
public abstract class BW6ProjectCommonMojo extends BW6CommonMojo {

	@GlobalParameter (property = BW6MojoInformation.BW6Project.metaInfSource, defaultValue = BW6MojoInformation.BW6Project.metaInfSource_default, category = BW6MojoInformation.BW6Project.category, required = true)
	protected File metaInfSource;

	@GlobalParameter (property = BW6MojoInformation.BW6Project.metaInf, defaultValue = BW6MojoInformation.BW6Project.metaInf_default, category = BW6MojoInformation.BW6Project.category, required = true)
	protected File metaInf;

	@GlobalParameter (property = BW6MojoInformation.BW6Project.manifestSource, defaultValue = BW6MojoInformation.BW6Project.manifestSource_default, category = BW6MojoInformation.BW6Project.category, required = true)
	protected File manifestSource;

	@GlobalParameter (property = BW6MojoInformation.BW6Project.manifest, defaultValue = BW6MojoInformation.BW6Project.manifest_default, category = BW6MojoInformation.BW6Project.category, required = true)
	protected File manifest;

	@GlobalParameter (property = BW6MojoInformation.BW6Project.dotConfig, defaultValue = BW6MojoInformation.BW6Project.dotConfig_default, category = BW6MojoInformation.BW6Project.category, required = true)
	protected File dotConfig;

	@GlobalParameter (property = BW6MojoInformation.BW6Project.dotProject, defaultValue = BW6MojoInformation.BW6Project.dotProject_default, category = BW6MojoInformation.BW6Project.category, required = true)
	protected File dotProject;

	/**
	 * @return a list of all files with ".substvar" extension in "META-INF" folder of the project.
	 */
	protected List<File> getAllSubstVarFiles() {
		FileFilter substVarFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".substvar");
			}
		};

		File[] substVarFiles = metaInfSource.listFiles(substVarFilter);

		List<File> result = new ArrayList<File>();
		List<?> substVarFilesList = Arrays.asList(substVarFiles);
		for (Object substVarFileObject : substVarFilesList) {
			File substVarFile = (File) substVarFileObject;
			result.add(substVarFile);
		}

		return result;
	}
}
