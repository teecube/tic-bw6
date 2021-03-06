/**
 * (C) Copyright 2016-2018 teecube
 * (https://teecu.be) and others.
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
package t3.tic.bw6.osgi;

import de.pdark.decentxml.*;
import org.codehaus.plexus.util.IOUtil;
import org.eclipse.tycho.core.resolver.shared.IncludeSourceMode;
import org.eclipse.tycho.p2.target.facade.TargetDefinition;
import org.eclipse.tycho.p2.target.facade.TargetDefinitionSyntaxException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TargetDefinitionFile implements TargetDefinition {

    private static XMLParser parser = new XMLParser();

    private final File origin;
    private final byte[] fileContentHash;

    private final Element dom;
    private final Document document;

    final IncludeSourceMode includeSourceMode;

    public class IULocation implements TargetDefinition.InstallableUnitLocation {
        private final Element dom;

        public IULocation(Element dom) {
            this.dom = dom;
        }

        @Override
        public List<? extends TargetDefinition.Unit> getUnits() {
            ArrayList<Unit> units = new ArrayList<Unit>();
            for (Element unitDom : dom.getChildren("unit")) {
                units.add(new Unit(unitDom));
            }
            return Collections.unmodifiableList(units);
        }

        @Override
        public List<? extends TargetDefinition.Repository> getRepositories() {
            return getRepositoryImpls();
        }

        public List<Repository> getRepositoryImpls() {
            final List<Element> repositoryNodes = dom.getChildren("repository");

            final List<Repository> repositories = new ArrayList<TargetDefinitionFile.Repository>(repositoryNodes.size());
            for (Element node : repositoryNodes) {
                repositories.add(new Repository(node));
            }
            return repositories;
        }

        @Override
        public String getTypeDescription() {
            return dom.getAttributeValue("type");
        }

        @Override
        public IncludeMode getIncludeMode() {
            String attributeValue = dom.getAttributeValue("includeMode");
            if ("slicer".equals(attributeValue)) {
                return IncludeMode.SLICER;
            } else if ("planner".equals(attributeValue) || attributeValue == null) {
                return IncludeMode.PLANNER;
            }
            throw new TargetDefinitionSyntaxException("Invalid value for attribute 'includeMode': " + attributeValue
                    + "");
        }

        @Override
        public boolean includeAllEnvironments() {
            return Boolean.parseBoolean(dom.getAttributeValue("includeAllPlatforms"));
        }

        @Override
        public boolean includeSource() {
            switch (includeSourceMode) {
            case ignore:
                return false;
            case force:
                return true;
            default:
                return Boolean.parseBoolean(dom.getAttributeValue("includeSource"));
            }
        }
    }

    public static class OtherLocation implements Location {
        private final String description;
        private final String path;

        public OtherLocation(String description, String path) {
            this.description = description;
            this.path = path;
        }

        @Override
        public String getTypeDescription() {
            return description;
        }

        public String getPath() {
            return path;
        }
    }

    public static final class Repository implements TargetDefinition.Repository {
        private final Element dom;

        public Repository(Element dom) {
            this.dom = dom;
        }

        @Override
        public String getId() {
            // this is Maven specific, used to match credentials and mirrors
            return dom.getAttributeValue("id");
        }

        @Override
        public URI getLocation() {
            try {
                return new URI(dom.getAttributeValue("location"));
            } catch (URISyntaxException e) {
                // this should be checked earlier (but is currently ugly to do)
                throw new RuntimeException(e);
            }
        }

        /**
         * @deprecated Not for productive use. Breaks the
         *             {@link TargetDefinitionFile#equals(Object)} and
         *             {@link TargetDefinitionFile#hashCode()} implementations.
         */
        @Deprecated
        public void setLocation(String location) {
            dom.setAttribute("location", location);
        }
    }

    public static class Unit implements TargetDefinition.Unit {
        private final Element dom;

        public Unit(Element dom) {
            this.dom = dom;
        }

        @Override
        public String getId() {
            return dom.getAttributeValue("id");
        }

        @Override
        public String getVersion() {
            return dom.getAttributeValue("version");
        }

        /**
         * @deprecated Not for productive use. Breaks the
         *             {@link TargetDefinitionFile#equals(Object)} and
         *             {@link TargetDefinitionFile#hashCode()} implementations.
         */
        @Deprecated
        public void setVersion(String version) {
            dom.setAttribute("version", version);
        }
    }

    private TargetDefinitionFile(File source, IncludeSourceMode includeSourceMode)
            throws TargetDefinitionSyntaxException {
        try {
            this.origin = source;
            this.fileContentHash = computeFileContentHash(source);

            this.includeSourceMode = includeSourceMode;

            FileInputStream input = new FileInputStream(source);
            try {
                this.document = parser.parse(new XMLIOSource(source));
                this.dom = document.getRootElement();
            } finally {
                input.close();
            }
        } catch (XMLParseException e) {
            throw new TargetDefinitionSyntaxException("Target definition is not well-formed XML: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new TargetDefinitionSyntaxException("I/O error while reading target definition file: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public List<? extends TargetDefinition.Location> getLocations() {
        ArrayList<TargetDefinition.Location> locations = new ArrayList<TargetDefinition.Location>();
        Element locationsDom = dom.getChild("locations");
        if (locationsDom != null) {
            for (Element locationDom : locationsDom.getChildren("location")) {
                String type = locationDom.getAttributeValue("type");
                if ("InstallableUnit".equals(type))
                    locations.add(new IULocation(locationDom));
                else
                    locations.add(new OtherLocation(type, locationDom.getAttribute("path").getValue()));
            }
        }
        return Collections.unmodifiableList(locations);
    }

    @Override
    public boolean hasIncludedBundles() {
        return dom.getChild("includeBundles") != null;
    }

    @Override
    public String getOrigin() {
        return origin.getAbsolutePath();
    }

    public static TargetDefinitionFile read(File file, IncludeSourceMode includeSourceMode) {
        try {
            return new TargetDefinitionFile(file, includeSourceMode);
        } catch (TargetDefinitionSyntaxException e) {
            throw new RuntimeException("Invalid syntax in target definition " + file + ": " + e.getMessage(), e);
        }
    }

    public static void write(TargetDefinitionFile target, File file) throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));

        Document document = target.document;
        try {
            String enc = document.getEncoding() != null ? document.getEncoding() : "UTF-8";
            Writer w = new OutputStreamWriter(os, enc);
            XMLWriter xw = new XMLWriter(w);
            try {
                document.toXML(xw);
            } finally {
                xw.flush();
            }
        } finally {
            IOUtil.close(os);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(fileContentHash);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TargetDefinitionFile))
            return false;

        TargetDefinitionFile other = (TargetDefinitionFile) obj;
        return Arrays.equals(fileContentHash, other.fileContentHash);
    }

    private static byte[] computeFileContentHash(File source) {
        byte[] digest;
        try {
            FileInputStream in = new FileInputStream(source);
            try {
                digest = computeMD5Digest(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O error while reading \"" + source + "\": " + e.getMessage(), e);
        }
        return digest;
    }

    private static byte[] computeMD5Digest(FileInputStream in) throws IOException {
        MessageDigest digest = newMD5Digest();

        byte[] buffer = new byte[4 * 1024];
        while (in.read(buffer) > 0) {
            digest.update(buffer);
        }
        return digest.digest();
    }

    private static MessageDigest newMD5Digest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
