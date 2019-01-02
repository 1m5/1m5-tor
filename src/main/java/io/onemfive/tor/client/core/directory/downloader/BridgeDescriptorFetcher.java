package io.onemfive.tor.client.core.directory.downloader;

import java.nio.ByteBuffer;

import io.onemfive.tor.client.core.RouterDescriptor;
import io.onemfive.tor.client.core.directory.parsing.DocumentParser;
import io.onemfive.tor.client.core.RouterDescriptor;
import io.onemfive.tor.client.core.directory.parsing.DocumentParser;

public class BridgeDescriptorFetcher extends DocumentFetcher<RouterDescriptor>{

	@Override
	String getRequestPath() {
		return "/tor/server/authority";
	}

	@Override
    DocumentParser<RouterDescriptor> createParser(ByteBuffer response) {
		return PARSER_FACTORY.createRouterDescriptorParser(response, true);
	}
}
