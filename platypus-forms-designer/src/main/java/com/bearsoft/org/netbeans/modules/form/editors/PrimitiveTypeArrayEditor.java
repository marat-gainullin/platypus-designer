/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.org.netbeans.modules.form.FormCookie;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Property editor for single-dimensional arrays of primitive types
 *
 * @author Jiri Vagner
 *
 */
public class PrimitiveTypeArrayEditor extends PropertyEditorSupport implements ExPropertyEditor {

    private Class<?> valueType;    // type of edited form property
    private FormProperty<?> formProperty;  // edited form property
    private static final String ARR_BEGIN = "["; // NOI18N
    private static final String ARR_END = "]"; // NOI18N
    private static final String NULL_STR = "null"; // NOI18N    
    // arrays for easy converting escape sequences
    private final char[] escChars = {'\t', '\b', '\n', '\r', '\f', '\'', '\"', '\\'}; // NOI18N
    private final String[] escCharsStr = {"\\t", "\\b", "\\n", "\\r", "\\f", "\\'", "\\\"", "\\\\"}; // NOI18N

    /**
     * Splits string from inplace editor into char array
     */
    private String[] splitCharArray(String body) {
        boolean reading = false;
        String tempVal = ""; // NOI18N
        char prevChar = ' '; // NOI18N
        char prevPrevChar = ' '; // NOI18N            
        List<String> list = new ArrayList<>();
        for (int i = 0; i < body.length(); i++) {
            char actChar = body.charAt(i);

            if ((actChar == '\'') && (prevChar != '\\')) {
                if (!reading) {
                    reading = true;
                    tempVal = ""; // NOI18N
                } else {
                    reading = false;
                    list.add(tempVal);
                    tempVal = ""; // NOI18N
                }
            } else if ((actChar == '\'') && (prevChar == '\\')
                    && (prevPrevChar == '\\')) {
                // special '\\' case ...
                reading = false;
                list.add(tempVal);
                tempVal = ""; // NOI18N
            } else {
                if (reading) {
                    tempVal += actChar;
                }
            } // NOI18N
            prevPrevChar = prevChar;
            prevChar = actChar;
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Converts char[] into String[] with custom conversion
     */
    private Object[] toCharObjectArray(Object array) {
        Object[] result;
        char[] source = (char[]) array;
        result = new String[source.length];
        for (int i = 0; i < source.length; i++) {
            result[i] = convertChar2String(source[i]);
        }
        return result;
    }

    /**
     * Converts char into string, takes care about escape sequencies
     */
    private String convertChar2String(char source) {
        for (int i = 0; i < escChars.length; i++) {
            if (source == escChars[i]) {
                return "'" + escCharsStr[i] + "'"; // NOI18N
            }
        }
        return "'" + String.valueOf(source) + "'"; // NOI18N
    }

    /**
     * Converts string into char, takes care about escape sequencies
     */
    private char convertString2Char(String source) throws ParseException {
        if (source.length() > 1) {
            for (int i = 0; i < escCharsStr.length; i++) {
                if (source.equals(escCharsStr[i])) {
                    return escChars[i];
                }
            }
            throw new ParseException("", 0); // NOI18N
        } else if (source.length() == 1) {
            return source.charAt(0);
        } else {
            throw new ParseException("", 0); // NOI18N
        }
    }

    /**
     * Converts array of objects into string for inplace editor content
     */
    private String arr2Text(Object[] arr, boolean justContent) {
        StringBuilder strBuild = new StringBuilder();

        if (!justContent) {
            strBuild.append(ARR_BEGIN);
        }

        for (int i = 0; i < arr.length; i++) {
            Object act = arr[i];
            strBuild.append(act);

            if (justContent && this.valueType.equals(float[].class)) {
                strBuild.append("f"); // NOI18N
            }
            if (i != (arr.length - 1)) {
                strBuild.append(", "); // NOI18N
            }
        }

        if (!justContent) {
            strBuild.append(ARR_END);
        }
        return strBuild.toString();
    }

    /**
     * Converts text to array of objects
     */
    private Object text2Arr(String text) throws ParseException {
        String[] parts;
        String trimText = text.trim();

        if ((trimText.length() == 0) || trimText.toLowerCase().equals(NULL_STR)) {
            return null;
        }

        if (text.length() < 2) {
            parts = new String[0];
        } else {
            int arrBeginPos = text.indexOf(ARR_BEGIN);
            int arrEndPos = text.indexOf(ARR_END);

            if (arrBeginPos + 1 < arrEndPos) {
                String body = text.substring(text.indexOf(ARR_BEGIN) + 1,
                        text.indexOf(ARR_END));

                if (!valueType.equals(char[].class)) {
                    parts = body.split(","); // NOI18N
                } else {
                    parts = splitCharArray(body);
                }
            } else {
                parts = new String[0];
            }
        }

        if (valueType.equals(boolean[].class)) {
            boolean[] result = new boolean[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Boolean.parseBoolean(parts[i].trim());
            }
            return result;
        } else if (valueType.equals(byte[].class)) {
            byte[] result = new byte[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Byte.parseByte(parts[i].trim());
            }
            return result;
        } else if (valueType.equals(short[].class)) {
            short[] result = new short[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Short.parseShort(parts[i].trim());
            }
            return result;
        } else if (valueType.equals(int[].class)) {
            int[] result = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Integer.parseInt(parts[i].trim());
            }
            return result;
        } else if (valueType.equals(long[].class)) {
            long[] result = new long[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Long.parseLong(parts[i].trim());
            }
            return result;
        } else if (valueType.equals(float[].class)) {
            float[] result = new float[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Float.parseFloat(parts[i].trim());
            }
            return result;
        } else if (valueType.equals(double[].class)) {
            double[] result = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Double.parseDouble(parts[i].trim());
            }
            return result;
        } else if (valueType.equals(char[].class)) {
            char[] result = new char[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = convertString2Char(parts[i]);
            }
            return result;
        }
        return null;
    }

