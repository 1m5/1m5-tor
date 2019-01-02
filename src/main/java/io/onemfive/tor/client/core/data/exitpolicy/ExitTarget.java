package io.onemfive.tor.client.core.data.exitpolicy;

import io.onemfive.tor.client.core.data.IPv4Address;
import io.onemfive.tor.client.core.data.IPv4Address;

public interface ExitTarget {
	boolean isAddressTarget();
	IPv4Address getAddress();
	String getHostname();
	int getPort();
}
