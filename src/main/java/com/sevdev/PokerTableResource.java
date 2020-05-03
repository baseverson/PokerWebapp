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
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("getTableState")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTableState(@Context Providers providers, @QueryParam("playerName") String playerName) {
        ContextResolver<Table> myTableResolver = providers.getContextResolver(Table.class, MediaType.WILDCARD_TYPE);
        return myTableResolver.getContext(Table.class).getTableStateAsJSON(playerName);
    }

    /**
     * Method handling HTTP POST requests for a player sitting down at a table.
     *
     * @return String confirming the player sat and at which seat.
     */
    @POST
    @Path("sitDown")
    @Produces(MediaType.TEXT_PLAIN)
    public String sitDown(@Context Providers providers,
                          @QueryParam("playerName") String playerName,
                          @QueryParam("seatNum") int seatNum) {
        ContextResolver<Table> myTableResolver = providers.getContextResolver(Table.class, MediaType.WILDCARD_TYPE);
        return myTableResolver.getContext(Table.class).sitDown(playerName, seatNum);
    }

    /**
     * Method handling HTTP POST requests for a player leaving a seat at a table.
     *
     * @return String confirming the player left the table and which seat.
     */
    @POST
    @Path("leaveTable")
    @Produces(MediaType.TEXT_PLAIN)
    public String leaveTable(@Context Providers providers,
                          @QueryParam("seatNum") int seatNum) {
        ContextResolver<Table> myTableResolver = providers.getContextResolver(Table.class, MediaType.WILDCARD_TYPE);
        return myTableResolver.getContext(Table.class).leaveTable(seatNum);
    }

    public static void main(String[] args) {
        PokerTableResource poker = new PokerTableResource();
        //System.out.println(poker.getTableId());
        //System.out.println(poker.getDeck());
    }
}

