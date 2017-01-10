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
package t3.tic.bw6.project.module;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.eclipse.emf.common.util.URI;

import t3.plugin.annotations.Mojo;
import t3.plugin.annotations.Parameter;
import t3.tic.bw6.BW6MojoInformation;
import t3.tic.bw6.osgi.OSGIEnabled;
import t3.tic.bw6.osgi.nonosgi.registry.RegistryFactoryHelper;

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

	private Object resourceSet;

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
				prepareBW6Environment();
				for (File processFile : processesFiles) {
					generateProcessDiagram(processFile, new File(processFile.getAbsolutePath() + ".svg"));
				}
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}

	}

	/* original method */
//	private void prepareBW6Environment() {
//		AdapterRegistry.INSTANCE.registerAdapterFactory(new org.eclipse.bpel.validator.factory.AdapterFactory());
//		AdapterRegistry.INSTANCE.registerAdapterFactory(ExtensionsPackage.eINSTANCE, new ExtensionsAdapterFactory());
//		AdapterRegistry.INSTANCE.registerAdapterFactory(BWNotationPackage.eINSTANCE, new BWNotationAdapterFactory());
//		AdapterRegistry.INSTANCE.registerAdapterFactory(NotationPackage.eINSTANCE, new NotationAdapterFactory());
//
//		resourceSet = new BWBPELResourceSetImpl();
//		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bwp", new BWDTProcessResourceFactory());
//		resourceSet.getPackageRegistry().put(BwextPackage.eNS_URI, BwextPackage.eINSTANCE);
//		resourceSet.getPackageRegistry().put(GeneralActivitiesPackage.eNS_URI, GeneralActivitiesPackage.eINSTANCE);
//
//		RegistryFactoryHelper.getRegistry(osgiClassLoader);
//	}

	private void prepareBW6Environment() throws Exception {
		Class<?> AdapterRegistry_class = osgiClassLoader.loadClass("org.eclipse.bpel.model.adapters.AdapterRegistry");

		// get static field AdapterRegistry.INSTANCE
		Field AdapterRegistry_INSTANCE_field = AdapterRegistry_class.getDeclaredField("INSTANCE");
		AdapterRegistry_INSTANCE_field.setAccessible(true);
		Object AdapterRegistryINSTANCE = AdapterRegistry_INSTANCE_field.get(null);

		// get method AdapterRegistry.registerAdapterFactory(IAdapterFactory)
		Class<?> IAdapterFactory_class = osgiClassLoader.loadClass("org.eclipse.core.runtime.IAdapterFactory");
		Method registerAdapterFactory = AdapterRegistry_class.getDeclaredMethod("registerAdapterFactory", IAdapterFactory_class);

		// get method AdapterRegistry.registerAdapterFactory(EPackage, AdapterFactory)
		Class<?> EPackage_class = osgiClassLoader.loadClass("org.eclipse.emf.ecore.EPackage");
		Class<?> AdapterFactory_class = osgiClassLoader.loadClass("org.eclipse.emf.common.notify.AdapterFactory");
		Method registerAdapterFactory2 = AdapterRegistry_class.getDeclaredMethod("registerAdapterFactory", EPackage_class, AdapterFactory_class);

		AdapterFactory_class = osgiClassLoader.loadClass("org.eclipse.bpel.validator.factory.AdapterFactory");

//		AdapterRegistry.INSTANCE.registerAdapterFactory(new org.eclipse.bpel.validator.factory.AdapterFactory());
		registerAdapterFactory.invoke(AdapterRegistryINSTANCE, AdapterFactory_class.newInstance());

//		AdapterRegistry.INSTANCE.registerAdapterFactory(ExtensionsPackage.eINSTANCE, new ExtensionsAdapterFactory());
		Object ExtensionsPackage_eINSTANCE = osgiClassLoader.loadClass("com.tibco.bx.bpelExtension.extensions.ExtensionsPackage").getDeclaredField("eINSTANCE").get(null);
		Object extensionsAdapterFactory = osgiClassLoader.loadClass("com.tibco.bx.bpelExtension.extensions.util.ExtensionsAdapterFactory").newInstance();
		registerAdapterFactory2.invoke(AdapterRegistryINSTANCE, ExtensionsPackage_eINSTANCE, extensionsAdapterFactory);

//		AdapterRegistry.INSTANCE.registerAdapterFactory(BWNotationPackage.eINSTANCE, new BWNotationAdapterFactory());
		Object BWNotationPackage_eINSTANCE = osgiClassLoader.loadClass("com.tibco.bw.core.design.model.bwnotation.BWNotationPackage").getDeclaredField("eINSTANCE").get(null);
		Object bwNotationAdapterFactory = osgiClassLoader.loadClass("com.tibco.bw.core.design.model.bwnotation.util.BWNotationAdapterFactory").newInstance();
		registerAdapterFactory2.invoke(AdapterRegistryINSTANCE, BWNotationPackage_eINSTANCE, bwNotationAdapterFactory);

//		AdapterRegistry.INSTANCE.registerAdapterFactory(NotationPackage.eINSTANCE, new NotationAdapterFactory());
		Object NotationPackage_eINSTANCE = osgiClassLoader.loadClass("org.eclipse.gmf.runtime.notation.NotationPackage").getDeclaredField("eINSTANCE").get(null);
		Object notationAdapterFactory = osgiClassLoader.loadClass("org.eclipse.gmf.runtime.notation.util.NotationAdapterFactory").newInstance();
		registerAdapterFactory2.invoke(AdapterRegistryINSTANCE, NotationPackage_eINSTANCE, notationAdapterFactory);

//		ResourceSet resourceSet = new BWBPELResourceSetImpl();
		Class<?> BWBPELResourceSetImpl_class = osgiClassLoader.loadClass("com.tibco.bw.core.model.process.resource.BWBPELResourceSetImpl");
		resourceSet = BWBPELResourceSetImpl_class.newInstance();

//		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bwp", new BWDTProcessResourceFactory());
		Object resourceFactoryRegistry = BWBPELResourceSetImpl_class.getSuperclass().getSuperclass().getDeclaredMethod("getResourceFactoryRegistry", (Class<?>[]) null).invoke(resourceSet, (Object[]) null);
		Object extensionToFactoryMap = resourceFactoryRegistry.getClass().getSuperclass().getDeclaredMethod("getExtensionToFactoryMap", (Class<?>[]) null).invoke(resourceFactoryRegistry, (Object[]) null);
		Class<?> BWDTProcessResourceFactory_class = osgiClassLoader.loadClass("com.tibco.bw.core.design.model.bpelext.BWDTProcessResourceFactory");
		extensionToFactoryMap.getClass().getDeclaredMethod("put", Object.class, Object.class).invoke(extensionToFactoryMap, "bwp", BWDTProcessResourceFactory_class.newInstance());

//		resourceSet.getPackageRegistry().put(BwextPackage.eNS_URI, BwextPackage.eINSTANCE);
		Object packageRegistry = BWBPELResourceSetImpl_class.getSuperclass().getSuperclass().getDeclaredMethod("getPackageRegistry", (Class<?>[]) null).invoke(resourceSet, (Object[]) null);
		Class<?> BwextPackage_class = osgiClassLoader.loadClass("com.tibco.bw.core.model.bwext.BwextPackage");
		Object BwextPackage_eNS_URI = BwextPackage_class.getDeclaredField("eNS_URI").get(null);
		Object BwextPackage_eINSTANCE = BwextPackage_class.getDeclaredField("eINSTANCE").get(null);
		packageRegistry.getClass().getSuperclass().getDeclaredMethod("put", Object.class, Object.class).invoke(packageRegistry, BwextPackage_eNS_URI, BwextPackage_eINSTANCE);

//		resourceSet.getPackageRegistry().put(GeneralActivitiesPackage.eNS_URI, GeneralActivitiesPackage.eINSTANCE);
		Class<?> GeneralActivitiesPackage_class = osgiClassLoader.loadClass("com.tibco.bw.palette.generalactivities.model.GeneralActivitiesPackage");
		Object GeneralActivitiesPackage_eNS_URI = GeneralActivitiesPackage_class.getDeclaredField("eNS_URI").get(null);
		Object GeneralActivitiesPackage_eINSTANCE = GeneralActivitiesPackage_class.getDeclaredField("eINSTANCE").get(null);
		packageRegistry.getClass().getSuperclass().getDeclaredMethod("put", Object.class, Object.class).invoke(packageRegistry, GeneralActivitiesPackage_eNS_URI, GeneralActivitiesPackage_eINSTANCE);

		RegistryFactoryHelper.getRegistry(osgiClassLoader);
	}

	/* original method */
