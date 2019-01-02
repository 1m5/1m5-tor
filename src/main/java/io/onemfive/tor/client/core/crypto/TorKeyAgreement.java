package io.onemfive.tor.client.core.crypto;

public interface TorKeyAgreement {
	byte[] createOnionSkin();
	boolean deriveKeysFromHandshakeResponse(byte[] handshakeResponse, byte[] keyMaterialOut, byte[] verifyHashOut);
}
