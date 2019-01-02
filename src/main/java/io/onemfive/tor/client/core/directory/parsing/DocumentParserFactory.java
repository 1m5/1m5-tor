package io.onemfive.tor.client.core.directory.parsing;

import java.nio.ByteBuffer;

import io.onemfive.tor.client.core.ConsensusDocument;
import io.onemfive.tor.client.core.KeyCertificate;
import io.onemfive.tor.client.core.RouterDescriptor;
import io.onemfive.tor.client.core.RouterMicrodescriptor;
import io.onemfive.tor.client.core.ConsensusDocument;
import io.onemfive.tor.client.core.KeyCertificate;
import io.onemfive.tor.client.core.RouterDescriptor;
import io.onemfive.tor.client.core.RouterMicrodescriptor;

public interface DocumentParserFactory {
	DocumentParser<RouterDescriptor> createRouterDescriptorParser(ByteBuffer buffer, boolean verifySignatures);
	
	DocumentParser<RouterMicrodescriptor> createRouterMicrodescriptorParser(ByteBuffer buffer);

	DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer);

	DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer);
}
