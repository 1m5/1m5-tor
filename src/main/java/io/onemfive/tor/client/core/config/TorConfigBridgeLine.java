package io.onemfive.tor.client.core.config;

import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.data.IPv4Address;
import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.data.IPv4Address;

public class TorConfigBridgeLine {
	
	private final IPv4Address address;
	private final int port;
	private final HexDigest fingerprint;

	TorConfigBridgeLine(IPv4Address address, int port, HexDigest fingerprint) {
		this.address = address;
		this.port = port;
		this.fingerprint = fingerprint;
	}
	
	public IPv4Address getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public HexDigest getFingerprint() {
		return fingerprint;
	}
}
