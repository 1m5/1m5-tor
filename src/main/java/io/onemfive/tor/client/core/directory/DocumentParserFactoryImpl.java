package io.onemfive.tor.client.core.directory;

import java.nio.ByteBuffer;

import io.onemfive.tor.client.core.ConsensusDocument;
import io.onemfive.tor.client.core.KeyCertificate;
import io.onemfive.tor.client.core.RouterDescriptor;
import io.onemfive.tor.client.core.RouterMicrodescriptor;
import io.onemfive.tor.client.core.directory.certificate.KeyCertificateParser;
import io.onemfive.tor.client.core.directory.consensus.ConsensusDocumentParser;
import io.onemfive.tor.client.core.directory.parsing.DocumentFieldParser;
import io.onemfive.tor.client.core.directory.parsing.DocumentParser;
import io.onemfive.tor.client.core.directory.parsing.DocumentParserFactory;
import io.onemfive.tor.client.core.directory.router.RouterDescriptorParser;
import io.onemfive.tor.client.core.directory.router.RouterMicrodescriptorParser;
import io.onemfive.tor.client.core.ConsensusDocument;
import io.onemfive.tor.client.core.KeyCertificate;
import io.onemfive.tor.client.core.RouterDescriptor;
import io.onemfive.tor.client.core.RouterMicrodescriptor;

public class DocumentParserFactoryImpl implements DocumentParserFactory {
	
	public DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer) {
		return new KeyCertificateParser(new DocumentFieldParserImpl(buffer));
	}

	public DocumentParser<RouterDescriptor> createRouterDescriptorParser(ByteBuffer buffer, boolean verifySignatures) {
		return new RouterDescriptorParser(new DocumentFieldParserImpl(buffer), verifySignatures);
	}

	public DocumentParser<RouterMicrodescriptor> createRouterMicrodescriptorParser(ByteBuffer buffer) {
		buffer.rewind();
		DocumentFieldParser dfp = new DocumentFieldParserImpl(buffer);
		return new RouterMicrodescriptorParser(dfp);
	}

	public DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer) {
		return new ConsensusDocumentParser(new DocumentFieldParserImpl(buffer));
	}
}
