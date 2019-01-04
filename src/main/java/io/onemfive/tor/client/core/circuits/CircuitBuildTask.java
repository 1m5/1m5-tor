package io.onemfive.tor.client.core.circuits;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.onemfive.tor.client.core.CircuitNode;
import io.onemfive.tor.client.core.Connection;
import io.onemfive.tor.client.core.ConnectionCache;
import io.onemfive.tor.client.core.ConnectionFailedException;
import io.onemfive.tor.client.core.ConnectionHandshakeException;
import io.onemfive.tor.client.core.ConnectionTimeoutException;
import io.onemfive.tor.client.core.Router;
import io.onemfive.tor.client.core.Tor;
import io.onemfive.tor.client.core.TorException;
import io.onemfive.tor.client.core.circuits.path.PathSelectionFailedException;

public class CircuitBuildTask implements Runnable {
	private final static Logger logger = Logger.getLogger(CircuitBuildTask.class.getName());
	private final CircuitCreationRequest creationRequest;
	private final ConnectionCache connectionCache;
	private final TorInitializationTracker initializationTracker;
	private final CircuitImpl circuit;
	private final CircuitExtender extender;

	private Connection connection = null;
	
	public CircuitBuildTask(CircuitCreationRequest request, ConnectionCache connectionCache, boolean ntorEnabled) {
		this(request, connectionCache, ntorEnabled, null);
	}

	public CircuitBuildTask(CircuitCreationRequest request, ConnectionCache connectionCache, boolean ntorEnabled, TorInitializationTracker initializationTracker) {
		this.creationRequest = request;
		this.connectionCache = connectionCache;
		this.initializationTracker = initializationTracker;
		this.circuit = request.getCircuit();
		this.extender = new CircuitExtender(request.getCircuit(), ntorEnabled);
	}

	public void run() {
		Router firstRouter = null;
		try {
			circuit.notifyCircuitBuildStart();
			creationRequest.choosePath();
			if(logger.isLoggable(Level.FINE)) {
				logger.fine("Opening a new circuit to "+ pathToString(creationRequest));
			}

			// Open Connection
			firstRouter = creationRequest.getPathElement(0);
			connection = connectionCache.getConnectionTo(firstRouter, creationRequest.isDirectoryCircuit());
			circuit.bindToConnection(connection);
			creationRequest.connectionCompleted(connection);

			// Notify Circuit Create
			if(initializationTracker != null) {
				final int event = creationRequest.isDirectoryCircuit() ?
						Tor.BOOTSTRAP_STATUS_ONEHOP_CREATE : Tor.BOOTSTRAP_STATUS_CIRCUIT_CREATE;
				initializationTracker.notifyEvent(event);
			}

			// Create Circuit
			final CircuitNode firstNode = extender.createFastTo(firstRouter);
			creationRequest.nodeAdded(firstNode);

			for(int i = 1; i < creationRequest.getPathLength(); i++) {
				final CircuitNode extendedNode = extender.extendTo(creationRequest.getPathElement(i));
				creationRequest.nodeAdded(extendedNode);
			}
			creationRequest.circuitBuildCompleted(circuit);

			// Notify Done
			if(initializationTracker != null && !creationRequest.isDirectoryCircuit()) {
				initializationTracker.notifyEvent(Tor.BOOTSTRAP_STATUS_DONE);
			}
			circuit.notifyCircuitBuildCompleted();
		} catch (ConnectionTimeoutException e) {
			connectionFailed("Timeout connecting to "+ firstRouter);
		} catch (ConnectionFailedException e) {
			connectionFailed("Connection failed to "+ firstRouter + " : " + e.getMessage());
		} catch (ConnectionHandshakeException e) {
			connectionFailed("Handshake error connecting to "+ firstRouter + " : " + e.getMessage());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			circuitBuildFailed("Circuit building thread interrupted");
		} catch(PathSelectionFailedException e) { 
			circuitBuildFailed(e.getMessage());
		} catch (TorException e) {
			circuitBuildFailed(e.getMessage());
		} catch(Exception e) {
			circuitBuildFailed("Unexpected exception: "+ e);
			logger.log(Level.WARNING, "Unexpected exception while building circuit: "+ e, e);
		}
	}

	private String pathToString(CircuitCreationRequest ccr) {
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(Router r: ccr.getPath()) {
			if(sb.length() > 1)
				sb.append(",");
			sb.append(r.getNickname());
		}
		sb.append("]");
		return sb.toString();
	}

	private void connectionFailed(String message) {
		logger.info(message);
		creationRequest.connectionFailed(message);
		circuit.notifyCircuitBuildFailed();
	}
	
	private void circuitBuildFailed(String message) {
		logger.info(message);
		creationRequest.circuitBuildFailed(message);
		circuit.notifyCircuitBuildFailed();
		if(connection != null) {
			connection.removeCircuit(circuit);
		}
	}

}
