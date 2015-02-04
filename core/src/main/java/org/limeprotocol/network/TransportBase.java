package org.limeprotocol.network;

import org.limeprotocol.Session;

import java.io.IOException;
import java.util.Arrays;

/**
 *  Base class for transport implementation.
 */
public abstract class TransportBase implements Transport {
    
    private TransportListenerBroadcastSender transportListenerBroadcastSender;
    private boolean closingInvoked;
    private boolean closedInvoked;

    private Session.SessionCompression compression;
    private Session.SessionEncryption encryption;
    
    protected TransportBase() {
        compression = Session.SessionCompression.NONE;
        encryption = Session.SessionEncryption.NONE;
        //TODO use dependency injection ?
        transportListenerBroadcastSender = new TransportListenerBroadcastSenderImpl();
    }

    /**
     * Closes the transport.
     */
    protected abstract void performClose() throws IOException;


    @Override
    public void addListener(TransportListener transportListener) {
        transportListenerBroadcastSender.addListener(transportListener);
    }

    @Override
    public void addListener(TransportListener transportListener, Integer priority) {
        transportListenerBroadcastSender.addListener(transportListener, priority);
    }

    protected TransportListenerBroadcastSender getListenerBroadcastSender() {
        return transportListenerBroadcastSender;
    }

    @Override
    public Session.SessionCompression[] getSupportedCompression() {
        return new Session.SessionCompression[] { getCompression() };
    }

    @Override
    public Session.SessionCompression getCompression() {
        return compression;
    }

    @Override
    public void setCompression(Session.SessionCompression compression) throws IOException  {
        if (Arrays.asList(getSupportedCompression()).contains(compression)) {
            throw new IllegalArgumentException("compression");
        }
        this.compression = compression;
    }

    @Override
    public Session.SessionEncryption[] getSupportedEncryption() {
        return new Session.SessionEncryption[] { getEncryption() };
    }

    @Override
    public Session.SessionEncryption getEncryption() {
        return encryption;
    }

    @Override
    public void setEncryption(Session.SessionEncryption encryption) throws IOException {
        if (Arrays.asList(getSupportedEncryption()).contains(encryption)) {
            throw new IllegalArgumentException("encryption");
        }
        this.encryption = encryption;
    }

    @Override
    public void close() throws IOException {
        if (!closingInvoked) {
            transportListenerBroadcastSender.broadcastOnClosing();
            closingInvoked = true;
        }
        performClose();
        if (!closedInvoked) {
            transportListenerBroadcastSender.broadcastOnClosed();
            closedInvoked = true;
        }
    }
}