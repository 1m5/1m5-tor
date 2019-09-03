package io.onemfive.tor.client;

import io.onemfive.clearnet.client.ClearnetClientSensor;
import io.onemfive.data.Envelope;
import io.onemfive.data.Message;
import io.onemfive.data.util.DLC;
import io.onemfive.sensors.SensorStatus;
import io.onemfive.sensors.SensorsService;

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
        // Setup local Tor instance as proxy for Tor Client
        proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1",9050));
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
        LOG.info("Tor Sensor sending request...");
        boolean successful = super.send(e);
        if(successful) {
            LOG.info("Tor Sensor successful response received.");
            // Change flag to None so Client Server Sensor will pick it back up
            e.setSensitivity(Envelope.Sensitivity.NONE);
            DLC.addRoute(SensorsService.class, SensorsService.OPERATION_REPLY, e);
            if(!getStatus().equals(SensorStatus.NETWORK_CONNECTED)) {
                LOG.info("Tor Network status changed back to CONNECTED.");
                updateStatus(SensorStatus.NETWORK_CONNECTED);
            }
        }
        return successful;
    }

    protected void handleFailure(Message m) {
        if(m!=null && m.getErrorMessages()!=null && m.getErrorMessages().size()>0) {
            boolean blocked = false;
            for (String err : m.getErrorMessages()) {
                LOG.warning("HTTP Error Message (Tor): " + err);
                if(!blocked) {
                    switch (err) {
                        case "403": {
                            // Forbidden
                            LOG.info("Received HTTP 403 response (Tor): Forbidden. Tor Sensor considered blocked.");
                            updateStatus(SensorStatus.NETWORK_BLOCKED);
                            blocked = true;
                            break;
                        }
                        case "408": {
                            // Request Timeout
                            LOG.info("Received HTTP 408 response (Tor): Request Timeout. Tor Sensor considered blocked.");
                            updateStatus(SensorStatus.NETWORK_BLOCKED);
                            blocked = true;
                            break;
                        }
                        case "410": {
                            // Gone
                            LOG.info("Received HTTP 410 response (Tor): Gone. Tor Sensor considered blocked.");
                            updateStatus(SensorStatus.NETWORK_BLOCKED);
                            blocked = true;
                            break;
                        }
                        case "418": {
                            // I'm a teapot
                            LOG.warning("Received HTTP 418 response (Tor): I'm a teapot. Tor Sensor ignoring.");
                            break;
                        }
                        case "451": {
                            // Unavailable for legal reasons; your IP address might be denied access to the resource
                            LOG.info("Received HTTP 451 response (Tor): unavailable for legal reasons. Tor Sensor considered blocked.");
                            // Notify Sensor Manager Tor is getting blocked
                            updateStatus(SensorStatus.NETWORK_BLOCKED);
                            blocked = true;
                            break;
                        }
                        case "511": {
                            // Network Authentication Required
                            LOG.info("Received HTTP511 response (Tor): network authentication required. Tor Sensor considered blocked.");
                            updateStatus(SensorStatus.NETWORK_BLOCKED);
                            blocked = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean reply(Envelope e) {
        return super.reply(e);
    }

    @Override
    public boolean start(Properties properties) {
        if(super.start(properties)) {
            LOG.info("Starting Tor Client Sensor...");
            String sensorsDirStr = properties.getProperty("1m5.dir.sensors");
            if(sensorsDirStr==null) {
                LOG.warning("1m5.dir.sensors property is null. Please set prior to instantiating Tor Client Sensor.");
                return false;
            }
            try {
                sensorDir = new File(new File(sensorsDirStr),"tor");
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
        } else {
            LOG.warning("Clearnet Client Sensor failed to start. Unable to start Tor Client Sensor.");
            return false;
        }
    }

}
