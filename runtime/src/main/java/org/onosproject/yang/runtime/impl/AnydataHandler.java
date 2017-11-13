package org.onosproject.yang.runtime.impl;

import org.onosproject.yang.compiler.datamodel.SchemaDataNode;
import org.onosproject.yang.compiler.datamodel.YangImport;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeContextInfo;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.YangVersionHolder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.FMT_NOT_REG;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.errorMsg;
import static org.onosproject.yang.runtime.RuntimeHelper.PERIOD;
import static org.onosproject.yang.runtime.impl.UtilsConstants.DOT_REGEX;
import static org.onosproject.yang.runtime.impl.UtilsConstants.QNAME_PRE;
import static org.onosproject.yang.runtime.impl.UtilsConstants.REV_REGEX;

public class AnydataHandler {

    // YANG module registry.
    private DefaultYangModelRegistry reg;

    /*
     *Reference for the index in paths array which contains revision info.
     */
    private int index = 6;

    /*
     *Reference for path array of nodes created from generated class.
     */
    private String[] paths;

    /**
     * Creates the instance of anydataHandler.
     *
     * @param r module registry
     */
    public AnydataHandler(DefaultYangModelRegistry r) {
        reg = r;
    }

    /**
     * Returns the schema node context info of target node with given class
     * context.
     *
     * @param c generated class for data node
     * @return schema node context info
     */
    YangSchemaNodeContextInfo getContextInfoFromClass(Class c) {
        YangSchemaNodeContextInfo info = null;
        YangNode s = (YangNode) getModuleSchemaFromQname(c);
        if (s != null) {
            int len = paths.length - 1;
            int maxlen = len - 1;
            info = getTargetNode(len, s);

            // In-case of augment check all imported module
            if (info == null) {
                // Replacing the augmented node name with node name in given
                // class
                paths[maxlen] = paths[len];
                List<YangImport> list = ((YangVersionHolder) s).getImportList();
                for (YangImport i : list) {
                    info = getTargetNode(maxlen, i.getImportedNode());
                    if (info == null) {
                        continue;
                    } else {
                        return info;
                    }
                }
            }
        }
        return info;
    }

    /**
     * Returns the targeted child node YANG schema from the given schema node.
     *
     * @param maxLen last index of class paths to indicate the last target node
     * @param s      top level schema node
     * @return targeted child node YANG schema
     */
    private YangSchemaNodeContextInfo getTargetNode(int maxLen, YangNode s) {
        YangSchemaNodeContextInfo info;
        // skipping the revision part in class path
        int i = index + 1;
        info = getNodeInfo(s.getYsnContextInfoMap(), i);
        while (i < maxLen) {
            if (info != null) {
                info = getNodeInfo(((YangNode) info.getSchemaNode())
                                           .getYsnContextInfoMap(), ++i);
            } else {
                break;
            }
        }
        return info;
    }

    /**
     * Returns the schema node context info pointed by given path index from
     * given YANG schema node context info map.
     *
     * @param m YANG schema node context info map
     * @param i path index
     * @return schema node context info
     */
    private YangSchemaNodeContextInfo getNodeInfo(
            Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> m,
            int i) {
        YangSchemaNodeContextInfo info;
        YangSchemaNode schema;
        Iterator<YangSchemaNodeContextInfo> it = m.values().iterator();
        while (it.hasNext()) {
            info = it.next();
            schema = info.getSchemaNode();
            if (schema instanceof SchemaDataNode) {
                if (schema.getJavaAttributeName().equalsIgnoreCase(paths[i])) {
                    return info;
                }
            }
        }
        return null;
    }

    /**
     * Returns the module YANG schema node for given generated node class.
     *
     * @param c generated class
     * @return module schema node
     */
    private YangSchemaNode getModuleSchemaFromQname(Class c) {
        String cn = c.getCanonicalName();
        paths = cn.split(DOT_REGEX);
        if (paths[index].matches(REV_REGEX)) {
            index++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(QNAME_PRE);
        for (int i = 4; i <= index; i++) {
            sb.append(PERIOD);
            sb.append(paths[i]);
        }
        YangSchemaNode s = reg.getForRegClassQualifiedName(sb.toString(), false);
        if (s == null) {
            throw new IllegalArgumentException(errorMsg(FMT_NOT_REG,
                                                        paths[index]));
        }
        return s;
    }
}
