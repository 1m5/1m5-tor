package io.onemfive.tor.client.core;

import java.util.List;
import java.util.Set;

import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.directory.downloader.DirectoryRequestFailedException;

public interface DirectoryDownloader {
	void start(Directory directory);
	void stop();
	
	RouterDescriptor downloadBridgeDescriptor(Router bridge) throws DirectoryRequestFailedException;
	
	ConsensusDocument downloadCurrentConsensus(boolean useMicrodescriptors) throws DirectoryRequestFailedException;
	ConsensusDocument downloadCurrentConsensus(boolean useMicrodescriptors, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
	
	List<KeyCertificate> downloadKeyCertificates(Set<ConsensusDocument.RequiredCertificate> required) throws DirectoryRequestFailedException;
	List<KeyCertificate> downloadKeyCertificates(Set<ConsensusDocument.RequiredCertificate> required, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
	
	List<RouterDescriptor> downloadRouterDescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException;
	List<RouterDescriptor> downloadRouterDescriptors(Set<HexDigest> fingerprints, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
	
	List<RouterMicrodescriptor> downloadRouterMicrodescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException;
	List<RouterMicrodescriptor> downloadRouterMicrodescriptors(Set<HexDigest> fingerprints, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
}
