package io.onemfive.tor.client.core.directory.parsing;

public interface DocumentParsingHandler {
	void parseKeywordLine();
	void endOfDocument();
}
