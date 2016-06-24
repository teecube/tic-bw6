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
package t3.tic.bw6.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import t3.tic.bw6.BW6Constants;
import t3.tic.bw6.BW6PackagingConvertor;

public class ManifestManager {

	public static class ManifestVersionUpdate {
		public boolean updated;
		public String oldVersion;
		public String newVersion;
	}

	public static String getManifestSymbolicName(Manifest manifest) throws IOException {
		if (manifest == null) return null;

		Attributes attr = manifest.getMainAttributes();
		return attr.getValue("Bundle-SymbolicName");
	}

	public static String getManifestVersion(Manifest manifest) throws IOException {
		if (manifest == null) return null;

		Attributes attr = manifest.getMainAttributes();
		return attr.getValue("Bundle-Version");
	}

	public static String getManifestRequiredCapability(Manifest manifest) throws IOException {
		if (manifest == null) return null;

		Attributes attr = manifest.getMainAttributes();
		return attr.getValue("Require-Capability");
	}

	public static String getManifestVersion(File manifestFile) throws IOException {
		Manifest mf = getManifest(manifestFile);
		return getManifestVersion(mf);
	}

	public static Manifest getManifest(File manifestFile) throws IOException {
		FileInputStream is = new FileInputStream(manifestFile);
		Manifest mf = new Manifest(is);
		is.close();

		return mf;
	}

	/**
	 * <p>
	 * Change the Bundle-Version field in a MANIFEST.MF file.
	 * </p>
	 * @param oldVersion 
	 *
	 * @param manifestFile, the MANIFEST.MF file
	 * @param version, the new version to set
	 * @throws IOException
	 * @return true if the version has been changed, false otherwise
	 */
	public static ManifestVersionUpdate updatedManifestVersion(File manifestFile, String version) throws IOException {
		ManifestVersionUpdate result = new ManifestVersionUpdate();
		result.newVersion = version;
		result.updated = false;

		if (manifestFile == null || version == null) return result;

		if (version.endsWith("-SNAPSHOT")) {
			version = ManifestManager.convertVersionFromMavenToOSGi(version);
		}
		Manifest mf = getManifest(manifestFile);

		// update the Bundle-Version
		Attributes attr = mf.getMainAttributes();
		String oldVersion = getManifestVersion(mf);
		attr.putValue("Bundle-Version", version);

		//Write the updated file and return the same.
		FileOutputStream os = new FileOutputStream(manifestFile);
		mf.write(os);
		os.close();

		result.newVersion = version;
		result.oldVersion = oldVersion;
		result.updated = !version.equals(oldVersion);
		return result;
	}

	/**
	 * <p>
	 * Change the Require-Capability field in a MANIFEST.MF file.
	 * </p>
	 * @param oldVersion 
	 *
	 * @param manifestFile, the MANIFEST.MF file
	 * @param version, the new version to set
	 * @throws IOException
	 * @return true if the version has been changed, false otherwise
	 */
	public static void removeRequiredCapability(File manifestFile, String capability) throws IOException {
		Manifest mf = getManifest(manifestFile);

		// update the Require-Capability
		Attributes attr = mf.getMainAttributes();
		String requiredCapability = getManifestRequiredCapability(mf);

		Pattern p1 = Pattern.compile(BW6Constants.capabilityPattern);
		List<String> capabilities = BW6PackagingConvertor.getCapabilites(requiredCapability);
		for (Iterator<String> it = capabilities.iterator(); it.hasNext();) {
			String c = (String) it.next();

			Matcher m1 = p1.matcher(c);
			if (m1.matches()) {
				String capabilityType = m1.group(1);
				String capabilityFilter = m1.group(2);
				Pattern p2 = Pattern.compile(BW6Constants.capabilityFilterPattern);
				Matcher m2 = p2.matcher(capabilityFilter);
				if (capabilityType.equals("com.tibco.bw.module") && m2.matches()) {
					String capabilityName = m2.group(1);
					String capabilityVersion = m2.group(2);

					if (capabilityName.equals(capability)) {
						it.remove();
					}
				}
			}
		}

		requiredCapability = StringUtils.join(capabilities, ",");
		attr.putValue("Require-Capability", requiredCapability);

		//Write the updated file and return the same.
		FileOutputStream os = new FileOutputStream(manifestFile);
		mf.write(os);
		os.close();
	}

	/**
	 * <p>
	 * Convert from Maven version format (ending with "-SNAPSHOT") to OSGi
	 * version format (ending with ".qualifier").
	 * </p>
	 *
	 * @param version
	 * @return
	 */
	public static String convertVersionFromMavenToOSGi(String version) {
		if (version == null) return null;

		Pattern p = Pattern.compile("(.*)-SNAPSHOT", Pattern.CASE_INSENSITIVE);
		
		return p.matcher(version).replaceAll("$1.qualifier");
	}

}
