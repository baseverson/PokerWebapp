package com.sevdev;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    //public String getTableId(final @Context ContextResolver<Table> myTable) {
    public String getTableId(@Context Providers providers) {
        ContextResolver<Table> myTableResolver = providers.getContextResolver(Table.class, MediaType.WILDCARD_TYPE);
        return myTableResolver.getContext(Table.class).getTableStateAsJSON();
    }

    public static void main(String[] args) {
        PokerTableResource poker = new PokerTableResource();
        //System.out.println(poker.getTableId());
        //System.out.println(poker.getDeck());
    }
}

