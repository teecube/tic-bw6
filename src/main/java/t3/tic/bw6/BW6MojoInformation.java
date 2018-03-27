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
package t3.tic.bw6;

import t3.plugin.annotations.Categories;
import t3.plugin.annotations.Category;

/**
* <p>
* Centralization of all Mojo parameters.
* </p>
*
* @author Mathieu Debove &lt;mad@teecu.be&gt;
*
*/
@Categories({
	@Category(title = BW6MojoInformation.BW6.category, description = BW6MojoInformation.BW6.categoryDescription),
	@Category(title = BW6MojoInformation.BW6Project.category, description = BW6MojoInformation.BW6Project.categoryDescription),
	@Category(title = BW6MojoInformation.BW6Application.category, description = BW6MojoInformation.BW6Application.categoryDescription),
	@Category(title = BW6MojoInformation.BW6Module.category, description = BW6MojoInformation.BW6Module.categoryDescription),
	@Category(title = BW6MojoInformation.Studio.category, description = BW6MojoInformation.Studio.categoryDescription),
})
public class BW6MojoInformation {

	public static class BW6 {
		public static final String category = "TIBCO BusinessWorks 6";
		public static final String categoryDescription = "Properties concerning TIBCO BusinessWorks 6 binaries & environment";

		public static final String bwVersion = "tibco.bw6.version";
		public static final String bwVersion_description = "The TIBCO BusinessWorks 6 version with major and minor version.";

		public static final String bw6P2Repository = "tibco.bw6.p2repository";
		public static final String bw6P2Repository_default = "${tibco.home}/bw/${tibco.bw6.version}/maven/p2repo";
		public static final String bw6P2Repository_description = "TIBCO BusinessWorks 6 needs a p2 repository to build. By default, this plugin will use the same repository used by built-in TIBCO Maven plugin.";

		public static final String projectP2Repository = "tibco.bw6.maven.p2repository";
		public static final String projectP2Repository_default = "${project.build.directory}/p2repo";
		public static final String projectP2Repository_description = "Project-specific p2 repository which will be used to include Maven dependencies in OSGI format.";

		public static final String bwHome = "tibco.bw6.bw.home";
		public static final String bwHome_default = "${tibco.home}/bw/${tibco.bw6.version}";
		public static final String bwHome_description = "The path where TIBCO BusinessWorks 6 is installed.";

		public static final String bwBin = "tibco.bw6.bw.bin";
		public static final String bwBin_default = "${tibco.home}/bw/${tibco.bw6.version}/bin";
		public static final String bwBin_description = "The path to TIBCO BusinessWorks 6 binaries directory.";

		public static final String bwConfig = "tibco.bw6.bw.config";
		public static final String bwConfig_default = "${tibco.home}/bw/${tibco.bw6.version}/config";
		public static final String bwConfig_description = "The path to TIBCO BusinessWorks 6 configuration directory.";

		public static final String bwAdmin = "tibco.bw6.bw.bwadmin";
		public static final String bwAdmin_default = "${tibco.bw6.bw.bin}/bwadmin${executables.extension}";
		public static final String bwAdmin_description = "The path of 'bwadmin' binary.";

		public static final String bwAdminTRA = "tibco.bw6.bw.bwadmin.tra";
		public static final String bwAdminTRA_default = "${tibco.bw6.bw.bin}/bwadmin.tra";
		public static final String bwAdminTRA_description = "The path of 'bwadmin' TRA file.";

		public static final String bwAgent = "tibco.bw6.bw.bwagent";
		public static final String bwAgent_default = "${tibco.bw6.bw.bin}/bwagent${executables.extension}";
		public static final String bwAgent_description = "The path of 'bwagent' binary.";

		public static final String bwAgentTRA = "tibco.bw6.bw.bwagent.tra";
		public static final String bwAgentTRA_default = "${tibco.bw6.bw.bin}/bwagent.tra";
		public static final String bwAgentTRA_description = "The path of 'bwagent' TRA file.";

		public static final String bwAgentIni = "tibco.bw6.bw.bwagent.ini";
		public static final String bwAgentIni_default = "${tibco.bw6.bw.config}/bwagent.ini";
		public static final String bwAgentIni_description = "The path of 'bwagent.ini' file.";

		public static final String bwInstall = "tibco.bw6.bw.bwinstall";
		public static final String bwInstall_default = "${tibco.bw6.bw.bin}/bwinstall${executables.extension}";
		public static final String bwInstall_description = "The path of 'bwinstall' binary.";

		public static final String bwInstallTRA = "tibco.bw6.bw.bwinstall.tra";
		public static final String bwInstallTRA_default = "${tibco.bw6.bw.bin}/bwinstall.tra";
		public static final String bwInstallTRA_description = "The path of 'bwinstall' TRA file.";

		public static final String bwInstallTimeOut = "tibco.bw6.bw.bwinstall.timeout";
		public static final String bwInstallTimeOut_default = "1800";

		public static final String eclipsePlatform = "tibco.bw6.eclipse.platform";
		public static final String eclipsePlatform_default = "${tibco.home}/eclipse-platform/bundlepool/${tibco.bw6.eclipse.platform.version}";

