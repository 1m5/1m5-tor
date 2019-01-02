package io.onemfive.tor.client.core;

import java.util.List;

import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.data.IPv4Address;
import io.onemfive.tor.client.core.directory.consensus.DirectorySignature;
import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.data.IPv4Address;

public interface VoteAuthorityEntry {
	String getNickname();
	HexDigest getIdentity();
	String getHostname();
	IPv4Address getAddress();
	int getDirectoryPort();
	int getRouterPort();
	String getContact();
	HexDigest getVoteDigest();
	List<DirectorySignature> getSignatures();
}
