/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math4.ode.nonstiff;


import org.apache.commons.math4.Field;
import org.apache.commons.math4.RealFieldElement;
import org.apache.commons.math4.util.Decimal64Field;

public class MidpointFieldIntegratorTest extends RungeKuttaFieldIntegratorAbstractTest {

    protected <T extends RealFieldElement<T>> RungeKuttaFieldIntegrator<T>
    createIntegrator(Field<T> field, T step) {
        return new MidpointFieldIntegrator<T>(field, step);
    }

    @Override
    public void testNonFieldIntegratorConsistency() {
        doTestNonFieldIntegratorConsistency(Decimal64Field.getInstance());
    }

    @Override
    public void testMissedEndEvent() {
        doTestMissedEndEvent(Decimal64Field.getInstance(), 1.0e-15, 6.0e-5);
    }

    @Override
    public void testSanityChecks() {
        doTestSanityChecks(Decimal64Field.getInstance());
    }

    @Override
    public void testDecreasingSteps() {
        doTestDecreasingSteps(Decimal64Field.getInstance(), 1.0, 1.0, 1.0e-10);
    }

    @Override
    public void testSmallStep() {
        doTestSmallStep(Decimal64Field.getInstance(), 2.0e-7, 1.0e-6, 1.0e-12, "midpoint");
    }

    @Override
    public void testBigStep() {
        doTestBigStep(Decimal64Field.getInstance(), 0.01, 0.05, 1.0e-12, "midpoint");

    }

    @Override
    public void testBackward() {
        doTestBackward(Decimal64Field.getInstance(), 6.0e-4, 6.0e-4, 1.0e-12, "midpoint");
    }

    @Override
    public void testKepler() {
        doTestKepler(Decimal64Field.getInstance(), 1.19, 0.01);
    }

    @Override
    public void testStepSize() {
        doTestStepSize(Decimal64Field.getInstance(), 1.0e-12);
    }

    @Override
    public void testSingleStep() {
        doTestSingleStep(Decimal64Field.getInstance(), 0.21);
    }

    @Override
    public void testTooLargeFirstStep() {
        doTestTooLargeFirstStep(Decimal64Field.getInstance());
    }

    @Override
    public void testUnstableDerivative() {
        doTestUnstableDerivative(Decimal64Field.getInstance(), 1.0e-12);
    }

    @Override
    public void testDerivativesConsistency() {
        doTestDerivativesConsistency(Decimal64Field.getInstance(), 1.0e-10);
    }

    @Override
    public void testPartialDerivatives() {
        doTestPartialDerivatives(1.7e-4, new double[] { 1.0e-3, 2.8e-4, 3.8e-5, 2.8e-4, 2.8e-4 });
    }

}
