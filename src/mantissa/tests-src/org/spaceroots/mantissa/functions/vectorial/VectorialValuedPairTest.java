// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.functions.vectorial;

import junit.framework.*;

public class VectorialValuedPairTest
  extends TestCase {

  public VectorialValuedPairTest(String name) {
    super(name);
  }

  public void testConstructor() {
    double[] tab = new double[2];
    tab[0] = -8.4;
    tab[1] = -3.2;
    VectorialValuedPair pair = new VectorialValuedPair(1.2, tab);
    assertTrue(Math.abs(pair.getX()    - 1.2) < 1.0e-10);
    assertTrue(Math.abs(pair.getY()[0] + 8.4) < 1.0e-10);
    assertTrue(Math.abs(pair.getY()[1] + 3.2) < 1.0e-10);
  }

  public void testCopyConstructor() {
    double[] tab = new double[2];
    tab[0] = -8.4;
    tab[1] = -3.2;
    VectorialValuedPair pair1 = new VectorialValuedPair(1.2, tab);
    VectorialValuedPair pair2 = new VectorialValuedPair(pair1);
    assertTrue(Math.abs(pair2.getX()    - pair1.getX())    < 1.0e-10);
    assertTrue(Math.abs(pair2.getY()[0] - pair1.getY()[0]) < 1.0e-10);
    assertTrue(Math.abs(pair2.getY()[1] - pair1.getY()[1]) < 1.0e-10);
    assertTrue(Math.abs(pair2.getX()    - 1.2)        < 1.0e-10);
    assertTrue(Math.abs(pair2.getY()[0] + 8.4)        < 1.0e-10);
    assertTrue(Math.abs(pair2.getY()[1] + 3.2)        < 1.0e-10);
  }

  public static Test suite() {
    return new TestSuite(VectorialValuedPairTest.class);
  }

}
