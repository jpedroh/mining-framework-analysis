package com.github.dakusui.petronia.ut;

import com.github.dakusui.jcunit.generators.PairwiseTestArrayGenerator;
import com.github.dakusui.jcunit.generators.TestArrayGenerator;

public class PairwiseTestArrayGeneratorTest extends TestArrayGeneratorTest {
	@Override
	protected TestArrayGenerator<String, String> createTestArrayGenerator() {
		return new PairwiseTestArrayGenerator<String, String>();
	}
}
