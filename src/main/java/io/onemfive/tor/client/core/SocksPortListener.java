package io.onemfive.tor.client.core;

public interface SocksPortListener {
	void addListeningPort(int port);
	void stop();
}
