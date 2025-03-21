/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.codegen.java.views.interfaces;

import static com.speedment.codegen.Formatting.EMPTY;
import static com.speedment.codegen.Formatting.nl;
import com.speedment.codegen.base.CodeGenerator;
import com.speedment.codegen.base.CodeView;
import com.speedment.codegen.lang.interfaces.Documentable;

/**
 *
 * @author Emil Forslund
 * @param <M>
 */
public interface DocumentableView<M extends Documentable<M>> extends CodeView<M> {
    default String renderJavadoc(CodeGenerator cg, M model) {
<<<<<<< /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/interfaces/DocumentableView.java/left.java
        return cg.on(model.getJavadoc()).map(jd -> jd + nl()).orElse(EMPTY);
||||||| /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/interfaces/DocumentableView.java/base.java
        return cg.on(model.getJavadoc()).map().orElse(EMPTY);
=======
        return cg.on(model.getJavadoc()).map(j -> j + nl()).orElse(EMPTY);
>>>>>>> /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/interfaces/DocumentableView.java/right.java
    }
}
