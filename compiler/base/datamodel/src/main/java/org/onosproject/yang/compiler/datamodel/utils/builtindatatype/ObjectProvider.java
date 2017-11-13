/*
 * Copyright 2016-present Open Networking Foundation
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

package org.onosproject.yang.compiler.datamodel.utils.builtindatatype;

import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangUnion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

public final class ObjectProvider {

    // Object provider constant error string.
    private static final String E_DATATYPE = "Data type not supported.";
    private static final String E_NONEMPTY = "Value is of non Empty type";

    /**
     * Restricts creation of object providers instance.
     */
    private ObjectProvider() {
    }

    /**
     * Returns object from string value.
     *
     * @param typeInfo  refers to YANG type information
     * @param leafValue leafValue argument is used to set the value
     *                  in method
     * @param dataType  yang data type
     * @return values object of corresponding given data type
     * @throws IllegalArgumentException if input is not valid
     */
    public static Object getObject(YangType typeInfo, String leafValue,
                                   YangDataTypes dataType)
            throws IllegalArgumentException {
        YangDataTypes type;
        if (dataType != null) {
            type = dataType;
        } else {
            type = typeInfo.getDataType();
        }
        switch (type) {
            case INT8:
                return Byte.parseByte(leafValue);
            case UINT8:
            case INT16:
                return Short.parseShort(leafValue);
            case UINT16:
            case INT32:
                return Integer.parseInt(leafValue);
            case UINT32:
            case INT64:
                return Long.parseLong(leafValue);
            case UINT64:
                return new BigInteger(leafValue);
            case EMPTY:
                if (leafValue == null || leafValue.equals("")) {
                    return null;
                } else if (leafValue.equals("true") ||
                        leafValue.equals("false")) {
                    return Boolean.parseBoolean(leafValue);
                }
            case BOOLEAN:
                if (leafValue.equals("false") || leafValue.equals("true")) {
                    return Boolean.parseBoolean(leafValue);
                }
                throw new IllegalArgumentException(E_DATATYPE);
            case BINARY:
            case BITS:
            case IDENTITYREF:
            case ENUMERATION:
            case STRING:
            case INSTANCE_IDENTIFIER:
                return leafValue;
            case DECIMAL64:
                return new BigDecimal(leafValue);
            case LEAFREF:
                YangType refType = ((YangLeafRef) typeInfo
                        .getDataTypeExtendedInfo()).getEffectiveDataType();
                return getObject(refType, leafValue, refType.getDataType());
            case DERIVED:
                // referred typedef's list of type will always has only one type
                YangType rt = ((YangDerivedInfo) typeInfo
                        .getDataTypeExtendedInfo()).getReferredTypeDef()
                        .getTypeList().get(0);
                return getObject(rt, leafValue, ((YangDerivedInfo)
                        typeInfo.getDataTypeExtendedInfo()).getEffectiveBuiltInType());
            case UNION:
                return parseUnionTypeInfo(typeInfo, leafValue);
            default:
                throw new IllegalArgumentException(E_DATATYPE);
        }
    }

    /**
     * Returns the object for given data type with respective value.
     *
     * @param type      data type of value
     * @param leafValue value
     * @return object of data type containing the value
     */
    private static Object parseUnionTypeInfo(YangType type, String leafValue) {
        Iterator<YangType<?>> it = ((YangUnion) type.getDataTypeExtendedInfo())
                .getTypeList().listIterator();
        while (it.hasNext()) {
            YangType t = it.next();
            try {
                return getObject(t, leafValue, t.getDataType());
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        throw new IllegalArgumentException("Invalid value of data");
    }
}
