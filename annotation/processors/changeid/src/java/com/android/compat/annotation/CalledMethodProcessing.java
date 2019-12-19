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

import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.tree.MethodInvocationTree;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.SimpleElementVisitor8;

public class CalledMethodProcessing {

    private final ProcessingEnvironment mProcessingEnv;
    private final RoundEnvironment mRoundEnv;

    public CalledMethodProcessing(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        mProcessingEnv = processingEnv;
        mRoundEnv = roundEnv;
    }

    public boolean process() {
        final Trees trees = Trees.instance(mProcessingEnv);
        InvocationScanner scanner = new InvocationScanner();
        //final Trees trees = Trees.instance(mProcessingEnv);
        for (Element root : mRoundEnv.getRootElements()) {
            final TreePath tp = trees.getPath(root);
            scanner.scan(tp, trees);

        }
        return true;
    }

    class InvocationScanner extends TreePathScanner<Void, Trees> {
        @Override
        public Void visitMethodInvocation(MethodInvocationTree m, Trees t) {

            return super.visitMethodInvocation(m, t);
        }
    }


}
