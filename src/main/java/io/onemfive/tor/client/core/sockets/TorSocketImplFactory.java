package io.onemfive.tor.client.core.sockets;

import java.net.SocketImpl;
import java.net.SocketImplFactory;

import io.onemfive.tor.client.core.TorClient;

public class TorSocketImplFactory implements SocketImplFactory {
	private final TorClient torClient;
	
	public TorSocketImplFactory(TorClient torClient) {
		this.torClient = torClient;
	}

	public SocketImpl createSocketImpl() {
		return new TorSocketImpl(torClient);
	}
}
