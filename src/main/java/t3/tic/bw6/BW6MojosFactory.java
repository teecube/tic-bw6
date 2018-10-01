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
package t3.tic.bw6;

import org.apache.maven.plugin.AbstractMojo;
import t3.MojosFactory;
import t3.tic.bw6.install.BW6P2MavenInstallMojo;
import t3.tic.bw6.install.proxy.StudioProyxInstallMojo;
import t3.tic.bw6.install.proxy.StudioProyxUninstallMojo;
import t3.tic.bw6.project.application.PackageBW6Mojo;
import t3.tic.bw6.project.application.PrepareApplicationMetaMojo;
import t3.tic.bw6.project.application.modules.AddModuleMojo;
import t3.tic.bw6.project.application.modules.RemoveModuleMojo;
import t3.tic.bw6.project.module.CleanBuildProperties;
import t3.tic.bw6.project.module.PrepareModuleMetaMojo;
import t3.tic.bw6.studio.StudioLauncherMojo;
import t3.tic.bw6.studio.WorkspaceMojo;

public class BW6MojosFactory extends MojosFactory {
    @SuppressWarnings("unchecked")
    public <T extends AbstractMojo> T getMojo(Class<T> type) {
        if (type == null) {
            return null;
        }

        String typeName = type.getSimpleName();

        switch (typeName) {
        case "AddModuleMojo":
            return (T) new AddModuleMojo();
        case "RemoveModuleMojo":
            return (T) new RemoveModuleMojo();
        case "BW6P2MavenInstallMojo":
            return (T) new BW6P2MavenInstallMojo();
        case "CleanBuildProperties":
            return (T) new CleanBuildProperties();
        case "PackageBW6Mojo":
            return (T) new PackageBW6Mojo();
        case "PrepareApplicationMetaMojo":
            return (T) new PrepareApplicationMetaMojo();
        case "PrepareModuleMetaMojo":
            return (T) new PrepareModuleMetaMojo();
        case "StudioLauncherMojo":
            return (T) new StudioLauncherMojo();
        case "StudioProyxInstallMojo":
            return (T) new StudioProyxInstallMojo();
        case "StudioProyxUninstallMojo":
            return (T) new StudioProyxUninstallMojo();
        case "WorkspaceMojo":
            return (T) new WorkspaceMojo();
        default:
            return super.getMojo(type);
        }

    }

}
