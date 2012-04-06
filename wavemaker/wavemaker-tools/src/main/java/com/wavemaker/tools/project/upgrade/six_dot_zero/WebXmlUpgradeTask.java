/*
 *  Copyright (C) 2008-2012 VMware, Inc. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.wavemaker.tools.project.upgrade.six_dot_zero;

import com.wavemaker.tools.io.File;
import com.wavemaker.tools.io.exception.ResourceException;
import com.wavemaker.tools.project.Project;
import com.wavemaker.tools.project.upgrade.UpgradeInfo;
import com.wavemaker.tools.project.upgrade.UpgradeTask;

/**
 * Changes the package of WMPropertyPlaceholderConfigurer.
 * 
 * @author Seung Lee
 * @author Jeremy Grelle
 */
public class WebXmlUpgradeTask implements UpgradeTask {

    private final String wmListenerStr = "<listener-class>com.wavemaker.runtime.server.CleanupListener</listener-class>";

    private final String springListenerStr = "<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>";

    private final String dummyStr = "xxx123456789yyy";

    private boolean error = false;

    @Override
    public void doUpgrade(Project project, UpgradeInfo upgradeInfo) {
        File webxml = project.getWebXmlFile();

        try {
            String content = webxml.getContent().asString();
            int indxwm = content.indexOf(this.wmListenerStr);
            int indxspring = content.indexOf(this.springListenerStr);
            if (indxwm < indxspring) {
                return;
            }
            content = content.replace(this.wmListenerStr, this.dummyStr);
            content = content.replace(this.springListenerStr, this.wmListenerStr);
            content = content.replace(this.dummyStr, this.springListenerStr);
            webxml.getContent().write(content);
        } catch (ResourceException e) {
            e.printStackTrace();
            this.error = true;
        }

        if (this.error) {
            upgradeInfo.addMessage("*** Terminated with error while upgrading web.xml. " + "Please check the console message.***");
        } else {
            upgradeInfo.addMessage("Upgrading web.xml completed successfully.");
        }
    }
}
