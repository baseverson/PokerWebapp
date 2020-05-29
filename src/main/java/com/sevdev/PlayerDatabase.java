package com.sevdev;

import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;

public class PlayerDatabase {

    // Address of the Redis Player DB
    private static String dbAddress = new String("192.168.86.16");

    // Static keys for accessing the DB
    private static String playerKey = new String("player:");
    private static String stackKey = new String("stack");
    private static String buyinKey = new String("buyin");

    // Make this a singleton
    private static PlayerDatabase instance = null;

    public static PlayerDatabase getInstance() {
        if (instance == null) {
            instance = new PlayerDatabase();
            instance.initialize();
        }
        return instance;
    }

    // Memory store for the player DB
    private HashMap<String, Player> playerMap;

    // Connection to the local Redis DB
    Jedis jedis = null;

    /**
     * Constructor
     */
    public PlayerDatabase() {
        playerMap = new HashMap<String, Player>();

    }

    /**
     * Initializer for the Player DB.
     */
    public void initialize() {
        // Connect to the local Redis server
        jedis = new Jedis(dbAddress);

        // Pass the Jedis connection to the Player class
        Player.setJedis(jedis);
    }

    /**
     * Get the list of players as a JSON.
     *
     * @return Stringified JSON of the player list
     */
    public String getPlayerListAsJSON() {
        String jsonPlayerList = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonPlayerList = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(playerMap);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return jsonPlayerList;
    }

    /**
     * Create a new user.
     *
     * @param playerName - name of user to create
     *
     * @return If successful, returns a JSON containing the new player info. Otherwise, returns null.
     *
     * @throws Exception - player already exists in the database.
     */
    public String createPlayer(String playerName) throws Exception {
        // Check to see if the player already exists in the DB
        if (jedis.hexists("player:"+playerName, "stack")) {
            Exception e = new Exception("Player already exists in the database.");
            throw e;
        }
        else {
            // Make sure the player is not already in the map.  If so, remove it.
            if (getPlayer(playerName) != null) {
                playerMap.remove(playerName);
            }

            // Create the player in the DB and set the stack and buyin to 0
            jedis.hsetnx(playerKey+playerName, stackKey, "0");
            jedis.hsetnx(playerKey+playerName, buyinKey, "0");

            return "Player '" + playerName + "' created successfully.";
        }
    }

    /**
     * Player login.
     *
     * @param playerName - name of player to log in
     *
     * @return JSON with player info.
     *
     * @throws Exception - player does not exist in the database
     */
    public String login(String playerName) throws Exception {
        // Check to see if the player already exists in the DB
        if (!jedis.hexists("player:"+playerName, "stack")) {
            Exception e = new Exception("Player does not exist in the database.");
            throw e;
        }
        // Verify playerName does not already exist in the map
        else if (getPlayer(playerName) == null) {
            // Player object does not exist in thew map.  Create a player object and add it to the map.
            playerMap.put(playerName, new Player(playerName));
        }

        // Return a JSON with the player info.
        return getPlayerAsJSON(playerName);
    }

    /**
     * Player logout.
     *
     * @param playerName - name of the player to log out
     *
     * @return String with result of logout action
     */
    public String logout(String playerName) {
        // Remove the player from the map
        playerMap.remove(playerName);

        return ("Player '" + playerName + "' logged out successfully.");
    }

    /**
     *  Retrieve a player object from the database;
     *
     * @param playerName - name of player to retrieve
     *
     * @return player retrieved; null if it does not exist.
     */
    public Player getPlayer(String playerName) {
        return playerMap.get(playerName);
    }

    /**
     * Get the player as a JSON
     *
     * @param playerName - name of player to return info for
     *
     * @return - player info as a JSON
     *
     * @throws Exception - player not found in the database
     */
    public String getPlayerAsJSON(String playerName) throws Exception {
        String jsonPlayer = "";
        Player player = getPlayer(playerName);

        if (player != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                jsonPlayer = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(player);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
           // No player was found
           throw new Exception("Player '" + playerName + "' not found.");
        }

        return jsonPlayer;
    }


/********************************************************************************************************************/
    /**
     * Test main program.
     *
     * @param args
     */
    public static void main(String[] args) {
        PlayerDatabase playerDB = PlayerDatabase.getInstance();

        try {
            playerDB.createPlayer("Traci");
            playerDB.login("Traci");
            System.out.println(playerDB.getPlayerAsJSON("Traci"));

            playerDB.createPlayer("Zoe");
            playerDB.login("Zoe");
            playerDB.createPlayer("Claire");
            playerDB.login("Claire");
            playerDB.createPlayer("Lucky");
            playerDB.login("Lucky");
            playerDB.createPlayer("Donkey");
            playerDB.login("Donkey");

            System.out.println(playerDB.getPlayerListAsJSON());
            System.out.println("player list test complete");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
