package io.onemfive.tor.client;

import io.onemfive.clearnet.client.ClearnetClientSensor;
import io.onemfive.data.Envelope;
import io.onemfive.data.Message;

import java.net.*;
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
