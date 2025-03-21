<<<<<<< /usr/src/app/output/technophobia/substeps-core/3b4badff3ac2ea23135ed5b9f3158e5a90bf1345/src/main/java/com/technophobia/substeps/runner/runtime/StepClassLocator.java/left.java
/*
 *     Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps.
 *
 *    Substeps is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
||||||| /usr/src/app/output/technophobia/substeps-core/3b4badff3ac2ea23135ed5b9f3158e5a90bf1345/src/main/java/com/technophobia/substeps/runner/runtime/StepClassLocator.java/base.java
=======
/*
 *	Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps.
 *
 *    Substeps is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
>>>>>>> /usr/src/app/output/technophobia/substeps-core/3b4badff3ac2ea23135ed5b9f3158e5a90bf1345/src/main/java/com/technophobia/substeps/runner/runtime/StepClassLocator.java/right.java
package com.technophobia.substeps.runner.runtime;

public class StepClassLocator extends PredicatedClassLocator {

    public StepClassLocator(final String path) {
        super(new StepClassFilter(), new ClassLoadingFunction(path));
    }


    public StepClassLocator(final String path, final ClassLoader classLoader) {
        super(new StepClassFilter(), new ClassLoadingFunction(classLoader, path));
    }
}
