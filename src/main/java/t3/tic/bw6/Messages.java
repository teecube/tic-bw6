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

/**
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
public class Messages extends t3.Messages {

    public static final String MESSAGE_PREFIX = "~-> TIC BW6: ";

    public static final String LOADED = MESSAGE_PREFIX + "Loaded.";

    public static final String RESOLVING_BW6_DEPENDENCIES = MESSAGE_PREFIX + "Resolving BW6 dependencies...";
    public static final String RESOLVED_BW6_DEPENDENCIES = "BW6 dependencies resolved.";

    public static final String DEPENDENCY_RESOLUTION_FAILED = "Failed to resolve a dependency.";

    public static final String APPLICATION_PACKAGING = MESSAGE_PREFIX + "Packaging BW6 application (EAR)...";
    public static final String APPLICATION_ADDING_MODULE = "Adding BW6 module: ";
    public static final String APPLICATION_ADDING_DIAGRAMS = "Adding diagrams from: ";

    public static final String BW6_MAVEN_INSTALL = "Installing BW6 Maven feature... (This might take some minutes)";
    public static final String BW6_MAVEN_INSTALL_FAILED = "Failed to install BW6 Maven feature.";

    public static class Studio {
        public static final String PROXY_INSTALL = "Installing TIBCO Business Studio proxy feature...";
        public static final String PROXY_INSTALL_FAILED = "Failed to install TIBCO Business Studio proxy feature.";

        public static final String PROXY_UNINSTALL = "Uninstalling TIBCO Business Studio proxy feature...";
        public static final String PROXY_UNINSTALL_FAILED = "Failed to uninstall TIBCO Business Studio proxy feature.";
    }
}
