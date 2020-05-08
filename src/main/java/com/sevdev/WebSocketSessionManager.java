package com.sevdev;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.websocket.Session;

public class WebSocketSessionManager {

    private HashMap<String, Session> hmap = null;


    /**
     * This class is a singleton. Add static instance and getInstance() method.
     */
    private static WebSocketSessionManager instance = null;
    public static WebSocketSessionManager getInstance() {
        if (instance == null) {
            instance = new WebSocketSessionManager();
        }
        return instance;
    }

    /**
     * Constructor
     */
    public WebSocketSessionManager() {
        hmap = new HashMap<String, Session>();
    }

    /**
     * Add a session to the collection and associate it with a playerName and table Id.
     *
     * @param session - session to add
     * @param playerName - playerName associated with the session
     */
    public void addSession(Session session, String playerName) {
        // TODO: Add the session/playerName into the collection
        hmap.put(playerName, session);
        System.out.println("Registered '" + playerName + "' to Session " + session.getId());
    }

    /**
     * Find the session with the same Id and remove it from the collection.
     *
     * @param session - session to remove
     */
    public void removeSession(Session session) {
        // TODO: find the session with the same Id and remove it from the collection
        Iterator iter = hmap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            if (((Session)entry.getValue()).getId() == session.getId()) {
                hmap.remove((String)entry.getKey());
                System.out.println("Removed registration for Session " + session.getId());
                return;
            }
        }

    }

    /**
     * Send the message via the session associated with the playerName.
     *
     * @param playerName - player to send notification to
     * @param message - message to send to the player
     */
    public void notifyPlayer(String playerName, String message) {

        // If playerName is "ALL" send the message to all sessions
        if (playerName.equals("ALL")) {
            Iterator iter = hmap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry)iter.next();
                Session session = (Session)entry.getValue();
                try {
                    session.getBasicRemote().sendText(message);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            try {
                hmap.get(playerName).getBasicRemote().sendText(message);
                System.out.println("Sent message: '" + message + "' to player: '" + playerName + "'");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
