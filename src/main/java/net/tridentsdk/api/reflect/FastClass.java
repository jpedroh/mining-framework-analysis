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

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.reflect.Field;

public class FastClass {
    private final Class<?> cls;

    private final FieldAccess fieldAccess;
    private final MethodAccess methodAccess;
    private final ConstructorAccess constructorAccess;

    private FastClass(Class<?> cls) {
        this.cls = cls;
        this.fieldAccess = FieldAccess.get(cls);
        this.methodAccess = MethodAccess.get(cls);
        this.constructorAccess = ConstructorAccess.get(cls);
    }

    public static FastClass get(Class<?> cls) {
        return new FastClass(cls);
    }

    public static FastClass get(Object obj) {
        return get(obj.getClass());
    }

    /**
     * Get a field from the class
     *
     * @param name     Name of the field
     * @return FastField instance
     */
    public FastField getField(String name) {
        return new FastField(this, this.fieldAccess, name);
    }

    /**
     * Get a method from the class
     *
     * @param name     Name of the method
     * @return FastMethod instance
     */
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastClass.java/left.java
    public FastMethod getMethod(Object object,String name) {
||||||| /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastClass.java/base.java
    public FastMethod getMethod(String name) {
=======
    public FastMethod getMethod(Object o,String name) {
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastClass.java/right.java
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastClass.java/left.java
        return new FastMethod(object,this.methodAccess, name);
||||||| /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastClass.java/base.java
        return new FastMethod(this.methodAccess, name);
=======
        return new FastMethod(o,this.methodAccess, name);
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastClass.java/right.java
    }

    public FastField[] getFields() {
        Field[] fields = this.cls.getDeclaredFields();
        FastField[] fastFields = new FastField[fields.length];

        for (int i = 0; i < fields.length; i += 1) {
            fastFields[i] = new FastField(this, this.fieldAccess, fields[i].getName());
        }

        return fastFields;
    }

    /**
     * Get the default constructor found
     *
     * @return the default FastConstructor
     */
    public FastConstructor getConstructor() {
        return new FastConstructor(this.constructorAccess);
    }

    public Class<?> toClass() {
        return this.cls;
    }
}
