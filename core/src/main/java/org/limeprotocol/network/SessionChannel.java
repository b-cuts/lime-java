package org.limeprotocol.network;

import org.limeprotocol.Session;

import java.io.IOException;

/**
 * Defines a session envelopes exchanging channel.
 */
public interface SessionChannel {
    /**
     * Sends a session to the remote node.
     * @param session
     */
    void sendSession(Session session) throws IOException;

    /**
     * Sets the listener for receiving session.
     * The listener is removed after receiving a session envelope.
     * @param listener
     */
    void enqueueSessionListener(SessionChannelListener listener);

    /**
     * Defines a session channel listener.
     */
    interface SessionChannelListener {
        /**
         * Occurs when a session is received by the channel.
         * @param session
         */
        void onReceiveSession(Session session);
    }
}
