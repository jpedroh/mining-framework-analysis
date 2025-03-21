package com.speedment.codegen.lang.models;
import com.speedment.codegen.lang.interfaces.Copyable;
import com.speedment.codegen.lang.interfaces.HasType;
import com.speedment.codegen.lang.models.implementation.ImportImpl;
import com.speedment.codegen.lang.models.modifiers.ImportModifier;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A model that represents the explicit import of an dependency in code.
 * 
 * @author Emil Forslund
 */
public interface Import extends Copyable<Import>, HasType<Import>, ImportModifier<Import> {
  /**
     * Returns any static member referenced in this import. For non-static
     * imports, this value will be empty.
     * 
     * @return  the static member referenced
     */
  Optional<String> getStaticMember();

  /**
     * Sets the static member referenced by this import. Remember to also set
     * the modifier to static using the {@link #static_()} method!
     * 
     * @param member  the new static member to reference
     * @return        a reference to this model
     */
  Import setStaticMember(String member);

  enum Factory {
    INST
    ;

    private Supplier<Import> prototype = () -> new ImportImpl(null);
  }

  /**
     * Creates a new instance implementing this interface by using the class
     * supplied by the default factory. To change implementation, please use
     * the {@link #setSupplier(java.util.function.Supplier) setSupplier} method.
     * 
     * @param type  the type
     * @return      the new instance
     */
  static Import of(Type type) {
    return Factory.INST.prototype.get().set(type);
  }

  /**
     * Sets the instantiation method used to create new instances of this
     * interface.
     * 
     * @param supplier  the new constructor 
     */
  static void setSupplier(Supplier<Import> supplier) {
    Factory.INST.prototype = supplier;
  }
}