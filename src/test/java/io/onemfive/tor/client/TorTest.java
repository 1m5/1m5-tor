package io.onemfive.tor.client;

import io.onemfive.data.Envelope;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.Properties;

/**
 * Tests
 *
 * @author objectorange
 */
public class TorTest {

    private TorClientSensor sensor;

    @Before
    public void init() {
        Properties properties = new Properties();
        properties.setProperty("1m5.dir.sensors",".");
        sensor = new TorClientSensor();
        sensor.start(properties);
    }

    @Test
    public void testRequest() throws Exception {
        URL url = new URL("https://1m5.io");
//        URL url = new URL("https://3g2upl4pq6kufc4m.onion/?q=1m5&ia=web");
        Envelope e = Envelope.documentFactory();
        e.setAction(Envelope.Action.VIEW);
        e.setURL(url);
        sensor.send(e);
    }
}