		public static final String eclipsePlatformVersion = "tibco.bw6.eclipse.platform.version";
		public static final String eclipsePlatformVersion_default = "1.0";
	}

	public static class BW6Project {
		public static final String category = "TIBCO BusinessWorks 6 project";
		public static final String categoryDescription = "Properties concerning a TIBCO BusinessWorks 6 project";

		public static final String metaInfSource = "tibco.bw6.project.metainf.source";
		public static final String metaInfSource_default = "${basedir}/META-INF";

		public static final String metaInf = "tibco.bw6.project.metainf";
		public static final String metaInf_default = "${project.build.directory}/META-INF";

		public static final String manifestSource = "tibco.bw6.project.manifest.source";
		public static final String manifestSource_default = "${basedir}/META-INF/MANIFEST.MF";

		public static final String manifest = "tibco.bw6.project.manifest";
		public static final String manifest_default = "${project.build.directory}/META-INF/MANIFEST.MF";

		public static final String dotConfig = "tibco.bw6.project.dot.config";
		public static final String dotConfig_default = "${basedir}/.config";

		public static final String dotProject = "tibco.bw6.project.dot.project";
		public static final String dotProject_default = "${basedir}/.project";
	}

	public static class BW6Application {
		public static final String category = "TIBCO BusinessWorks 6 application";
		public static final String categoryDescription = "Properties concerning a TIBCO BusinessWorks 6 application";

		public static final String tibcoXML = "tibco.bw6.project.application.tibco.xml";
		public static final String tibcoXML_default = "${project.build.directory}/META-INF/TIBCO.xml";

		public static final String tibcoXMLSource = "tibco.bw6.project.application.tibco.xml.source";
		public static final String tibcoXMLSource_default = "${basedir}/META-INF/TIBCO.xml";

		public static final String moduleRelativePath = "tibco.bw6.project.module.relativePath";
	}

	public static class BW6Module {
		public static final String category = "TIBCO BusinessWorks 6 module";
		public static final String categoryDescription = "Properties concerning a TIBCO BusinessWorks 6 module";

		public static final String buildProperties = "tibco.bw6.project.module.build.properties";
		public static final String buildProperties_default = "${project.build.directory}/build.properties";

		public static final String buildPropertiesSource = "tibco.bw6.project.module.build.properties.source";
		public static final String buildPropertiesSource_default = "${basedir}/build.properties";

		public static final String manifestVersionIsMaster = "tibco.bw6.project.module.manifestVersionIsMaster";
		public static final String manifestVersionIsMaster_default = "false";

		public static final String skipUpdate = "tibco.bw6.project.module.skipUpdate";
		public static final String skipUpdate_default = "false";

		public static final String diagramsRelativePath = "tibco.bw6.project.module.diagrams.relativePath";
		public static final String diagramsRelativePath_default = "diagrams";

		public static final String diagramsDirectory = "tibco.bw6.project.module.diagrams.directory";
		public static final String diagramsDirectory_default = "${project.build.directory}/${tibco.bw6.project.module.diagrams.relativePath}";		
	}

	public static class Studio {
		public static final String category = "TIBCO BusinessStudio";
		public static final String categoryDescription = "Properties concerning TIBCO BusinessStudio embedded in TIBCO BusinessWokrs 6";

		public static final String studioVersion = "tibco.bw6.studio.version";
		public static final String studioVersion_description = "The TIBCO BusinessStudio version with major and minor version.";

		public static final String studioHome = "tibco.bw6.studio.home";
		public static final String studioHome_default = "${tibco.home}/studio/${tibco.bw6.studio.version}/eclipse";
		public static final String studioHome_description = "The path where TIBCO BusinessStudio is installed.";

		public static final String studio = "tibco.bw6.studio";
		public static final String studio_default = "${tibco.bw6.studio.home}/TIBCOBusinessStudio${executables.extension}";
		public static final String studio_description = "The path of TIBCO BusinessStudio binary (used to launch Eclipse).";

		public static final String studioProxyGroupId = "tibco.bw6.studio.proxyGroupId";
		public static final String studioProxyGroupId_default = "io.teecube.tic.tic-bw6-studio";
		public static final String studioProxyArtifactId = "tibco.bw6.studio.proxyArtifactId";
		public static final String studioProxyArtifactId_default = "tic-bw6-studio-proxy-site";
		public static final String studioProxyVersion = "tibco.bw6.studio.proxyVersion";
		public static final String studioProxyVersion_default = "1";
		public static final String studioProxyFeature = "tibco.bw6.studio.proxyFeature";
		public static final String studioProxyFeature_default = "tic-bw6-studio-proxy-feature.feature.group";

		public static final String workspaceOverwriteExisting = "tibco.bw6.studio.workspace.overwriteExisting";
		public static final String workspaceOverwriteExisting_default = "false";

		public static final String workspaceCreateIfNotExists = "tibco.bw6.studio.workspace.createIfNotExists";
		public static final String workspaceCreateIfNotExists_default = "true";

		public static final String workspaceLocation = "tibco.bw6.studio.workspace.location";
	}
}
