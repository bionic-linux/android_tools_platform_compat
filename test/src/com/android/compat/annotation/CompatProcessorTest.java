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

import static java.nio.charset.StandardCharsets.UTF_8;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.CompilationSubject;

import com.google.common.io.ByteSource;

import javax.tools.JavaFileObject;

import org.junit.Before;
import org.junit.Test;

import java.util.List;


public class CompatProcessorTest {

    private JavaFileObject[] mAnnotations;

    private static final String HEADER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    @Before
    public void setUp() {
        mAnnotations = new JavaFileObject[4];
        mAnnotations[0] = JavaFileObjects.forSourceLines("android.compat.annotation.ChangeId",
                "package android.compat.annotation;",
                "import static java.lang.annotation.ElementType.FIELD;",
                "import static java.lang.annotation.ElementType.PARAMETER;",
                "import static java.lang.annotation.RetentionPolicy.CLASS;",
                "import java.lang.annotation.Retention;",
                "import java.lang.annotation.Target;",
                "@Retention(CLASS)",
                "@Target({FIELD, PARAMETER})",
                "public @interface ChangeId {",
                "}");
        mAnnotations[1] = JavaFileObjects.forSourceLines("android.compat.annotation.Disabled",
                "package android.compat.annotation;",
                "import static java.lang.annotation.ElementType.FIELD;",
                "import static java.lang.annotation.RetentionPolicy.CLASS;",
                "import java.lang.annotation.Retention;",
                "import java.lang.annotation.Target;",
                "@Retention(CLASS)",
                "@Target({FIELD})",
                "public @interface Disabled {",
                "}");
        mAnnotations[2] = JavaFileObjects.forSourceLines("android.compat.annotation.EnabledAfter",
                "package android.compat.annotation;",
                "import static java.lang.annotation.ElementType.FIELD;",
                "import static java.lang.annotation.RetentionPolicy.CLASS;",
                "import java.lang.annotation.Retention;",
                "import java.lang.annotation.Target;",
                "@Retention(CLASS)",
                "@Target({FIELD})",
                "public @interface EnabledAfter {",
                "int targetSdkVersion();",
                "}");
    }

    @Test
    public void testSuccessfulCompilation() {
        JavaFileObject sourceLines =
                JavaFileObjects.forSourceLines(
                        "libcore.util.Compat",
                        "package libcore.util;",
                        "import android.compat.annotation.ChangeId;",
                        "import android.compat.annotation.EnabledAfter;",
                        "import android.compat.annotation.Disabled;",
                        "public class Compat {",
                        "    @EnabledAfter(targetSdkVersion=29)",
                        "    @ChangeId",
                        "    static final long MY_CHANGE_ID = 123456789l;",
                        "    @ChangeId",
                        "    @Disabled",
                        "    public static final long ANOTHER_CHANGE = 23456700l;",
                        "}");
        String expectedFile = HEADER + "<config>" +
                "<compat-change enableAfterTargetSdk=\"29\" id=\"123456789\" "
                + "name=\"MY_CHANGE_ID\"/>" +
                "<compat-change disabled=\"true\" id=\"23456700\" name=\"ANOTHER_CHANGE\"/>" +
                "</config>";
        mAnnotations[3] = sourceLines;
        Compilation compilation =
                Compiler.javac()
                        .withProcessors(new CompatProcessor())
                        .compile(mAnnotations);
        CompilationSubject.assertThat(compilation).succeeded();
        CompilationSubject.assertThat(compilation).generatedFile(CLASS_OUTPUT, "compat",
                "compat_config.xml").hasContents(ByteSource.wrap(expectedFile.getBytes(UTF_8)));
    }

    @Test
    public void testIgnoredParams() {
        JavaFileObject sourceLines =
                JavaFileObjects.forSourceLines(
                        "android.compat.Compatibility",
                        "package android.compat;",
                        "import android.compat.annotation.ChangeId;",
                        "public final class Compatibility {",
                        "   public static void reportChange(@ChangeId long changeId) {}",
                        "   public static boolean isChangeEnabled(@ChangeId long changeId) {",
                        "       return true;",
                        "   }",
                        "}");
        mAnnotations[3] = sourceLines;
        Compilation compilation =
                Compiler.javac()
                        .withProcessors(new CompatProcessor())
                        .compile(mAnnotations);
        CompilationSubject.assertThat(compilation).succeeded();
    }

