/*
 * Copyright 2016-present Open Networking Laboratory
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

package org.onosproject.yangutils.plugin.manager;

import java.io.IOException;
import java.util.Iterator;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.parser.exceptions.ParserException;
import org.onosproject.yangutils.utils.io.YangPluginConfig;
import org.onosproject.yangutils.utils.io.impl.YangFileScanner;

import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.deleteDirectory;

/**
 * Test cases for testing YANG schema node.
 */
public class IncludeReferenceWithPrefix {

    private final YangUtilManager utilManager = new YangUtilManager();

    /**
     * Checks method to get schema node from map.
     *
     * @throws MojoExecutionException
     */
    @Test
    public void processRefToIncludeWithPrefix() throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/refincludecontentwithprefix";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();
        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/refincludecontentwithprefix/");
        utilManager.translateToJava(yangPluginConfig);

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();
        YangNode rootNode = yangNodeIterator.next();

        deleteDirectory("target/schemaMap/");
    }
}