/*
 * Copyright 2010-2021 Australian Signals Directorate
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
package au.gov.asd.tac.constellation.preferences;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * UI panel to define the session parameters
 *
 * @author algol
 */
final class ApplicationOptionsPanel extends javax.swing.JPanel {

    private final ApplicationOptionsPanelController controller;

    private static final String USER_HOME_PROPERTY = "user.home";

    public ApplicationOptionsPanel(final ApplicationOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
    }

    public String getUserDirectory() {
        return userDirectoryText.getText();
    }

    public void setUserDirectory(final String userDirectory) {
        userDirectoryText.setText(userDirectory);
    }

    public boolean getAustosaveEnabled() {
        return autosaveCheckBox.isSelected();
    }

    public void setAutosaveEnabled(final boolean autosaveEnabled) {
        autosaveCheckBox.setSelected(autosaveEnabled);
        autosaveSpinner.setEnabled(autosaveEnabled);
    }

    public int getAustosaveFrequency() {
        return (Integer) autosaveSpinner.getModel().getValue();
    }

    public void setAutosaveFrequency(final int autosaveFrequency) {
        autosaveSpinner.getModel().setValue(autosaveFrequency);
    }

    public boolean getWelcomeOnStartup() {
        return startupWelcomeCheckbox.isSelected();
    }

    public void setWelcomeOnStartup(final boolean welcomeOnStartup) {
        startupWelcomeCheckbox.setSelected(welcomeOnStartup);
    }

    public boolean getWhatsNewOnStartup() {
        return startupWhatsNewCheckbox.isSelected();
    }

    public void setWhatsNewOnStartup(final boolean whatsNewOnStartup) {
        startupWhatsNewCheckbox.setSelected(whatsNewOnStartup);
    }

    public int getWebserverPort() {
        return (Integer) webserverPortSpinner.getModel().getValue();
    }

    public void setWebserverPort(final int webserverPort) {
        webserverPortSpinner.getModel().setValue(webserverPort);
    }

    public String getNotebookDirectory() {
        return notebookDirectoryText.getText();
    }

    public void setNotebookDirectory(final String notebookDirectory) {
        notebookDirectoryText.setText(notebookDirectory);
    }

    public String getRestDirectory() {
        return restDirectoryText.getText();
    }

    public void setRestDirectory(final String restDirectory) {
        restDirectoryText.setText(restDirectory);
    }

    public boolean getDownloadPythonClient() {
        return downloadPythonClientCheckBox.isSelected();
    }

    public void setDownloadPythonClient(final boolean downloadPythonClient) {
        downloadPythonClientCheckBox.setSelected(downloadPythonClient);
    }

    public boolean getRememberSaveLocation() {
        return rememberSaveLocationCheckBox.isSelected();
    }

