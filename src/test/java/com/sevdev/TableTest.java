package com.sevdev;

import junit.framework.TestCase;
import redis.clients.jedis.Jedis;

public class TableTest extends TestCase {
    Jedis jedis = null;

    public TableTest(String name) {
        super(name);
        jedis = new Jedis("192.168.86.16");
    }

    private void setup() {
    }

    public void createPlayers() {
        try {
            jedis.del("player:A");
            jedis.del("player:B");
            jedis.del("player:C");
            jedis.del("player:D");
            PlayerDatabase.getInstance().createPlayer("A");
            PlayerDatabase.getInstance().createPlayer("B");
            PlayerDatabase.getInstance().createPlayer("C");
            PlayerDatabase.getInstance().createPlayer("D");
        }
        catch (Exception e) {
            fail("Exception caught creating players. Message = " + e.getMessage());
        }
    }

    public void playerLogin() {
        try {
            PlayerDatabase.getInstance().login("A");
            PlayerDatabase.getInstance().login("B");
            PlayerDatabase.getInstance().login("C");
            PlayerDatabase.getInstance().login("D");
        }
        catch (Exception e) {
            fail("Exception caught logging in players. Message = " + e.getMessage());
        }
    }

    public void playerBuyIn() {
        try {
            PlayerDatabase.getInstance().getPlayer("A").buyIn(160);
            PlayerDatabase.getInstance().getPlayer("B").buyIn(160);
            PlayerDatabase.getInstance().getPlayer("C").buyIn(160);
            PlayerDatabase.getInstance().getPlayer("D").buyIn(160);
        }
        catch (Exception e) {
            fail("Exception caught buying in chips for players. Message = " + e.getMessage());
        }
    }

    public void testPotCollection() {
        createPlayers();
        playerLogin();
        playerBuyIn();

        Table table = Table.getInstance();

        try {
            table.sitDown("C", 3);
            table.sitDown("D", 4);
            table.sitDown("A", 1);
            table.sitDown("B", 2);
        }
        catch (Exception e) {
            fail("Exception caught sitting players down. Message = " + e.getMessage());
        }

        table.setDealerPosition(3);

        try {
            table.newRound();
        }
        catch (Exception e) {
            fail("Exception caught starting new round. Message = " + e.getMessage());

        }

        table.call("C");
        table.call("D");
        table.call("A");
        table.call("B");

        try {
            table.advanceRound(); // to FLOP
            table.advanceRound(); // to TURN
            table.advanceRound(); // to RIVER
            table.advanceRound(); // to SHOWDOWN
            table.advanceRound(); // to CLEAN_UP
        }
        catch (Exception e) {
            fail("Exception caught advancing round. Message = " + e.getMessage());
        }

    }
}
