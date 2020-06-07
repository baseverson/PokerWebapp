package com.sevdev;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Root resource (exposed at "PokerTable" path)
 */
@Path("Table")
public class PokerTableResource {

    /**
     * Handle HTTP GET request for version info.
     *
     * @return HTTP response with version info
     */
    @GET
    @Path("getVersion")
    public Response getVersion() {
        return Response
                .status(Response.Status.OK)
                .type(MediaType.TEXT_PLAIN)
                .entity("v1.5")
                .build();
    }

    /**
     * Handle HTTP GET request for Table Id.
     *
     * @return HTTP response with Table Id
     */
    @GET
    @Path("getTableId")
    public Response getTableId() {
        return Response
                .status(Response.Status.OK)
                .type(MediaType.TEXT_PLAIN)
                .entity(Table.getInstance().getTableId())
                .build();
    }

    /**
     * Method handling HTTP GET requests for table state. The returned object will be sent
     * to the client as "application/json" media type.
     *
     * @param playerName - player retrieving table state
     *
     * @return HTTP response
     */
    @GET
    @Path("getTableState")
    public Response getTableState(@QueryParam("playerName") String playerName) {
        try {
            String tableStateJSON = Table.getInstance().getTableStateAsJSON(playerName);
            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(tableStateJSON)
                    .build();
        }
        catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }
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
    public Response sitDown(@QueryParam("playerName") String playerName,
                            @QueryParam("seatNum") int seatNum) {
        String result = null;
        try {
            result = Table.getInstance().sitDown(playerName, seatNum);

            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(result)
                    .build();
        }
        catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Method handling HTTP POST requests for a player leaving a seat at a table.
     *
     * @param playerName - name of the player player that is vacating
     *
     * @return String confirming the player left the table
     */
    @POST
    @Path("leaveTable")
    @Produces(MediaType.TEXT_PLAIN)
    public Response leaveTable(@QueryParam("playerName") String playerName) {
        String result = null;
        try {
            result = Table.getInstance().leaveTable(playerName);

            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(result)
                    .build();
        }
        catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }

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
    public Response fold(@QueryParam("playerName") String playerName) {
        String result = null;
        try {
            result =  Table.getInstance().fold(playerName);

            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(result)
                    .build();
        }
        catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }

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
    public Response bet(@QueryParam("playerName") String playerName) {
        String result = null;
        try {
            result =  Table.getInstance().check(playerName);

            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(result)
                    .build();
        }
        catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }

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
    public Response bet(@QueryParam("playerName") String playerName,
                      @QueryParam("betAmount") Integer betAmount) {
        String result = null;
        try {
            result =  Table.getInstance().bet(playerName, betAmount);

            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(result)
                    .build();
        }
        catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }

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
    public Response call(@QueryParam("playerName") String playerName) {
        String result = null;
        try {
            result =  Table.getInstance().call(playerName);

            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(result)
                    .build();
        }
        catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }

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
    public Response allIn(@QueryParam("playerName") String playerName) {
        String result = null;
        try {
            result =  Table.getInstance().allIn(playerName);

            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(result)
                    .build();
        }
        catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }

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

