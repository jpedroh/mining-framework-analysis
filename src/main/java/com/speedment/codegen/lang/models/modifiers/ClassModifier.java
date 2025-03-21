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
package com.speedment.codegen.lang.models.modifiers;

import com.speedment.codegen.lang.models.modifiers.Keyword.abstract_;
import com.speedment.codegen.lang.models.modifiers.Keyword.final_;
import com.speedment.codegen.lang.models.modifiers.Keyword.private_;
import com.speedment.codegen.lang.models.modifiers.Keyword.protected_;
import com.speedment.codegen.lang.models.modifiers.Keyword.public_;
import com.speedment.codegen.lang.models.modifiers.Keyword.static_;
import com.speedment.codegen.lang.models.modifiers.Keyword.strictfp_;

/**
 *
 * @author Emil Forslund
 * @param <T>
 */
public interface ClassModifier<T extends ClassModifier<T>> extends public_<T>, 
<<<<<<< /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ClassModifier.java/left.java
protected_<T>, private_<T>, abstract_<T>, static_<T>, final_<T>, strictfp_<T> {}
||||||| /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ClassModifier.java/base.java
protected_<T>, private_<T>, abstract_<T>, static_<T>, final_<T>, strictfp_<T> {
	default T public_() {
		getModifiers().add(PUBLIC);
		return (T) this;
	}
	
	default T protected_() {
		getModifiers().add(PROTECTED);
		return (T) this;
	}
	
	default T private_() {
		getModifiers().add(PRIVATE);
		return (T) this;
	}
	
	default T abstract_() {
		getModifiers().add(ABSTRACT);
		return (T) this;
	}
	
	default T static_() {
		getModifiers().add(STATIC);
		return (T) this;
	}
	
	default T final_() {
		getModifiers().add(FINAL);
		return (T) this;
	}
	
	default T strictfp_() {
		getModifiers().add(STRICTFP);
		return (T) this;
	}
}
=======
protected_<T>, private_<T>, abstract_<T>, static_<T>, final_<T>, strictfp_<T> {
	@SuppressWarnings("unchecked")
    default T public_() {
		getModifiers().add(PUBLIC);
		return (T) this;
	}
	
    @SuppressWarnings("unchecked")
	default T protected_() {
		getModifiers().add(PROTECTED);
		return (T) this;
	}
	
    @SuppressWarnings("unchecked")
	default T private_() {
		getModifiers().add(PRIVATE);
		return (T) this;
	}
	
    @SuppressWarnings("unchecked")
	default T abstract_() {
		getModifiers().add(ABSTRACT);
		return (T) this;
	}
	
    @SuppressWarnings("unchecked")
	default T static_() {
		getModifiers().add(STATIC);
		return (T) this;
	}
	
    @SuppressWarnings("unchecked")
	default T final_() {
		getModifiers().add(FINAL);
		return (T) this;
	}
	
    @SuppressWarnings("unchecked")
	default T strictfp_() {
		getModifiers().add(STRICTFP);
		return (T) this;
	}
}
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ClassModifier.java/right.java
