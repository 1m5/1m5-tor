package io.onemfive.tor.client.core.circuits;

import io.onemfive.tor.client.core.data.IPv4Address;
import io.onemfive.tor.client.core.data.exitpolicy.ExitTarget;
import io.onemfive.tor.client.core.data.IPv4Address;
import io.onemfive.tor.client.core.data.exitpolicy.ExitTarget;

public class PredictedPortTarget implements ExitTarget {
	
	final int port;

	public PredictedPortTarget(int port) {
		this.port = port;
	}

	public boolean isAddressTarget() {
		return false;
	}

	public IPv4Address getAddress() {
		return new IPv4Address(0);
	}

	public String getHostname() {
		return "";
	}

	public int getPort() {
		return port;
	}
}
