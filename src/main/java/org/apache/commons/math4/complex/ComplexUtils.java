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

package org.apache.commons.math4.complex;

import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.IntegerSequence;

/**
 * Static implementations of common
 * {@link org.apache.commons.math3.complex.Complex} utilities functions.
 *
 */
public class ComplexUtils {

    /**
     * Default constructor.
     */
    private ComplexUtils() {}

    /**
     * Creates a complex number from the given polar representation.
     * <p>
     * The value returned is <code>r&middot;e<sup>i&middot;theta</sup></code>,
     * computed as <code>r&middot;cos(theta) + r&middot;sin(theta)i</code></p>
     * <p>
     * If either <code>r</code> or <code>theta</code> is NaN, or
     * <code>theta</code> is infinite, {@link Complex#NaN} is returned.</p>
     * <p>
     * If <code>r</code> is infinite and <code>theta</code> is finite,
     * infinite or NaN values may be returned in parts of the result, following
     * the rules for double arithmetic.<pre>
     * Examples:
     * <code>
     * polar2Complex(INFINITY, &pi;/4) = INFINITY + INFINITY i
     * polar2Complex(INFINITY, 0) = INFINITY + NaN i
     * polar2Complex(INFINITY, -&pi;/4) = INFINITY - INFINITY i
     * polar2Complex(INFINITY, 5&pi;/4) = -INFINITY - INFINITY i </code></pre></p>
     *
     * @param r the modulus of the complex number to create
     * @param theta  the argument of the complex number to create
     * @return <code>r&middot;e<sup>i&middot;theta</sup></code>
     * @throws MathIllegalArgumentException if {@code r} is negative.
     * @since 1.1
     */
    public static Complex polar2Complex(double r, double theta) throws MathIllegalArgumentException {
        if (r < 0) {
            throw new MathIllegalArgumentException(
                  LocalizedFormats.NEGATIVE_COMPLEX_MODULE, r);
        }
        return new Complex(r * FastMath.cos(theta), r * FastMath.sin(theta));
    }

    /**
     * Returns double from array {@code real[]} at entry {@code index} as a {@code Complex}.
     *
     * @param real Array of real numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromRealArray(double[] real, int index) {
        return new Complex(real[index]);
    }

	/**
     * Returns float from array {@code real[]} at entry {@code index} as a {@code Complex}.
     *
     * @param real Array of real numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromRealArray(float[] real, int index) {
        return new Complex(real[index]);
    }

    /**
     * Returns double from array {@code imaginary[]} at entry {@code index} as a {@code Complex}.
     *
     * @param imaginary Array of imaginary numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromImaginaryArray(double[] imaginary, int index) {
        return new Complex(0, imaginary[index]);
    }

	/**
     * Returns float from array {@code imaginary[]} at entry {@code index} as a {@code Complex}.
     *
     * @param imaginary Array of imaginary numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromImaginaryArray(float[] imaginary, int index) {
        return new Complex(0, imaginary[index]);
    }

	/**
     * Returns real component of Complex from array {@code complex[]} 
	 * at entry {@code index} as a {@code double}.
     *
     * @param complex Array of complex numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static double extractRealFromComplexArray(Complex[] complex, int index) {
        return complex[index].getReal();
    }

	/**
     * Returns real component of array {@code complex[]} at entry {@code index} as a {@code float}.
     *
     * @param complex Array of complex numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static float extractRealFloatFromComplexArray(Complex[] complex, int index) {
        return (float)complex[index].getReal();
    }

	/**
     * Returns imaginary component of Complex from array {@code complex[]} 
	 * at entry {@code index} as a {@code double}.
     *
     * @param complex Array of complex numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static double extractImaginaryFromComplexArray(Complex[] complex, int index) {
        return complex[index].getImaginary();
    }

	/**
     * Returns imaginary component of array {@code complex[]} at entry {@code index} as a {@code float}.
     *
     * @param complex Array of complex numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static float extractImaginaryFloatFromComplexArray(Complex[] complex, int index) {
        return (float)complex[index].getImaginary();
    }
	
    /**
     * Returns Complex object from interleaved {@code double[]} array {@code Complex[]} at entry {@code index}.
     *
     * @param d {@code double[]} of interleaved complex numbers alternating real and imaginary values
	 * @param index Location in the array. This is the location by complex number, e.g.
	 * index number 5 in the {@code double[]} array will return a {@code new Complex(d[10], d[11])}
     * @return size 2 {@code double[]} array.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromInterleavedArray(double[] d, int index) {
        return new Complex(d[index*2], d[index*2+1]);
    }
    
    /**
     * Returns Complex object from interleaved {@code float[]} array {@code Complex[]} at entry {@code index}.
     *
     * @param f {@code float[]} of interleaved complex numbers alternating real and imaginary values
	 * @param index Location in the array. This is the location by complex number, e.g.
	 * index number 5 in the {@code float[]} array will return a new {@code Complex(d[10], d[11])}
     * @return size 2 {@code float[]} array.
     *
     * @since 4.0
     */
    public static Complex extractComplexFromInterleavedArray(float[] f, int index) {
        return new Complex(f[index*2], f[index*2+1]);
    }
        
