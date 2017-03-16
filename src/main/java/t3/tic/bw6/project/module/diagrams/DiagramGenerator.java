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
package t3.tic.bw6.project.module.diagrams;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.emf.common.util.URI;

import t3.tic.bw6.osgi.nonosgi.registry.RegistryFactoryHelper;

/**
 * 
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
public class DiagramGenerator {

	private Log logger;
	private ClassLoader osgiClassLoader;
	private Object resourceSet;

	public DiagramGenerator(ClassLoader osgiClassLoader, Log logger) throws Exception {
		this.logger = logger;
		this.osgiClassLoader = osgiClassLoader;
		prepareBW6Environment();
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

	public void generateProcessDiagram(File bwp, File svgFile) throws Exception {
//		URI uri = URI.createFileURI(bwp.getAbsolutePath()); // the URI
		Class<?> URI_class = osgiClassLoader.loadClass("org.eclipse.emf.common.util.URI");
		Method m = URI_class.getDeclaredMethod("createFileURI", String.class);
		Object uri = m.invoke(null, bwp.getAbsolutePath());

//		Resource resource = resourceSet.getResource(uri, true);
		Method getResource_method = resourceSet.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getResource", URI.class, boolean.class);
		PrintStream oldSystemErr = System.err;
		Object resource;
		try {
			System.setErr(new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
				}
			}));
			resource = getResource_method.invoke(resourceSet, uri, true);
		} finally {
			System.setErr(oldSystemErr);
		}

		Method getProcess_method = resource.getClass().getDeclaredMethod("getProcess", (Class<?>[]) null);
		Object process = getProcess_method.invoke(resource, (Object[]) null);

		Class<?> BWGenerateSVGServiceImpl_class = osgiClassLoader.loadClass("com.tibco.bw.core.design.svg.BWGenerateSVGServiceImpl");
		Class<?> Process_class = osgiClassLoader.loadClass("org.eclipse.bpel.model.Process");
		Method parseProcessDiagram_method = BWGenerateSVGServiceImpl_class.getDeclaredMethod("parseProcessDiagram", Process_class, File.class);
		parseProcessDiagram_method.setAccessible(true);
		parseProcessDiagram_method.invoke(BWGenerateSVGServiceImpl_class.newInstance(), process, svgFile);

		logger.debug("Saved SVG process diagram of '" + bwp + "' to '" + svgFile + "'");
	}

}
