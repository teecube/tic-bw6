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

import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.spi.RegistryContributor;

/**
 * The contributor factory creates new registry contributors for use in NONE
 * OSGi-based registries.
 *
 * <p>
 * This class emulate the org.eclipse.core.runtime.ContributorFactoryOSGi which
 * use Bundle information to create {@link IContributor}.
 * </p>
 *
 */
public class ContributorFactoryNonOSGI {

	// Emulate Bundle#getBundleId();
	private static long bundleId;

	/**
	 * Creates registry contributor object based on a "Bundle-SymbolicName". The
	 * symbolicName must not be <code>null</code>.
	 *
	 * @param symbolicName
	 *            "Bundle-SymbolicName" metadata from Bundle MANIFEST.MF
	 *            associated with the contribution
	 * @return new registry contributor based on the "Bundle-SymbolicName"
	 *         metadata.
	 */
	public static RegistryContributor createContributor(String symbolicName) {
		String id = Long.toString(bundleId++);
		String name = symbolicName;
		String hostId = null;
		String hostName = null;

		return new RegistryContributor(id, name, hostId, hostName);
	}

}
