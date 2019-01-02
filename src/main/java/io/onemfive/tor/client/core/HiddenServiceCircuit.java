package io.onemfive.tor.client.core;

import java.util.concurrent.TimeoutException;


public interface HiddenServiceCircuit extends Circuit {
	Stream openStream(int port, long timeout) throws InterruptedException, TimeoutException, StreamConnectFailedException;
}
