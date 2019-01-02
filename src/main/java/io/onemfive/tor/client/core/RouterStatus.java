package io.onemfive.tor.client.core;

import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.data.IPv4Address;
import io.onemfive.tor.client.core.data.Timestamp;
import io.onemfive.tor.client.core.data.exitpolicy.ExitPorts;

public interface RouterStatus {
	String getNickname();
	HexDigest getIdentity();
	HexDigest getDescriptorDigest();
	HexDigest getMicrodescriptorDigest();
	Timestamp getPublicationTime();
	IPv4Address getAddress();
	int getRouterPort();
	boolean isDirectory();
	int getDirectoryPort();
	boolean hasFlag(String flag);
	String getVersion();
	boolean hasBandwidth();
	int getEstimatedBandwidth();
	int getMeasuredBandwidth();
	ExitPorts getExitPorts();
}
