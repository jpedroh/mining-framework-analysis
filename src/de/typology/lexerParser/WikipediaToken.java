package de.typology.lexerParser;

public enum WikipediaToken {
	OBJECT, STRING, OTHER, WS, LINESEPARATOR, URI, EOL, EOF, FULLSTOP, COMMA, HYPHEN, EXCLAMATIONMARK, QUESTIONMARK, QUOTATIONMARK,
	//
	BRACKET, CLOSEDBRACKET, CURLYBRACKET, CLOSEDCURLYBRACKET,
	//
	INFOBOX,
	//
	LINK, LABELEDLINK,
	//
	PAGE, CLOSEDPAGE,
	//
	TITLE, CLOSEDTITLE,
	//
	TEXT, CLOSEDTEXT,
	//
	REF, CLOSEDREF
}
