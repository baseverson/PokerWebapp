package com.sevdev;

public class Player {
    private String playerName;
    private Integer stackSize;

    public Player (String newPlayerName, int newStackSize) {
        playerName = newPlayerName;
        stackSize = newStackSize;
    }

    public String getPlayerName() { return playerName; }
    public Integer getStackSize() { return stackSize; }

    public void setStackSize(int newStackSize) { this.stackSize = newStackSize; }

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
        }
    }
}
