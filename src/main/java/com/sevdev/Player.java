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

}