//	private void generateProcessDiagram(File bwp, File svgFile) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
//		URI uri = URI.createFileURI(bwp.getAbsolutePath()); // the URI
//
//		Resource resource = resourceSet.getResource(uri, true);
//		VirtualRootImpl vri = (VirtualRootImpl) resource.getContents().get(0);
//		org.eclipse.bpel.model.Process process = (org.eclipse.bpel.model.Process) vri.getProcess();
//
//		parseProcessDiagram(process, svgFile, resourceSet);
//		System.out.println("Saved SVG process diagram of '" + bwp + "' to '" + svgFile + "'");
//	}

	private void generateProcessDiagram(File bwp, File svgFile) throws Exception {
//		URI uri = URI.createFileURI(bwp.getAbsolutePath()); // the URI
		Class<?> URI_class = osgiClassLoader.loadClass("org.eclipse.emf.common.util.URI");
		Method m = URI_class.getDeclaredMethod("createFileURI", String.class);
		Object uri = m.invoke(null, bwp.getAbsolutePath());

//		ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
		try {
//			Thread.currentThread().setContextClassLoader(osgiClassLoader);;

//			Resource resource = resourceSet.getResource(uri, true);
			Method getResource_method = resourceSet.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getResource", URI.class, boolean.class);
			Object resource = getResource_method.invoke(resourceSet, uri, true);

			Method getProcess_method = resource.getClass().getDeclaredMethod("getProcess", (Class<?>[]) null);
			Object process = getProcess_method.invoke(resource, (Object[]) null);

			Class<?> BWGenerateSVGServiceImpl_class = osgiClassLoader.loadClass("com.tibco.bw.core.design.svg.BWGenerateSVGServiceImpl");
			Class<?> Process_class = osgiClassLoader.loadClass("org.eclipse.bpel.model.Process");
			Method parseProcessDiagram_method = BWGenerateSVGServiceImpl_class.getDeclaredMethod("parseProcessDiagram", Process_class, File.class);
			parseProcessDiagram_method.setAccessible(true);
			parseProcessDiagram_method.invoke(BWGenerateSVGServiceImpl_class.newInstance(), process, svgFile);
		} finally {
//			Thread.currentThread().setContextClassLoader(oldClassloader);
		}

		getLog().debug("Saved SVG process diagram of '" + bwp + "' to '" + svgFile + "'");
	}

	@Override
	public ArrayList<String> getOSGIClasspathElements() {
		return new ArrayList<String>(Arrays.asList("${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.bw.core.design.model_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.bw.core.design.resource_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.bw.core.design_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.amf.sharedresource.model.common.eclipse_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.bw.core.model.eclipse_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.bw.design_6.3.400.011.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.core.runtime_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/javax.annotation_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/javax.inject_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.osgi_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.osgi.compatibility.state_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.equinox.common_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.core.jobs_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.core.runtime.compatibility.registry_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.equinox.registry_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.equinox.preferences_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.core.contenttype_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.equinox.app_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.core.databinding_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.core.databinding.observable_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.emf.transaction_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.emf.edit_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.emf.common_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.emf.ecore_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.emf.ecore.change_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.emf.validation_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.core.resources_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.ui_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.swt_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.swt.win32.win32.x86_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.jface_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.core.commands_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.ui.workbench_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.e4.ui.workbench3_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.ui.views.properties.tabbed_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.ui.forms_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.ui.views_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.wst.wsdl_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.xsd_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.xpd.resources_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.ltk.core.refactoring_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.amf.tools.composite.resources.ui_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.amf.tools.composite.editor_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.amf.sca.model.composite.eclipse_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.amf.sca.model.eclipse_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.amf.sca.model.edit_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.gmf.runtime.draw2d.ui_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.draw2d_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.gef_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.ocl.ecore_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.ocl_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.emf.ecore.xmi_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/lpg.runtime.java_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.ocl.common_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.zion.common_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.bw.model.eclipse_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.bw.palette.generalactivities.model_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.bx.bpelExtensions.model.eclipse_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.security.tibcrypt.eclipse_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/javax.wsdl_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.apache.xerces_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/javax.xml_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.apache.xml.resolver_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.e4.core.di_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.e4.ui.model.workbench_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.e4.ui.workbench_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.gmf.runtime.notation_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.osgi.services_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.pde.core_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.ui.ide_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/org.eclipse.wst.validation_*.jar",
												   "${tibco.bw6.eclipse.platform}/org.eclipse.equinox.p2.touchpoint.eclipse/plugins/com.tibco.bw.core.design.svg_*.jar",

												   "${tibco.bw6.bw.home}/system/shared/com.tibco.tpcl.org.eclipse.bpel_*/com.tibco.tpcl.org.eclipse.bpel.model.jar",
												   "${tibco.bw6.bw.home}/system/shared/com.tibco.tpcl.org.eclipse.bpel_*/com.tibco.tpcl.org.eclipse.bpel.common.model.jar",
												   "${tibco.bw6.bw.home}/system/shared/com.tibco.tpcl.org.eclipse.bpel.validator_*/com.tibco.tpcl.org.eclipse.bpel.validator.jar",
												   "${tibco.bw6.bw.home}/system/shared/com.tibco.tpcl.javax.wsdl4j_*/wsdl4j.jar",

												   "${tibco.bw6.bw.home}/system/design/plugins/com.tibco.zion.dc_*.jar",
												   "${tibco.bw6.bw.home}/system/design/plugins/com.tibco.bw.core.design.svg_*.jar",
												   "${tibco.bw6.bw.home}/system/design/plugins/com.tibco.bx.bpelExtensions.model.eclipse_*.jar",
												   "${tibco.bw6.bw.home}/system/design/plugins/com.tibco.tra.packaging.model_*.jar"));
	}

}
