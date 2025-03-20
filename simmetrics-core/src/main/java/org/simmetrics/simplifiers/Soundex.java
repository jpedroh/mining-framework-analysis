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
package org.simmetrics.simplifiers;

import static org.apache.commons.codec.language.Soundex.US_ENGLISH;

/**
 * Encodes a string into a Soundex value. Soundex is an encoding used to relate
 * similar names, but can also be used as a general purpose scheme to find word
 * with similar phonemes.
 *
 * This class is thread-safe and immutable.
 * 
 * @see org.apache.commons.codec.language.Soundex
 *
 */
public class Soundex implements Simplifier {

	@Override
	public String toString() {
<<<<<<< /usr/src/app/output/simmetrics/simmetrics/5f973be314a72f132d668ea0500013139b0481de/simmetrics-core/src/main/java/org/simmetrics/simplifiers/Soundex.java/left.java
		return "Soundex";
||||||| /usr/src/app/output/simmetrics/simmetrics/5f973be314a72f132d668ea0500013139b0481de/simmetrics-core/src/main/java/org/simmetrics/simplifiers/Soundex.java/base.java
		return "SoundexSimplifier [length=" + length + "]";
=======
		return "SoundexSimplifier";
>>>>>>> /usr/src/app/output/simmetrics/simmetrics/5f973be314a72f132d668ea0500013139b0481de/simmetrics-core/src/main/java/org/simmetrics/simplifiers/Soundex.java/right.java
	}

	@Override
	public String simplify(String input) {
		return US_ENGLISH.soundex(input);
	}
	
}
