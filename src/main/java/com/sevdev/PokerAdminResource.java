package com.sevdev;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.sevdev.RoundState.*;

/**
 * Root resource (exposed at "PokerAdmin" path)
 */
@Path("PokerAdmin")
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

    /*
    public static void main(String[] args) {
        PokerTestResource poker = new PokerTestResource();
        //System.out.println(poker.getTableId());
        //System.out.println(poker.getDeck());
    }
    */
}

