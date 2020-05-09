package com.sevdev;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import java.io.IOException;
import java.util.StringTokenizer;

@ServerEndpoint("/PokerWebSocket")
public class PokerWebSocket {

    /**
     * Called when web socket is opened.
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen::" + session.getId());

    }

    /**
     * Called when web socket is closed.
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        System.out.println("onClose::" +  session.getId());
        WebSocketSessionManager.getInstance().removeSession(session);
    }

    /**
     * Called when message is received over the socket.
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("onMessage::From=" + session.getId() + " Message=" + message);

        WebSocketSessionManager sessionMgr = WebSocketSessionManager.getInstance();

        // Check the message type and take action accordingly
        StringTokenizer tokenizer = new StringTokenizer(message, ":");
        while (tokenizer.hasMoreElements()) {

            // Check to see if this is a player registration message.  If so, add the session to
            // the session manager and associate it with the supplier player name.
            if (tokenizer.nextElement().equals("RegisterSession")) {
                sessionMgr.addSession(session, tokenizer.nextElement().toString());
            }
            // Check to see if this is an unregister message.  If so, find the session with the matching
            // session Id and remove it from the map.
            else if (tokenizer.nextElement().equals("UnregisterSession")) {
                sessionMgr.removeSession(session);
            }
        }

    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("onError::" + t.getMessage());
    }
}
