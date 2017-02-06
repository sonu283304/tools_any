/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.runtime.app;

import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.runtime.api.CompositeData;
import org.onosproject.yang.runtime.api.CompositeStream;
import org.onosproject.yang.runtime.api.YangRuntimeException;
import org.onosproject.yang.runtime.api.YangRuntimeService;
import org.onosproject.yang.runtime.api.YangSerializer;
import org.onosproject.yang.runtime.api.YangSerializerContext;
import org.onosproject.yang.runtime.api.YangSerializerRegistry;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents YANG runtime codec service implementation.
 */
public class DefaultYangRuntimeHandler implements YangRuntimeService {

    private static final Logger log = getLogger(DefaultYangModelRegistry.class);
    private static final String DF = "Data format ";
    private static final String NR = " is not registered.";
    private YangSerializerRegistry registry;
    private SchemaContext rootContext;
    private YangSerializerContext serializerContext;

    /**
     * Creates a new YANG runtime manager.
     *
     * @param r serializer registry
     * @param c root's schema context
     */
    public DefaultYangRuntimeHandler(YangSerializerRegistry r,
                                     SchemaContext c) {
        registry = r;
        rootContext = c;
        serializerContext = new DefaultYangSerializerContext(rootContext);
    }

    @Override
    public CompositeData decode(CompositeStream external, String dataFormat) {
        YangSerializer ys = getRegisteredSerializer(dataFormat);
        return ys.decode(external, serializerContext);
    }

    @Override
    public CompositeStream encode(CompositeData internal, String dataFormat) {
        YangSerializer ys = getRegisteredSerializer(dataFormat);
        return ys.encode(internal, serializerContext);
    }

    /**
     * Returns serializer for a given data format.
     *
     * @param df data format
     * @return YANG serializer
     */
    private YangSerializer getRegisteredSerializer(String df) {
        YangSerializer s =
                ((DefaultYangSerializerRegistry) registry).getSerializer(df);
        if (s == null) {
            log.info(DF + " {} " + NR, df);
            throw new YangRuntimeException(DF + df + NR);
        }
        return s;
    }
}