package io.onemfive.tor.client.core.circuits.hs;

import io.onemfive.tor.client.core.Router;
import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.Router;
import io.onemfive.tor.client.core.data.HexDigest;

public class HSDescriptorDirectory {
	
	private final HexDigest descriptorId;
	private final Router directory;
	
	HSDescriptorDirectory(HexDigest descriptorId, Router directory) {
		this.descriptorId = descriptorId;
		this.directory = directory;
	}
	
	Router getDirectory() {
		return directory;
	}
	
	HexDigest getDescriptorId() {
		return descriptorId;
	}
	
	public String toString() {
		return descriptorId + " : " + directory;
	}

}
