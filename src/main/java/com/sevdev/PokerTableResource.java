package com.sevdev;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

/**
 * Root resource (exposed at "PokerTable" path)
 */
@Path("PokerTable")
public class PokerTableResource {

    /**
     * Method handling HTTP GET requests for table state. The returned object will be sent
     * to the client as "application/json" media type.
     *
     * @param playerName - player retrieving table state
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("getTableState")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTableState(@QueryParam("playerName") String playerName) {
        return Table.getTable().getTableStateAsJSON(playerName);
    }

    /**
     * Method handling HTTP POST requests for a player sitting down at a table.
     *
     * @param playerName - player taking a seat
     * @param seatNum - seat player has chosen
     *
     * @return String confirming the player sat and at which seat.
     */
    @POST
    @Path("sitDown")
    @Produces(MediaType.TEXT_PLAIN)
    public String sitDown(@QueryParam("playerName") String playerName,
                          @QueryParam("seatNum") int seatNum) {
        return Table.getTable().sitDown(playerName, seatNum);
    }

    /**
     * Method handling HTTP POST requests for a player leaving a seat at a table.
     *
     * @param seatNum - seat player is vacating
     *
     * @return String confirming the player left the table and which seat.
     */
    @POST
    @Path("leaveTable")
    @Produces(MediaType.TEXT_PLAIN)
    public String leaveTable(@QueryParam("seatNum") int seatNum) {
        return Table.getTable().leaveTable(seatNum);
    }

    /**
     * Method handling HTTP POST requests for a player folding a hand.
     *
     * @param playerName - player that is folding
     *
     * @return String confirming the player left the table and which seat.
     */
    @POST
    @Path("fold")
    @Produces(MediaType.TEXT_PLAIN)
    public String fold(@QueryParam("playerName") String playerName) {
        return Table.getTable().fold(playerName);
    }

    /**
     * Method handling HTTP POST requests for a player wanting to check.
     *
     * @param playerName - player that is checking
     *
     * @return String confirming that the player has successfully checked
     */
    @POST
    @Path("check")
    @Produces(MediaType.TEXT_PLAIN)
    public String bet(@QueryParam("playerName") String playerName) {
        return Table.getTable().check(playerName);
    }

    /**
     * Method handling HTTP POST requests for a player wanting to bet.
     *
     * @param playerName - player placing the bet
     * @param betAmount - the amount of the bet
     *
     * @return String confirm the bet was successful
     */
    @POST
    @Path("bet")
    @Produces(MediaType.TEXT_PLAIN)
    public String bet(@QueryParam("playerName") String playerName,
                      @QueryParam("betAmount") Integer betAmount) {
        return Table.getTable().bet(playerName, betAmount);
    }

    /**
     * Method handling HTTP POST requests for a player wanting to call.
     *
     * @param playerName - player that is calling
     *
     * @return String confirm the call was successful
     */
    @POST
    @Path("call")
    @Produces(MediaType.TEXT_PLAIN)
    public String call(@QueryParam("playerName") String playerName) {
        return Table.getTable().call(playerName);
    }

    /**
     * Method handling HTTP POST requests for a player wanting to go All In.
     *
     * @param playerName - player that is going All In
     *
     * @return String confirm the All In was successful
     */
    @POST
    @Path("allIn")
    @Produces(MediaType.TEXT_PLAIN)
    public String allIn(@QueryParam("playerName") String playerName) {
        return Table.getTable().allIn(playerName);
    }

    /**
     * Main - test program
     * @param args
     */
    public static void main(String[] args) {
        PokerTableResource poker = new PokerTableResource();
        //System.out.println(poker.getTableId());
        //System.out.println(poker.getDeck());
    }

}

