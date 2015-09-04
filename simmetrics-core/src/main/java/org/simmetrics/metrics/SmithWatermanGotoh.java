/*
 * #%L
 * Simmetrics Core
 * %%
 * Copyright (C) 2014 - 2015 Simmetrics Authors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


package org.simmetrics.metrics;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.functions.MatchMismatch;
import org.simmetrics.metrics.functions.Substitution;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.simmetrics.metrics.Math.max;

/**
 * Smith-Waterman algorithm providing a similarity measure between two strings.
 * <p>
 * Implementation uses optimizations described in Osamu Gotoh (1982).
 * "An improved algorithm for matching biological sequences". Journal of
 * molecular biology 162: 705". This implementation uses constant space and
 * quadratic time.
 * 
 * <p>
 * This class is immutable and thread-safe if its substitution functions are.
 * 
 * @see NeedlemanWunch
 * @see SmithWaterman
 * @see <a
 *      href="https://en.wikipedia.org/wiki/Smith%E2%80%93Waterman_algorithm">Wikipedia
 *      - Smith-Waterman algorithm</a>
 *
 */
public class SmithWatermanGotoh implements StringMetric {

	private static final Substitution MATCH_1_MISMATCH_MINUS_2 = new MatchMismatch(
			1.0f, -2.0f);

	private final float gapValue;

	private Substitution substitution;

	/**
	 * Constructs a new Smith Waterman metric. Gap penalty is -0.5, mismatch
	 * penalty -2.0 and a matching score 1.0.
	 * 
	 */
	public SmithWatermanGotoh() {
		this(-0.5f, MATCH_1_MISMATCH_MINUS_2);
	}

	/**
	 * Constructs a new Smith Waterman metric.
	 * 
	 * @param gapValue
	 *            a non-positive gap penalty
	 * @param substitution
	 *            a substitution function
	 */
	public SmithWatermanGotoh(float gapValue, Substitution substitution) {
		checkArgument(gapValue <= 0.0f);
		checkNotNull(substitution);
		this.gapValue = gapValue;
		this.substitution = substitution;
	}

	@Override
	public float compare(final String a, final String b) {

		if (a.isEmpty() && b.isEmpty()) {
			return 1.0f;
		}

		if (a.isEmpty() || b.isEmpty()) {
			return 0.0f;
		}

		float maxDistance = min(a.length(), b.length())
				* max(substitution.max(), gapValue);
		return smithWatermanGotoh(a, b) / maxDistance;
	}

	private float smithWatermanGotoh(final String s, final String t) {
		
		float[] v0 = new float[t.length()];
		float[] v1 = new float[t.length()];

		float max = v0[0] = max(0, gapValue, substitution.compare(s, 0, t, 0));

		for (int j = 1; j < v0.length; j++) {
			v0[j] = max(0, v0[j - 1] + gapValue,
					substitution.compare(s, 0, t, j));

			max = max(max, v0[j]);
		}

		// Find max
		for (int i = 1; i < s.length(); i++) {
			v1[0] = max(0, v0[0] + gapValue, substitution.compare(s, i, t, 0));

			max = max(max, v1[0]);

			for (int j = 1; j < v0.length; j++) {
				v1[j] = max(0, v0[j] + gapValue, v1[j - 1] + gapValue,
						v0[j - 1] + substitution.compare(s, i, t, j));

				max = max(max, v1[j]);
			}

			for (int j = 0; j < v0.length; j++) {
				v0[j] = v1[j];
			}
		}

		return max;
	}

	@Override
	public String toString() {
		return "SmithWatermanGotoh [substitution=" + substitution + ", gapValue="
				+ gapValue + "]";
	}
}
