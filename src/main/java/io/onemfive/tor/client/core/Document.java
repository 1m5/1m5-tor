package io.onemfive.tor.client.core;

import java.nio.ByteBuffer;

public interface Document {
	ByteBuffer getRawDocumentBytes();
	String getRawDocumentData();
	boolean isValidDocument();
}
