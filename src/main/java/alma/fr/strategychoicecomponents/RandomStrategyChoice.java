package alma.fr.strategychoicecomponents;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import alma.fr.basecomponents.IBase;
import alma.fr.data.Positions;
import alma.fr.logootenginecomponents.Replica;
import alma.fr.strategiescomponents.IIdProviderStrategy;

import com.google.inject.Inject;

public class RandomStrategyChoice implements IStrategyChoice {
	private HashMap<Positions, FakeListNode> spectrum = new HashMap<Positions, FakeListNode>();

	private Integer date = 0;
	
	Map<Integer, IIdProviderStrategy> strategies ;

	@Inject
	IBase base;

	private IIdProviderStrategy strategy1;
	private IIdProviderStrategy strategy2;

	@Inject
	public RandomStrategyChoice(IBase base,
			@Strat1 IIdProviderStrategy strategy1,
			@Strat2 IIdProviderStrategy strategy2) {
		this.base = base;
		this.strategy1 = strategy1;
		this.strategy2 = strategy2;
		strategies = new HashMap<Integer, IIdProviderStrategy>(0);
	}

	/** add the new id in the structure **/
	public void add(Positions prev, Positions id, Positions next) {

		if (!spectrum.containsKey(prev)) {
			FakeListNode prevfln = new FakeListNode(null, date, id);
			spectrum.put(prev, prevfln);
		} else {
			spectrum.get(prev).setNext(id);
		}

		if (!spectrum.containsKey(next)) {
			FakeListNode nextfln = new FakeListNode(id, date, null);
			spectrum.put(next, nextfln);
		} else {
			spectrum.get(next).setPrev(id);
		}

		FakeListNode fln = new FakeListNode(prev, date, next);

		spectrum.put(id, fln);
	}

	public void del(Positions id) {
		FakeListNode fln = spectrum.get(id);
		if (fln.getPrev() != null) {
			spectrum.get(fln.getPrev()).setNext(fln.getNext());

		}
		if (fln.getNext() != null) {
			spectrum.get(fln.getNext()).setPrev(fln.getPrev());
		}
		spectrum.remove(id);
	}

	public Iterator<Positions> generateLineIdentifiers(Positions p,
			Positions q, Integer N, Replica rep) {
		ArrayList<BigInteger> qprefix = q.prefix(q.size());
		ArrayList<BigInteger> pprefix = p.prefix(p.size());

		Integer index = 0;
		BigInteger interval = new BigInteger("0");
		BigInteger nBigInteger = new BigInteger(N.toString());
		while (interval.compareTo(nBigInteger) == -1) {
			++index;

			interval = base.count(qprefix, index).subtract(
					base.count(pprefix, index)).subtract(new BigInteger("1"));
		}

<<<<<<< /usr/src/app/output/chat-wane/lseq/916d8a5efc2654df3f31715ed05eea52f7d09957/src/main/java/alma/fr/strategychoicecomponents/RandomStrategyChoice.java/left.java
		// #2 if not already setted value in strategies
		// random a full 64 bits of strategies, bitsize.size limitation
		if (index >= strategies.size()) {
			int sizeBefore = strategies.size();
			strategies.set(strategies.size());
			for (int j = sizeBefore; j < strategies.size(); ++j) {
				if (r.nextBoolean()) {
					strategies.set(j);
				} else {
					strategies.clear(j);
				}
||||||| /usr/src/app/output/chat-wane/lseq/916d8a5efc2654df3f31715ed05eea52f7d09957/src/main/java/alma/fr/strategychoicecomponents/RandomStrategyChoice.java/base.java
		// #2 if already setted value in strategies
		while (strategies.size() < index) {
			// #2b else random & use strategy
			if (r.nextBoolean()) {
				strategies.set(strategies.size());
			} else {
				strategies.clear(strategies.size());
=======
		Random r = new Random();
		if (!strategies.containsKey(index)) {
			if (r.nextInt(2) == 0) {
				strategies.put(index, strategy1);
			} else {
				strategies.put(index, strategy2);
>>>>>>> /usr/src/app/output/chat-wane/lseq/916d8a5efc2654df3f31715ed05eea52f7d09957/src/main/java/alma/fr/strategychoicecomponents/RandomStrategyChoice.java/right.java
			}
		}

		return strategies.get(index).generateLineIdentifiers(p, q, N, rep);
	}

	public void incDate() {
		++date;
	}

	public HashMap<Positions, FakeListNode> getSpectrum() {
		return spectrum;
	}

}
