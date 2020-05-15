package com.sevdev;

public class Player {

    // TODO: Temporarily hard coding the max stack size
    private static int maxStackSize = 160;

    private String playerName;
    private Integer stackSize;
    private Integer totalBuyIn;

    /**
     * Constructor.
     *
     * @param newPlayerName - name of the new player
     */
    public Player (String newPlayerName) {
        playerName = newPlayerName;
        stackSize = 0;
        totalBuyIn = 0;
    }

    /**
     * Copy constructor.
     *
     * @param playerToCopy - player object to copy
     */
    public Player (Player playerToCopy) {
        this.playerName = playerToCopy.playerName;
        this.stackSize = playerToCopy.stackSize;
        this.totalBuyIn = playerToCopy.totalBuyIn;
    }

    public String getPlayerName() { return playerName; }
    public Integer getStackSize() { return stackSize; }
    public Integer getTotalBuyIn() { return totalBuyIn; }

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
        else if ((this.stackSize + chipAmount) > maxStackSize) {
            Exception e = new Exception("Cannot buy in for a stack size greater than " + maxStackSize);
            throw (e);
        }
        else{
            this.totalBuyIn += chipAmount;
            this.stackSize += chipAmount;

            // Notify the player UI that a player change was made and the page needs to be updated.
            WebSocketSessionManager.getInstance().notifyPlayer(playerName, "PlayerUpdated");
            WebSocketSessionManager.getInstance().notifyPlayer("ALL", "TableUpdated");
        }
    }

    /**
     * Remove the specified chips from the player's stack.
     *
     * @param chipAmount: Amount of chips to deduct.
     * @throws Exception: If chip amount specified is less than 0 or greater than the players stack size.
     */
    public void deductChipsFromStack(int chipAmount) throws Exception {
        // Check to see if the deduction amount is less than 0.
        if (chipAmount < 0) {
            Exception e = new Exception("Invalid amount to chips to deduct (negative value).");
            throw (e);
        }
        // Check to see if the deduction amount is greater than the player's stack.
        else if (chipAmount > stackSize) {
            Exception e = new Exception("Cannot deduct more chips from a player's stack than the stack size.");
            throw (e);
        }
        else {
            stackSize -= chipAmount;

            // Notify the player UI that a player change was made and the page needs to be updated.
            WebSocketSessionManager.getInstance().notifyPlayer(playerName, "PlayerUpdated");
        }
    }

    /**
     * Add chips to the player's stack.
     *
     * @param chipAmount - amount of chips to add to this player's stack
     */
    public void addChipsToStack(int chipAmount) throws Exception {
        // Check to see if the deduction amount is less than 0.
        if (chipAmount < 0) {
            Exception e = new Exception("Invalid amount to chips to deduct (negative value).");
            throw (e);
        }
        else {
            stackSize += chipAmount;

            // Notify the player UI that a player change was made and the page needs to be updated.
            WebSocketSessionManager.getInstance().notifyPlayer(playerName, "PlayerUpdated");
        }
    }
}
