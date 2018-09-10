package io.onemfive.tor.client;

import io.onemfive.clearnet.client.ClearnetClientSensor;
import io.onemfive.sensors.SensorsService;
import io.onemfive.data.Envelope;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

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
public final class TorClientSensor extends ClearnetClientSensor {

    private static final Logger LOG = Logger.getLogger(TorClientSensor.class.getName());

    public TorClientSensor() {super();}

    public TorClientSensor(SensorsService sensorsService, Envelope.Sensitivity sensitivity, Integer priority) {
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

        return true;
    }

}
