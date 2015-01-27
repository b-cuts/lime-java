package org.limeprotocol.network;

import org.limeprotocol.Envelope;

/**
 * Defines a network connection with a node.
 */
public interface Transport {
    
    /**
     * Sends an envelope to the remote node. 
     * @param envelope
     */
    void send(Envelope envelope);

    /**
     *  Sets the listener for receiving envelopes.
     * @param transportListener
     */
    void setListener(TransportListener transportListener);

    /**
     * Defines a envelope transport listener. 
     */
    public interface TransportListener
    {
        /**
         * Occurs when a envelope is received by the transport.
         * @param envelope
         */
        void onReceive(Envelope envelope);
    }
}