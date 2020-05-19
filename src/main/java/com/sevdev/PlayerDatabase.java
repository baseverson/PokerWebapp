package com.sevdev;

import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;

public class PlayerDatabase {

    // Make this a singleton
    private static PlayerDatabase instance = null;

    public static PlayerDatabase getInstance() {
        if (instance == null) {
            instance = new PlayerDatabase();
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
        jedis = new Jedis("192.168.86.16");

        // TODO: Read all players from the Redis DB and construct the player list in memory
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
        // Verify playerName does not already exist in the map
        if (getPlayer(playerName) == null) {
            // Player does not exist. Create user object and add to list.
            Player player = new Player(playerName);
            playerMap.put(playerName, player);

            // Create the player in Redis
            jedis.set("player:" + playerName, "This will eventually be a JSON");

            return getPlayerAsJSON(playerName);
        }
        else {
            Exception e = new Exception("Player already exists in the database.");
            System.out.println(e.getMessage());
            throw e;
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
        // Verify playerName already exists in the map
        if (getPlayer(playerName) != null) {
            // Player does exist. Return a JSON with the player info.
            return getPlayerAsJSON(playerName);
        }
        else {
            Exception e = new Exception("Player down not exist in the database.");
            throw e;
        }
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
        PlayerDatabase playerDB = new PlayerDatabase();

        playerDB.initialize();


        System.out.println("Retrieving player 'Brandt'");
        if (playerDB.getPlayer("Brandt") == null) {
            System.out.println("SUCCESS - return is null.");
        }
        else {
            System.out.println("FAIL - return is NOT null.");
        }

        try {
            System.out.println("Adding Brandt - 1st time");
            playerDB.createPlayer("Brandt");
            System.out.println("Player 'Brandt' added successfully.");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println("Adding Brandt - 2nd time");
            playerDB.createPlayer("Brandt");
            System.out.println("Player 'Brandt' added successfully.");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Retrieving player 'Brandt'");
        if (playerDB.getPlayer("Brandt") == null) {
            System.out.println("FAIL - return is null.");
        }
        else {
            System.out.println("SUCCESS - return is NOT null.");
        }
    }
}