    @Test
    public void testOtherClassParams() {
        JavaFileObject sourceLines =
                JavaFileObjects.forSourceLines(
                        "android.compat.OtherClass",
                        "package android.compat;",
                        "import android.compat.annotation.ChangeId;",
                        "public final class OtherClass {",
                        "   public static void reportChange(@ChangeId long changeId) {}",
                        "}");
        mAnnotations[3] = sourceLines;
        Compilation compilation =
                Compiler.javac()
                        .withProcessors(new CompatProcessor())
                        .compile(mAnnotations);
        CompilationSubject.assertThat(compilation).hadErrorContaining(
                "Non field element changeId annotated with @ChangeId. Got type PARAMETER, "
                        + "expected FIELD.");
    }

    @Test
    public void testOtherMethodParams() {
        JavaFileObject sourceLines =
                JavaFileObjects.forSourceLines(
                        "android.compat.Compatibility",
                        "package android.compat;",
                        "import android.compat.annotation.ChangeId;",
                        "public final class Compatibility {",
                        "   public static void otherMethod(@ChangeId long changeId) {}",
                        "}");
        mAnnotations[3] = sourceLines;
        Compilation compilation =
                Compiler.javac()
                        .withProcessors(new CompatProcessor())
                        .compile(mAnnotations);
        CompilationSubject.assertThat(compilation).hadErrorContaining(
                "Non field element changeId annotated with @ChangeId. Got type PARAMETER, "
                        + "expected FIELD.");
    }

    @Test
    public void testNonFinal() {
        JavaFileObject sourceLines =
                JavaFileObjects.forSourceLines(
                        "libcore.util.Compat",
                        "package libcore.util;",
                        "import android.compat.annotation.ChangeId;",
                        "import android.compat.annotation.EnabledAfter;",
                        "public class Compat {",
                        "    @EnabledAfter(targetSdkVersion=29)",
                        "    @ChangeId",
                        "    static long MY_CHANGE_ID = 123456789l;",
                        "}");
        mAnnotations[3] = sourceLines;
        Compilation compilation =
                Compiler.javac()
                        .withProcessors(new CompatProcessor())
                        .compile(mAnnotations);
        CompilationSubject.assertThat(compilation).hadErrorContaining(
                "Non constant/final variable MY_CHANGE_ID (or non constant value) annotated with "
                        + "@ChangeId.");
    }

    @Test
    public void testNonLong() {
        JavaFileObject sourceLines =
                JavaFileObjects.forSourceLines(
                        "libcore.util.Compat",
                        "package libcore.util;",
                        "import android.compat.annotation.ChangeId;",
                        "import android.compat.annotation.EnabledAfter;",
                        "public class Compat {",
                        "    @EnabledAfter(targetSdkVersion=29)",
                        "    @ChangeId",
                        "    static final int MY_CHANGE_ID = 12345;",
                        "}");
        mAnnotations[3] = sourceLines;
        Compilation compilation =
                Compiler.javac()
                        .withProcessors(new CompatProcessor())
                        .compile(mAnnotations);
        CompilationSubject.assertThat(compilation).hadErrorContaining(
                "Variables annotated with @ChangeId should be of type long.");
    }

    @Test
    public void testNonStatic() {
        JavaFileObject sourceLines =
                JavaFileObjects.forSourceLines(
                        "libcore.util.Compat",
                        "package libcore.util;",
                        "import android.compat.annotation.ChangeId;",
                        "import android.compat.annotation.EnabledAfter;",
                        "public class Compat {",
                        "    @EnabledAfter(targetSdkVersion=29)",
                        "    @ChangeId",
                        "    final long MY_CHANGE_ID = 123456789l;",
                        "}");
        mAnnotations[3] = sourceLines;
        Compilation compilation =
                Compiler.javac()
                        .withProcessors(new CompatProcessor())
                        .compile(mAnnotations);
        CompilationSubject.assertThat(compilation).hadErrorContaining(
                "Non static variable MY_CHANGE_ID annotated with @ChangeId.");
    }
}