    public void setRememberSaveLocation(final boolean rememberSaveLocation) {
        this.rememberSaveLocationCheckBox.setSelected(rememberSaveLocation);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userDirectoryLabel = new javax.swing.JLabel();
        userDirectoryText = new javax.swing.JTextField();
        userDirectoryButton = new javax.swing.JButton();
        autosavePanel = new javax.swing.JPanel();
        autosaveCheckBox = new javax.swing.JCheckBox();
        autosaveSpinner = new javax.swing.JSpinner();
        autosaveLabel = new javax.swing.JLabel();
        startupPanel = new javax.swing.JPanel();
        startupWelcomeCheckbox = new javax.swing.JCheckBox();
        startupWhatsNewCheckbox = new javax.swing.JCheckBox();
        webserverPanel = new javax.swing.JPanel();
        webserverPortLabel = new javax.swing.JLabel();
        webserverPortSpinner = new javax.swing.JSpinner();
        restDirectoryLabel = new javax.swing.JLabel();
        restDirectoryText = new javax.swing.JTextField();
        restDirectoryButton = new javax.swing.JButton();
        notebookPanel = new javax.swing.JPanel();
        notebookDirectoryLabel = new javax.swing.JLabel();
        notebookDirectoryText = new javax.swing.JTextField();
        notebookDirectoryButton = new javax.swing.JButton();
        downloadPythonClientCheckBox = new javax.swing.JCheckBox();
        saveLocationPanel = new javax.swing.JPanel();
        rememberSaveLocationCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(userDirectoryLabel, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.userDirectoryLabel.text")); // NOI18N

        userDirectoryText.setText(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.userDirectoryText.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(userDirectoryButton, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.userDirectoryButton.text")); // NOI18N
        userDirectoryButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        userDirectoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userDirectoryButtonActionPerformed(evt);
            }
        });

        autosavePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.autosavePanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(autosaveCheckBox, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.autosaveCheckBox.text")); // NOI18N
        autosaveCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autosaveCheckBoxActionPerformed(evt);
            }
        });

        autosaveSpinner.setModel(new javax.swing.SpinnerNumberModel(10, 5, null, 1));

        org.openide.awt.Mnemonics.setLocalizedText(autosaveLabel, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.autosaveLabel.text")); // NOI18N

        javax.swing.GroupLayout autosavePanelLayout = new javax.swing.GroupLayout(autosavePanel);
        autosavePanel.setLayout(autosavePanelLayout);
        autosavePanelLayout.setHorizontalGroup(
            autosavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(autosavePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(autosaveCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(autosaveSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autosaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(182, Short.MAX_VALUE))
        );
        autosavePanelLayout.setVerticalGroup(
            autosavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(autosavePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(autosavePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autosaveCheckBox)
                    .addComponent(autosaveSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autosaveLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        startupPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.startupPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(startupWelcomeCheckbox, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.startupWelcomeCheckbox.text")); // NOI18N
        startupWelcomeCheckbox.setActionCommand(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.startupWelcomeCheckbox.actionCommand")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(startupWhatsNewCheckbox, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.startupWhatsNewCheckbox.text")); // NOI18N
        startupWhatsNewCheckbox.setActionCommand(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.startupWhatsNewCheckbox.actionCommand")); // NOI18N

        javax.swing.GroupLayout startupPanelLayout = new javax.swing.GroupLayout(startupPanel);
        startupPanel.setLayout(startupPanelLayout);
        startupPanelLayout.setHorizontalGroup(
            startupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startupPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(startupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startupWhatsNewCheckbox)
                    .addComponent(startupWelcomeCheckbox))
                .addContainerGap(349, Short.MAX_VALUE))
        );
        startupPanelLayout.setVerticalGroup(
            startupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startupPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(startupWelcomeCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startupWhatsNewCheckbox)
                .addGap(12, 12, 12))
        );

        webserverPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.webserverPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(webserverPortLabel, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.webserverPortLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(restDirectoryLabel, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.restDirectoryLabel.text")); // NOI18N

        restDirectoryText.setText(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.restDirectoryText.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(restDirectoryButton, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.restDirectoryButton.text")); // NOI18N
        restDirectoryButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        restDirectoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restDirectoryButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout webserverPanelLayout = new javax.swing.GroupLayout(webserverPanel);
        webserverPanel.setLayout(webserverPanelLayout);
        webserverPanelLayout.setHorizontalGroup(
            webserverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(webserverPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(webserverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(restDirectoryLabel)
                    .addComponent(webserverPortLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(webserverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(webserverPanelLayout.createSequentialGroup()
                        .addComponent(webserverPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(restDirectoryText, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(restDirectoryButton)
                .addGap(4, 4, 4))
        );
        webserverPanelLayout.setVerticalGroup(
            webserverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(webserverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(webserverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(webserverPortLabel)
                    .addComponent(webserverPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(webserverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(restDirectoryLabel)
                    .addComponent(restDirectoryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(restDirectoryButton))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        notebookPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.notebookPanel.border.title_1"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(notebookDirectoryLabel, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.notebookDirectoryLabel.text")); // NOI18N

        notebookDirectoryText.setText(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.notebookDirectoryText.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(notebookDirectoryButton, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.notebookDirectoryButton.text")); // NOI18N
        notebookDirectoryButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        notebookDirectoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notebookDirectoryButtonActionPerformed(evt);
            }
        });

        downloadPythonClientCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(downloadPythonClientCheckBox, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.downloadPythonClientCheckBox.text")); // NOI18N

        javax.swing.GroupLayout notebookPanelLayout = new javax.swing.GroupLayout(notebookPanel);
        notebookPanel.setLayout(notebookPanelLayout);
        notebookPanelLayout.setHorizontalGroup(
            notebookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notebookPanelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(notebookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(notebookPanelLayout.createSequentialGroup()
                        .addComponent(downloadPythonClientCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(notebookPanelLayout.createSequentialGroup()
                        .addComponent(notebookDirectoryLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(notebookDirectoryText, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(notebookDirectoryButton)))
                .addContainerGap())
        );
        notebookPanelLayout.setVerticalGroup(
            notebookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notebookPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(notebookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(notebookDirectoryLabel)
                    .addComponent(notebookDirectoryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(notebookDirectoryButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downloadPythonClientCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        saveLocationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.saveLocationPanel.border.title"))); // NOI18N

        rememberSaveLocationCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(rememberSaveLocationCheckBox, org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.rememberSaveLocationCheckBox.text")); // NOI18N
        rememberSaveLocationCheckBox.setActionCommand(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.rememberSaveLocationCheckBox.actionCommand")); // NOI18N

        javax.swing.GroupLayout saveLocationPanelLayout = new javax.swing.GroupLayout(saveLocationPanel);
        saveLocationPanel.setLayout(saveLocationPanelLayout);
        saveLocationPanelLayout.setHorizontalGroup(
            saveLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saveLocationPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(rememberSaveLocationCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        saveLocationPanelLayout.setVerticalGroup(
            saveLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saveLocationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rememberSaveLocationCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startupPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(autosavePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(userDirectoryLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userDirectoryText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userDirectoryButton))
                    .addComponent(webserverPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(notebookPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveLocationPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userDirectoryLabel)
                    .addComponent(userDirectoryButton)
                    .addComponent(userDirectoryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(autosavePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(startupPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(webserverPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notebookPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveLocationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        notebookPanel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.notebookPanel.AccessibleContext.accessibleName")); // NOI18N
        saveLocationPanel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ApplicationOptionsPanel.class, "ApplicationOptionsPanel.saveLocationPanel.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void userDirectoryButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_userDirectoryButtonActionPerformed
    {//GEN-HEADEREND:event_userDirectoryButtonActionPerformed
        final JFileChooser fc = new JFileChooser(System.getProperty(USER_HOME_PROPERTY));
        final String dir = userDirectoryText.getText().trim();
        if (!dir.isEmpty()) {
            fc.setSelectedFile(new File(dir));
        }
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showDialog(this, "Select CONSTELLATION directory") == JFileChooser.APPROVE_OPTION) {
            final String fnam = fc.getSelectedFile().getPath();
            userDirectoryText.setText(fnam);
        }
    }//GEN-LAST:event_userDirectoryButtonActionPerformed

    private void autosaveCheckBoxActionPerformed(ActionEvent evt)//GEN-FIRST:event_autosaveCheckBoxActionPerformed
    {//GEN-HEADEREND:event_autosaveCheckBoxActionPerformed
        autosaveSpinner.setEnabled(autosaveCheckBox.isSelected());
    }//GEN-LAST:event_autosaveCheckBoxActionPerformed

    private void notebookDirectoryButtonActionPerformed(ActionEvent evt)//GEN-FIRST:event_notebookDirectoryButtonActionPerformed
    {//GEN-HEADEREND:event_notebookDirectoryButtonActionPerformed
        final JFileChooser fc = new JFileChooser(System.getProperty(USER_HOME_PROPERTY));
        final String dir = notebookDirectoryText.getText().trim();
        if (!dir.isEmpty()) {
            fc.setSelectedFile(new File(dir));
        }
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showDialog(this, "Select Jupyter notebook directory") == JFileChooser.APPROVE_OPTION) {
            final String fnam = fc.getSelectedFile().getPath();
            notebookDirectoryText.setText(fnam);
        }
    }//GEN-LAST:event_notebookDirectoryButtonActionPerformed

    private void restDirectoryButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_restDirectoryButtonActionPerformed
        final JFileChooser fc = new JFileChooser(System.getProperty(USER_HOME_PROPERTY));
        final String dir = restDirectoryText.getText().trim();
        if (!dir.isEmpty()) {
            fc.setSelectedFile(new File(dir));
        }
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showDialog(this, "Select REST directory") == JFileChooser.APPROVE_OPTION) {
            final String fnam = fc.getSelectedFile().getPath();
            restDirectoryText.setText(fnam);
        }
    }//GEN-LAST:event_restDirectoryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autosaveCheckBox;
    private javax.swing.JLabel autosaveLabel;
    private javax.swing.JPanel autosavePanel;
    private javax.swing.JSpinner autosaveSpinner;
    private javax.swing.JCheckBox downloadPythonClientCheckBox;
    private javax.swing.JButton notebookDirectoryButton;
    private javax.swing.JLabel notebookDirectoryLabel;
    private javax.swing.JTextField notebookDirectoryText;
    private javax.swing.JPanel notebookPanel;
    private javax.swing.JCheckBox rememberSaveLocationCheckBox;
    private javax.swing.JButton restDirectoryButton;
    private javax.swing.JLabel restDirectoryLabel;
    private javax.swing.JTextField restDirectoryText;
    private javax.swing.JPanel saveLocationPanel;
    private javax.swing.JPanel startupPanel;
    private javax.swing.JCheckBox startupWelcomeCheckbox;
    private javax.swing.JCheckBox startupWhatsNewCheckbox;
    private javax.swing.JButton userDirectoryButton;
    private javax.swing.JLabel userDirectoryLabel;
    private javax.swing.JTextField userDirectoryText;
    private javax.swing.JPanel webserverPanel;
    private javax.swing.JLabel webserverPortLabel;
    private javax.swing.JSpinner webserverPortSpinner;
    // End of variables declaration//GEN-END:variables
}
