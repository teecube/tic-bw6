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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.xml.sax.SAXException;
import t3.plugin.annotations.Parameter;
import t3.tic.bw6.BW6MojoInformation;
import t3.tic.bw6.BW6PackagingConvertor;
import t3.tic.bw6.project.application.BW6ApplicationCommonMojo;
import t3.tic.bw6.util.ManifestManager;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.jar.Manifest;

/**
 * <p>
 * Generic class to add/remove a module in/from an application.
 * </p>
 *
 * @author Mathieu Debove &lt;mad@teecu.be&gt;
 *
 */
public abstract class CommonModule extends BW6ApplicationCommonMojo {

    /**
     * <p>
     * The relative path of a <i>module</i> (<i>app module</i> or
     * <i>shared module</i>) to add (or remove) in (from) a TIBCO BusinessWorks
     * 6 <i>application</i>.
     * </p>
     * <p>
     * The TIBCO BusinessWorks 6 <i>application</i> and <i>module</i> both have a
     * <i>POM</i>.
     * The first one points to the second one in its &lt;modules> section with a
     * &lt;module> entry which value is the path of the <i>module</i> relatively
     * to the <i>application</i>.
     * </p>
     */
    @Parameter (property = BW6MojoInformation.BW6Application.moduleRelativePath, required = true, defaultValue = "")
    protected String moduleRelativePath;

    protected abstract boolean updateModule(String moduleSymbolicName, String moduleVersion, ModuleComponentsMarshaller moduleComposite, PackageUnitMarshaller packageUnit) throws MojoExecutionException;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        switch (project.getPackaging()) {
        case "eclipse-plugin":
        case BW6_APP_MODULE_PACKAGING:
            return; // skip
        case "pom":
        case BW6_APPLICATION_PACKAGING:
            break; // continue

        default:
            throw new MojoExecutionException("This project is not a BusinessWorks 6 application or is not supported.");
        }

        File moduleBasedir = new File(project.getBasedir(), moduleRelativePath);
        if (moduleBasedir == null || !moduleBasedir.exists()) {
            throw new MojoExecutionException("Unable to find module directory.");
        }

        File moduleManifestFile = BW6PackagingConvertor.getManifest(moduleBasedir);
        if (moduleManifestFile == null || !moduleManifestFile.exists()) {
            throw new MojoExecutionException("Unable to find module manifest.");
        }

        Manifest moduleManifest;
        String moduleSymbolicName = null;
        String moduleVersion = null;
        try {
            moduleManifest = ManifestManager.getManifest(moduleManifestFile);
            moduleSymbolicName = ManifestManager.getManifestSymbolicName(moduleManifest);
            moduleVersion = ManifestManager.getManifestVersion(moduleManifest);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }

        if (moduleSymbolicName == null || moduleVersion == null) {
            throw new MojoExecutionException("Unable to identify module to add.");
        }

        File packageUnitFile = getPackageUnitFile();
        File moduleCompositeFile = new File(moduleBasedir, "META-INF" + File.separator + "module.bwm");

        PackageUnitMarshaller packageUnit;
        ModuleComponentsMarshaller moduleComposite;
        try {
            moduleComposite = new ModuleComponentsMarshaller(moduleCompositeFile);
            packageUnit = new PackageUnitMarshaller(packageUnitFile);

            if (updateModule(moduleSymbolicName, moduleVersion, moduleComposite, packageUnit)) {
                packageUnit.saveWithoutFilter();
            }
        } catch (JAXBException | SAXException | UnsupportedEncodingException | FileNotFoundException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
        
    }

}
