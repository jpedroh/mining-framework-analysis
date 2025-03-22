/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.api.reflect;

import com.esotericsoftware.reflectasm.MethodAccess;
import net.tridentsdk.api.docs.InternalUseOnly;

/**
 * Wrapper for the provided ReflectASM method library
 *
 * @author The TridentSDK Team
 */
public class FastMethod {
    private final MethodAccess access;
    private final String name;
    private final Object instance;

    @InternalUseOnly
    public FastMethod(Object instance, MethodAccess access, String name) {
        this.access = access;
        this.name = name;
        this.instance = instance;
    }

<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastMethod.java/left.java
    public Object invoke(Object instance, Object... args) {
        return this.access.invoke(instance, this.name, args);
    }

    public Object invoke(Object instance) {
        return this.access.invoke(instance, this.name);
||||||| /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastMethod.java/base.java
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public Object invoke(Object instance, Object... args) {
        return this.access.invoke(instance, this.name, args);
    }

    public Object invoke(Object instance) {
        return this.access.invoke(instance, this.name);
=======
=======
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastMethod.java/right.java
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    /**
     * Invokes the method with parameters
     *
     * @param args the method parameters
     * @return the return value after calling the method
     */
    public Object invoke(Object... args) {
        return this.access.invoke(this.instance, this.name, args);
    }

    /**
     * Invokes a no-arg method
     *
     * @return the return value after calling the method
     */
    public Object invoke() {
        return this.access.invoke(this.instance, this.name);
>>>>>>> Stashed changes
    }

    /**
     * Gets the object instance used by this class
     *
     * @return the instance for method invocation
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Gets the object instance used by this class
     *
     * @return the instance for method invocation
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Gets the object instance used by this class
     *
     * @return the instance for method invocation
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Gets the object instance used by this class
     *
     * @return the instance for method invocation
     */
    public Object getInstance() {
        return instance;
=======
    /**
     * Invokes the method with parameters
     *
     * @param args the method parameters
     * @return the return value after calling the method
     */
    public Object invoke(Object... args) {
        return this.access.invoke(this.instance, this.name, args);
    }

    /**
     * Invokes a no-arg method
     *
     * @return the return value after calling the method
     */
    public Object invoke() {
        return this.access.invoke(this.instance, this.name);
>>>>>>> theirs
    }

    /**
     * Gets the object instance used by this class
     *
     * @return the instance for method invocation
     */
    public Object getInstance() {
        return instance;
    }
}
