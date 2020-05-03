package com.sevdev;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

/**
 * Root resource (exposed at "PokerTest" path)
 */
@Path("PokerTest")
public class PokerTestResource {

    Table table;

    /**
     * Constructor
     */
    public PokerTestResource(@Context Providers providers) {
        ContextResolver<Table> myTableResolver = providers.getContextResolver(Table.class, MediaType.WILDCARD_TYPE);
        table =  myTableResolver.getContext(Table.class);
    }

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "Welcome to Brandt's Poker Server!!!";
    }

    @GET
    @Path("getDeck")
    @Produces(MediaType.APPLICATION_JSON)
    //public String getDeck(final @Context ContextResolver<Table> myTableResolver) {
    public String getDeck(@Context Providers providers) {
        ContextResolver<Table> myTableResolver = providers.getContextResolver(Table.class, MediaType.WILDCARD_TYPE);
        return myTableResolver.getContext(Table.class).getDeckAsJSON();
    }

    @POST
    @Path("newDeck")
    @Produces(MediaType.TEXT_PLAIN)
    public String newDeck(@Context Providers providers) {
        ContextResolver<Table> myTableResolver = providers.getContextResolver(Table.class, MediaType.WILDCARD_TYPE);
        myTableResolver.getContext(Table.class).initializeDeck();
        return "New deck initialized.";
    }

    @GET
    @Path("newRound")
    @Produces(MediaType.TEXT_PLAIN)
    public String newRound() {
        try {
            table.newRound();
        }
        catch (Exception e) {
            return e.toString();
        }
        return "New round initialized.";
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
    public String getTableState(@Context Providers providers) {
        ContextResolver<Table> myTableResolver = providers.getContextResolver(Table.class, MediaType.WILDCARD_TYPE);
        return myTableResolver.getContext(Table.class).getTableStateAsJSON("ALL");
    }

    /*
    public static void main(String[] args) {
        PokerTestResource poker = new PokerTestResource();
        //System.out.println(poker.getTableId());
        //System.out.println(poker.getDeck());
    }
    */
}

