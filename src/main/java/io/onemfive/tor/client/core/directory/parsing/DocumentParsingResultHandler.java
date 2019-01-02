package io.onemfive.tor.client.core.directory.parsing;


public interface DocumentParsingResultHandler<T> {
	void documentParsed(T document);
	void documentInvalid(T document, String message);
	void parsingError(String message);
}
