package io.onemfive.tor.client.core.circuits.hs;

import io.onemfive.tor.client.core.crypto.TorPublicKey;
import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.data.IPv4Address;
import io.onemfive.tor.client.core.crypto.TorPublicKey;
import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.data.IPv4Address;

public class IntroductionPoint {

	private HexDigest identity;
	private IPv4Address address;
	private int onionPort;
	private TorPublicKey onionKey;
	private TorPublicKey serviceKey;
	
	IntroductionPoint(HexDigest identity) {
		this.identity = identity;
	}

	void setAddress(IPv4Address address) {
		this.address = address;
	}
	
	void setOnionPort(int onionPort) {
		this.onionPort = onionPort;
	}
	
	void setOnionKey(TorPublicKey onionKey) {
		this.onionKey = onionKey;
	}
	
	void setServiceKey(TorPublicKey serviceKey) {
		this.serviceKey = serviceKey;
	}
	
	boolean isValidDocument() {
		return identity != null && address != null && onionPort != 0 && onionKey != null && serviceKey != null;
	}
	
	public HexDigest getIdentity() {
		return identity;
	}
	
	public IPv4Address getAddress() {
		return address;
	}
	
	public int getPort() {
		return onionPort;
	}
	
	public TorPublicKey getOnionKey() {
		return onionKey;
	}
	
	public TorPublicKey getServiceKey() {
		return serviceKey;
	}
}