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

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.spi.IRegistryProvider;
import org.eclipse.core.runtime.spi.RegistryStrategy;

import java.io.File;

/**
 * Provider for {@link IExtensionRegistry} into No OSGi-env.
 *
 * <p>
 * This class emulate the
 * org.eclipse.core.internal.registry.osgi.RegistryProviderOSGI which use OSGi
 * services to retrieve an instance of {@link IExtensionRegistry}.
 * </p>
 *
 */
public class RegistryProviderNonOSGI implements IRegistryProvider {

    private ClassLoader classLoader;

    private Object masterRegistryKey = new Object();
    private Object userRegistryKey = new Object();

    // Cache the regisrty
    private IExtensionRegistry registry = null;

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.runtime.spi.IRegistryProvider#getRegistry()
     */
    public IExtensionRegistry getRegistry() {
        if (registry != null)
            return registry;

        synchronized (this) {
            // FIXME : study if theses parameters must be filled???
            File[] storageDirs = null;
            boolean[] cacheReadOnly = null;

            // Create an instance of IExtensionRegistry.
            // Into OSGi-env (see RegistryProviderOSGI), it use OSGi
            // ServiceTracker
            // to retrieve the instance of IExtensionRegistry (see
            // org.eclipse.core.internal.registry.osgi.Activator#startRegistry()).

            // To create an instance of IExtensionRegistry :
            // 1. Create an instance of RegistryStrategy
            // 2. Create an instance of IExtensionRegistry by using the instance
            // of
            // RegistryStrategy

            // 1. Create an instance of RegistryStrategy for no OSGi-env.
            RegistryStrategy strategy = new RegistryStrategyNonOSGI(storageDirs, cacheReadOnly, masterRegistryKey, classLoader);

            // 2. Create an instance of IExtensionRegistry by using the instance
            // of
            // RegistryStrategy
            registry = RegistryFactory.createRegistry(strategy,
                    masterRegistryKey, userRegistryKey);
        }
        return registry;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}
