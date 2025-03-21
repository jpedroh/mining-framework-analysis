/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.php.symbols;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class MethodSymbolImpl extends FunctionSymbolIndex.FunctionSymbolImpl implements MethodSymbol {

  private final MethodSymbolData data;
  private final ClassSymbol owner;
  private Trilean isOverriding;

  public MethodSymbolImpl(MethodSymbolData data, ClassSymbol owner) {
    super(new FunctionSymbolData(data.location(), data.qualifiedName(), data.parameters(), data.properties()));
    this.data = data;
    this.owner = owner;
  }

  @Override
  public Visibility visibility() {
    return data.visibility();
  }

  @Override
  public String name() {
    return data.name();
  }

  @Override
  public Trilean isOverriding() {
    if (isOverriding == null) {
      isOverriding = computeIsOverriding();
    }
    return isOverriding;
  }

  private Trilean computeIsOverriding() {
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/2ebc3248b4cc888b29b2dc46de1ee82a9e5b281c/php-frontend/src/main/java/org/sonar/php/symbols/MethodSymbolImpl.java/left.java
    if (visibility().equals(Visibility.PRIVATE) || name().equals("__construct")) {
      return  Trilean.FALSE;
    }

    Deque<ClassSymbol> workList = new ArrayDeque<>();
    Set<ClassSymbol> visitedClasses = new HashSet<>();
    visitedClasses.add(owner);

    pushOnIsOverridingWorkList(owner, workList);

    boolean isUnknown = false;
    while (!workList.isEmpty()) {
      ClassSymbol visitedClass = workList.removeLast();
      if (!visitedClasses.add(visitedClass)) {
        continue;
||||||| /usr/src/app/output/sonarcommunity/sonar-php/2ebc3248b4cc888b29b2dc46de1ee82a9e5b281c/php-frontend/src/main/java/org/sonar/php/symbols/MethodSymbolImpl.java/base.java
    Optional<ClassSymbol> superClass = owner.superClass();
    while (superClass.isPresent()) {
      if (superClass.get().isUnknownSymbol()) {
        return Trilean.UNKNOWN;
=======
    if ("__construct".equals(name())) {
      return Trilean.FALSE;
    }
    Optional<ClassSymbol> superClass = owner.superClass();
    while (superClass.isPresent()) {
      if (superClass.get().isUnknownSymbol()) {
        return Trilean.UNKNOWN;
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/2ebc3248b4cc888b29b2dc46de1ee82a9e5b281c/php-frontend/src/main/java/org/sonar/php/symbols/MethodSymbolImpl.java/right.java
      }

      if (visitedClass.isUnknownSymbol()) {
        isUnknown = true;
        continue;
      }

      MethodSymbol methodSymbol = visitedClass.getDeclaredMethod(name());
      if (!methodSymbol.isUnknownSymbol() && !methodSymbol.visibility().equals(Visibility.PRIVATE)) {
        return Trilean.TRUE;
      }

      pushOnIsOverridingWorkList(visitedClass, workList);
    }

    if (isUnknown) {
      return Trilean.UNKNOWN;
    }
    return Trilean.FALSE;
  }

  /**
   * Push super classes and interfaces to the work list if they were not on the list.
   */
  private static void pushOnIsOverridingWorkList(ClassSymbol classSymbol, Deque<ClassSymbol> workList) {
    classSymbol.superClass().ifPresent(workList::add);
    workList.addAll(classSymbol.implementedInterfaces());
  }

}
