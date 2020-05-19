package com.sevdev;

import redis.clients.jedis.Jedis;

import static java.lang.Integer.parseInt;
import static java.lang.StrictMath.abs;
import static java.lang.String.*;

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
    //private Integer stackSize;
    //private Integer totalBuyIn;

    /**
     * Constructor.
     *
     * @param newPlayerName - name of the new player
     */
    public Player (String newPlayerName) {
        playerName = newPlayerName;
        //stackSize = 0;
        //totalBuyIn = 0;
    }

    /**
     * Copy constructor.
     *
     * @param playerToCopy - player object to copy
     */
    public Player (Player playerToCopy) {
        this.playerName = playerToCopy.playerName;
        //this.stackSize = playerToCopy.stackSize;
        //this.totalBuyIn = playerToCopy.totalBuyIn;
    }

    public String getPlayerName() { return playerName; }
    public Integer getStack() { return parseInt(jedis.hget(playerKey + playerName, stackKey)); }
    public Integer getBuyIn() { return parseInt(jedis.hget(playerKey + playerName, buyinKey)); }

    /**
     * Player buys in for (more) chips.
     *
     * @param chipAmount - amount of chips to buy in for
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
        else{
            jedis.hincrBy(playerKey+playerName, buyinKey, chipAmount);
            jedis.hincrBy(playerKey+playerName, stackKey, chipAmount);
            //this.totalBuyIn += chipAmount;
            //this.stackSize += chipAmount;

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
            jedis.hincrBy(playerKey + playerName, stackKey, chipAmount);
            // Notify the player UI that a player change was made and the page needs to be updated.
            WebSocketSessionManager.getInstance().notifyPlayer(playerName, "PlayerUpdated");
        }
    }
}
