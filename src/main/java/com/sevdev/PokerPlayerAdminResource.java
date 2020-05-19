package com.sevdev;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("PlayerAdmin")
public class PokerPlayerAdminResource {


    /**
     * Method handling HTTP POST request to add chips to a player's stack
     *
     * @return - Status message indicating that chips were added to the player's stack
     */
    @GET
    @Path("increaseStack")
    @Produces(MediaType.TEXT_PLAIN)
    public String increaseStack(@QueryParam("playerName") String playerName,
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
    @Path("decreaseStack")
    @Produces(MediaType.TEXT_PLAIN)
    public String decreaseStack(@QueryParam("playerName") String playerName,
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

}
