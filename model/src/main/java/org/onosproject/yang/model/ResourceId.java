/*
 * Copyright 2017-present Open Networking Foundation
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

package org.onosproject.yang.model;

import com.google.common.collect.ImmutableList;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.hash;
import static org.onosproject.yang.model.ModelConstants.LEAF_IS_TERMINAL;
import static org.onosproject.yang.model.ModelConstants.NON_KEY_LEAF;

/**
 * Representation of an entity which identifies a resource in the logical tree
 * data store. It is a list of node keys to identify the branch point
 * hierarchy to reach a resource in the instance tree.
 */

public final class ResourceId {

    /**
     * List of node keys.
     */
    private final List<NodeKey> nodeKeyList;

    /**
     * Create object from builder.
     *
     * @param builder initialized builder
     */
    public ResourceId(Builder builder) {
        nodeKeyList = ImmutableList.copyOf(builder.nodeKeyList);
    }

    /**
     * Retrieves a new resource builder.
     *
     * @return resource builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the list of node key used to uniquely identify the branch in the
     * logical tree starting from root.
     *
     * @return node key uniquely identifying the branch
     */
    public List<NodeKey> nodeKeys() {
        return nodeKeyList;
    }

    /**
     * Returns resource identifier builder for a given resource identifier.
     * It contains all the attributes from the resource identifier. It is to
     * provide mutability of resource identifier using builder pattern.
     *
     * @return data node builder
     * @throws CloneNotSupportedException when clone is not supported
     */
    public Builder copyBuilder() throws CloneNotSupportedException {
        return new Builder(this);
    }

    @Override
    public int hashCode() {
        return hash(nodeKeyList);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ResourceId)) {
            return false;
        }
        ResourceId that = (ResourceId) obj;
        List<NodeKey> thatList = that.nodeKeyList;
        return nodeKeyList.size() == thatList.size() &&
                nodeKeyList.containsAll(thatList);
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("nodeKeyList", nodeKeyList)
                .toString();
    }

    /**
     * Builder to construct resource identifier.
     */
    public static class Builder {

        /**
         * Application related information, this enables application to use
         * this builder as there work bench.
         */
        protected Object appInfo;

        protected List<NodeKey> nodeKeyList;
        protected NodeKey.NodeKeyBuilder curKeyBuilder;

        /**
         * Creates an instance of resource identifier builder.
         */
        public Builder() {
            nodeKeyList = new LinkedList<>();
        }

        /**
         * Creates an instance of resource identifier builder. This is used
         * in scenario when builder is required from a given resource
         * identifier.
         *
         * @param id old resource identifier
         * @throws CloneNotSupportedException when clone is not supported
         */
        public Builder(ResourceId id) throws CloneNotSupportedException {
            nodeKeyList = new LinkedList<>();
            for (NodeKey key : id.nodeKeyList) {
                nodeKeyList.add(key.clone());
            }
        }

        /**
         * Appends a given resource id to current builder.
         *
         * @param id resource identifier to be appended
         * @return builder
         * @throws CloneNotSupportedException when clone is not supported
         */
        public Builder append(ResourceId id) throws CloneNotSupportedException {
            processCurKey();
            curKeyBuilder = null;
            Builder ob = id.copyBuilder();
            nodeKeyList.addAll(ob.nodeKeyList);
            return this;
        }

        /**
         * Appends a given list of NodeKey to current builder.
         *
         * @param nodeKeys list of NodeKey to be appended
         * @return builder
         */
        public Builder append(List<NodeKey> nodeKeys) {
            processCurKey();
            curKeyBuilder = null;
            nodeKeyList.addAll(nodeKeys);
            return this;
        }

        /**
         * Validates, build and add current key.
         */
        protected void processCurKey() {
            if (curKeyBuilder != null) {
                if (curKeyBuilder instanceof LeafListKey.LeafListKeyBuilder) {
                    throw new ModelException(LEAF_IS_TERMINAL);
                }
                nodeKeyList.add(curKeyBuilder.build());
            }
        }

        /**
         * Adds the descendant node's schema identity.
         *
         * @param name      name of descendant node
         * @param nameSpace name space pf descendant node
         * @return updated builder pointing to the specified schema location
         */
        public Builder addBranchPointSchema(String name, String nameSpace) {
            processCurKey();
            curKeyBuilder = new NodeKey.NodeKeyBuilder();
            curKeyBuilder.schemaId(name, nameSpace);
            return this;
        }

        /**
         * Adds a multi instance attribute's node identity.
         *
         * @param name      name of the leaf list
         * @param nameSpace name space of leaf list
         * @param val       value of attribute to identify the instance
         * @return updated builder pointing to the specific attribute
         * value instance
         */
        public Builder addLeafListBranchPoint(String name, String nameSpace,
                                              Object val) {
            LeafListKey.LeafListKeyBuilder leafListKeyBuilder;
            if (curKeyBuilder instanceof LeafListKey.LeafListKeyBuilder) {
                throw new ModelException(NON_KEY_LEAF);
            }
            leafListKeyBuilder = new LeafListKey.LeafListKeyBuilder()
                    .schemaId(name, nameSpace).value(val);
            processCurKey();
            curKeyBuilder = leafListKeyBuilder;
            return this;
        }

        /**
         * Adds a multi instance nodes key attribute value to identify
         * the branch point of instance tree.
         *
         * @param name      name of the key attribute
         * @param nameSpace name space of key attribute
         * @param val       value of the key leaf, to match in the list entry
         * @return updated builder with list branching information
         */
        public Builder addKeyLeaf(String name, String nameSpace, Object val) {
            ListKey.ListKeyBuilder listKeyBuilder;
            if (!(curKeyBuilder instanceof ListKey.ListKeyBuilder)) {
                if (curKeyBuilder instanceof LeafListKey.LeafListKeyBuilder) {
                    throw new ModelException(LEAF_IS_TERMINAL);
                }

                listKeyBuilder = new ListKey.ListKeyBuilder(curKeyBuilder);
            } else {
                listKeyBuilder = (ListKey.ListKeyBuilder) curKeyBuilder;
            }

            listKeyBuilder.addKeyLeaf(name, nameSpace, val);
            curKeyBuilder = listKeyBuilder;
            return this;
        }

        /**
         * Builds a resource identifier to based on set path information of
         * the resource.
         *
         * @return built resource identifier
         */
        public ResourceId build() {
            if (curKeyBuilder != null) {
                nodeKeyList.add(curKeyBuilder.build());
                curKeyBuilder = null;
            }
            return new ResourceId(this);
        }

        /**
         * Removes last key in the node key list.
         *
         * @return updated builder
         */
        public Builder removeLastKey() {
            nodeKeyList.remove(nodeKeyList.size() - 1);
            return this;
        }

        /**
         * Returns application information. This enables application to use
         * this builder as there work bench.
         *
         * @return application information
         */
        public Object appInfo() {
            return appInfo;
        }

        /**
         * Sets application information. This enables application to use
         * this builder as there work bench.
         *
         * @param appInfo application related information
         */
        public void appInfo(Object appInfo) {
            this.appInfo = appInfo;
        }
    }
}
