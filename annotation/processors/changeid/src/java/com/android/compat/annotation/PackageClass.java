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
 * limitations under the License.
 */

package com.android.compat.annotation;

import com.google.common.base.Objects;

public class PackageClass {

    public final String javaPackage;
    public final String javaClass;

    public PackageClass(String pkg, String cls) {
        this.javaPackage = pkg;
        this.javaClass = cls;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PackageClass) {
            PackageClass that = (PackageClass) obj;
            return Objects.equal(this.javaPackage, that.javaPackage) &&
                    Objects.equal(this.javaClass, that.javaClass);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode(javaPackage, javaClass);
    }
}
