package io.onemfive.tor.client.core.sockets;

import java.net.SocketImpl;
import java.net.SocketImplFactory;

import io.onemfive.tor.client.core.TorClient;
import io.onemfive.tor.client.core.TorClient;

public class OrchidSocketImplFactory implements SocketImplFactory {
	private final TorClient torClient;
	
	public OrchidSocketImplFactory(TorClient torClient) {
		this.torClient = torClient;
	}

	public SocketImpl createSocketImpl() {
		return new OrchidSocketImpl(torClient);
	}
}
