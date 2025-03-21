package com.speedment.codegen.java.views;
import com.speedment.codegen.util.CodeCombiner;
import com.speedment.codegen.lang.models.Import;
import java.util.Optional;
import static com.speedment.codegen.util.Formatting.*;
import com.speedment.codegen.base.Generator;
import com.speedment.codegen.base.Transform;
import com.speedment.codegen.lang.models.File;
import com.speedment.codegen.lang.models.Type;

/**
 * Transforms from an {@link Import} to java code.
 * 
 * @author Emil Forslund
 */
public class ImportView implements Transform<Import, String> {
  private final static String IMPORT_STRING = "import ";

  /**
     * {@inheritDoc}
     */
  @Override public Optional<String> transform(Generator gen, Import model) {
    if (shouldImport(gen, model.getType())) {
      return Optional.of(IMPORT_STRING + 
<<<<<<< /usr/src/app/output/pyknic/codegen/3e0d4067f68ebf4163e850c7031571271b4893e0/src/main/java/com/speedment/codegen/java/views/ImportView.java/left.java
      gen
=======
      cg
>>>>>>> /usr/src/app/output/pyknic/codegen/3e0d4067f68ebf4163e850c7031571271b4893e0/src/main/java/com/speedment/codegen/java/views/ImportView.java/right.java
      .onEach(model.getModifiers()).collect(CodeCombiner.joinIfNotEmpty(SPACE, EMPTY, SPACE)) + model.getType().getName() + model.getStaticMember().map((str) -> DOT + str).orElse(EMPTY) + SC).filter((x) -> {
        gen.getDependencyMgr().load(model.getType().getName());
        return true;
      });
    } else {
      return Optional.empty();
    }
  }

  /**
     * Returns <code>true</code> if the specified type requires an explicit 
     * import. If the type has already been imported or is part of a package 
     * that does not need to be imported, <code>false</code> is returned.
     * 
     * @param gen   the generator
     * @param type  the type to import
     * @return      <code>true</code> if it should be imported explicitly
     */
  private boolean shouldImport(Generator gen, Type type) {
    return gen.getRenderStack().fromBottom(File.class).map((f) -> fileToClassName(f.getName())).filter((f) -> f.isPresent()).map((f) -> f.get()).filter((n) -> {
      final Optional<String> pack = packageName(n);
      return !(pack.isPresent() && type.getName().startsWith(pack.get() + DOT));
    }).findAny().isPresent();
  }
}