package com.sevdev;

import com.sevdev.WebSocketSessionManager;
import redis.clients.jedis.Jedis;

import static java.lang.Integer.parseInt;
import static java.lang.StrictMath.abs;

public class Player {

    // Static strings used as keys in the Redis DB
    private static String playerKey = "player:";
    private static String stackKey = "stack";
    private static String buyinKey = "buyin";

    // TODO: Temporarily hard coding the max stack size
    private static int maxStackSize = 160;

    // Jedis connection to the Redis DB and the method to set it are static - one connection for all players.
    private static Jedis jedis = null;
    public static void setJedis(Jedis newJedis) { Player.jedis = newJedis; }

    private String playerName;
    private Integer stack;
    private Integer buyin;

    /**
     * Constructor.
     *
     * @param newPlayerName - name of the new player
     *
     * @throws Exception - if player does not exist in the database
     */
    public Player (String newPlayerName) throws Exception {
        // Veryify player exists in the DB. Otherwise throw an exception.
        if (jedis.hexists(playerKey+newPlayerName, stackKey)) {
            this.playerName = newPlayerName;
            this.stack = parseInt(jedis.hget(playerKey + playerName, stackKey));
            this.buyin = parseInt(jedis.hget(playerKey + playerName, buyinKey));
        }
        else {
            throw new Exception("Player does not exist in the database.");
        }
    }

    public String getPlayerName() { return playerName; }
    public Integer getStack() { return stack.intValue(); }
    public Integer getBuyin() { return buyin.intValue(); }

    /**
     * Player buys in for (more) chips.
     *
     * @param chipAmount - amount of chips to buy in for
     *
     * @throws Exception - invalid chip amount or buy in would exceed max stack
     */
    public void buyIn(int chipAmount) throws Exception {
        // Check to see if the deduction amount is less than 0.
        if (chipAmount < 0) {
            Exception e = new Exception("Invalid amount to chips to deduct (negative value).");
            throw (e);
        }
        else if ((getStack() + chipAmount) > maxStackSize) {
            Exception e = new Exception("Cannot buy in for a stack size greater than " + maxStackSize);
            throw (e);
        }
        else {
            // Increase the buyin by the chip amount
            this.buyin += chipAmount;
            // Increase the stack by the chip amount
            this.stack += chipAmount;

            // Update the database for both
            jedis.hincrBy(playerKey+playerName, buyinKey, chipAmount);
            jedis.hincrBy(playerKey+playerName, stackKey, chipAmount);

            // Notify the player UI that a player change was made and the page needs to be updated.
            WebSocketSessionManager.getInstance().notifyPlayer(playerName, "PlayerUpdated");
            WebSocketSessionManager.getInstance().notifyPlayer("ALL", "TableUpdated");
        }
    }

    /**
     * Remove the specified chips from the player's stack.
     *
     * @param chipAmount: Amount of chips to deduct.
     *
     * @throws Exception: If chip amount specified is a deduction greater than the player's stack size
     */
    public void adjustStack(int chipAmount) throws Exception {
        // Check to see if this is a dedcution and the deduction amount is greater than the player's stack.
        if (chipAmount < 0 && abs(chipAmount) > getStack()) {
            Exception e = new Exception("Cannot deduct more chips from a player's stack than the stack size.");
            throw (e);
        }
        else {
            // Update the stack
            stack += chipAmount;

            // Update the database
            jedis.hincrBy(playerKey + playerName, stackKey, chipAmount);

            // Notify the player UI that a player change was made and the page needs to be updated.
            WebSocketSessionManager.getInstance().notifyPlayer(playerName, "PlayerUpdated");
        }
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.86.16");
        Player.setJedis(jedis);

        // Create the player in the DB and set the stack and buyin to 0
        jedis.hsetnx(playerKey+"Brandt", stackKey, "0");
        jedis.hsetnx(playerKey+"Brandt", buyinKey, "0");

        try {
            Player player1 = new Player("Brandt");
            System.out.println("Brandt - stack: " + player1.getStack());
            System.out.println("Brandt - buyin: " + player1.getBuyin());
        }
        catch (Exception e) {
            System.out.println("Exception caught creating player object for 'Brandt'.");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }
}
