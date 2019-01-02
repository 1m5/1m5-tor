package io.onemfive.tor.client.core.circuits.hs;

import java.math.BigInteger;
import java.util.logging.Logger;

import io.onemfive.tor.client.core.Cell;
import io.onemfive.tor.client.core.HiddenServiceCircuit;
import io.onemfive.tor.client.core.InternalCircuit;
import io.onemfive.tor.client.core.RelayCell;
import io.onemfive.tor.client.core.Router;
import io.onemfive.tor.client.core.circuits.CircuitNodeCryptoState;
import io.onemfive.tor.client.core.circuits.CircuitNodeImpl;
import io.onemfive.tor.client.core.crypto.TorMessageDigest;
import io.onemfive.tor.client.core.crypto.TorRandom;
import io.onemfive.tor.client.core.crypto.TorTapKeyAgreement;
import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.*;
import io.onemfive.tor.client.core.circuits.CircuitNodeCryptoState;
import io.onemfive.tor.client.core.circuits.CircuitNodeImpl;
import io.onemfive.tor.client.core.crypto.TorMessageDigest;
import io.onemfive.tor.client.core.crypto.TorRandom;
import io.onemfive.tor.client.core.crypto.TorTapKeyAgreement;
import io.onemfive.tor.client.core.data.HexDigest;

public class RendezvousProcessor {
	private final static Logger logger = Logger.getLogger(RendezvousProcessor.class.getName());
	
	private final static int RENDEZVOUS_COOKIE_LEN = 20;
	private final static TorRandom random = new TorRandom();
	
	private final InternalCircuit circuit;
	private final byte[] cookie;
	
	protected RendezvousProcessor(InternalCircuit circuit) {
		this.circuit = circuit;
		this.cookie = random.getBytes(RENDEZVOUS_COOKIE_LEN);
	}
	
	boolean establishRendezvous() {
		final RelayCell cell = circuit.createRelayCell(RelayCell.RELAY_COMMAND_ESTABLISH_RENDEZVOUS, 0, circuit.getFinalCircuitNode());
		cell.putByteArray(cookie);
		circuit.sendRelayCell(cell);
		final RelayCell response = circuit.receiveRelayCell();
		if(response == null) {
			logger.info("Timeout waiting for Rendezvous establish response");
			return false;
		} else if(response.getRelayCommand() != RelayCell.RELAY_COMMAND_RENDEZVOUS_ESTABLISHED) {
			logger.info("Response received from Rendezvous establish was not expected acknowledgement, Relay Command: "+ response.getRelayCommand());
			return false;
		} else {
			return true;
		}
	}
	
	HiddenServiceCircuit processRendezvous2(TorTapKeyAgreement kex) {
		final RelayCell cell = circuit.receiveRelayCell();
		if(cell == null) {
			logger.info("Timeout waiting for RENDEZVOUS2");
			return null;
		} else if (cell.getRelayCommand() != RelayCell.RELAY_COMMAND_RENDEZVOUS2) {
			logger.info("Unexpected Relay cell type received while waiting for RENDEZVOUS2: "+ cell.getRelayCommand());
			return null;
		}
		final BigInteger peerPublic = readPeerPublic(cell);
		final HexDigest handshakeDigest = readHandshakeDigest(cell);
		if(peerPublic == null || handshakeDigest == null) {
			return null;
		}
		final byte[] verifyHash = new byte[TorMessageDigest.TOR_DIGEST_SIZE];
		final byte[] keyMaterial = new byte[CircuitNodeCryptoState.KEY_MATERIAL_SIZE];
		if(!kex.deriveKeysFromDHPublicAndHash(peerPublic, handshakeDigest.getRawBytes(), keyMaterial, verifyHash)) {
			logger.info("Error deriving session keys while extending to hidden service");
			return null;
		}
		return circuit.connectHiddenService(CircuitNodeImpl.createAnonymous(circuit.getFinalCircuitNode(), keyMaterial, verifyHash));
	}
	
	private BigInteger readPeerPublic(Cell cell) {
		final byte[] dhPublic = new byte[TorTapKeyAgreement.DH_LEN];
		cell.getByteArray(dhPublic);
		final BigInteger peerPublic = new BigInteger(1, dhPublic);
		if(!TorTapKeyAgreement.isValidPublicValue(peerPublic)) {
			logger.warning("Illegal DH public value received: "+ peerPublic);
			return null;
		}
		return peerPublic;
	}
	
	HexDigest readHandshakeDigest(Cell cell) {
		final byte[] digestBytes = new byte[TorMessageDigest.TOR_DIGEST_SIZE];
		cell.getByteArray(digestBytes);
		return HexDigest.createFromDigestBytes(digestBytes);
	}
	
	
	byte[] getCookie() {
		return cookie;
	}

	Router getRendezvousRouter() {
		return circuit.getFinalCircuitNode().getRouter();
	}
}