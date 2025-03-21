package com.speedment.codegen.lang.interfaces;
import com.speedment.codegen.lang.models.ClassOrInterface;
import java.util.Collection;
import java.util.List;

/**
 * A trait for models that contain {@link ClassOrInterface} components.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasClasses<T extends HasClasses<T>> {
  /**
     * Adds the specified {@link ClassOrInterface} to this model.
     * 
     * @param member  the new child
     * @return        a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(final ClassOrInterface<?> member) {
    getClasses().add(member.copy());
    return (T) this;
  }

  /**
     * Adds all the specified {@link ClassOrInterface} members to this model.
     * 
     * @param members  the new children
     * @return         a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T addAllClasses(final Collection<? extends ClassOrInterface<?>> members) {

<<<<<<< /usr/src/app/output/pyknic/codegen/3e0d4067f68ebf4163e850c7031571271b4893e0/src/main/java/com/speedment/codegen/lang/interfaces/HasClasses.java/left.java
    getClasses().addAll(members)
=======
    members.forEach(this::add)
>>>>>>> /usr/src/app/output/pyknic/codegen/3e0d4067f68ebf4163e850c7031571271b4893e0/src/main/java/com/speedment/codegen/lang/interfaces/HasClasses.java/right.java
    ;
    return (T) this;
  }

  /**
     * Returns a list of all the classes, interfaces and enumerations in this 
     * model.
     * <p>
     * The list returned must be mutable for changes!
     * 
     * @return all the classes
     */
  List<ClassOrInterface<?>> getClasses();
}