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

package org.gephi.visualization.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.gephi.visualization.VizController;
import org.gephi.visualization.apiimpl.VizConfig;
import org.gephi.visualization.opengl.GraphicalConfiguration;
import org.openide.util.NbPreferences;

final class OpenGLPanel extends javax.swing.JPanel {

    private final OpenGLOptionsPanelController controller;
    //Settings
    private int antiAliasing = 0;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox antialisaingCombobox;
    private javax.swing.JCheckBox fpsCheckbox;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator1;
    private javax.swing.JLabel labelAntialiasing;
    private javax.swing.JLabel labelShow;
    private javax.swing.JTextArea openInfoText;
    private javax.swing.JPanel openglInfoPanel;
    private javax.swing.JButton resetButton;

    OpenGLPanel(OpenGLOptionsPanelController controller) {
        this.controller = controller;
        initComponents();

        antialisaingCombobox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (antialisaingCombobox.getSelectedIndex() > 0) {
                    antiAliasing = (int) Math.pow(2, antialisaingCombobox.getSelectedIndex());
                } else {
                    antiAliasing = 0;
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jXTitledSeparator1 = new org.jdesktop.swingx.JXTitledSeparator();
        labelAntialiasing = new javax.swing.JLabel();
        antialisaingCombobox = new javax.swing.JComboBox();
        labelShow = new javax.swing.JLabel();
        fpsCheckbox = new javax.swing.JCheckBox();
        resetButton = new javax.swing.JButton();
        openglInfoPanel = new javax.swing.JPanel();
        openInfoText = new javax.swing.JTextArea();

        jXTitledSeparator1.setTitle(
            org.openide.util.NbBundle.getMessage(OpenGLPanel.class, "OpenGLPanel.jXTitledSeparator1.title")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelAntialiasing,
            org.openide.util.NbBundle.getMessage(OpenGLPanel.class, "OpenGLPanel.labelAntialiasing.text")); // NOI18N

        antialisaingCombobox
            .setModel(new javax.swing.DefaultComboBoxModel(new String[] {"0x", "2x", "4x", "8x", "16x"}));

        org.openide.awt.Mnemonics.setLocalizedText(labelShow,
            org.openide.util.NbBundle.getMessage(OpenGLPanel.class, "OpenGLPanel.labelShow.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(fpsCheckbox,
            org.openide.util.NbBundle.getMessage(OpenGLPanel.class, "OpenGLPanel.fpsCheckbox.text")); // NOI18N
        fpsCheckbox.setMargin(new java.awt.Insets(2, 0, 2, 2));

        org.openide.awt.Mnemonics.setLocalizedText(resetButton,
            org.openide.util.NbBundle.getMessage(OpenGLPanel.class, "OpenGLPanel.resetButton.text")); // NOI18N
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        openglInfoPanel.setLayout(new java.awt.GridBagLayout());

        openInfoText.setColumns(5);
        openInfoText.setEditable(false);
        openInfoText.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        openInfoText.setRows(3);
        openInfoText.setText("Vendor\nModel\nVersion"); // NOI18N
        openInfoText.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        openglInfoPanel.add(openInfoText, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jXTitledSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
                        .addComponent(resetButton)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(labelShow, javax.swing.GroupLayout.PREFERRED_SIZE, 52,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labelAntialiasing))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(fpsCheckbox))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addComponent(antialisaingCombobox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 192, Short.MAX_VALUE)
                            .addComponent(openglInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 196,
                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jXTitledSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(openglInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 86,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labelAntialiasing)
                                .addComponent(antialisaingCombobox))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labelShow, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fpsCheckbox))))
                    .addGap(18, 18, Short.MAX_VALUE)
                    .addComponent(resetButton)
                    .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void resetButtonActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed

        NbPreferences.forModule(VizConfig.class).remove(VizConfig.ANTIALIASING);
        NbPreferences.forModule(VizConfig.class).remove(VizConfig.SHOW_FPS);

        load();
    }//GEN-LAST:event_resetButtonActionPerformed

    void load() {
        antiAliasing =
            NbPreferences.forModule(VizConfig.class).getInt(VizConfig.ANTIALIASING, VizConfig.DEFAULT_ANTIALIASING);
        antialisaingCombobox
            .setSelectedIndex(antiAliasing == 0 ? 0 : Math.round((float) (Math.log(antiAliasing) / Math.log(2))));
        fpsCheckbox.setSelected(
            NbPreferences.forModule(VizConfig.class).getBoolean(VizConfig.SHOW_FPS, VizConfig.DEFAULT_SHOW_FPS));

        //OpenGLInfo
        GraphicalConfiguration gc = VizController.getInstance().getDrawable().getGraphicalConfiguration();
        if (gc != null) {
            openInfoText.setText(gc.getVendor() + "\n" + gc.getRenderer() + "\nOpenGL2 " + gc.getVersionStr());
        }
    }

    void store() {
        NbPreferences.forModule(VizConfig.class).putInt(VizConfig.ANTIALIASING, antiAliasing);
        NbPreferences.forModule(VizConfig.class).putBoolean(VizConfig.SHOW_FPS, fpsCheckbox.isSelected());

        VizController.getInstance().getEngine().reinit();
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }
    // End of variables declaration//GEN-END:variables
}
