package io.onemfive.tor.client.core.directory.parsing;


public interface DocumentParser<T> {
	boolean parse(DocumentParsingResultHandler<T> resultHandler);
	DocumentParsingResult<T> parse();
}
