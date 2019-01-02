package io.onemfive.tor.client.core.circuits;

import java.util.List;
import java.util.concurrent.TimeoutException;

import io.onemfive.tor.client.core.Circuit;
import io.onemfive.tor.client.core.CircuitNode;
import io.onemfive.tor.client.core.DirectoryCircuit;
import io.onemfive.tor.client.core.HiddenServiceCircuit;
import io.onemfive.tor.client.core.InternalCircuit;
import io.onemfive.tor.client.core.Router;
import io.onemfive.tor.client.core.Stream;
import io.onemfive.tor.client.core.StreamConnectFailedException;
import io.onemfive.tor.client.core.circuits.path.CircuitPathChooser;
import io.onemfive.tor.client.core.circuits.path.PathSelectionFailedException;
import io.onemfive.tor.client.core.*;

public class InternalCircuitImpl extends CircuitImpl implements InternalCircuit, DirectoryCircuit, HiddenServiceCircuit {

	private enum InternalType { UNUSED, HS_INTRODUCTION, HS_DIRECTORY, HS_CIRCUIT }
	
	private InternalType type;
	private boolean ntorEnabled;
	
	InternalCircuitImpl(CircuitManagerImpl circuitManager, List<Router> prechosenPath) {
		super(circuitManager, prechosenPath);
		this.type = InternalType.UNUSED;
		this.ntorEnabled = circuitManager.isNtorEnabled();
	}
	
	protected InternalCircuitImpl(CircuitManagerImpl circuitManager) {
		this(circuitManager, null);
	}
	
	@Override
	protected List<Router> choosePathForCircuit(CircuitPathChooser pathChooser)
			throws InterruptedException, PathSelectionFailedException {
		return pathChooser.chooseInternalPath();
	}
	

	public Circuit cannibalizeToIntroductionPoint(Router target) {
		cannibalizeTo(target);
		type = InternalType.HS_INTRODUCTION;
		return this;
	}

	private void cannibalizeTo(Router target) {
		if(type != InternalType.UNUSED) {
			throw new IllegalStateException("Cannot cannibalize internal circuit with type "+ type);
			
		}
		final CircuitExtender extender = new CircuitExtender(this, ntorEnabled);
		extender.extendTo(target);
	}
	
	public Stream openDirectoryStream(long timeout, boolean autoclose) throws InterruptedException, TimeoutException, StreamConnectFailedException {
		if(type != InternalType.HS_DIRECTORY) {
			throw new IllegalStateException("Cannot open directory stream on internal circuit with type "+ type);
		}
		final StreamImpl stream = createNewStream();
		try {
			stream.openDirectory(timeout);
			return stream;
		} catch (Exception e) {
			removeStream(stream);
			return processStreamOpenException(e);
		}
	}

	
	public DirectoryCircuit cannibalizeToDirectory(Router target) {
		cannibalizeTo(target);
		type = InternalType.HS_DIRECTORY;
		return this;
	}


	public HiddenServiceCircuit connectHiddenService(CircuitNode node) {
		if(type != InternalType.UNUSED) {
			throw new IllegalStateException("Cannot connect hidden service from internal circuit type "+ type);
		}
		appendNode(node);
		type = InternalType.HS_CIRCUIT;
		return this;
	}

	public Stream openStream(int port, long timeout) 
			throws InterruptedException, TimeoutException, StreamConnectFailedException {
		if(type != InternalType.HS_CIRCUIT) {
			throw new IllegalStateException("Cannot open stream to hidden service from internal circuit type "+ type);
		}
		final StreamImpl stream = createNewStream();
		try {
			stream.openExit("", port, timeout);
			return stream;
		} catch (Exception e) {
			removeStream(stream);
			return processStreamOpenException(e);
		}
	}


	@Override
	protected String getCircuitTypeLabel() {
		switch(type) {
		case HS_CIRCUIT:
			return "Hidden Service";
		case HS_DIRECTORY:
			return "HS Directory";
		case HS_INTRODUCTION:
			return "HS Introduction";
		case UNUSED:
			return "Internal";
		default:
			return "(null)";
		}
	}
}