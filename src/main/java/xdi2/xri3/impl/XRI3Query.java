package xdi2.xri3.impl;

import xdi2.xri3.XRIQuery;
import xdi2.xri3.impl.parser.Parser.iquery;
import xdi2.xri3.impl.parser.ParserException;
import xdi2.xri3.impl.parser.Rule;

public class XRI3Query extends XRI3SyntaxComponent implements XRIQuery {

	private static final long serialVersionUID = 8838957773108506171L;

	private Rule rule;
	
	private String value;

	public XRI3Query(String string) throws ParserException {

		this.rule = XRI3Util.getParser().parse("iquery", string);
		this.read();
	}

	XRI3Query(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {
		
		this.value = null;
	}

	private void read() {

		this.reset();
		
		Object object = this.rule;	// iquery

		this.value = ((iquery) object).spelling;
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public String getValue() {
		
		return(this.value);
	}
}
