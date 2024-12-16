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

import org.simmetrics.Metric;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.Jaro;


@SuppressWarnings("javadoc")
public final class JaroTest {
	/**
	 * Tests references from <a
	 * href="http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance"
	 * >Wikipedia - Jaro Winkler Distance</a>
	 */
	public static final class WikipediaExamples extends StringMetricTest {
		@Override
		protected boolean satisfiesSubadditivity() {
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected T<String>[] getTests() {
			return new T[]{ new T<>(0.9444F, "MARTHA", "MARHTA"), new T<>(0.8222F, "DWAYNE", "DUANE"), new T<>(0.7666F, "DIXON", "DICKSONX") };
		}

		@Override
		protected Metric<String> getMetric() {
			return new Jaro();
		}
	}

	public static final class Defaults extends StringMetricTest {
		@Override
		protected boolean satisfiesSubadditivity(){
			return false;
		}

	@Override
	protected StringMetric getMetric() {
		return new Jaro();
	}

		@SuppressWarnings("unchecked")
		@Override
		protected T<String>[] getTests() {
			return new T[]{ new T(0.0F, "aaaa", "bbbb"), new T<>(0.9444F, "test string1", "test string2"), new T<>(0.0F, "test string1", "Sold"), new T<>(0.7777F, "test", "test string2"), new T<>(0.0F, "", "test string2"), new T<>(0.8667F, "aaa bbb ccc ddd", "aaa bbb ccc eee"), new T<>(0.9048F, "a b c d", "a b c e"), new T<>(0.8889F, "Healed", "Sealed"), new T<>(0.746F, "Healed", "Healthy"), new T<>(0.8222F, "Healed", "Heard"), new T<>(0.6944F, "Healed", "Herded"), new T<>(0.75F, "Healed", "Help"), new T<>(0.6111F, "Healed", "Sold"), new T<>(0.75F, "Healed", "Help"), new T<>(0.7922F, "Sam J Chapman", "Samuel John Chapman"), new T<>(0.8098F, "Sam Chapman", "S Chapman"), new T<>(0.5945F, "John Smith", "Samuel John Chapman"), new T<>(0.4131F, "John Smith", "Sam Chapman"), new T<>(0.4949F, "John Smith", "Sam J Chapman"), new T<>(0.4333F, "John Smith", "S Chapman"), new T<>(0.8651F, "Web Database Applications", "Web Database Applications with PHP & MySQL"), new T<>(0.6901F, "Web Database Applications", "Creating Database Web Applications with PHP and ASP"), new T<>(0.6353F, "Web Database Applications", "Building Database Applications on the Web Using PHP3"), new T<>(0.6582F, "Web Database Applications", "Building Web Database Applications with Visual Studio 6"), new T<>(0.631F, "Web Database Applications", "Web Application Development With PHP"), new T<>(0.6291F, "Web Database Applications", "WebRAD: Building Database Applications on the Web with Visual FoxPro and Web Connection"), new T<>(0.4751F, "Web Database Applications", "Structural Assessment: The Role of Large and Full-Scale Testing"), new T<>(0.4882F, "Web Database Applications", "How to Find a Scholarship Online"), new T<>(0.6635F, "Web Aplications", "Web Database Applications with PHP & MySQL"), new T<>(0.598F, "Web Aplications", "Creating Database Web Applications with PHP and ASP"), new T<>(0.5675F, "Web Aplications", "Building Database Applications on the Web Using PHP3"), new T<>(0.5909F, "Web Aplications", "Building Web Database Applications with Visual Studio 6"), new T<>(0.7741F, "Web Aplications", "Web Application Development With PHP"), new T<>(0.6352F, "Web Aplications", "WebRAD: Building Database Applications on the Web with Visual FoxPro and Web Connection"), new T<>(0.4751F, "Web Aplications", "Structural Assessment: The Role of Large and Full-Scale Testing"), new T<>(0.4931F, "Web Aplications", "How to Find a Scholarship Online") };
		}
	}
}