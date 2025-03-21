package com.speedment.codegen.base;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Emil Forslund
 */
public interface CodeGenerator {
  /**
     * @return the dependency manager.
     */
  DependencyManager getDependencyMgr();

  /**
     * Returns the current rendering stack. The top element will be the one most
     * recent rendered and the bottom one will be the element that was first
     * passed to the generator. Elements are removed from the stack once they
     * have finished rendering.
     *
     * If an element needs to access its parent, it can call this method and
     * peek on the second element from the top.
     *
     * The elements in the Stack will be of Object type. That is because the
     * framework doesn't put any constraints on what can be rendered. The
     * elements should not be cast directly to the model class but rather to an
     * interface describing the properties you need to read. That way, the
     * design remains dynamic even if the exact implementation isn't the same.
     *
     * The returned Stack will be immutable.
     *
     * @return the current rendering stack.
     */
  List<Object> getRenderStack();

  /**
     * Renders the specified model into a stream of code models. This is used
     * internally to provide the other interface methods.
     *
     * @param <M>
     * @param model The model to generate.
     * @return A stream of code objects.
     */
  <M extends java.lang.Object> Stream<Code<M>> codeOn(M model);

  /**
     * Renders all the specified models into a stream of code models. This is
     * used internally to provide the other interface methods. ¨
     *
     * @param <M>
     * @param models The models to generate.
     * @return A stream of code objects.
     */
  default <M extends java.lang.Object> Stream<Code<M>> codeOn(Collection<M> models) {
    return models.stream().map((model) -> codeOn(model)).flatMap((m) -> m);
  }

  /**
     * Locates the <code>CodeView</code> that corresponds to the specified model
     * and uses it to generate a String. If no view is associated with the model
     * type, an empty optional will be returned.
     *
     * @param model The model.
     * @return The viewed text if any.
     */
  default Optional<String> on(Object model) {
    if (
<<<<<<< /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/base/CodeGenerator.java/left.java
    model instanceof Optional<?>
=======
    model instanceof Optional
>>>>>>> /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/base/CodeGenerator.java/right.java
    ) {

<<<<<<< /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/base/CodeGenerator.java/left.java
      return ((Optional<?>) model).flatMap((m) -> codeOn(m).findAny().map((c) -> c.getText()));
=======
      final Optional result = (Optional<?>) model;
>>>>>>> /usr/src/app/output/pyknic/codegen/b718264121f2990b0cd2df982f93714ce9b2fe14/src/main/java/com/speedment/codegen/base/CodeGenerator.java/right.java

      if (result.isPresent()) {
        model = result.get();
      } else {
        return Optional.empty();
      }
    } else {
      return codeOn(model).findAny().map((c) -> c.getText());
    }
  }

  /**
     * Renders all the specified models into a stream of strings.
     *
     * @param <M>
     * @param models The models to generate.
     * @return A stream of code objects.
     */
  default <M extends java.lang.Object> Stream<String> onEach(Collection<M> models) {
    return codeOn(models).map((c) -> c.getText());
  }
}