    /**
     * Returns values of Complex object from array {@code Complex[]} at entry {@code index} as a
	 * size 2 {@code double} of the form {real, imag}.
     *
     * @param complex Array of complex numbers.
	 * @param index Location in the array.
     * @return size 2 {@code double[]} array.
     *
     * @since 4.0
     */
    public static double[] extractInterleavedFromComplexArray(Complex[] complex, int index) {
        return new double[]{complex[index].getReal(), complex[index].getImaginary()};
    }

	/**
     * Returns Complex object from array {@code Complex[]} at entry {@code index} as a
	 * size 2 {@code float} of the form {real, imag}.
     *
     * @param complex Array of complex numbers.
	 * @param index Location in the array.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static float[] extractInterleavedFloatFromComplexArray(Complex[] complex, int index) {
        return new float[]{(float)complex[index].getReal(), (float)complex[index].getImaginary()};
    }
    
    /**
     * Converts a {@code double[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end}.
     *
     * @param real Array of real numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }
    
    /**
     * Converts a {@code float[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end}.
     *
     * @param real Array of real numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a {@code float[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real, int start, int end, int increment) {
    	Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }
  
	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects
     * for the {@code IntegerSequence} range.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real, Iterable<Integer> range) {
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a {@code float[]} array to an array of {@code Complex} objects
     * for the {@code IntegerSequence} range.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real, Iterable<Integer> range) {
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }
    
	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(double[] real) {
		int index = 0;
        final Complex c[] = new Complex[real.length];
        for (double d : real) {
            c[index] = new Complex(d);
            index++;
        }
        return c;
    }
 
    /**
     * Converts a {@code float[]} array to an array of {@code Complex} objects.
     *
     * @param real Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] real2Complex(float[] real) {
		int index = 0;
        final Complex c[] = new Complex[real.length];
        for (float d : real) {
            c[index] = new Complex(d);
            index++;
        }
        return c;
    }

	/**
     * Converts a 2D real array {@code double[][]} 
     * to a 2d array of {@code Complex} objects.
     *
     * @param d 2d {@code double[][]}
     * @return 2d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][] real2Complex(double[][] d) {
		int width = d.length;
		int height = d[0].length;
		Complex[][] c = new Complex[width][height];
		for (int n = 0; n < width; n++) {
			c[n] = ComplexUtils.real2Complex(d[n]);
		}
		return c;
	}
	
	/**
     * Converts a 2D real array {@code double[][][]} 
     * to an array of {@code Complex} objects.
     *
     * @param d 3d complex interleaved {@code double[][][]}
     * @return 3d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][][] real2Complex(double[][][] d) {
		int width = d.length;
		int height = d[0].length;
		int depth = d[0].length;
		Complex[][][] c = new Complex[width][height][depth];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				c[x][y] = ComplexUtils.real2Complex(d[x][y]);
			}
		}
		return c;
	}
    
    /**
     * Converts an array of {@code Complex} objects to a {@code double[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param c Array of {@code Complex} objects.
     * @param start Start index.
     * @param end End index.
     * @return a {@code double[]} array of the real component.
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)];
        for (Integer i : range) {
            d[index] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }
    
    /**
     * Converts an array of {@code Complex} objects to a {@code float[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param c Array of Complex objects
     * @param start Start index.
     * @param end End index.
     * @return a {@code float[]} array of the real component.
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)];
        for (Integer i : range) {
            f[index] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

	/**
     * Converts an array of {@code Complex} objects to a {@code double[]} array 
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c Array of {@code Complex} objects.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return a {@code double[]} array of the real component.
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)];
        for (Integer i : range) {
            d[index] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

	/**
     * Converts an array of {@code Complex} objects to a {@code float[]} array 
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c Array of {@code Complex} objects.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return a {@code float[]} array of the real component.
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)];
        for (Integer i : range) {
            f[index] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }
  
	/**
     * Converts an array of {@code Complex} objects to a {@code double[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param c Array of {@code Complex} objects.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return a {@code double[]} array of the real component.
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c, Iterable<Integer> range) {
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)];
        for (Integer i : range) {
            d[index] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    /**
     * Converts an array of {@code Complex} objects to a {@code float[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param c Array of {@code Complex} objects.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return a {@code float[]} array of the real component.
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c, Iterable<Integer> range) {
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)];
        for (Integer i : range) {
            f[index] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }
    
	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects.
     *
     * @param c Array of {@code Complex} objects.
     * @return a {@code double[]} array of the real component.
     *
     * @since 4.0
     */
    public static double[] complex2Real(Complex[] c) {
		int index = 0;
        final double d[] = new double[c.length];
        for (Complex cc : c) {
            d[index] = cc.getReal();
            index++;
        }
        return d;
    }
 
