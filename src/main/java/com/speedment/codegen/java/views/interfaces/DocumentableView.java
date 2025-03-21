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
    return cg.on(model.getJavadoc()).map((
<<<<<<< /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/interfaces/DocumentableView.java/left.java
    jd
=======
    j
>>>>>>> /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/interfaces/DocumentableView.java/right.java
    ) -> 
<<<<<<< /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/interfaces/DocumentableView.java/left.java
    jd
=======
    j
>>>>>>> /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/interfaces/DocumentableView.java/right.java
     + nl()).orElse(EMPTY);
  }
}