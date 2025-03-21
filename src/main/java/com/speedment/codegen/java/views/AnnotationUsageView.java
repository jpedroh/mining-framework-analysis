package com.speedment.codegen.java.views;
import com.speedment.codegen.base.CodeView;
import com.speedment.codegen.lang.models.AnnotationUsage;
import java.util.Optional;
import static com.speedment.codegen.Formatting.*;
import com.speedment.codegen.base.CodeGenerator;
import com.speedment.util.CodeCombiner;
import java.util.stream.Stream;

/**
 *
 * @author Emil Forslund
 */
public class AnnotationUsageView implements CodeView<AnnotationUsage> {
  private final static String PSTART = "(", EQUALS = " = ";

  @Override public Optional<String> render(CodeGenerator cg, AnnotationUsage model) {
    final Optional<String> value = cg.on(model.getValue());
    final Stream<String> valueStream = value.isPresent() ? Stream.of(value.get()) : Stream.empty();
    return Optional.of(AT + cg.on(model.getType()).get() + 
<<<<<<< /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/AnnotationUsageView.java/left.java
    cg.on(model.getValue()).orElse(EMPTY)
=======
    Stream.of(model.getValues().stream().map((e) -> e.getKey() + cg.on(e.getValue()).map((s) -> EQUALS + s).orElse(EMPTY)), valueStream).flatMap((s) -> s).collect(CodeCombiner.joinIfNotEmpty(cnl(), PSTART, PE))
>>>>>>> /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/AnnotationUsageView.java/right.java
    );
  }
}