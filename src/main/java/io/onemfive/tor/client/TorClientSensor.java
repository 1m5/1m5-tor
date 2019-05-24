package io.onemfive.tor.client;

import io.onemfive.clearnet.client.ClearnetClientSensor;
import io.onemfive.data.Envelope;
import io.onemfive.data.Message;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Sets up an HttpClientSensor with the local Tor instance as a proxy (127.0.0.1:9150).
 *
 * @author objectorange
 */
public final class TorClientSensor extends ClearnetClientSensor {

    private static final Logger LOG = Logger.getLogger(TorClientSensor.class.getName());

    public TorClientSensor() {
        proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1",9150));
    }

    private File sensorDir;

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
    public boolean send(Envelope e) {
        boolean successful = super.send(e);
        Message m = e.getMessage();
        if(m!=null && m.getErrorMessages()!=null && m.getErrorMessages().size()>0) {
            for(String err : m.getErrorMessages()) {
                LOG.warning(err);
            }
        }
        return successful;
    }

    @Override
    public boolean start(Properties properties) {
        if(super.start(properties)) {
            String sensorsDirStr = properties.getProperty("1m5.dir.sensors");
            if(sensorsDirStr==null) {
                LOG.warning("1m5.dir.sensors property is null. Please set prior to instantiating Tor Client Sensor.");
                return false;
            }
            try {
                sensorDir = new File(new File(sensorsDirStr).getCanonicalPath()+"/tor");
                if(!sensorDir.exists() && !sensorDir.mkdir()) {
                    LOG.warning("Unable to create Tor sensor directory.");
                    return false;
                } else {
                    properties.put("1m5.dir.sensors.tor",sensorDir.getCanonicalPath());
                }
            } catch (IOException e) {
                LOG.warning("IOException caught while building Tor sensor directory: \n"+e.getLocalizedMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        URL url = new URL("https://1m5.io");
//        URL url = new URL("https://3g2upl4pq6kufc4m.onion/?q=1m5&ia=web");
        Envelope e = Envelope.documentFactory();
        e.setAction(Envelope.Action.VIEW);
        e.setURL(url);
        TorClientSensor sensor = new TorClientSensor();
        sensor.start(null);
        sensor.send(e);
    }
}
