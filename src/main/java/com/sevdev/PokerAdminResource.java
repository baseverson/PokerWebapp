package com.sevdev;

import static java.lang.Math.abs;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static com.sevdev.RoundState.*;

/**
 * Root resource (exposed at "PokerAdmin" path)
 */
@Path("Admin")
public class PokerAdminResource {

    @GET
    @Path("newRound")
    @Produces(MediaType.TEXT_PLAIN)
    public String newRound() {
        try {
            Table.getTable().newRound();
        }
        catch (Exception e) {
            return e.toString();
        }
        return "New round initialized.";
    }

    /**
     * Method handling HTTP GET requests to move the game to the next round.
     *
     * @return - Status message indicating what is the new current round
     */
    @GET
    @Path("advanceRound")
    @Produces(MediaType.TEXT_PLAIN)
    public String advanceRound() {
        RoundState rs = UNDEFINED;
        try {
            Table.getTable().advanceRound();
        }
        catch (Exception e) {
            return e.toString();
        }
        return "Round advanced to " + rs;
    }

    /**
     * Method handling HTTP GET requests to move the action to the next player.
     *
     * @return - Status message including that the action was advanced.
     */
    @GET
    @Path("advanceAction")
    @Produces(MediaType.TEXT_PLAIN)
    public String advanceAction() {
        try {
            Table.getTable().advanceAction();
        }
        catch (Exception e) {
            return e.toString();
        }
        return "Action advanced.";
    }

    @GET
    @Path("getPlayerDatabase")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPlayerDatabase() {
        return PlayerDatabase.getInstance().getPlayerListAsJSON();
    }

    /**
     * Method handling HTTP GET requests for the complete table state. The returned object will be sent
     * to the client as "application/json" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("getTotalTableState")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTableState() {
        return Table.getTable().getTableStateAsJSON("none");
    }

    /**
     * Method handling HTTP POST request to add chips to a player's stack
     *
     * @return - Status message indicating that chips were added to the player's stack
     */
    @GET
    @Path("addChipsToPlayer")
    @Produces(MediaType.TEXT_PLAIN)
    public String addChipsToPlayer(@QueryParam("playerName") String playerName,
                                   @QueryParam("chipAmount") Integer chipAmount) {
        try {
            // TODO
            //return Table.getTable().adjustPlayerChips(playerName, abs(chipAmount));
            return "";
        }
        catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * Method handling HTTP POST request to remove chips to a player's stack
     *
     * @return - Status message indicating that chips were removed to the player's stack
     */
    @GET
    @Path("removeChipsFromPlayer")
    @Produces(MediaType.TEXT_PLAIN)
    public String removeChipsFromPlayer(@QueryParam("playerName") String playerName,
                                        @QueryParam("chipAmount") Integer chipAmount) {
        try {
            // TODO
            //return Table.getTable().adjustPlayerChips(playerName, (abs(chipAmount) * -1));
            return "";
        }
        catch (Exception e) {
            return e.toString();
        }
    }


    /*
    public static void main(String[] args) {
        PokerTestResource poker = new PokerTestResource();
        //System.out.println(poker.getTableId());
        //System.out.println(poker.getDeck());
    }
    */
}

