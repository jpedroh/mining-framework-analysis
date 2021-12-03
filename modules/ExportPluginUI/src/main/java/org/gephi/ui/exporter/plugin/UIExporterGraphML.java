/*
 Copyright 2008-2010 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2011 Gephi Consortium. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2011 Gephi Consortium.
 */

package org.gephi.ui.exporter.plugin;

import javax.swing.JPanel;
import org.gephi.io.exporter.plugin.ExporterGraphML;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.exporter.spi.ExporterUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Mathieu Bastian
 */
@ServiceProvider(service = ExporterUI.class)
public class UIExporterGraphML implements ExporterUI {

    private UIExporterGraphMLPanel panel;
    private ExporterGraphML exporterGraphML;
    private final ExporterGraphMLSettings settings = new ExporterGraphMLSettings();

    @Override
    public void setup(Exporter exporter) {
        exporterGraphML = (ExporterGraphML) exporter;
        settings.load(exporterGraphML);
        panel.setup(exporterGraphML);
    }

    @Override
    public void unsetup(boolean update) {
        if (update) {
            panel.unsetup(exporterGraphML);
            settings.save(exporterGraphML);
        }
        panel = null;
        exporterGraphML = null;
    }

    @Override
    public JPanel getPanel() {
        panel = new UIExporterGraphMLPanel();
        return panel;
    }

    @Override
    public boolean isUIForExporter(Exporter exporter) {
        return exporter instanceof ExporterGraphML;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(UIExporterGraphML.class, "UIExporterGraphML.name");
    }

    private static class ExporterGraphMLSettings {

        private boolean normalize = false;
        private boolean exportColors = true;
        private boolean exportPosition = true;
        private boolean exportSize = true;
        private boolean exportAttributes = true;
        private final boolean exportHierarchy = false;

        private void save(ExporterGraphML exporterGraphML) {
            this.normalize = exporterGraphML.isNormalize();
            this.exportColors = exporterGraphML.isExportColors();
            this.exportPosition = exporterGraphML.isExportPosition();
            this.exportSize = exporterGraphML.isExportSize();
            this.exportAttributes = exporterGraphML.isExportAttributes();
        }

        private void load(ExporterGraphML exporterGraphML) {
            exporterGraphML.setNormalize(normalize);
            exporterGraphML.setExportColors(exportColors);
            exporterGraphML.setExportAttributes(exportAttributes);
            exporterGraphML.setExportPosition(exportPosition);
            exporterGraphML.setExportSize(exportSize);
        }
    }
}
