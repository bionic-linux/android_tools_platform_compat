/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.compat.annotation;

import com.google.common.annotations.VisibleForTesting;

/**
 * Simple data class that represents a change, built from the code annotations.
 */
@VisibleForTesting
public class Change {
    final Long id;
    final String name;
    final boolean disabled;
    final Integer enabledAfter;
    final String description;
    final String javaPackage;
    final String javaClass;
    final String sourcePosition;

    @VisibleForTesting
    public Change(Long id, String name, boolean disabled, Integer enabledAfter,
            String description, String javaPackage, String javaClass, String sourcePosition) {
        this.id = id;
        this.name = name;
        this.disabled = disabled;
        this.enabledAfter = enabledAfter;
        this.description = description;
        this.javaPackage = javaPackage;
        this.javaClass = javaClass;
        this.sourcePosition = sourcePosition;
    }

    public static class Builder {
        Long id;
        String name;
        boolean disabled;
        Integer enabledAfter;
        String description;
        String javaPackage;
        String javaClass;
        String sourcePosition;

        public Builder() {
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder disabled() {
            this.disabled = true;
            return this;
        }

        public Builder enabledAfter(int sdkVersion) {
            this.enabledAfter = sdkVersion;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder javaPackage(String javaPackage) {
            this.javaPackage = javaPackage;
            return this;
        }

        public Builder javaClass(String javaClass) {
            this.javaClass = javaClass;
            return this;
        }

        public Builder sourcePosition(String sourcePosition) {
            this.sourcePosition = sourcePosition;
            return this;
        }

        public Change build() {
            return new Change(id, name, disabled, enabledAfter, description, javaPackage, javaClass,
                    sourcePosition);
        }

    }
}
