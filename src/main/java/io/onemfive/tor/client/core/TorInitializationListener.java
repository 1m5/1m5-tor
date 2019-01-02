package io.onemfive.tor.client.core;

public interface TorInitializationListener {
	void initializationProgress(String message, int percent);
	void initializationCompleted();
}
