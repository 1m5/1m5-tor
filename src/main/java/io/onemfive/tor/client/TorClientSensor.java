package io.onemfive.tor.client;

import io.onemfive.data.Envelope;
import io.onemfive.sensors.BaseSensor;
import io.onemfive.sensors.SensorStatus;
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
public final class TorClientSensor extends BaseSensor implements TorInitializationListener {

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
        updateStatus(SensorStatus.STARTING);
        client.addInitializationListener(this);
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
        updateStatus(SensorStatus.SHUTTING_DOWN);
        client.stop();
        updateStatus(SensorStatus.SHUTDOWN);
        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        updateStatus(SensorStatus.GRACEFULLY_SHUTTING_DOWN);
        client.stop();
        updateStatus(SensorStatus.GRACEFULLY_SHUTDOWN);
        return true;
    }


    public void initializationProgress(String message, int percent) {
        updateStatus(SensorStatus.NETWORK_WARMUP);
        System.out.println(">>> [ "+ percent + "% ]: "+ message);
    }

    public void initializationCompleted() {
        updateStatus(SensorStatus.NETWORK_CONNECTED);
        System.out.println("Tor is ready to go!");
    }

    public static void main(String[] args) {
        TorClientSensor sensor = new TorClientSensor();
        sensor.start(null);
        while(sensor.getStatus()==SensorStatus.STARTING || sensor.getStatus()==SensorStatus.NETWORK_WARMUP) {
            LOG.info("...waiting on startup...");
            try {
                synchronized (sensor) {
                    sensor.wait(1 * 60 * 1000);
                }
            } catch (InterruptedException ex) {
            }
        }

        while(sensor.getStatus()!=SensorStatus.SHUTTING_DOWN || sensor.getStatus()!=SensorStatus.GRACEFULLY_SHUTTING_DOWN) {
            LOG.info("...running...");
            try {
                synchronized (sensor) {
                    sensor.wait(1 * 60 * 1000);
                }
            } catch (InterruptedException ex) {
            }
        }
    }
}
