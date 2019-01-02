package io.onemfive.tor.client;

import io.onemfive.data.Envelope;
import io.onemfive.sensors.BaseSensor;
import io.onemfive.tor.client.core.TorClient;
import io.onemfive.tor.client.core.TorInitializationListener;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * https://www.torproject.org/
 * https://subgraph.com/orchid/index.en.html
 *
 * @author objectorange
 */
public final class TorClientSensor extends BaseSensor {

    private static final Logger LOG = Logger.getLogger(TorClientSensor.class.getName());

    private TorClient client = new TorClient();

    public TorClientSensor() {}

    public String[] getOperationEndsWith() {
        return new String[]{".onion"};
    }

    @Override
    public String[] getURLBeginsWith() {
        return new String[]{"tor"};
    }

    @Override
    public String[] getURLEndsWith() {
        return new String[]{".onion"};
    }


    @Override
    public boolean send(Envelope envelope) {
        return false;
    }

    @Override
    public boolean reply(Envelope envelope) {
        return false;
    }

    @Override
    public boolean start(Properties properties) {
        LOG.info("Starting...");
        client.addInitializationListener(createInitalizationListner());
        client.start();
        client.enableSocksListener();
        return true;
    }


    @Override
    public boolean pause() {
        return false;
    }

    @Override
    public boolean unpause() {
        return false;
    }

    @Override
    public boolean restart() {
        return false;
    }

    @Override
    public boolean shutdown() {

        return false;
    }

    @Override
    public boolean gracefulShutdown() {
        return false;
    }

    private static TorInitializationListener createInitalizationListner() {
        return new TorInitializationListener() {

            public void initializationProgress(String message, int percent) {
                System.out.println(">>> [ "+ percent + "% ]: "+ message);
            }

            public void initializationCompleted() {
                System.out.println("Tor is ready to go!");
            }
        };
    }

    public static void main(String[] args) {
        TorClientSensor sensor = new TorClientSensor();
        sensor.start(null);
    }
}
