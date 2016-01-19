package org.limeprotocol.network.modules;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.Command;
import org.limeprotocol.Message;
import org.limeprotocol.Session;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.network.Channel;
import org.limeprotocol.network.Transport;
import org.limeprotocol.testHelpers.Dummy;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.limeprotocol.testHelpers.Dummy.*;
import static org.mockito.Mockito.*;


public class RemotePingChannelModuleTest {

    @Mock
    private ClientChannel channel;
    @Mock
    private Transport transport;
    @Captor
    private ArgumentCaptor<Command> commandCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(transport.isConnected()).thenReturn(true);
        when(channel.getTransport()).thenReturn(transport);
        when(channel.getState()).thenReturn(Session.SessionState.ESTABLISHED);
    }

    private RemotePingChannelModule getTarget(long pingInterval, long pingDisconnectionInterval) {
        return RemotePingChannelModule.createAndRegister(channel, pingInterval, pingDisconnectionInterval);
    }

    @Test
    public void onStateChanged_established_sendsPingsAfterPingInterval() throws InterruptedException, IOException {
        // Arrange
        RemotePingChannelModule target = getTarget(100, 400);

        // Act
        target.onStateChanged(Session.SessionState.ESTABLISHED);
        Thread.sleep(500);

        // Assert
        verify(channel, times(3)).sendCommand(commandCaptor.capture());
        assertEquals("/ping", commandCaptor.getValue().getUri().toString());
        assertEquals(Command.CommandMethod.GET, commandCaptor.getValue().getMethod());
    }

    @Test
    public void onStateChanged_established_sendsFinishingSessionAfterDisconnectionInterval() throws InterruptedException, IOException {
        // Arrange
        RemotePingChannelModule target = getTarget(100, 100);

        // Act
        target.onStateChanged(Session.SessionState.ESTABLISHED);
        Thread.sleep(150);

        // Assert
        verify(channel, times(1)).sendFinishingSession();
    }

    @Test
    public void onReceived_schedulePing_doNotSendPing() throws InterruptedException, IOException {
        // Arrange
        Message message = createMessage(createTextContent());
        RemotePingChannelModule target = getTarget(200, 0);
        target.onStateChanged(Session.SessionState.ESTABLISHED);

        // Act
        Thread.sleep(125);
        target.onReceiving(message);
        Thread.sleep(125);

        // Assert
        verify(channel, never()).sendCommand(any(Command.class));
    }

//    @Test
//    public void schedulePing_inactiveEstablishedChannel_callsDisconnect() throws InterruptedException {
//        // Arrange
//        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED, false, true, 100, 350, null, null, UUID.randomUUID());
//
//        // Act
//        Thread.sleep(390);
//
//        // Assert
//        assertEquals(3, ((TestTransport)target.getTransport()).sentEnvelopes.size());
//        assertTrue(((TestTransport) target.getTransport()).closeInvoked);
//    }
//
//    @Test
//    public void schedulePing_sendEnvelopeAfterReceivingPing_doNotDisconnect() throws InterruptedException, IOException {
//        // Arrange
//        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED, false, true, 100, 300, null, null, UUID.randomUUID());
//
//        // Act
//        Thread.sleep(150);
//        ((TestTransport)target.getTransport()).raiseOnReceive(Dummy.createCommand());
//        Thread.sleep(100);
//
//        // Assert
//        assertEquals(1, ((TestTransport) target.getTransport()).sentEnvelopes.size());
//        assertFalse(((TestTransport) target.getTransport()).closeInvoked);
//    }
}
