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
    @Path("getTable")
    @Produces(MediaType.TEXT_PLAIN)
    //public String getTableId(final @Context ContextResolver<Table> myTable) {
    public String getTableId(@Context Providers providers) {
        ContextResolver<Table> myTableResolver = providers.getContextResolver(Table.class, MediaType.WILDCARD_TYPE);
        return myTableResolver.getContext(Table.class).getTableId();
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

    public static void main(String[] args) {
        PokerTestResource poker = new PokerTestResource();
        //System.out.println(poker.getTableId());
        //System.out.println(poker.getDeck());
    }
}

