package com.sevdev;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.sevdev.RoundState.*;

public class Table {

    // Make this a singleton
    private static Table tableInstance = null;

    /**
     * Singleton getInstance
     *
     * @return The single instance of Table
     */
    public static Table getInstance() {
        if (tableInstance == null) {
            tableInstance = new Table();
        }
        return tableInstance;
    }

    private Deck deck;

    private Integer tableId = 0;
    private Integer numSeats = 0;
    private Integer numPlayers = 0;
    private Integer bigBlind = 0;
    private List<Pot> potList = new ArrayList<Pot>();
    private Integer currentPotNum = 0;
    private Integer currentBet = 0;
    private Integer currentBetPosition = 0;
    private Integer dealerPosition = -1;
    private Integer smallBlindPosition = -1;
    private Integer bigBlindPosition = -1;
    private Integer currentAction = -1;
    private List<Integer> winningSeats = new ArrayList<Integer>();
    private HandType winningHand = HandType.UNDEFINED;
    private RoundState roundState = RoundState.UNDEFINED;
    private List<Card> board = new ArrayList<Card>();
    private List<Seat> seatList = new ArrayList<Seat>();

    public Integer getTableId() {
        return tableId;
    }

    public void setDealerPosition(Integer seatNum) { this.dealerPosition = seatNum; }

    public void addWinningSeat(int seatNum) {
        this.winningSeats.add(seatNum);
        sendTableStateChangeNotification("ALL");
    }

    /**
     * Constructor
     */
    public Table() {
        if (tableId == 0) {
            Random r = new Random();
            tableId = r.nextInt(1000);
        }

        // TODO: For now, run intialize in the construtor. Later we may support multiple tables and the creation
        // of new tables.
        initialize(10, 2);
    }

    /**
     * Initialize the table for a fresh start. Clears everything back to a starting state.
     *
     * @param newNumSeats
     * @param newBigBlind
     */
    public void initialize(Integer newNumSeats, Integer newBigBlind) {
        deck = new Deck();

        numSeats = newNumSeats;
        bigBlind = newBigBlind;
        currentPotNum = 0;
        dealerPosition = -1;
        smallBlindPosition = -1;
        bigBlindPosition = -1;

        currentAction = -1;
        //board = new Card[5];
        //seats = new Seat[newNumSeats];

        // initialize the seat array
        //for (int i=0; i<numSeats; i++) {
        //    seatList.add(new Seat(i));
        //}

        // Set up the linkages for the seat elements to create the linked list
        int i = 0;
        if (numSeats >= 1) {
            seatList.add(new Seat(i));
        }
        for (i = 1; i < numSeats; i++) {
            seatList.add(new Seat(i));
            seatList.get(i-1).setNext(seatList.get(i));
        }
        seatList.get(i-1).setNext(seatList.get(0));
    }

