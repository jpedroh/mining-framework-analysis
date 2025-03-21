package com.speedment.codegen.java.views;
import static com.speedment.codegen.Formatting.*;
import com.speedment.codegen.base.CodeGenerator;
import com.speedment.codegen.base.CodeView;
import com.speedment.codegen.base.DependencyManager;
import com.speedment.codegen.java.views.interfaces.ClassableView;
import com.speedment.codegen.java.views.interfaces.DocumentableView;
import com.speedment.codegen.java.views.interfaces.ImportableView;
import com.speedment.codegen.lang.models.File;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Emil Forslund
 */
public class FileView implements CodeView<File>, DocumentableView<File>, ClassableView<File>, ImportableView<File> {
  private final static String PACKAGE_STRING = "package ";

  private String renderPackage(File file) {
    final Optional<String> name = fileToClassName(file.getName());
    if (name.isPresent()) {
      final Optional<String> pack = packageName(name.get());
      if (pack.isPresent()) {
        return PACKAGE_STRING + pack.get() + scdnl();
      }
    }
    return EMPTY;
  }

  @Override public Optional<String> render(CodeGenerator cg, File model) {
    final DependencyManager mgr = cg.getDependencyMgr();
    Optional<String> className = fileToClassName(model.getName());
    Optional<String> packageName = packageName(className.orElse(EMPTY));
    mgr.clearDependencies();
    if (packageName.isPresent()) {
      if (mgr.isIgnored(packageName.get())) {
        packageName = Optional.empty();
      } else {
        mgr.ignorePackage(packageName.get());
      }
    }
    final Optional<String> view = Optional.of(renderJavadoc(cg, model) + renderPackage(model) + 
<<<<<<< /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/FileView.java/left.java
    renderImports(cg, model)
=======
    cg.onEach(model.getImports()).distinct().sorted().collect(CodeCombiner.joinIfNotEmpty(nl(), EMPTY, dnl()))
>>>>>>> /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/java/views/FileView.java/right.java
     + renderClasses(cg, model));
    if (packageName.isPresent()) {
      mgr.acceptPackage(packageName.get());
    }
    return view;
  }
}