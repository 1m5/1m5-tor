package io.onemfive.tor.client.core.circuits;

import java.util.List;
import java.util.concurrent.TimeoutException;

import io.onemfive.tor.client.core.DirectoryCircuit;
import io.onemfive.tor.client.core.Router;
import io.onemfive.tor.client.core.Stream;
import io.onemfive.tor.client.core.StreamConnectFailedException;
import io.onemfive.tor.client.core.circuits.path.CircuitPathChooser;
import io.onemfive.tor.client.core.circuits.path.PathSelectionFailedException;
import io.onemfive.tor.client.core.DirectoryCircuit;
import io.onemfive.tor.client.core.Router;
import io.onemfive.tor.client.core.Stream;
import io.onemfive.tor.client.core.StreamConnectFailedException;

public class DirectoryCircuitImpl extends CircuitImpl implements DirectoryCircuit {
	
	protected DirectoryCircuitImpl(CircuitManagerImpl circuitManager, List<Router> prechosenPath) {
		super(circuitManager, prechosenPath);
	}
	
	public Stream openDirectoryStream(long timeout, boolean autoclose) throws InterruptedException, TimeoutException, StreamConnectFailedException {
		final StreamImpl stream = createNewStream(autoclose);
		try {
			stream.openDirectory(timeout);
			return stream;
		} catch (Exception e) {
			removeStream(stream);
			return processStreamOpenException(e);
		}
	}

	@Override
	protected List<Router> choosePathForCircuit(CircuitPathChooser pathChooser) throws InterruptedException, PathSelectionFailedException {
		if(prechosenPath != null) {
			return prechosenPath;
		}
		return pathChooser.chooseDirectoryPath();
	}

	@Override
	protected String getCircuitTypeLabel() {
		return "Directory";
	}
}
