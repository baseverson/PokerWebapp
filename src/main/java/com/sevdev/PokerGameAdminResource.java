package com.sevdev;

import static java.lang.Math.abs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.sevdev.RoundState.*;

/**
 * Root resource (exposed at "PokerAdmin" path)
 */
@Path("GameAdmin")
public class PokerGameAdminResource {

    @GET
    @Path("newRound")
    @Produces(MediaType.TEXT_PLAIN)
    public String newRound() {
        try {
            Table.getInstance().newRound();
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
            Table.getInstance().advanceRound();
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
            Table.getInstance().advanceAction();
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
        return Table.getInstance().getTableStateAsJSON("none");
    }

    /**
     * Manually set the seat numer that should win the game.
     *
     * @param seatNum - winning seat number
     *
     * @return HTTP Response indicating successful setting of the winner
     */
    @POST
    @Path("setWinningSeatNum")
    public Response setWinningSeatNum(@QueryParam("seatNum") Integer seatNum) {
        System.out.println("Setting winning seat to " + seatNum);
        Table.getInstance().setWinningSeat(seatNum);
        System.out.println("Winning seat set to " + seatNum);
        return Response
                .status(Response.Status.OK)
                .type(MediaType.TEXT_PLAIN)
                .entity("Winner set to seat #" + seatNum)
                .build();
    }

    /**
     * Method handling HTTP POST request to add chips to a player's stack
     *
     * @return - Status message indicating that chips were added to the player's stack
     */
    @GET
    @Path("addChipsToPlayerStack")
    @Produces(MediaType.TEXT_PLAIN)
    public String addChipsToPlayerStack(@QueryParam("playerName") String playerName,
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