    /**
     * Creates array of object wrappers for easier manipulation with arrray and
     * converts it into text value
     *
     */
    @Override
    public String getAsText() {
        if (getValue() != null) {
            Object[] result;
            if (valueType.equals(char[].class)) {
                result = toCharObjectArray(this.getValue());
            } else {
                result = Utilities.toObjectArray(getValue());
            }
            return arr2Text(result, false);
        } else {
            return NULL_STR;
        }
    }

    /**
     * Sets value of edited property, shows information dialog about parsing
     * troubles
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            setValue(text2Arr(text));
        } catch (Exception e) {
            String msg = NbBundle.getMessage(
                    PrimitiveTypeArrayEditor.class, "MSG_ERR_ParseError"); // NOI18N
            throw new IllegalArgumentException(
                    String.format(msg, text, this.valueType.getSimpleName()),
                    e);
        }
    }

    @Override
    public void attachEnv(PropertyEnv aEnv) {
        aEnv.getFeatureDescriptor().setValue("canEditAsText", Boolean.TRUE); // NOI18N
        Object bean = aEnv.getBeans()[0];
        if (bean instanceof Node) {
            Node node = (Node) bean;
            FormCookie formCookie = node.getLookup().lookup(FormCookie.class);
            if (formCookie != null && aEnv.getFeatureDescriptor() instanceof FormProperty<?>) {
                formProperty = (FormProperty<?>) aEnv.getFeatureDescriptor();
                valueType = formProperty.getValueType();
            }
        }
    }

    @Override
    public String getJavaInitializationString() {
        Object[] valObj;

        if (valueType.equals(char[].class)) {
            valObj = toCharObjectArray(getValue());
        } else {
            valObj = Utilities.toObjectArray(getValue());
        }

        return "new " + valueType.getSimpleName() + " {" + arr2Text(valObj, true) + "}"; // NOI18N
    }
}
