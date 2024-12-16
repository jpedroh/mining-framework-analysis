/*
 * SimMetrics - SimMetrics is a java library of Similarity or Distance Metrics,
 * e.g. Levenshtein Distance, that provide float based similarity measures
 * between String Data. All metrics return consistent measures rather than
 * unbounded similarity scores.
 * 
 * Copyright (C) 2014 SimMetrics authors
 * 
 * This file is part of SimMetrics. This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SimMetrics. If not, see <http://www.gnu.org/licenses/>.
 */
package org.simmetrics.metrics;

import org.simmetrics.StringMetric;
import org.simmetrics.simplifiers.SoundexSimplifier;
import org.simmetrics.tokenizers.WhitespaceTokenizer;
import static org.simmetrics.StringMetricBuilder.with;


public class ChapmanMatchingSoundexTest extends StringMetricTest {
	@Override
	protected StringMetric getMetric() {
		return with(new MongeElkan(with(new JaroWinkler()).simplify(new SoundexSimplifier()).build())).tokenize(new WhitespaceTokenizer()).build();
	}

	@Override
	protected T[] getTests() {
		return new T[]{ new T(1.0F, "test string1", "test string2"), new T(0.9667F, "aaa bbb ccc ddd", "aaa bbb ccc eee"), new T(0.9667F, "a b c d", "a b c e"), new T(0.8667F, "Healed", "Sealed"), new T(1.0F, "Healed", "Healthy"), new T(0.88F, "Healed", "Heard"), new T(0.76F, "Healed", "Herded"), new T(0.8933F, "Healed", "Help"), new T(0.8667F, "Healed", "Sold"), new T(0.8933F, "Healed", "Help"), new T(0.9244F, "Sam J Chapman", "Samuel John Chapman"), new T(0.94F, "Sam Chapman", "S Chapman"), new T(0.887F, "John Smith", "Samuel John Chapman"), new T(0.8105F, "John Smith", "Sam Chapman"), new T(0.8375F, "John Smith", "Sam J Chapman"), new T(0.7125F, "John Smith", "S Chapman") };
	}
}