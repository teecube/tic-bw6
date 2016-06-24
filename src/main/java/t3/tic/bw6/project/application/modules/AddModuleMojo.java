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
package t3.tic.bw6.project.application.modules;

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.osoa.xmlns.sca._1.PropertyType;

import com.tibco.schemas.tra.model.core.packagingmodel.Module;
import com.tibco.schemas.tra.model.core.packagingmodel.PackageUnit.Modules;
import com.tibco.schemas.tra.model.core.packagingmodel.PackageUnit.Properties;
import com.tibco.schemas.tra.model.core.packagingmodel.Property;

import t3.POMManager;
import t3.plugin.annotations.Mojo;
import t3.tic.bw6.BW6LifecycleParticipant;

/**
 * <p>
 * This goals adds a BW6 module in an BW6 application.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="add-module", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class AddModuleMojo extends CommonModule {

	@Override
	protected boolean updateModule(String moduleSymbolicName, String moduleVersion, ModuleComponentsMarshaller moduleComposite, PackageUnitMarshaller packageUnit) throws MojoExecutionException {
		if (packageUnit.getObject().getModules() != null) {
			for (Module module : packageUnit.getObject().getModules().getModule()) {
				if (moduleSymbolicName.equals(module.getSymbolicName()) && 
					moduleVersion.equals(module.getTechnologyVersion())) {

					getLog().warn("Module '" + moduleSymbolicName + ":" + moduleVersion + "' already exists in application. Skipping.");

					addModuleInApplication();

					return false;
				}
			}
		} else {
			packageUnit.getObject().setModules(new Modules());
		}

		// not found, adding it
		Module module = new Module();
		module.setSymbolicName(moduleSymbolicName);
		module.setTechnologyType("bw-appmodule,osgi-bundle"); // TODO : externalize
		module.setTechnologyVersion(moduleVersion);
		packageUnit.getObject().getModules().getModule().add(module);

		if (packageUnit.getObject().getProperties() == null) {
			packageUnit.getObject().setProperties(new Properties());
		}

		for (PropertyType property : moduleComposite.getObject().getProperty()) {
			Property p = new Property();
			String scalable = property.getOtherAttributes().get(new QName("scalable"));
			p.setName("//" + moduleSymbolicName + "//" + property.getName());
			p.setType("xs:" + property.getType().getLocalPart());
			p.setVisibility("private");
			p.setScalable(Boolean.parseBoolean(scalable));
			p.setOverrideValue(false);
			packageUnit.getObject().getProperties().getProperty().add(p);
		}

		getLog().info("Adding module '" + moduleSymbolicName + ":" + moduleVersion + "' in application.");

		addModuleInApplication();
		removeParentIfApplication();

		return true;
	}

	private void removeParentIfApplication() throws MojoExecutionException {
		try {
			if (BW6LifecycleParticipant.hasBadParentDefinition(project, moduleRelativePath)) {
				Model moduleModel = POMManager.getModelOfModule(project, moduleRelativePath);
				File modulePom = POMManager.getPomOfModule(project.getFile(), moduleRelativePath);

				getLog().info("Removing parent from '" + moduleModel.getGroupId() + ":" + moduleModel.getArtifactId() + ":" + moduleModel.getVersion() + ":" + moduleModel.getPackaging() + "'");
				POMManager.removeParent(modulePom);
			}
		} catch (IOException | XmlPullParserException e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}
	}

	private void addModuleInApplication() throws MojoExecutionException {
		try {
			if (project.getFile() != null && project.getFile().exists()) {
				POMManager.addProjectAsModule(project.getFile(), moduleRelativePath, null, true);
			}
		} catch (IOException | XmlPullParserException e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}
	}

}
