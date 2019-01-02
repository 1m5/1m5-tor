package io.onemfive.tor.client.core.circuits;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import io.onemfive.tor.client.core.ExitCircuit;
import io.onemfive.tor.client.core.Router;
import io.onemfive.tor.client.core.Stream;
import io.onemfive.tor.client.core.StreamConnectFailedException;
import io.onemfive.tor.client.core.circuits.path.CircuitPathChooser;
import io.onemfive.tor.client.core.circuits.path.PathSelectionFailedException;
import io.onemfive.tor.client.core.data.IPv4Address;
import io.onemfive.tor.client.core.data.exitpolicy.ExitTarget;
import io.onemfive.tor.client.core.ExitCircuit;
import io.onemfive.tor.client.core.Router;
import io.onemfive.tor.client.core.Stream;
import io.onemfive.tor.client.core.StreamConnectFailedException;
import io.onemfive.tor.client.core.data.IPv4Address;
import io.onemfive.tor.client.core.data.exitpolicy.ExitTarget;

public class ExitCircuitImpl extends CircuitImpl implements ExitCircuit {
	
	private final Router exitRouter;
	private final Set<ExitTarget> failedExitRequests;

	ExitCircuitImpl(CircuitManagerImpl circuitManager, List<Router> prechosenPath) {
		super(circuitManager, prechosenPath);
		this.exitRouter = prechosenPath.get(prechosenPath.size() - 1);
		this.failedExitRequests = new HashSet<ExitTarget>();
	}

	ExitCircuitImpl(CircuitManagerImpl circuitManager, Router exitRouter) {
		super(circuitManager);
		this.exitRouter = exitRouter;
		this.failedExitRequests = new HashSet<ExitTarget>();
	}
	
	public Stream openExitStream(IPv4Address address, int port, long timeout) throws InterruptedException, TimeoutException, StreamConnectFailedException {
		return openExitStream(address.toString(), port, timeout);
	}

	public Stream openExitStream(String target, int port, long timeout) throws InterruptedException, TimeoutException, StreamConnectFailedException {
		final StreamImpl stream = createNewStream();
		try {
			stream.openExit(target, port, timeout);
			return stream;
		} catch (Exception e) {
			removeStream(stream);
			return processStreamOpenException(e);
		}
	}
	
	public void recordFailedExitTarget(ExitTarget target) {
		synchronized(failedExitRequests) {
			failedExitRequests.add(target);
		}
	}

	public boolean canHandleExitTo(ExitTarget target) {
		synchronized(failedExitRequests) {
			if(failedExitRequests.contains(target)) {
				return false;
			}
		}
		
		if(isMarkedForClose()) {
			return false;
		}

		if(target.isAddressTarget()) {
			return exitRouter.exitPolicyAccepts(target.getAddress(), target.getPort());
		} else {
			return exitRouter.exitPolicyAccepts(target.getPort());
		}
	}
	
	public boolean canHandleExitToPort(int port) {
		return exitRouter.exitPolicyAccepts(port);
	}

	
	@Override
	protected List<Router> choosePathForCircuit(CircuitPathChooser pathChooser) throws InterruptedException, PathSelectionFailedException {
		return pathChooser.choosePathWithExit(exitRouter);
	}
	
	@Override
	protected String getCircuitTypeLabel() {
		return "Exit";
	}
}
