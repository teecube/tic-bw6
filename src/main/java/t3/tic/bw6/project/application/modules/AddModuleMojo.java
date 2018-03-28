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
package t3.tic.bw6.project.application.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.tools.ant.taskdefs.optional.ReplaceRegExp;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.osoa.xmlns.sca._1.Component;
import org.osoa.xmlns.sca._1.PropertyType;

import com.tibco.schemas.tra.model.core.packagingmodel.Module;
import com.tibco.schemas.tra.model.core.packagingmodel.PackageUnit.Modules;
import com.tibco.schemas.tra.model.core.packagingmodel.PackageUnit.Properties;
import com.tibco.schemas.tra.model.core.packagingmodel.Property;
import com.tibco.xmlns.repo.types._2002.GlobalVariable;
import com.tibco.xmlns.repo.types._2002.GlobalVariables;

import org.xml.sax.SAXException;
import t3.POMManager;
import t3.Utils;
import t3.plugin.annotations.Mojo;
import t3.tic.bw6.BW6LifecycleParticipant;
import t3.tic.bw6.util.BW6Utils;
import t3.tic.bw6.util.SubstVarMarshaller;

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
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
	}

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

		// updated UUID in .bwm file
		QName idAttribute = new QName("http://www.omg.org/XMI", "id", "xmi");
		moduleComposite.getObject().getOtherAttributes().put(idAttribute, EcoreUtil.generateUUID());
		for (PropertyType property : moduleComposite.getObject().getProperty()) {
			property.getOtherAttributes().put(idAttribute, EcoreUtil.generateUUID());
		}
		for (Component component : moduleComposite.getObject().getComponent()) {
			component.getOtherAttributes().put(idAttribute, EcoreUtil.generateUUID());
			component.getImplementation().getOtherAttributes().put(idAttribute, EcoreUtil.generateUUID());
		}

		try {
			moduleComposite.save();
		} catch (UnsupportedEncodingException | FileNotFoundException | JAXBException e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
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

		List<File> substVarFiles = getAllSubstVarFiles();
		for (File substVarFile : substVarFiles) {
			SubstVarMarshaller substVarMarshaller;
			try {
				substVarMarshaller = new SubstVarMarshaller(substVarFile);

				GlobalVariables globalVariables = substVarMarshaller.getObject().getGlobalVariables();
				if (globalVariables == null) {
					substVarMarshaller.getObject().setGlobalVariables(new GlobalVariables());
					globalVariables = substVarMarshaller.getObject().getGlobalVariables();
				}

				for (PropertyType property : moduleComposite.getObject().getProperty()) {
					String gvName = "//" + moduleSymbolicName + "//" + property.getName();
					if (BW6Utils.globalVariableExists(globalVariables, gvName)) continue;

					GlobalVariable gv = new GlobalVariable();
					String type = property.getType().getLocalPart();
					if (type != null && type.length() > 0) {
						type = type.substring(0, 1).toUpperCase() + type.substring(1); // Capitalize first letter
					}

					gv.setName(gvName);
					if (property.getName().endsWith("BW.HOST.NAME")) {
						gv.setValue("localhost");
					} else {
						gv.setValue("");
					}
					gv.setDeploymentSettable(false);
					gv.setServiceSettable(false);
					gv.setType(type);
					gv.setIsOverride(false);

					globalVariables.getGlobalVariable().add(gv);
				}

				substVarMarshaller.save();
			} catch (JAXBException | SAXException | UnsupportedEncodingException | FileNotFoundException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			}
		}

		// replace UUID by really unique UUID and namespaces by really unique namespaces
		File modulePath = new File(project.getFile().getParentFile(), moduleRelativePath);

		List<File> bwpFiles = getAllBWPFiles(modulePath);

		for (File bwpFile : bwpFiles) {
			// first replace all UUID ending by 123456789000
			String pattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-123456789000";
			Integer uuidCount = 0;
			try {
				uuidCount = Utils.countMatchesInFile(bwpFile, pattern);
			} catch (IOException e) {
				continue;
			}

			ReplaceRegExp replaceRegExp;
			for (int i = 0; i < uuidCount; i++) {
				replaceRegExp = new ReplaceRegExp();
				replaceRegExp.setFile(bwpFile);
				replaceRegExp.setMatch(pattern);
				replaceRegExp.setReplace(UUID.randomUUID().toString());
				replaceRegExp.setByLine(false);
				replaceRegExp.execute();
			}

			// then replace all namespace ending by /1234567890
			String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			replaceRegExp = new ReplaceRegExp();
			replaceRegExp.setFile(bwpFile);
			replaceRegExp.setMatch("http:\\/\\/xmlns\\.example\\.com\\/1234567890");
			replaceRegExp.setReplace("http://xmlns.example.com/" + timeStamp);
			replaceRegExp.setByLine(true);
			replaceRegExp.execute();
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