    /**
     * Retrieve the current state of the table. This will be customized based on the playerName,
     * showing him/her only the info they are currently allowed to see.
     *
     * @param playerName - the name of the player requesting the table state
     *
     * @return String containing the current state of the table (JSON formatted string)
     *
     * @throws Exception - table state cannot be translated into JSON
     */
    public String getTableStateAsJSON(String playerName) throws Exception {

        System.out.println("Getting table state - playerName = '" + playerName + "'");

        TableState tableState = new TableState();

        // Include all publicly allowed data.
        tableState.setPlayerName(playerName);
        tableState.setTableId(tableId);
        tableState.setNumSeats(numSeats);
        tableState.setNumPlayers(numPlayers);
        tableState.setBigBlind(bigBlind);
        tableState.setCurrentBet(currentBet);
        tableState.setCurrentBetPosition(currentBetPosition);
        tableState.setDealerPosition(dealerPosition);
        tableState.setSmallBlindPosition(smallBlindPosition);
        tableState.setBigBlindPosition(bigBlindPosition);
        tableState.setCurrentAction(currentAction);
        tableState.setCurrentPotNum(currentPotNum);
        tableState.setWinningSeats(winningSeats);
        tableState.setRoundState(roundState);
        tableState.setBoard(board);
        tableState.setSeats(seatList);
        tableState.setPotList(potList);
        tableState.setLog(ActionLog.getInstance().getLog());

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tableState);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error converting TableState object to JSON.");
        }
    }

    /**
     * Send a notification to the specified player that the table state has been updated
     * and the UI should be refreshed.
     * <p>
     * A playerName value of "ALL" will notify all players sitting at the table.
     *
     * @param playerName
     */
    public void sendTableStateChangeNotification(String playerName) {
        WebSocketSessionManager sessionManager = WebSocketSessionManager.getInstance();

        if (playerName.equals("ALL_SEATED_PLAYERS")) {
            // Notify all players
            for (Seat seat : seatList) {
                if (seat.getPlayer() != null) {
                    sessionManager.notifyPlayer(seat.getPlayer().getPlayerName(), "TableUpdated");
                }
            }
        } else {
            sessionManager.notifyPlayer(playerName, "TableUpdated");
        }
    }

    /**
     * Method for indicating a player wishes to sit in a seat.
     *
     * @param playerName - the name of the player wishing to sit in the seat
     * @param seatNum    - the seat the player wishes to sit in
     *
     * @return String indicating the outcome of the sit-down request
     *
     * @throws Exception - when a player is already in seat or the or when the specified player does not exist
     */
    public String sitDown(String playerName, int seatNum) throws Exception{
        Player player = PlayerDatabase.getInstance().getPlayer(playerName);

        // Check to see if the player was found in the database
        if (player == null) {
            // Player does not exist
            throw new Exception("Player '" + playerName + "' does not exist.");
        }
        // check to see if the seat is already taken
        else if (seatList.get(seatNum).getPlayer() != null) {
            // This seat is already taken.
            throw new Exception("Failed to seat player \"" + playerName + "\" in seat " + seatNum + ". Seat already taken.");
        } else {
            // Seat is open
            seatList.get(seatNum).setPlayer(player);
            numPlayers++;

            // Notify all players that the table has been updates
            sendTableStateChangeNotification("ALL");

            return "player \"" + playerName + "\" successfully seated in seat " + seatNum;
        }
    }

    /**
     * Method for indicating a player wishes to leave the table.
     *
     * @param playerName - name of the player leaving
     * @return String indicating the outcome of the request to leave the table
     */
    public String leaveTable(String playerName) {

        // TODO: Check to make sure the player is really in the seat

        // Find the seat the player is currently occupying (if any)
        int seatNum = getPlayerSeatNum(playerName);

        if (seatNum >= 0) {
            seatList.get(seatNum).setPlayer(null);
            numPlayers--;

            // Notify all players that the table has been updates
            sendTableStateChangeNotification("ALL");

            return "Player '" + playerName + "' has successfully left seat " + seatNum;
        }
        else {
            return "Player '" + playerName + "' is not in a seat.";
        }
    }

    /**
     * Check if the action is on the current player.
     *
     * @param playerName - player checking the action against
     *
     * @return True if action is on the specified player, otherwise false
     */
    public boolean actionOnPlayer(String playerName) {
        // Get the seat that the player is in
        int seatNum = getPlayerSeatNum(playerName);

        // Check to see if that seat is where the current action is
        if (currentAction == seatNum) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Method for indicating a player wishes to fold.
     *
     * @param playerName - player who wished to fold
     *
     * @return - result of the fold
     */
    public String fold(String playerName) {
        // Get which seat the player is in.
        int seatNum = getPlayerSeatNum(playerName);

        // Make sure the player is actually sitting at the table
        if (seatNum == -1) {
            // Do nothing - player is not sitting at a seat, nor is he in the hand.
            return ("Player not sitting at the table");
        } else {
            // Move the current bet to the pot
            potList.get(currentPotNum).incrementSize(seatList.get(seatNum).getPlayerBet());
            seatList.get(seatNum).setPlayerBet(0);

            // Remove the player eligibility from all pots
            for (Pot pot : potList) {
                pot.removeSeat(seatNum);
            }

            // Mark the seat as not in the current hand.
            seatList.get(seatNum).setInHand(false);

            // Clear this seat's cards.
            seatList.get(seatNum).clearCards();
        }

        // If all players but one have folded, end the game.  The single remaining player is the winner.
        if (getPlayersInHand() == 1) {
            finishRound();
            for (int i=0; i<potList.size(); i++) {
                determineWinners(i);
            }
            finishGame();
        }
        else if (actionOnPlayer(playerName)) {
            // Move on to the next player.
            advanceAction();
        }

        // Check to see if this seat is currently the bet position.  Why would anyone fold here and not check?
        if (currentBetPosition == getPlayerSeatNum(playerName)) {
            // Move the bet position to the next player in the hand
            try {
                currentBetPosition = findNextPlayerNotAllIn(currentBetPosition);
            }
            catch (Exception e) {
                System.out.println("Unexpected exception caught");
                System.out.println(e.getMessage());
            }
        }

        // Log the fold
        ActionLog.getInstance().add(playerName + " folds.");

        // Notify all players that the table state has changes and needs to be refreshed.
        sendTableStateChangeNotification("ALL");

        return (playerName + " folded.");
    }

    /**
     * Handles a bet from a player
     *
     * @param playerName: Player that made the bet.
     * @param betAmount: Amount of the bet.
     *
     * @return: Status if the bet was accepted.
     */
    public String bet(String playerName, Integer betAmount) {
        // Verify the action is on the player requesting the action
        if (!actionOnPlayer(playerName)) {
            return ("Out of turn move. Action is not currently on " + playerName);
        }

        // First, find the player's seat number (real seat number, not the array index).
        int seatNum = getPlayerSeatNum(playerName);

        // If the current bet is 0, bet needs to be at least the big blind.
        if (currentBet == 0 && betAmount < bigBlind) {
            return ("Bet must at least be greater than or equal to the big blind.");
        }

        // If the current bet is greater than zero and not equal to the current bet,
        // the bet is a raise and needs to be at least double the current bet.
        if (currentBet > 0 &&
            !betAmount.equals(currentBet) &&
            betAmount < (currentBet * 2)) {
            return ("A raise must be at least twice the current bet.");
        }

        // If the bet is greater than the player's stack, assume the player is going All In.
        if (currentBet > seatList.get(seatNum).getPlayer().getStack()) {
            betAmount = seatList.get(seatNum).getPlayer().getStack();
        }

        try {
            // Deduct the chips from the players stack and set the seat's current bet to the new bet.
            seatList.get(seatNum).getPlayer().adjustStack(betAmount * -1);

            // Update the current bet for the seat
            seatList.get(seatNum).setPlayerBet(betAmount);

            // If the bet is an initial bet or raise, update the currentBet for the table as well as what seat number made the bet.
            if (betAmount > currentBet) {
                // Log the bet/raise
                if (currentBet == 0) {
                    // It's a bet.
                    ActionLog.getInstance().add(playerName + " bets.");
                }
                else {
                    // It's a raise
                    ActionLog.getInstance().add(playerName + " raises.");
                }

                // Set the current bet amount for the table and which seat made the bet/raise.
                currentBet = betAmount;
                currentBetPosition = seatNum;
            }

            // Check to see if the player is All In. If so, set the seat state as such.
            if (seatList.get(seatNum).getPlayer().getStack() == 0) {
                seatList.get(seatNum).setIsAllIn(true);
            }

            // Advance the action.
            advanceAction();

            return ("Accepted bet of " + betAmount + "from " + playerName);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return ("Invalid bet.");
        }
    }

    /**
     * Handles a player making a call.
     *
     * @param playerName - player making the call
     *
     * @return String confirming the success of the call
     */
    public String call(String playerName) {
        // Verify the action is on the player requesting the action
        if (!actionOnPlayer(playerName)) {
            return ("Out of turn move. Action is not currently on " + playerName);
        }

        // Find the seat the player is in
        int seatNum = getPlayerSeatNum(playerName);

        // Figure out the difference between the table's current bet and the player's current bet.  This is the call amount.
        int callAmount = currentBet - seatList.get(seatNum).getPlayerBet();

        // Check first that there are enough chips for a full call. If not, player is going all in.
        if (callAmount > seatList.get(seatNum).getPlayer().getStack()) {
            callAmount = seatList.get(seatNum).getPlayer().getStack();
        }

        try {
            // Deduct the difference between the table's current bet and the player's current bet from the player's stack
            seatList.get(seatNum).getPlayer().adjustStack(callAmount * -1);
            seatList.get(seatNum).increasePlayerBet(callAmount);


            // Check to see if the player stack is now 0. If so, they are all in.
            if (seatList.get(seatNum).getPlayer().getStack() == 0) {
                seatList.get(seatNum).setIsAllIn(true);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return ("Call failed - " + e.getMessage());
        }

        // Log the call
        ActionLog.getInstance().add(playerName + " calls.");

        // Advance the action.
        advanceAction();

        return ("Call successful.");
    }

    /**
     * Handles a check from a player
     *
     * @param playerName - player that is checking
     *
     * @return String confirming the check was successful
     */
    public String check(String playerName) {
        // Verify the action is on the player requesting the action
        if (!actionOnPlayer(playerName)) {
            return ("Out of turn move. Action is not currently on " + playerName);
        }

        // Check to make sure the current best is 0 and checking is allowed.
        if (currentBet != 0) {
            return ("Checking not allowed. Current bet is not 0.");
        }

        // Log the check.
        ActionLog.getInstance().add(playerName + " checks.");

        // Advance the action.
        advanceAction();

        return ("Check successful.");
    }

    /**
     * Handles a player request to go All In
     *
     * @param playerName - player that wishes to go All In
     *
     * @return String confriming the All In was successful
     */
    public String allIn(String playerName) {
        try {
            // Verify the action is on the player requesting the action
            if (!actionOnPlayer(playerName)) {
                return ("Out of turn move. Action is not currently on " + playerName);
            }

            // Find the seat the player is in
            int seatNum = getPlayerSeatNum(playerName);
            int betSize = seatList.get(seatNum).getPlayer().getStack();

            // Set the seat's player bet to the player's entire stack
            seatList.get(seatNum).increasePlayerBet(betSize);

            // Set the player's stack size to zero
            seatList.get(seatNum).getPlayer().adjustStack(betSize * -1);

            // Check to see if this is the new table current bet
            if (seatList.get(seatNum).getPlayerBet() > currentBet) {
                currentBet = seatList.get(seatNum).getPlayerBet();
                currentBetPosition = seatNum;
            }

            // Set the player/seat to All In
            seatList.get(seatNum).setIsAllIn(true);

            // Log the all in
            ActionLog.getInstance().add(playerName + " goes all-in.");

            // Advance the action.
            advanceAction();

            return ("All In successful");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return e.getMessage();
        }
    }

    /**
     * Return the current seat number (not the array index) that the player designated by playerName is occupying.
     *
     * @param playerName - Name of the player for which to find the seat number.
     *
     * @return The seat number the player is currently occupying. Returns -1 if the player is not currently in a seat.
     *         (Does NOT return the array index into seats.)
     */
    public Integer getPlayerSeatNum(String playerName) {
        for (Seat seat : seatList) {
            if (seat != null &&
                seat.getPlayer() != null &&
                seat.getPlayer().getPlayerName().equals(playerName)) {
                // Found the player. Return the seat number.
                return seat.getSeatNum();
            }
        }

        // If we got here, the player was not found in any seat. Return -1;
        return -1;
    }

    /**
     * Get the number of players currently in the hand.
     *
     * @return number of players currently in the hand (seats that have the "inHand" flag set to true).
     */
    public Integer getPlayersInHand() {
        int playersInHand = 0;

        // Loop through all the seats and add up the players in the hand.
        for (Seat seat : seatList) {
            if (seat != null && seat.getInHand()) {
                playersInHand++;
            }
        }
        return playersInHand;
    }

    /**
     * Find the number of players in the hand that are not all in.
     *
     * @return number of player currently in the hand that are not all in.
     */
    public Integer getPlayersInHandNotAllIn() {
        int playersInHandNotAllIn = 0;

        // Loop through all the seats and add up the players in the hand and not all in.
        for (Seat seat : seatList) {
            if (seat != null && seat.getInHand() == true && seat.getIsAllIn() == false) {
                playersInHandNotAllIn++;
            }
        }
        return playersInHandNotAllIn;
    }

    /**
     * Search through the remaining players in the round to find the winning hands.
     *
     * @param potNum - the pot for which to determine the winner(s) and award the chips.
     */
    public void determineWinners(int potNum) {
        // Determine winner(s) of each pot one at a time
        Pot pot = potList.get(potNum);

        try {
            // First, check for the case that only one player is eligible for this pot
            if (pot.getSeatNumberList().size() == 1) {
                // There's only one player eligible for this pot.  He's the winner.
                int seatNum = pot.getSeatNumberList().get(0);
                winningSeats.add(seatNum);
                winningHand = HandType.WIN_BY_FOLD;
                // Give this player all the chips in the pot.
                seatList.get(seatNum).getPlayer().adjustStack(pot.getPotSize());

                // Log the win.
                ActionLog.getInstance().add(seatList.get(seatNum).getPlayer().getPlayerName() +
                                            " wins Pot # " +
                                            potNum +
                                            " by default.");
                return;
            }
            else if (pot.getSeatNumberList().size() >= 2) {
                // More than 2 players are eligible for this pot. Gather all the hands and rank them.
                List<Hand> handList = new ArrayList<Hand>();

                // Check each seat
                for (Integer seatNum : pot.getSeatNumberList()) {
                    // If the seat is in, make a new hand and add all the seat's cards to the hand
                    Hand hand = new Hand(seatNum);
                    hand.addCards(seatList.get(seatNum).getCards());
                    hand.addCards(board);

                    // Evaluate the hand to determine it's rank
                    hand.evaluate();

                    // Add the hand to the list.
                    handList.add(hand);
                }

                // Once all the hands are assembled, sort the hand list by rank to see which hand is the winner
                Collections.sort(handList, Collections.<Hand>reverseOrder());

                //
                // Check for multiple winning hands (ties)
                //

                // There's always at least 1 winner
                int numWinners = 1;

                // Loop through the remaining hands and compare each to the previous.
                for (int i=1; i<handList.size(); i++) {
                    // Compare the next hand to the previous hand. If the result is 0, they tie.
                    if (handList.get(i).compareTo(handList.get(i-1)) == 0) {
                        // Tie found. Increment the number of winners.
                        numWinners++;
                    }
                    else {
                        // The next hand is not equal, which means none of the rest will be.  Stop looking.
                        break;
                    }
                }

                // Now that we know how many winners there are, we can divide the pot and award accordingly.
                int splitWinAmount = potList.get(potNum).getPotSize() / numWinners;
                int remainder = potList.get(potNum).getPotSize() % numWinners;

                // Distribute chips to the winning hand(s) and note the winning seats
                for (int i=0; i<numWinners; i++) {
                    Hand winningHand = handList.get(i);
                    winningHand.incrementWinnings(splitWinAmount);
                    winningSeats.add(winningHand.getSeatNum());

                    // Log the winner(s)
                    ActionLog.getInstance().add("Pot #" +
                                                potNum +
                                                " " +
                                                seatList.get(winningHand.getSeatNum()).getPlayer().getPlayerName() +
                                                " wins with a " +
                                                winningHand.getType());
                }

                // Hand out the remainders starting to the left of the dealer
                Seat seat = seatList.get(dealerPosition);
                while (remainder > 0) {
                    // Get the next seat
                    seat = seat.getNext();

                    // Check to see if this is one of the winning seats.
                    for (int i=0; i<numWinners; i++) {
                        if (handList.get(i).getSeatNum() ==  seat.getSeatNum()) {
                            // This is a winner.  Award a chip from the remainder.
                            handList.get(i).incrementWinnings(1);
                            // Decrement the remainder
                            remainder--;
                        }
                    }
                }

                // Finally, now that all the winners have been determined and the winnings have been distributed to the hands,
                // pass the hand list to the pot.  The winnings will be distributed to the player stacks in finishGame();
                pot.setHandList(handList);
            }
        }
        catch (Exception e) {
            System.out.println("Exception caught in Pot::awardPot() while awarding chips.");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }

    /**
     * Reset the table to finish a game and prepare for the next game.
     */
    public void finishGame() {
        // Reset the board
        board.clear();

        // Reset the round state
        roundState = CLEAN_UP;

        // Reset the action position
        currentAction = 0;

        // Reset the bet position
        currentBetPosition = 0;

        try {
            // Award the pot to the winners
            for (Pot pot : potList) {
                List<Hand> handList = pot.getHandList();
                for (Hand hand : handList) {
                    if (hand.getWinnings() > 0) {
                        // There's something to award.
                        seatList.get(hand.getSeatNum()).getPlayer().adjustStack(hand.getWinnings());

                        // TODO: Add to the action log: Pot #, player, hand, amount of chips won
                    }
                }

            }

//            List<Hand> handList = potList.get(0).getHandList();
//            for (Hand hand : handList) {
//                if (hand.getWinnings() > 0) {
//                    // There's something to award.
//                    seatList.get(hand.getSeatNum()-1).getPlayer().adjustStack(hand.getWinnings());
//
//                    // TODO: Add to the action log: Pot #, player, hand, amount of chips won
//                }
//            }
        }
        catch (Exception e) {
            // Exception caught adding chips to the winning player's stack.  Print it and move on.
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }

        // Clear the pot for the next game
        potList.clear();

        for (Seat seat : seatList) {
            // Clear the "in hand" flg for all seats
            seat.setInHand(false);

            // Clear the "all in" flg for all seats
            seat.setIsAllIn(false);

            // Clear the cards for all seats
            seat.clearCards();
        }
    }

    /**
     * Sets up the next round.
     *
     * @throws Exception - Not enough players sitting at the table to start a new round.  Need minimum of 2.
     */
    public void newRound() throws Exception {
        // Can only start a new round if there are at least 2 players at the table in the hand (with chips)

        // Create the starting pot.
        currentPotNum = 0;
        Pot startingPot = new Pot();

        // Any player sitting at the table with chips is in the next hand
        for (Seat seat : seatList) {
            // Only set the player as in the hand if they have chips
            if (seat.getPlayer() != null && seat.getPlayer().getStack() > 0) {
                seat.setInHand(true);

                // Add this seat as eligible for this pot
                startingPot.addSeat(seat);
            }
        }

        // Add the pot to the pot list
        potList.add(startingPot);

        if (getPlayersInHand() < 2) {
            Exception e = new Exception("Not enough players to start a new round.  Need a minimum of 2.");
            throw e;
        }

        // Reset the round state
        roundState = PRE_FLOP;

        // Reset the winning hand indicator
        winningSeats.clear();

        // Move the dealer button
        if (dealerPosition == -1) {
            // This must be the very first game.  Randomly pick a seat and then move the dealer button to the next player.
            Random r = new Random();
            dealerPosition = r.nextInt(numSeats-1);
        }

        // Move the buttons (dealer, small blind, and big blind) and set the currentAction position.
        incrementDealerPosition();

        // Pull small blind
        seatList.get(smallBlindPosition).getPlayer().adjustStack((bigBlind/2) * -1);
        seatList.get(smallBlindPosition).increasePlayerBet(bigBlind/2);

        // Pull big blind
        seatList.get(bigBlindPosition).getPlayer().adjustStack(bigBlind * -1);
        seatList.get(bigBlindPosition).increasePlayerBet(bigBlind);

        // Set the currentBet to the big blind
        currentBet = bigBlind;

        // Set the current action to the player to the left of the big blind
        currentAction = findNextPlayer(bigBlindPosition);

        // Set the current bet position to the big blind position
        currentBetPosition = bigBlindPosition;

        // Initialize a new deck
        deck.shuffle();

        // Deal 2 cards to each player
        for (int i=0; i<2; i++) {
            Seat seatPtr = seatList.get(dealerPosition).getNext();
            while (seatPtr.getSeatNum() != dealerPosition) {
                // If there is a player in this seat and they are in the hand, deal a card
                if (seatPtr.getPlayer() != null &&
                    seatPtr.getInHand().equals(true)) {
                    seatPtr.addCard(deck.getCard());
                }
                seatPtr = seatPtr.getNext();
            }
            // Don't forget to deal a card to the dealer
            seatPtr.addCard(deck.getCard());
        }

        // Deal board cards
        // Burn a card
        deck.getCard();
        // Flop
        board.add(deck.getCard());
        board.add(deck.getCard());
        board.add(deck.getCard());
        // Burn a card
        deck.getCard();
        // Turn
        board.add(deck.getCard());
        // Burn a card
        deck.getCard();
        // River
        board.add(deck.getCard());

        // Log the new round
        ActionLog.getInstance().add("New Round - Shuffle up and deal!");

        // Notify all players that the table has been updates
        sendTableStateChangeNotification("ALL");
    }

    /**
     * Move the action to the next player still in the hand. Skip the players that are All In.
     */
    public void advanceAction() {
        try {
            // Start out with the currentAction and move on from there.
            int newAction = currentAction;

            while (true) {
                // Try the next player
                newAction = findNextPlayer(newAction);

                if (newAction == currentBetPosition) {
                    // Special case for pre-flop, if the big blind bet was called all the way around, then the
                    // big blind position has an option to raise.
                    if (roundState == PRE_FLOP && newAction == bigBlindPosition && currentBet == bigBlind) {
                        // Set currentBetPosition to the next player so that the Big Blind has a chance to act, but
                        // if he just checks, the next action move will advance to the next round.
                        currentBetPosition = findNextPlayerNotAllIn(currentBetPosition);
                        currentAction = newAction;
                        break;
                    }
                    else {
                        // We've come around to the currentBetPosition (the seat that bet/raised last). Betting is
                        // done. Move to the next round.
                        advanceRound();
                        break;
                    }
                }
                else if (seatList.get(newAction).getIsAllIn() != true) {
                        // Check to see if the next player is not already all in. If not, this seat is the new action.
                        currentAction = newAction;
                        break;
                }

                // If we have not completed the round and have not found a player that can act (not all in),
                // then loop again to find the next player.
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }

        // Notify all players that the table state has changed and needs to be refreshed.
        sendTableStateChangeNotification("ALL");
    }

    /**
     * Clean up the round and prepare for the next round.
     */
    public void finishRound() {

        // Find the smallest player bet. This amount should be removed from each player bet and added to the current pot.
        // If there are any players left with a bet amount after this sweep, a new pot needs to be created and
        // any players still with bet amounts are eligible for that pot.
        // Repeat until all player bets have been completely collected into a pot.

        boolean betsToBeCollected = true;

        // Loop until there are no more bets to be collected
        while (betsToBeCollected) {
            // Find the smallest player bet
            int smallestBet = 0;
            for (Seat seat : seatList) {
                if ((seat.getPlayerBet() > 0 && smallestBet == 0) ||
                        (seat.getPlayerBet() > 0 && seat.getPlayerBet() < smallestBet)) {
                    smallestBet = seat.getPlayerBet();
                }
            }

            // If the smallestBet is still 0, we're done.  No more bets to collect.
            if (smallestBet <= 0) {
                betsToBeCollected = false;
            } else {
                // Loop through each seat and collect the smallestBet amount from each player bet into the currentPot.
                // At the same time, check to see if any players are all-in. If so, a side pot is needed.
                boolean newPotNeeded = false;
                for (Seat seat : seatList) {
                    // If the playerBet is greater than zero, he's in the hand. Collect chips for the pot.
                    if(seat.getPlayerBet() > 0) {
                        potList.get(currentPotNum).incrementSize(smallestBet);
                        seat.decreasePlayerBet(smallestBet);

                        if (seat.getIsAllIn() == true) {
                            // At least one player is all in with this collection. A side pot is needed.
                            newPotNeeded = true;
                        }
                    }
                }

                // If a new pot is needed, create one now.
                if (newPotNeeded == true) {
                    potList.add(new Pot());
                    currentPotNum++;

                    // Add the name of any player eligible for the next pot to the pot.
                    for (Seat seat : seatList) {
                        if ( (seat.getInHand() == true) && // they must be in the hand
                                ( (seat.getPlayer().getStack() > 0) || // and have chips left in their stack
                                        (seat.getPlayerBet() > 0))) { // or still have chips in their current bet to collect
                            potList.get(currentPotNum).addSeat(seat);
                        }
                    }
                }

                // Check for the new smallest bet
                smallestBet = 0;
                for (Seat seat : seatList) {
                    if ((seat.getPlayerBet() > 0 && smallestBet == 0) ||
                            (seat.getPlayerBet() > 0 && seat.getPlayerBet() < smallestBet)) {
                        smallestBet = seat.getPlayerBet();
                    }
                }

                // If the smallestBet is still 0, we're done.  No more bets to collect.
                if (smallestBet <= 0) {
                    betsToBeCollected = false;
                }
            }
        }

        // Set the table's current bet back to 0;
        currentBet = 0;
    }

    /**
     * Manually advance the round to the next state.
     *
     * @return New round state.
     *
     * @throws Exception - when not enough players to start a new round
     */
    public RoundState advanceRound() throws Exception {
        try {
            // Clean up the current round
            finishRound();

            // Set the action to the first player after the dealer that is not All In
            if (getPlayersInHandNotAllIn() <= 1) {
                // One or less players left in the hand that are not all in.  No player action to be taken.
                // Set the action to 0 to tell the UI that no one can act.
                currentAction = 0;
            }
            else {
                currentAction = findNextPlayerNotAllIn(dealerPosition);

                // Set the current bet position to the first action
                currentBetPosition = currentAction;
            }
        }
        catch (Exception e) {
            System.out.println("Unable to set action position. Dealer position passed into findNextPlayer() is invalid.");
            System.out.println(e.getStackTrace());
            currentAction = 0;
        }

        // Move to the next round state
        switch (roundState) {
            case PRE_FLOP:
                roundState = FLOP;
                break;
            case FLOP:
                roundState = TURN;
                break;
            case TURN:
                roundState = RIVER;
                break;
            case RIVER:
                roundState = SHOWDOWN;
                currentAction = -1;
                // Reset currentPotNum to use for processing the pot(s) and determining the winner(s)
                currentPotNum = -1;
                break;
            case SHOWDOWN:
                currentAction = -1;
                // Process each pot in the potList to determine the winner(s) and award chips.
                currentPotNum++;
                // If currentPotNum is greater than the number of pots, then we've processed the winners for all the pots.
                if (currentPotNum < potList.size()) {
                    determineWinners(currentPotNum);
                } else {
                    finishGame();
                }
                break;
            case CLEAN_UP:
                newRound();
                break;
        }

        // Notify all players that the table has been updates
        sendTableStateChangeNotification("ALL");

        return roundState;
    }

    /**
     * Find the next player from the specified seat position
     *
     * @param seatNum - starting seat number for player search
     * @returns Seat position of the next player; 0 if next player not found
     */
    public int findNextPlayer(int seatNum) throws Exception {
        if (seatNum <0 || seatNum >= numSeats) {
            throw new Exception("Invalid seatNum '" + seatNum + "' passed into findNextPlayer()");
        }

        // Find the next seat with a player in it.
        Seat nextSeat = seatList.get(seatNum).getNext();

        // Keep going around until either the next player is found,
        // or we've circled the whole table and are back to the current position.
        while (nextSeat != seatList.get(seatNum)) {
            // if nextSeat has a player in it, then this is the next player.
            if (nextSeat.getPlayer() != null && nextSeat.getInHand()) {
                return nextSeat.getSeatNum();
            }
            // If not, go to the next seat.
            else {
                nextSeat = nextSeat.getNext();
            }
        }

        // No next player found. Return 0
        return -1;
    }

    /**
     * Find the next player from the specified seat position that is not All In
     *
     * @param seatNum - starting seat number for player search
     *
     * @returns Seat position of the next player; 0 if next player not found
     *
     * @throws Exception - thrown from findNextPlayer() if invalid seat number is passed in
     */
    public int findNextPlayerNotAllIn(int seatNum) throws Exception {

        // Start out by finding the next player
        int newSeatNum = seatNum;
        newSeatNum = findNextPlayer(newSeatNum);

        while (newSeatNum != seatNum) {
            // If this player is not All In, return the seat number
            if (!seatList.get(newSeatNum).getIsAllIn()) {
                return newSeatNum;
            }
            // Otherwise, move on to the next player
            else {
                newSeatNum = findNextPlayer(newSeatNum);
            }
        }

        // No additional player that's not All In was found.  Return 0.
        return -1;
    }

    /**
     * Move the dealer button to the next player (the next seat with a player in it).
     */
    public void incrementDealerPosition() {
        try {
            dealerPosition = findNextPlayer(dealerPosition);
            smallBlindPosition = findNextPlayer(dealerPosition);
            bigBlindPosition = findNextPlayer(smallBlindPosition);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }

    /**
     * TEST FUNCTION - Retrieve the current deck of cards.
     *
     * @return String containing the current deck of cards (JSON formatted string)
     */
    public String getDeckAsJSON() {
        return deck.getDeckAsJSON();
    }

    /**
     * TEST FUNCTION - Initialize a new deck of cards.
     *
     * @return None.
     */
    public void initializeDeck() {
        deck.shuffle();
    }

    public static void main(String[] args) {
        Table myTable = Table.getInstance();

        try {
            //myTable.initialize(8, 2);

            //PlayerDatabase.getInstance().createPlayer("Claire");
            PlayerDatabase.getInstance().login("Claire");
            //PlayerDatabase.getInstance().getPlayer("Claire").buyIn(160);
            //PlayerDatabase.getInstance().createPlayer("Traci");
            PlayerDatabase.getInstance().login("Traci");
            //PlayerDatabase.getInstance().getPlayer("Traci").buyIn(160);
            //PlayerDatabase.getInstance().createPlayer("Zoe");
            PlayerDatabase.getInstance().login("Zoe");
            //PlayerDatabase.getInstance().getPlayer("Zoe").buyIn(160);

            myTable.sitDown("Claire", 0);
            myTable.sitDown("Traci", 1);
            myTable.sitDown("Zoe", 2);

            myTable.dealerPosition = 2;

            myTable.newRound();

            //System.out.println(myTable.getTableStateAsJSON("Claire"));

            myTable.fold("Claire");
            myTable.call("Traci");
            myTable.call("Zoe");

            //myTable.advanceRound(); // to TURN

            myTable.allIn("Traci");
            myTable.call("Zoe");
            myTable.call("Claire");

            myTable.advanceRound(); // to RIVER
            myTable.advanceRound(); // to SHOWDOWN
            System.out.println(myTable.getTableStateAsJSON("Claire"));
            myTable.advanceRound(); // to CLEAN_UP

            myTable.newRound();

            System.out.println("");
            System.out.println("");
//            System.out.println(myTable.getTableStateAsJSON("Claire"));
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
