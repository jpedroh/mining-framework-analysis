package de.uni_koblenz.jgralab.greql2.funlib.graph;

import java.util.Set;

import org.pcollections.ArrayPMap;
import org.pcollections.PMap;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.types.Undefined;
import de.uni_koblenz.jgralab.schema.Attribute;

public class Describe extends Function {

	public Describe() {
		super("Returns a description of the $element$.", 10, 3, 1.0,
				Category.GRAPH);
	}

	public PMap<String, Object> evaluate(AttributedElement el) {
		PMap<String, Object> result = ArrayPMap.empty();
		result = result.plus("type", el.getAttributedElementClass()
				.getQualifiedName());
		if (el instanceof Graph) {
			result = result.plus("id", ((Graph) el).getId());
		} else {
			result = result.plus("id", ((GraphElement) el).getId());
		}
		Set<Attribute> al = el.getAttributedElementClass().getAttributeList();
		if (al.size() > 0) {
			PMap<String, Object> attrs = ArrayPMap.empty();
			for (Attribute a : el.getAttributedElementClass()
					.getAttributeList()) {
				Object val = el.getAttribute(a.getName());
				attrs = attrs.plus(a.getName(),
						val == null ? Undefined.UNDEFINED : val);
			}
			result = result.plus("attributes", attrs);
		}
		return result;
	}
}
