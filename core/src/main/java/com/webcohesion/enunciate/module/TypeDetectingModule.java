/**
 * Copyright © 2006-2016 Web Cohesion (info@webcohesion.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webcohesion.enunciate.module;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import java.util.stream.Stream;

/**
 * @author Ryan Heaton
 */
public interface TypeDetectingModule extends EnunciateModule {

  boolean internal(ClassFile classFile);

  boolean typeDetected(ClassFile classFile);
  
  default Stream<String> annotationNames(ClassFile classFile) {
    return classFile.getAttributes().stream().filter(ai -> ai instanceof AnnotationsAttribute)
       .flatMap(ai -> Stream.of(((AnnotationsAttribute) ai).getAnnotations()))
       .map(Annotation::getTypeName);
  }

}