    /**
     * Converts a {@code float[]} array to an array of {@code Complex} objects.
     *
     * @param c Array of {@code Complex} objects.
     * @return a {@code float[]} array of the real component.
     *
     * @since 4.0
     */
    public static float[] complex2RealFloat(Complex[] c) {
		int index = 0;
        final float f[] = new float[c.length];
        for (Complex cc : c) {
            f[index] = (float)cc.getReal();
            index++;
        }
        return f;
    }
    
	/**
     * Converts real component a 2D array of {@code Complex} objects 
     * to a 2d double array. 
     *
     * @param c 2d array of {@code Complex} objects
     * @return double[][] of real component 
     * @since 4.0
     */
	public static double[][] complex2Real(Complex[][] c) {
		int width = c.length;
		int height = c[0].length;
		double[][] d = new double[width][height];
		for (int n = 0; n < width; n++) {
			d[n] = ComplexUtils.complex2Real(c[n]);
		}
		return d;
	}
	
	/**
     * Converts a 3D interleaved complex array {@code double[][][]} 
     * to an array of {@code Complex} objects. Interleaving is in final level of array,
     * i.e. for double [x][y][z] z is interleaved. 
     *
     * @param d 3d complex interleaved {@code double[][][]}
     * @return 3d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static double[][][] complex2Real(Complex[][][] c) {
		int width = c.length;
		int height = c[0].length;
		int depth = c[0].length;
		double[][][] d = new double[width][height][depth];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				d[x][y] = ComplexUtils.complex2Real(c[x][y]);
			}
		}
		return d;
	}

    /**
     * Converts a {@code double[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end}.
     *
     * @param imaginary Array of imaginary numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(double[] imaginary, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }
    
    /**
     * Converts a {@code float[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end}.
     *
     * @param imaginary Array of imaginary numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(float[] imaginary, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param imaginary Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(double[] imaginary, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a {@code float[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param imaginary Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(float[] imaginary, int start, int end, int increment) {
    	Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }
  
	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects
     * for the {@code IntegerSequence} range.
     *
     * @param imaginary Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(double[] imaginary, Iterable<Integer> range) {
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a {@code float[]} array to an array of {@code Complex} objects
     * for the {@code IntegerSequence} range.
     *
     * @param imaginary Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(float[] imaginary, Iterable<Integer> range) {
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }
    
	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects.
     *
     * @param imaginary Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(double[] imaginary) {
		int index = 0;
        final Complex c[] = new Complex[imaginary.length];
        for (double d : imaginary) {
            c[index] = new Complex(0, d);
            index++;
        }
        return c;
    }
 
    /**
     * Converts a {@code float[]} array to an array of {@code Complex} objects.
     *
     * @param imaginary Array of numbers to be converted to their {@code Complex}
     * equivalent.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] imaginary2Complex(float[] imaginary) {
		int index = 0;
        final Complex c[] = new Complex[imaginary.length];
        for (float d : imaginary) {
            c[index] = new Complex(0, d);
            index++;
        }
        return c;
    }

    /**
     * Converts a 2D imaginary array {@code double[][]} 
     * to a 2d array of {@code Complex} objects.
     *
     * @param d 2d {@code double[][]}
     * @return 2d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][] imaginary2Complex(double[][] d) {
		int width = d.length;
		int height = d[0].length;
		Complex[][] c = new Complex[width][height];
		for (int n = 0; n < width; n++) {
			c[n] = ComplexUtils.imaginary2Complex(d[n]);
		}
		return c;
	}
	
	/**
     * Converts a 3D imaginary array {@code double[][][]} 
     * to an array of {@code Complex} objects.
     *
     * @param d 3d complex imaginary {@code double[][][]}
     * @return 3d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][][] imaginary2Complex(double[][][] d) {
		int width = d.length;
		int height = d[0].length;
		int depth = d[0].length;
		Complex[][][] c = new Complex[width][height][depth];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				c[x][y] = ComplexUtils.imaginary2Complex(d[x][y]);
			}
		}
		return c;
	}
    
    /**
     * Converts an array of {@code Complex} objects to a {@code double[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param c Array of {@code Complex} objects.
     * @param start Start index.
     * @param end End index.
     * @return a {@code double[]} array of the imaginary component.
     *
     * @since 4.0
     */
    public static double[] complex2Imaginary(Complex[] c, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)];
        for (Integer i : range) {
            d[index] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }
    
    /**
     * Converts an array of {@code Complex} objects to a {@code float[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param c Array of Complex objects
     * @param start Start index.
     * @param end End index.
     * @return a {@code float[]} array of the imaginary component.
     *
     * @since 4.0
     */
    public static float[] complex2ImaginaryFloat(Complex[] c, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)];
        for (Integer i : range) {
            f[index] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

	/**
     * Converts an array of {@code Complex} objects to a {@code double[]} array 
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c Array of {@code Complex} objects.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return a {@code double[]} array of the imaginary component.
     *
     * @since 4.0
     */
    public static double[] complex2Imaginary(Complex[] c, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)];
        for (Integer i : range) {
            d[index] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

	/**
     * Converts an array of {@code Complex} objects to a {@code float[]} array 
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c Array of {@code Complex} objects.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return a {@code float[]} array of the imaginary component.
     *
     * @since 4.0
     */
    public static float[] complex2ImaginaryFloat(Complex[] c, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)];
        for (Integer i : range) {
            f[index] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }
  
	/**
     * Converts an array of {@code Complex} objects to a {@code double[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param c Array of {@code Complex} objects.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return a {@code double[]} array of the imaginary component.
     *
     * @since 4.0
     */
    public static double[] complex2Imaginary(Complex[] c, Iterable<Integer> range) {
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)];
        for (Integer i : range) {
            d[index] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    /**
     * Converts an array of {@code Complex} objects to a {@code float[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param c Array of {@code Complex} objects.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return a {@code float[]} array of the imaginary component.
     *
     * @since 4.0
     */
    public static float[] complex2ImaginaryFloat(Complex[] c, Iterable<Integer> range) {
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)];
        for (Integer i : range) {
            f[index] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }
    
	/**
     * Converts a {@code double[]} array to an array of {@code Complex} objects.
     *
     * @param c Array of {@code Complex} objects.
     * @return a {@code double[]} array of the imaginary component.
     *
     * @since 4.0
     */
    public static double[] complex2Imaginary(Complex[] c) {
		int index = 0;
        final double d[] = new double[c.length];
        for (Complex cc : c) {
            d[index] = cc.getImaginary();
            index++;
        }
        return d;
    }
 
    /**
     * Converts a {@code float[]} array to an array of {@code Complex} objects.
     *
     * @param c Array of {@code Complex} objects.
     * @return a {@code float[]} array of the imaginary component.
     *
     * @since 4.0
     */
    public static float[] complex2ImaginaryFloat(Complex[] c) {
		int index = 0;
        final float f[] = new float[c.length];
        for (Complex cc : c) {
            f[index] = (float)cc.getImaginary();
            index++;
        }
        return f;
    }
    
	/**
     * Converts imaginary component a 2D array of {@code Complex} objects 
     * to a 2d double array. 
     *
     * @param c 2d array of {@code Complex} objects
     * @return double[][] of imaginary component 
     * @since 4.0
     */
	public static double[][] complex2Imaginary(Complex[][] c) {
		int width = c.length;
		int height = c[0].length;
		double[][] d = new double[width][height];
		for (int n = 0; n < width; n++) {
			d[n] = ComplexUtils.complex2Imaginary(c[n]);
		}
		return d;
	}
	
	/**
     * Converts a 3D interleaved complex array {@code double[][][]} 
     * to an array of {@code Complex} objects. Interleaving is in final level of array,
     * i.e. for double [x][y][z] z is interleaved. 
     *
     * @param d 3d complex interleaved {@code double[][][]}
     * @return 3d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static double[][][] complex2Imaginary(Complex[][][] c) {
		int width = c.length;
		int height = c[0].length;
		int depth = c[0].length;
		double[][][] d = new double[width][height][depth];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				d[x][y] = ComplexUtils.complex2Imaginary(c[x][y]);
			}
		}
		return d;
	}

    // INTERLEAVED METHODS
        
    /**
     * Converts a complex interleaved {@code double[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end}.
     *
     * @param interleaved {@code double[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }
    
    /**
     * Converts a complex interleaved {@code float[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end}.
     *
     * @param interleaved {@code float[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a complex interleaved {@code double[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param interleaved {@code double[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a complex interleaved {@code float[]} array to an array of {@code Complex} objects
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param interleaved {@code float[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved, int start, int end, int increment) {
    	Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }
  
	/**
     * Converts a complex interleaved {@code double[]} array to an array of {@code Complex} objects
     * for the {@code IntegerSequence} range.
     *
     * @param interleaved {@code double[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved, Iterable<Integer> range) {
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

	/**
     * Converts a complex interleaved {@code float[]} array to an array of {@code Complex} objects
     * for the {@code IntegerSequence} range.
     *
     * @param interleaved {@code float[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved, Iterable<Integer> range) {
		int index = 0;
        final Complex c[] = new Complex[IntegerSequence.size(range)];
        for (Integer i : range) {
            c[index] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }
    
	/**
     * Converts a complex interleaved {@code double[]} array to an array of {@code Complex} objects
     *
     * @param interleaved {@code double[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(double[] interleaved) {
		final int length = interleaved.length/2;
        final Complex c[] = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = new Complex(interleaved[n*2], interleaved[n*2+1]);
        }
        return c;
    }
 
    /**
     * Converts a complex interleaved {@code float[]} array to an array of {@code Complex} objects
     *
     * @param interleaved {@code float[]} of numbers to be converted to their {@code Complex}
     * equivalent.
     * @return an array of {@code Complex} objects.
     *
     * @since 4.0
     */
    public static Complex[] interleaved2Complex(float[] interleaved) {
		final int length = interleaved.length/2;
        final Complex c[] = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[n] = new Complex(interleaved[n*2], interleaved[n*2+1]);
        }
        return c;
    }

    /**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code double[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param c Array of Complex objects.
     * @param start Start index.
     * @param end End index.
     * @return a complex interleaved {@code double[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            d[real] = c[i].getReal();
            d[imag] = c[i].getImaginary();
            index++;
        }
        return d;
    }
    
    /**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code float[]} array
     * for the range {@code start} - {@code end}.
     *
     * @param c Array of Complex objects.
     * @param start Start index.
     * @param end End index.
     * @return a complex interleaved {@code float[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c, int start, int end) {
		Iterable<Integer> range = IntegerSequence.range(start, end);
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            f[real] = (float)c[i].getReal();
            f[imag] = (float)c[i].getImaginary();
            index++;
        }
        return f;
    }
    
	/**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code double[]} array
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c Array of Complex objects.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return a complex interleaved {@code double[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            d[real] = c[i].getReal();
            d[imag] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    /**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code float[]} array
     * for the range {@code start} - {@code end} by {@code increment}.
     *
     * @param c Array of Complex objects.
     * @param start Start index.
     * @param end End index.
     * @param increment Range increment.
     * @return a complex interleaved {@code float[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c, int start, int end, int increment) {
		Iterable<Integer> range = IntegerSequence.range(start, end, increment);
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            f[real] = (float)c[i].getReal();
            f[imag] = (float)c[i].getImaginary();
            index++;
        }
        return f;
    }
  
	/**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code double[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param c Array of Complex objects.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return a complex interleaved {@code double[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c, Iterable<Integer> range) {
		int index = 0;
        final double d[] = new double[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            d[real] = c[i].getReal();
            d[imag] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    /**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code float[]} array
     * for the {@code IntegerSequence} range.
     *
     * @param c Array of Complex objects.
     * @param range an {@code Iterable<Integer>} object returned by {@code IntegerSequence.range()} 
     * @return a complex interleaved {@code float[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c, Iterable<Integer> range) {
		int index = 0;
        final float f[] = new float[IntegerSequence.size(range)*2];
        for (Integer i : range) {
        	int real = index*2;
        	int imag = index*2+1;
            f[real] = (float)c[i].getReal();
            f[imag] = (float)c[i].getImaginary();
            index++;
        }
        return f;
    }
    
	/**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code double[]} array
     *
     * @param c Array of Complex objects.
     * @return a complex interleaved {@code double[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static double[] complex2Interleaved(Complex[] c) {
		int index = 0;
        final double d[] = new double[c.length*2];
        for (Complex cc : c) {
        	int real = index*2;
        	int imag = index*2+1;
            d[real] = cc.getReal();
            d[imag] = cc.getImaginary();
            index++;
        }
        return d;
    }

    /**
     * Converts an array of {@code Complex} objects to an interleaved complex {@code float[]} array
     *
     * @param c Array of Complex objects.
     * @return a complex interleaved {@code float[]} alternating real and imaginary values.
     *
     * @since 4.0
     */
    public static float[] complex2InterleavedFloat(Complex[] c) {
		int index = 0;
        final float f[] = new float[c.length*2];
        for (Complex cc : c) {
        	int real = index*2;
        	int imag = index*2+1;
            f[real] = (float)cc.getReal();
            f[imag] = (float)cc.getImaginary();
            index++;
        }
        return f;
    }
    
    /**
     * Converts a 2D array of {@code Complex} objects
     * to an interleaved complex {@code double[][]} array. 
     * 
     * @param c 2D Array of Complex objects.
     * @param interleavedDim Depth level of the array to interleave.
     * @return a complex interleaved {@code double[][]} alternating real and imaginary values.
     *
     * @since 4.0
     */
	public static double[][] complex2Interleaved(Complex[][] c, int interleavedDim) {
		if (interleavedDim > 1 || interleavedDim < 0) {
            throw new OutOfRangeException(
                  interleavedDim, 0, 1);
        }
		final int width = c.length;
		final int height = c[0].length;
		double[][] d; 
		if (interleavedDim == 0) {
			d = new double[2*width][height];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					d[x*2][y] = c[x][y].getReal();
					d[x*2+1][y] = c[x][y].getImaginary();
				}
			}
		} else {
			d = new double[width][2*height];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					d[x][y*2] = c[x][y].getReal();
					d[x][y*2+1] = c[x][y].getImaginary();
				}
			}
		}
		return d;
	}
	 
    /**
     * Converts a 2D array of {@code Complex} objects
     * to an interleaved complex {@code double[][]} array.
     * The first depth level of the array is interleaved. 
     *
     * @param c 2D Array of Complex objects.
     * @return a complex interleaved {@code double[][]} alternating real and imaginary values.
     *
     * @since 4.0
     */
	public static double[][] complex2Interleaved(Complex[][] c) {
		return complex2Interleaved(c, 1);
	}
	
	/**
     * Converts a 3D array of {@code Complex} objects
     * to an interleaved complex {@code double[][][]} array.
     *
     * @param c 3D Array of Complex objects.
     * @param interleavedDim Depth level of the array to interleave.
     * @return a complex interleaved {@code double[][][]} alternating real and imaginary values.
     *
     * @since 4.0
     */
	public static double[][][] complex2Interleaved(Complex[][][] c, int interleavedDim) {
		if (interleavedDim > 2 || interleavedDim < 0) {
            throw new OutOfRangeException(
                  interleavedDim, 0, 2);
        }
		int width = c.length;
		int height = c[0].length;
		int depth = c[0].length;
		double[][][] d;
		if (interleavedDim == 0) {
			d = new double[2*width][height][depth];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						d[x*2][y][z] = c[x][y][z].getReal();
						d[x*2+1][y][z] = c[x][y][z].getImaginary();
					}
				}
			}
		} else if (interleavedDim == 1) {
			d = new double[width][2*height][depth];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						d[x][y][z] = c[x][y][z].getReal();
						d[x][y*2][z] = c[x][y][z].getImaginary();
					}
				}
			}
		} else {
			d = new double[width][height][2*depth];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						d[x][y][z*2] = c[x][y][z].getReal();
						d[x][y][z*2+1] = c[x][y][z].getImaginary();
					}
				}
			}
		}
		return d;
	}

	/**
     * Converts a 3D array of {@code Complex} objects
     * to an interleaved complex {@code double[][]} array.
     * The first depth level of the array is interleaved. 
     *
     * @param c 3D Array of Complex objects.
     * @return a complex interleaved {@code double[][]} alternating real and imaginary values.
     *
     * @since 4.0
     */
	public static double[][][] complex2Interleaved(Complex[][][] c) {
		return complex2Interleaved(c, 2);
	}
	
    /**
     * Converts a 2D array of {@code Complex} objects
     * to an interleaved complex {@code float[][]} array. 
     * 
     * @param c 2D Array of Complex objects.
     * @param interleavedDim Depth level of the array to interleave.
     * @return a complex interleaved {@code float[][]} alternating real and imaginary values.
     *
     * @since 4.0
     */
	public static float[][] complex2InterleavedFloat(Complex[][] c, int interleavedDim) {
		if (interleavedDim > 1 || interleavedDim < 0) {
            throw new OutOfRangeException(
                  interleavedDim, 0, 1);
        }
		final int width = c.length;
		final int height = c[0].length;
		float[][] d; 
		if (interleavedDim == 0) {
			d = new float[2*width][height];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					d[x*2][y] = (float)c[x][y].getReal();
					d[x*2+1][y] = (float)c[x][y].getImaginary();
				}
			}
		} else {
			d = new float[width][2*height];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					d[x][y*2] = (float)c[x][y].getReal();
					d[x][y*2+1] = (float)c[x][y].getImaginary();
				}
			}
		}
		return d;
	}
	 
    /**
     * Converts a 2D array of {@code Complex} objects
     * to an interleaved complex {@code float[][]} array.
     * The first depth level of the array is interleaved. 
     *
     * @param c 2D Array of Complex objects.
     * 
     * @return a complex interleaved {@code float[][]} alternating real and imaginary values.
     *
     * @since 4.0
     */
	public static float[][] complex2InterleavedFloat(Complex[][] c) {
		return complex2InterleavedFloat(c, 1);
	}
	
	/**
     * Converts a 3D array of {@code Complex} objects
     * to an interleaved complex {@code float[][][]} array.
     *
     * @param c 3D Array of Complex objects.
     * @param interleavedDim Depth level of the array to interleave.
     * @return a complex interleaved {@code float[][][]} alternating real and imaginary values.
     *
     * @since 4.0
     */
	public static float[][][] complex2InterleavedFloat(Complex[][][] c, int interleavedDim) {
		if (interleavedDim > 2 || interleavedDim < 0) {
            throw new OutOfRangeException(
                  interleavedDim, 0, 2);
        }
		int width = c.length;
		int height = c[0].length;
		int depth = c[0].length;
		float[][][] d;
		if (interleavedDim == 0) {
			d = new float[2*width][height][depth];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						d[x*2][y][z] = (float)c[x][y][z].getReal();
						d[x*2+1][y][z] = (float)c[x][y][z].getImaginary();
					}
				}
			}
		} else if (interleavedDim == 1) {
			d = new float[width][2*height][depth];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						d[x][y][z] = (float)c[x][y][z].getReal();
						d[x][y*2][z] = (float)c[x][y][z].getImaginary();
					}
				}
			}
		} else {
			d = new float[width][height][2*depth];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						d[x][y][z*2] = (float)c[x][y][z].getReal();
						d[x][y][z*2+1] = (float)c[x][y][z].getImaginary();
					}
				}
			}
		}
		return d;
	}

	/**
     * Converts a 3D array of {@code Complex} objects
     * to an interleaved complex {@code float[][]} array.
     * The first depth level of the array is interleaved. 
     *
     * @param c 2D Array of Complex objects.
     * 
     * @return a complex interleaved {@code float[][]} alternating real and imaginary values.
     *
     * @since 4.0
     */
	public static float[][][] complex2InterleavedFloat(Complex[][][] c) {
		return complex2InterleavedFloat(c, 2);
	}

	/**
     * Converts a 2D interleaved complex array {@code double[][]} 
     * to an array of {@code Complex} objects. 
     * 
     * @param d 2d complex interleaved {@code double[][]}
     * @param interleavedDim Depth level of the array to interleave.
     * @return 2d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][] interleaved2Complex(double[][] d, int interleavedDim) {
		if (interleavedDim > 1 || interleavedDim < 0) {
            throw new OutOfRangeException(
                  interleavedDim, 0, 1);
        }
		int width = d.length;
		int height = d[0].length;
		Complex[][] c;
		if (interleavedDim == 0) {
			c = new Complex[width/2][height];
			for (int x = 0; x < width/2; x++) {
				for (int y = 0; y < height; y++) {
					c[x][y] = new Complex(d[x*2][y], d[x*2+1][y]);
				}
			}
		} else {
			c = new Complex[width][height/2];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height/2; y++) {
					c[x][y] = new Complex(d[x][y*2], d[x][y*2+1]);
				}
			}
		}
		return c;
	}

	/**
     * Converts a 2D interleaved complex array {@code double[][]} 
     * to an array of {@code Complex} objects. 
     * 
     * @param d 2d complex interleaved {@code double[][]}
     * @return 2d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][] interleaved2Complex(double[][] d) {
		return interleaved2Complex(d,1);
	}
	
	/**
     * Converts a 3D interleaved complex array {@code double[][][]} 
     * to an array of {@code Complex} objects. 
     *
     * @param d 3d complex interleaved {@code double[][][]}
     * @param interleavedDim Depth level of the array to interleave.
     * @return 3d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][][] interleaved2Complex(double[][][] d, int interleavedDim) {
		if (interleavedDim > 2 || interleavedDim < 0) {
            throw new OutOfRangeException(
                  interleavedDim, 0, 2);
        }
		int width = d.length;
		int height = d[0].length;
		int depth = d[0].length;
		Complex[][][] c;
		if (interleavedDim == 0) {
			c = new Complex[width/2][height][depth];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						c[x][y][z] = new Complex(d[x/2][y][z], d[x/2+1][y][z]);
					}
				}
			}
		} else if (interleavedDim == 1) {
			c = new Complex[width][height/2][depth];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						c[x][y][z] = new Complex(d[x][y/2][z], d[x][y/2+1][z]);
					}
				}
			}
		} else {
			c = new Complex[width][height][depth/2];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						c[x][y][z] = new Complex(d[x][y][z/2], d[x][y][z/2+1]);
					}
				}
			}
		}
		return c;
	}

	/**
     * Converts a 3D interleaved complex array {@code double[][][]} 
     * to an array of {@code Complex} objects. 
     *
     * @param d 3d complex interleaved {@code double[][][]}
     * @return 3d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][][] interleaved2Complex(double[][][] d) {
		return interleaved2Complex(d,2);
	}

	/**
     * Converts a 2D interleaved complex array {@code float[][]} 
     * to an array of {@code Complex} objects. 
     * 
     * @param d 2d complex interleaved {@code float[][]}
     * @param interleavedDim Depth level of the array to interleave.
     * @return 2d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][] interleaved2Complex(float[][] d, int interleavedDim) {
		if (interleavedDim > 1 || interleavedDim < 0) {
            throw new OutOfRangeException(
                  interleavedDim, 0, 1);
        }
		int width = d.length;
		int height = d[0].length;
		Complex[][] c;
		if (interleavedDim == 0) {
			c = new Complex[width/2][height];
			for (int x = 0; x < width/2; x++) {
				for (int y = 0; y < height; y++) {
					c[x][y] = new Complex(d[x*2][y], d[x*2+1][y]);
				}
			}
		} else {
			c = new Complex[width][height/2];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height/2; y++) {
					c[x][y] = new Complex(d[x][y*2], d[x][y*2+1]);
				}
			}
		}
		return c;
	}

	/**
     * Converts a 2D interleaved complex array {@code float[][]} 
     * to an array of {@code Complex} objects. 
     * 
     * @param d 2d complex interleaved {@code float[][]}
     * @return 2d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][] interleaved2Complex(float[][] d) {
		return interleaved2Complex(d,1);
	}
	
	/**
     * Converts a 3D interleaved complex array {@code float[][][]} 
     * to an array of {@code Complex} objects. 
     *
     * @param d 3d complex interleaved {@code float[][][]}
     * @param interleavedDim Depth level of the array to interleave.
     * @return 3d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][][] interleaved2Complex(float[][][] d, int interleavedDim) {
		if (interleavedDim > 2 || interleavedDim < 0) {
            throw new OutOfRangeException(
                  interleavedDim, 0, 2);
        }
		int width = d.length;
		int height = d[0].length;
		int depth = d[0].length;
		Complex[][][] c;
		if (interleavedDim == 0) {
			c = new Complex[width/2][height][depth];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						c[x][y][z] = new Complex(d[x/2][y][z], d[x/2+1][y][z]);
					}
				}
			}
		} else if (interleavedDim == 1) {
			c = new Complex[width][height/2][depth];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						c[x][y][z] = new Complex(d[x][y/2][z], d[x][y/2+1][z]);
					}
				}
			}
		} else {
			c = new Complex[width][height][depth/2];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth/2; z++) {
						c[x][y][z] = new Complex(d[x][y][z*2], d[x][y][z*2+1]);
					}
				}
			}
		}
		return c;
	}

	/**
     * Converts a 3D interleaved complex array {@code float[][][]} 
     * to an array of {@code Complex} objects. 
     *
     * @param d 3d complex interleaved {@code float[][][]}
     * @return 3d array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][][] interleaved2Complex(float[][][] d) {
		return interleaved2Complex(d,2);
	}

	// SPLIT METHODS
	
	/**
     * Converts a split complex array {@code double[] r, double[] i} 
     * to an array of {@code Complex} objects. 
     *
     * @param real real component
     * @param imag imaginary component
     * @return array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[] split2Complex(double[] real, double[] imag) {
		int length = real.length;
		Complex[] c = new Complex[length];
		for (int n = 0; n < length; n++) {
			c[n] = new Complex(real[n], imag[n]);
		}
		return c;
	}    
	
	/**
     * Converts a 2d split complex array {@code double[][] r, double[][] i} 
     * to a 2d array of {@code Complex} objects. 
     *
     * @param real real component
     * @param imag imaginary component
     * @return array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][] split2Complex(double[][] real, double[][] imag) {
		int width = real.length;
		int height = real[0].length;
		Complex[][] c = new Complex[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				c[x][y] = new Complex(real[x][y], imag[x][y]);
			}
		}
		return c;
	}

	/**
     * Converts a 3d split complex array {@code double[][][] r, double[][][] i} 
     * to a 3d array of {@code Complex} objects. 
     *
     * @param real real component
     * @param imag imaginary component
     * @return array of {@code Complex} objects
     *
     * @since 4.0
     */
	public static Complex[][][] split2Complex(double[][][] real, double[][][] imag) {
		int width = real.length;
		int height = real[0].length;
		int depth = real[0][0].length;
		Complex[][][] c = new Complex[width][height][depth];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < depth; z++) {
					c[x][y][z] = new Complex(real[x][y][z], imag[x][y][z]);
				}
			}
		}
		return c;
	}
	
	// MISC
	
	public static Complex[] initialize(Complex[] c) {
		for (int i: IntegerSequence.range(0, c.length-1)) {
			c[i] = new Complex(0,0);
		}
		return c;
	}
	
	public static Complex[][] initialize(Complex[][] c) {
		for (int i: IntegerSequence.range(0, c.length-1)) {
			for (int j: IntegerSequence.range(0, c[0].length-1)) {
				c[i][j] = new Complex(0,0);
			}
		}
		return c;
	}
	
	public static Complex[][][] initialize(Complex[][][] c) {
		for (int i: IntegerSequence.range(0, c.length-1)) {
			for (int j: IntegerSequence.range(0, c[0].length-1)) {
				for (int k: IntegerSequence.range(0, c[1].length-1)) {
					c[i][j][k] = new Complex(0,0);
				}
			}
		}
		return c;
	}
	
}