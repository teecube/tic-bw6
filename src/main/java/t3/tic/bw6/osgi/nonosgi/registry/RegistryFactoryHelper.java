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
package t3.tic.bw6.osgi.nonosgi.registry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;

import t3.tic.bw6.osgi.nonosgi.registry.DebugHelper;
import t3.tic.bw6.osgi.nonosgi.registry.RegistryProviderNonOSGI;

/**
 * Helper class which returns {@link IExtensionRegistry} singleton switch the
 * environment (OSGi-env or none OSGi-env) :
 *
 * <ul>
 * <li>into OSGi-env, returns the "standard" OSGi {@link IExtensionRegistry}
 * singleton which use Bundle Activator to load "plugin.xml".</li>
 * <li>into NONE OSGi-env, returns the {@link IExtensionRegistry} singleton
 * which load the whole "plugin.xml" founded from the shared ClassLoader.</li>
 * </ul>
 *
 */
public class RegistryFactoryHelper {

	private static Boolean OSGI_ENV = null;

	protected static ClassLoader classLoader = null;

	public static IExtensionRegistry getRegistry(ClassLoader classLoader) {
		RegistryFactoryHelper.classLoader = classLoader;
		return getRegistry();
	}

	public static IExtensionRegistry getRegistry() {
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		if (registry != null) {
			// OSGi-env, returns the "standard" OSGi {@link
			// IExtensionRegistry} singleton (OR the cached IExtensionRegistry
			// already loaded).
			if (OSGI_ENV == null) {
				OSGI_ENV = true;
			}
		} else {
			try {
				OSGI_ENV = false;
				// NONE OSGi-env, load the whole "plugin.xml" founded from the
				// shared ClassLoader.
				registry = createNoOSGIRegistry();
			} catch (CoreException e) {
				// This error should never occurred.
				e.printStackTrace();
			}
		}
		if (DebugHelper.DEBUG) {
			// Debug mode, trace
			if (OSGI_ENV) {
				DebugHelper.log("Returns IExtensionRegistry from the OSGi-env. Thread=" + Thread.currentThread());
			} else {
				DebugHelper.log("Returns IExtensionRegistry from the NO OSGi-env. Thread=" + Thread.currentThread());
			}
		}
		return registry;
	}

	/**
	 * Create No OSG-env {@link IExtensionRegistry}. This method is synchronized
	 * to avoid loading twice the plugin.xml files with multi Thread context.
	 *
	 * @return
	 * @throws CoreException
	 */
	private static synchronized IExtensionRegistry createNoOSGIRegistry() throws CoreException {
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		if (registry != null) {
			// Registry was already created with another Thread.
			return registry;
		}

		RegistryProviderNonOSGI registryProviderNonOSGI = new RegistryProviderNonOSGI();
		if (classLoader != null) {
			registryProviderNonOSGI.setClassLoader(classLoader);
		}
		RegistryFactory.setDefaultRegistryProvider(registryProviderNonOSGI);
		return RegistryFactory.getRegistry();
	}
}
