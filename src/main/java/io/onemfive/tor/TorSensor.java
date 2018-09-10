package io.onemfive.tor;

import io.onemfive.clearnet.ClearnetSensor;
import io.onemfive.clearnet.HttpEnvelopeHandler;
import io.onemfive.sensors.SensorsService;
import io.onemfive.data.Envelope;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Tor Client Configuration: Install Tor Browser (https://www.torproject.org/projects/torbrowser.html.en)
 *
 * Tor Hidden Service Configuration: https://www.torproject.org/docs/tor-onion-service.html.en
 *
 * @author objectorange
 */
public final class TorSensor extends ClearnetSensor {

    private static final Logger LOG = Logger.getLogger(TorSensor.class.getName());

    public static final String PROP_TOR_CLIENT = "1m5.tor.client"; // true | false
    public static final String PROP_TOR_HIDDEN_SERVICE = "1m5.tor.hiddenservie"; // true | false

    public TorSensor(SensorsService sensorsService, Envelope.Sensitivity sensitivity, Integer priority) {
        super(sensorsService, sensitivity, priority);
        proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1",9150));
    }

    @Override
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
    public boolean start(Properties properties) {
        LOG.info("Starting...");

        if("true".equals(properties.getProperty(PROP_TOR_CLIENT))) {
            httpSpec = new ConnectionSpec
                    .Builder(ConnectionSpec.CLEARTEXT)
                    .build();
             httpClient = new OkHttpClient.Builder()
                        .connectionSpecs(Collections.singletonList(httpSpec))
                        .retryOnConnectionFailure(true)
                        .followRedirects(true)
                        .proxy(proxy)
                        .build();

            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");
            SSLContext sc = null;
            try {
                sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                httpsCompatibleSpec = new ConnectionSpec
                        .Builder(ConnectionSpec.COMPATIBLE_TLS)
//                    .supportsTlsExtensions(true)
//                    .allEnabledTlsVersions()
//                    .allEnabledCipherSuites()
                        .build();

                httpsCompatibleClient = new OkHttpClient.Builder()
                            .sslSocketFactory(sc.getSocketFactory(), x509TrustManager)
                            .hostnameVerifier(hostnameVerifier)
                            .proxy(proxy)
                            .build();

                httpsStrongSpec = new ConnectionSpec
                        .Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
                        .cipherSuites(
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                        .build();

                httpsStrongClient = new OkHttpClient.Builder()
                            .connectionSpecs(Collections.singletonList(httpsStrongSpec))
                            .retryOnConnectionFailure(true)
                            .followSslRedirects(true)
                            .sslSocketFactory(sc.getSocketFactory(), x509TrustManager)
                            .hostnameVerifier(hostnameVerifier)
                            .proxy(proxy)
                            .build();

            } catch (Exception e) {
                e.printStackTrace();
                LOG.warning(e.getLocalizedMessage());
            }
        }

        if("true".equals(properties.getProperty(PROP_TOR_HIDDEN_SERVICE))) {
            // TODO: Configure Tor Hidden Service
            ContextHandler context = new ContextHandler();
            context.setContextPath( "/" );
            context.setHandler( new HttpEnvelopeHandler(this) );

            String host = "127.0.0.1";
//            String hostProp = properties.getProperty(PROP_HTTP_SERVER_IP);
//            if(hostProp != null && !"".equals(hostProp)) {
//                host = hostProp;
//            }
            LOG.info("HTTP Server Host: "+host);

            int port = 8080;
//            String portStr = properties.getProperty(PROP_HTTP_SERVER_PORT);
//            if(portStr != null) {
//                port = Integer.parseInt(portStr);
//            }
            LOG.info("HTTP Server Port: "+port);

            InetSocketAddress addr = new InetSocketAddress(host,port);
            server = new Server(addr);
            server.setHandler(context);

            try {
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            server.dumpStdErr();
            try {
                // The use of server.join() the will make the current thread join and
                // wait until the server is done executing.
                // See
                // http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
                server.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LOG.info("Started.");
        return true;
    }

}
