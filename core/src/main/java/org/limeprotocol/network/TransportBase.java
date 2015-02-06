package org.limeprotocol.network;

import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;

import java.io.IOException;
import java.util.Arrays;

/**
 *  Base class for transport implementation.
 */
public abstract class TransportBase implements Transport {
    
    private boolean closingInvoked;
    private boolean closedInvoked;

    private SessionCompression compression;
    private SessionEncryption encryption;
    private TransportListener transportListener;

    protected TransportBase() {
        compression = SessionCompression.none;
        encryption = SessionEncryption.none;
    }

    /**
     * Closes the transport.
     */
    protected abstract void performClose() throws IOException;
    
    @Override
    public void setTransportListener(TransportListener transportListener) {
        this.transportListener = transportListener;
    }
    
    protected TransportListener getTransportListener() {
        return transportListener;
    }

    @Override
    public SessionCompression[] getSupportedCompression() {
        return new SessionCompression[] { getCompression() };
    }

    @Override
    public SessionCompression getCompression() {
        return compression;
    }

    @Override
    public void setCompression(SessionCompression compression) throws IOException  {
        if (!Arrays.asList(getSupportedCompression()).contains(compression)) {
            throw new IllegalArgumentException("compression");
        }
        this.compression = compression;
    }

    @Override
    public SessionEncryption[] getSupportedEncryption() {
        return new SessionEncryption[] { getEncryption() };
    }

    @Override
    public SessionEncryption getEncryption() {
        return encryption;
    }

    @Override
    public void setEncryption(SessionEncryption encryption) throws IOException {
        if (!Arrays.asList(getSupportedEncryption()).contains(encryption)) {
            throw new IllegalArgumentException("encryption");
        }
        this.encryption = encryption;
    }

    @Override
    public synchronized void close() throws IOException {
        if (!closingInvoked) {
            if (transportListener != null) {
                transportListener.onClosing();
            }
            closingInvoked = true;
        }
        performClose();
        if (!closedInvoked) {
            if (transportListener != null) {
                transportListener.onClosed();
            }
            closedInvoked = true;
        }
    }
}