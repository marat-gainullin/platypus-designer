package com.eas.designer.codecompletion;

import com.eas.designer.explorer.InstallerAdapter;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author mg
 */
public class Installer extends InstallerAdapter {

    @Override
    protected Collection<String> friendOfWhom() {
        return Arrays.asList(
                "org.netbeans.modules.javascript2.editor",
                "org.netbeans.modules.javascript2.lexer",
                "org.netbeans.modules.javascript2.model",
                "org.netbeans.modules.javascript2.types"
        );
    }

}
