package com.speedment.codegen.lang.models.values;
import com.speedment.codegen.lang.models.Value;
import com.speedment.codegen.lang.models.implementation.ValueImpl;
import com.speedment.codegen.util.Copier;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Emil Forslund
 */
public class ArrayValue extends ValueImpl<List<Value<?>>> {
  public ArrayValue() {
    super(new ArrayList<>());
  }

  public ArrayValue(List<Value<?>> val) {
    super(val);
  }

  @Override public ArrayValue copy() {
    return new ArrayValue(Copier.copy(getValue(), (s) -> s.copy()));
  }
}