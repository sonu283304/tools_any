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

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.onosproject.yang.model.ModelObject.ModelObjectType.NON_ATOMIC;

/**
 * Abstraction of an entity that provides common basis for all POJOs which are
 * generated from a YANG model.
 */
public abstract class InnerModelObject extends ModelObject implements
        Augmentable, Anydata {

    private final ConcurrentMap<Class<? extends InnerModelObject>, InnerModelObject> augments =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<Class<? extends InnerModelObject>, InnerModelObject> anydatas =
            new ConcurrentHashMap<>();

    /**
     * Creates an instance of Inner model object.
     */
    protected InnerModelObject() {
        super(NON_ATOMIC);
    }

    @Override
    public void addAugmentation(InnerModelObject obj) {
        augments.put(obj.getClass(), obj);
    }

    @Override
    public void removeAugmentation(InnerModelObject obj) {
        augments.remove(obj.getClass());
    }

    @Override
    public Map<Class<? extends InnerModelObject>, InnerModelObject> augmentations() {
        return ImmutableMap.copyOf(augments);
    }

    @Override
    public <T extends InnerModelObject> T augmentation(Class<T> c) {
        return (T) augments.get(c);
    }

    @Override
    public void addAnydata(InnerModelObject obj) {
        anydatas.put(obj.getClass(), obj);
    }

    @Override
    public void removeAnydata(InnerModelObject obj) {
        anydatas.remove(obj.getClass());
    }

    @Override
    public Map<Class<? extends InnerModelObject>, InnerModelObject> anydatas() {
        return ImmutableMap.copyOf(anydatas);
    }

    @Override
    public <T extends InnerModelObject> T anydata(Class<T> c) {
        return (T) anydatas.get(c);
    }
}
