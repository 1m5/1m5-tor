package io.onemfive.tor.client.core.directory.downloader;

import java.nio.ByteBuffer;

import io.onemfive.tor.client.core.ConsensusDocument;
import io.onemfive.tor.client.core.directory.parsing.DocumentParser;
import io.onemfive.tor.client.core.ConsensusDocument;
import io.onemfive.tor.client.core.directory.parsing.DocumentParser;

public class ConsensusFetcher extends DocumentFetcher<ConsensusDocument>{
	
	private final static String CONSENSUS_BASE_PATH = "/tor/status-vote/current/";
	
	private final boolean useMicrodescriptors;
	
	
	public ConsensusFetcher(boolean useMicrodescriptors) {
		this.useMicrodescriptors = useMicrodescriptors;
	}

	@Override
	String getRequestPath() {
		return CONSENSUS_BASE_PATH + ((useMicrodescriptors) ? 
				("consensus-microdesc") : ("consensus"));
	}

	@Override
    DocumentParser<ConsensusDocument> createParser(ByteBuffer response) {
		return PARSER_FACTORY.createConsensusDocumentParser(response);
	}
}
