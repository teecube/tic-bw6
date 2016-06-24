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

import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.tibco.schemas.tra.model.core.packagingmodel.Module;
import com.tibco.schemas.tra.model.core.packagingmodel.Property;

import t3.POMManager;
import t3.plugin.annotations.Mojo;

/**
 * <p>
 * This goals removes a module from an application.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
@Mojo(name="remove-module", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class RemoveModuleMojo extends CommonModule {

	@Override
	protected boolean updateModule(String moduleSymbolicName, String moduleVersion, ModuleComponentsMarshaller moduleComposite, PackageUnitMarshaller packageUnit) throws MojoExecutionException {
		boolean found = false;
		if (packageUnit.getObject().getModules() != null) {
			for (Iterator<Module> iterator = packageUnit.getObject().getModules().getModule().iterator(); iterator.hasNext();) {
				Module module = iterator.next();
				if (moduleSymbolicName.equals(module.getSymbolicName()) && 
					moduleVersion.equals(module.getTechnologyVersion())) {
					iterator.remove();
					found = true;
					getLog().info("Removing module '" + moduleSymbolicName + ":" + moduleVersion + "' from application.");
					break;
				}
			}
		}
		if (packageUnit.getObject().getProperties() != null) {
			for (Iterator<Property> iterator = packageUnit.getObject().getProperties().getProperty().iterator(); iterator.hasNext();) {
				Property property = iterator.next();
				
				if (property.getName().startsWith("//" + moduleSymbolicName + "//")) {
					iterator.remove();
				}
			}
		}

		if (!found) {
			getLog().warn("Module to remove not found.");
		}

		removeModuleInApplication();

		return found;
	}

	private void removeModuleInApplication() throws MojoExecutionException {
		try {
			if (project.getFile() != null && project.getFile().exists()) {
				POMManager.removeProjectAsModule(project.getFile(), moduleRelativePath, null);
			}
		} catch (IOException | XmlPullParserException e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}
	}
}
