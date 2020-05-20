package com.sevdev;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.Math.abs;

@Path("PlayerManagement")
public class PokerPlayerManagementResource {

    @GET
    @Path("playerInfo")
    public Response getPlayerInfo(@QueryParam("playerName") String playerName) {
        try {
            String playerJSON = PlayerDatabase.getInstance().getPlayerAsJSON(playerName);
            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(playerJSON)
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
     * Create a new player
     *
     * @param playerName - name for the new player
     *
     * @return If successful, returns a JSON with the new player info. Otherwise, returns INTERNAL_SERVER_ERROR.
     */
    @POST
    @Path("createPlayer")
    public Response createPlayer(@QueryParam("playerName") String playerName) {
        try {
            String responseMsg =  PlayerDatabase.getInstance().createPlayer(playerName);
            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(responseMsg)
                    .build();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Player login
     *
     * @param playerName - name of the player logging in
     *
     * @return If successful, returns a JSON with the new player info. Otherwise, returns INTERNAL_SERVER_ERROR.
     */
    @POST
    @Path("login")
    public Response login(@QueryParam("playerName") String playerName) {
        try {
            String playerJSON =  PlayerDatabase.getInstance().login(playerName);
            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(playerJSON)
                    .build();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Player logout
     *
     * @param playerName - name of the player logging out
     *
     * @return String with result of the logout action
     */
    @POST
    @Path("logout")
    public Response logout(@QueryParam("playerName") String playerName) {
        try {
            Table table = Table.getInstance();

            // If the player is in a hand, fold
            Table.getInstance().fold(playerName);

            // If the player is at the table, leave
            Table.getInstance().leaveTable(playerName);

            String result =  PlayerDatabase.getInstance().logout(playerName);
            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(result)
                    .build();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("buyIn")
    public Response buyIn(@QueryParam("playerName") String playerName,
                        @QueryParam("buyInAmount") int buyInAmount) {
        try {
            // Retrieve the player from the database
            Player player = PlayerDatabase.getInstance().getPlayer(playerName);

            // Check to make sure the player is not null (player exists in the database)
            if (player == null) {
                // If the return is null, the player does not exist in the database.
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .type(MediaType.TEXT_PLAIN)
                        .entity("Buy In failed - player does not exist.")
                        .build();
            }
            else {
                // Player exists.  Execute the buy in.
                player.buyIn(buyInAmount);
                return Response
                        .status(Response.Status.OK)
                        .type(MediaType.TEXT_PLAIN)
                        .entity("Buy In successful. Player: '" + playerName + "' Amount: '" + buyInAmount + "'")
                        .build();
            }
        }
        catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
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

