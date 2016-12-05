/*
 * QuerySettingsVisualPanel.java
 *
 * Created on 25.03.2011, 11:54:03
 */
package com.eas.designer.application.query.templates;

import com.eas.designer.application.utils.DatabaseConnectionRenderer;
import com.eas.designer.application.utils.DatabaseConnections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class QuerySettingsVisualPanel extends javax.swing.JPanel {

    protected NewQueryWizardSettingsPanel panel;
    protected String datasourceName;
    protected DefaultComboBoxModel<String> schemasModel;

    /**
     * Creates new form QuerySettingsVisualPanel
     */
    public QuerySettingsVisualPanel(NewQueryWizardSettingsPanel aWizardStep) {
        initComponents();
        panel = aWizardStep;
        txtConnection.setModel(new DefaultComboBoxModel(ConnectionManager.getDefault().getConnections()));
        txtConnection.setRenderer(new DatabaseConnectionRenderer(panel.getProject()));
        txtConnection.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseConnection conn = (DatabaseConnection) txtConnection.getSelectedItem();
                datasourceName = conn != null ? conn.getDisplayName() : null;
                panel.fireChangeEvent();
            }

        });
    }

    public void refreshControls() throws Exception {
        DatabaseConnection conn = DatabaseConnections.lookup(datasourceName);
        txtConnection.setSelectedItem(conn);
    }

    public boolean valid(WizardDescriptor wd) throws Exception {
        String lDatasourceName = datasourceName;
        if (lDatasourceName == null) {
            lDatasourceName = panel.getProject().getSettings().getDefaultDataSourceName();
        }
        if (lDatasourceName == null || !panel.connectionExist(lDatasourceName)) {
            wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, NbBundle.getMessage(QuerySettingsVisualPanel.class, "nonConnectionFile"));
            return false;
        }
        wd.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, NbBundle.getMessage(QuerySettingsVisualPanel.class, "readyForNextStep"));
        return true;
    }

    void store(WizardDescriptor wd) throws Exception {
        wd.putProperty(NewQueryWizardSettingsPanel.CONNECTION_PROP_NAME, datasourceName);
    }

    void read(WizardDescriptor wd) throws Exception {
        datasourceName = (String) wd.getProperty(NewQueryWizardSettingsPanel.CONNECTION_PROP_NAME);
        refreshControls();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblConnection = new javax.swing.JLabel();
        txtConnection = new javax.swing.JComboBox();
        btnApplicationConnection = new javax.swing.JButton();

        lblConnection.setText(org.openide.util.NbBundle.getMessage(QuerySettingsVisualPanel.class, "QuerySettingsVisualPanel.lblConnection.text")); // NOI18N

        btnApplicationConnection.setText(org.openide.util.NbBundle.getMessage(QuerySettingsVisualPanel.class, "QuerySettingsVisualPanel.btnApplicationConnection.text")); // NOI18N
        btnApplicationConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplicationConnectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblConnection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtConnection, 0, 210, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnApplicationConnection)
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConnection)
                    .addComponent(txtConnection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnApplicationConnection))
                .addContainerGap(266, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnApplicationConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplicationConnectionActionPerformed
        try {
            datasourceName = null;
            refreshControls();
            panel.fireChangeEvent();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_btnApplicationConnectionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApplicationConnection;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JComboBox txtConnection;
    // End of variables declaration//GEN-END:variables

}
