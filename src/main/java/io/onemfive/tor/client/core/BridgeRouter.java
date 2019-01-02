package io.onemfive.tor.client.core;

import io.onemfive.tor.client.core.data.HexDigest;

public interface BridgeRouter extends Router {
	void setIdentity(HexDigest identity);
	void setDescriptor(RouterDescriptor descriptor);
}
