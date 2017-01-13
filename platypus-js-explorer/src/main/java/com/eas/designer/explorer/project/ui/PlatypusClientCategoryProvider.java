package com.eas.designer.explorer.project.ui;

import com.eas.designer.explorer.project.PlatypusProjectImpl;
import javax.swing.JComponent;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "platypusClient", categoryLabel = "#platypusClient", projectType = "org-netbeans-modules-platypus", position=11)
public class PlatypusClientCategoryProvider implements ProjectCustomizer.CompositeCategoryProvider {

    @Override
    public ProjectCustomizer.Category createCategory(Lookup lkp) {
        return ProjectCustomizer.Category.create("platypusClient", NbBundle.getMessage(PlatypusProjectCustomizerProvider.class, "platypusClient"), null, new ProjectCustomizer.Category[]{});
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category ctgr, Lookup lkp) {
        try {
            PlatypusProjectImpl project = lkp.lookup(PlatypusProjectImpl.class);
            return new PlatypusClientCustomizer(project);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }
}
