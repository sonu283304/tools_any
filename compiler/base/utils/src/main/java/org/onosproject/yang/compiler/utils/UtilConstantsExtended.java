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

package org.onosproject.yang.compiler.utils;

/**
 * Represents utilities constants which are used while generating java files.
 */
public final class UtilConstantsExtended {

    // No instantiation.
    private UtilConstantsExtended() {
    }

    /**
     * Static param for add augmentation.
     */
    public static final String ADD_ANYDATA = "addAnydata";

    /**
     * Static param for remove augmentation.
     */
    public static final String REMOVE_ANYDATA = "removeAnydata";

    /**
     * Static param for augmentations.
     */
    public static final String ANYDATAS = "anydatas";

    /**
     * Static param for augmentation.
     */
    public static final String ANYDATA = "anydata";
}