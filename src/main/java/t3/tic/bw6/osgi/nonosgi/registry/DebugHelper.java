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

/**
 * Helper to debug None OSGi-env registry.
 */
public final class DebugHelper {

    private static final String PLUGIN_NAME = "org.eclipse.equinox.nonosgi.registry"; //$NON-NLS-1$
//    private static final String OPTION_DEBUG = PLUGIN_NAME + "/debug"; //$NON-NLS-1$

    public static boolean DEBUG = false;

//    static {
//        try {
//            // use qualified name for Activator to ensure that it won't
//            // be loaded outside of this block
//            DEBUG = Activator.getBooleanDebugOption(OPTION_DEBUG, false);
//        } catch (NoClassDefFoundError noClass) {
//            // no OSGi - OK
//        }
//    }

    /**
     * Log message.
     *
     * @param message
     */
    public static void log(String message) {
        log(message, 0);
    }

    /**
     * Log message with indent.
     *
     * @param message
     * @param indent
     */
    public static void log(String message, int indent) {
        System.out.println(createMessage(message, indent));
    }

    /**
     * Log error.
     *
     * @param error
     */
    public static void logError(String message) {
        logError(message, 0);
    }

    /**
     * Log error with indent.
     *
     * @param message
     * @param indent
     */
    public static void logError(String message, int indent) {
        System.err.println(createMessage(message, indent));
    }

    /**
     * Log error exception.
     *
     * @param e
     */
    public static void logError(Throwable e) {
        e.printStackTrace(System.err);
        System.err.println();
    }

    /**
     * Create message.
     *
     * @param message
     * @param indent
     * @return
     */
    private static String createMessage(String message, int indent) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            s.append("\t");
        }
        s.append("[");
        s.append(PLUGIN_NAME);
        s.append("] ");
        if (message != null) {
            s.append(message);
        }
        return s.toString();
    }
}
