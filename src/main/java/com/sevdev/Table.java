package com.sevdev;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    private Integer pot = 0;
    private Integer currentBet = 0;
    private Integer currentBetPosition = 0;
    private Integer dealerPosition = 0;
    private Integer smallBlindPosition = 0;
    private Integer bigBlindPosition = 0;
    private Integer currentAction = 0;
    private Integer winningSeat = 0;
    private HandType winningHand = HandType.UNDEFINED;
    private RoundState roundState = RoundState.UNDEFINED;
    private Card board[];
    private Seat seats[];

    public Integer getTableId() {
        return tableId;
    }

    public void setWinningSeat(int seatNum) {
        this.winningSeat = seatNum;
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
        initialize(12, 2);
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
        pot = 0;
        dealerPosition = 0;
        currentAction = 0;
        board = new Card[5];
        seats = new Seat[newNumSeats];

        // initialize the seat array
        int i = 0;
        if (numSeats >= 1) {
            seats[i] = new Seat(i + 1);
        }
        for (i = 1; i < numSeats; i++) {
            seats[i] = new Seat(i + 1);
            seats[i - 1].setNext(seats[i]);
        }
        seats[i - 1].setNext(seats[0]);
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
        tableState.setPot(pot);
        tableState.setCurrentBet(currentBet);
        tableState.setCurrentBetPosition(currentBetPosition);
        tableState.setDealerPosition(dealerPosition);
        tableState.setSmallBlindPosition(smallBlindPosition);
        tableState.setBigBlindPosition(bigBlindPosition);
        tableState.setCurrentAction(currentAction);
        tableState.setWinningSeat(winningSeat);
        tableState.setWinningHand(winningHand);
        tableState.setRoundState(roundState);
        tableState.setBoard(board);
        tableState.setSeats(seats);

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
            for (int i = 0; i < seats.length; i++) {
                if (seats[i].getPlayer() != null) {
                    sessionManager.notifyPlayer(seats[i].getPlayer().getPlayerName(), "TableUpdated");
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
        else if (seats[seatNum - 1].getPlayer() != null) {
            // This seat is already taken.
            throw new Exception("Failed to seat player \"" + playerName + "\" in seat " + seatNum + ". Seat already taken.");
        } else {
            // Seat is open
            // TODO - fix hard coded stack size
            seats[seatNum - 1].setPlayer(player);
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

        //if (seats[seatNum - 1].getPlayer() != null) {
        if (seatNum > 0) {
            seats[seatNum - 1].setPlayer(null);
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
        if (seatNum != 0) {
            // Move the current bet to the pot
            pot += seats[seatNum-1].getPlayerBet();
            seats[seatNum-1].setPlayerBet(0);

            // Mark the seat as not in the current hand.
            seats[seatNum - 1].setInHand(false);

            // Clear this seat's cards.
            seats[seatNum - 1].clearCards();
        }

        // If all players but one have folded, end the game.  The single remaining player is the winner.
        if (getPlayersInHand() == 1) {
            finishRound();
            determineWinner();
            finishGame();
        }
        else if (actionOnPlayer(playerName)) {
            // Move on to the next player.
            advanceAction();
        }

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
        if (currentBet > seats[seatNum - 1].getPlayer().getStack()) {
            betAmount = seats[seatNum - 1].getPlayer().getStack();
        }

        try {
            // Deduct the chips from the players stack and set the seat's current bet to the new bet.
            seats[seatNum-1].getPlayer().adjustStack(betAmount * -1);

            // Update the current bet for the seat
            seats[seatNum-1].setPlayerBet(betAmount);

            // If the bet is an initial bet or raise, update the currentBet for the table as well as what seat number made the bet.
            if (betAmount > currentBet) {
                currentBet = betAmount;
                currentBetPosition = seatNum;
            }

            // Check to see if the player is All In. If so, set the seat state as such.
            if (seats[seatNum-1].getPlayer().getStack() == 0) {
                seats[seatNum-1].setIsAllIn(true);
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
        int callAmount = currentBet - seats[seatNum-1].getPlayerBet();

        // Check first that there are enough chips for a full call. If not, player is going all in.
        if (callAmount > seats[seatNum-1].getPlayer().getStack()) {
            callAmount = seats[seatNum-1].getPlayer().getStack();
        }

        try {
            // Deduct the difference between the table's current bet and the player's current bet from the player's stack
            seats[seatNum-1].getPlayer().adjustStack(callAmount * -1);
            seats[seatNum-1].increasePlayerBet(callAmount);

            // Check to see if the player stack is now 0. If so, they are all in.
            if (seats[seatNum-1].getPlayer().getStack() == 0) {
                seats[seatNum-1].setIsAllIn(true);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return ("Call failed - " + e.getMessage());
        }

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
            int betSize = seats[seatNum-1].getPlayer().getStack();

            // Set the seat's player bet to the player's entire stack
            seats[seatNum-1].increasePlayerBet(betSize);

            // Set the player's stack size to zero

            seats[seatNum-1].getPlayer().adjustStack(betSize * -1);

            // Check to see if this is the new table current bet
            if (seats[seatNum-1].getPlayerBet() > currentBet) {
                currentBet = seats[seatNum - 1].getPlayerBet();
                currentBetPosition = seatNum;
            }

            // Set the player/seat to All In
            seats[seatNum-1].setIsAllIn(true);

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
     * @return The seat number the player is currently occupying. Retunrs 0 if the player is not currently in a seat.
     *         (Does NOT return the array index into seats.)
     */
    public Integer getPlayerSeatNum(String playerName) {
        for (int i=0; i<numSeats; i++) {
            if (seats[i] != null &&
                seats[i].getPlayer() != null &&
                seats[i].getPlayer().getPlayerName().equals(playerName)) {
                // Found the player. Return the seat number.
                return seats[i].getSeatNum();
            }
        }

        // If we got here, the player was not found in any seat. Return 0;
        return 0;
    }

    /**
     * Get the number of players currently in the hand.
     *
     * @return number of players currently in the hand (seats that have the "inHand" flag set to true).
     */
    public Integer getPlayersInHand() {
        int playersInHand = 0;

        // Loop through all the seats and add up the players in the hand.
        for (Seat seat : seats) {
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
        for (Seat seat : seats) {
            if (seat != null && seat.getInHand() == true && seat.getIsAllIn() == false) {
                playersInHandNotAllIn++;
            }
        }
        return playersInHandNotAllIn;
    }

    /**
     * Search through the remaining players in the round to find the winning hand.
     */
    public void determineWinner() {
        // First, check for the case that everyone folded
        if (getPlayersInHand() == 1) {
            // There's only one player left.  Everyone else must have folded.  He's the winner.
            for (int i=0; i<numSeats; i++) {
                if (seats[i].getInHand()) {
                    // This is the player still in the hand. Declare him the winner.
                    winningSeat = i+1;
                    winningHand = HandType.WIN_BY_FOLD;
                    return;
                }
            }
        }
        else if (getPlayersInHand() >= 2) {
            // More than 2 players still in the hand. Gather all the hands and rank them.
            List<Hand> handList = new ArrayList<Hand>();

            // Check each seat
            for (Seat seat : seats) {
                if (seat.getInHand() == true) {
                    // If the seat is in, make a new hand and add all the seat's cards to the hand
                    Hand hand = new Hand(seat.getSeatNum());
                    hand.addCards(seat.getCards());
                    hand.addCards(board);

                    // Evaluate the hand to determine it's rank
                    hand.evaluate();

                    // Add the hand to the list.
                    handList.add(hand);
                }
            }

            // Once all the hands are assembled, sort the hand list by rank to see which hand is the winner
            Collections.sort(handList, Collections.<Hand>reverseOrder());

            // TODO: Need to handle cases of ties

            // The hand at the top of the list is the winner.
            winningSeat = handList.get(0).getSeatNum();
            winningHand = handList.get(0).getType();
        }
    }

    /**
     * Reset the table to finish a game and prepare for the next game.
     */
    public void finishGame() {
        // Reset the board
        for (int i=0; i<5; i++) {
            board[i] = null;
        }

        // Reset the round state
        roundState = CLEAN_UP;

        // Reset the action position
        currentAction = 0;

        // Reset the bet position
        currentBetPosition = 0;

        try {
            // Award the pot to the winner
            //determineWinner();

            // Make sure the winning seat number is valid and has a player in it
            if (0 < winningSeat && winningSeat < numSeats && seats[winningSeat-1].getPlayer() != null) {
                // Add the pot to the winning player's stack
                seats[winningSeat-1].getPlayer().adjustStack(pot);
            }
        }
        catch (Exception e) {
            // Exception caught adding chips to the winning player's stack.  Print it and move on.
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }

        // Clear the pot for the next game
        pot = 0;

        for (int i=0; i<seats.length; i++) {
            // Clear the "in hand" flg for all seats
            seats[i].setInHand(false);

            // Clear the "all in" flg for all seats
            seats[i].setIsAllIn(false);

            // Clear the cards for all seats
            seats[i].clearCards();
        }
    }

    /**
     * Sets up the next round.
     *
     * @throws Exception - Not enough players sitting at the table to start a new round.  Need minimum of 2.
     */
    public void newRound() throws Exception {
        // Can only start a new round if there are at least 2 players at the table in the hand (with chips)

        // Any player sitting at the table with chips is in the next hand
        for (int i=0; i<seats.length; i++) {
            // Only set the player as in the hand if they have chips
            if (seats[i].getPlayer() != null && seats[i].getPlayer().getStack() > 0) {
                seats[i].setInHand(true);
            }
        }

        if (getPlayersInHand() < 2) {
            Exception e = new Exception("Not enough players to start a new round.  Need a minimum of 2.");
            throw e;
        }

        // Reset the round state
        roundState = PRE_FLOP;

        // Reset the winning hand indicator
        winningSeat = 0;

        // Move the dealer button
        if (dealerPosition==0) {
            // This must be the very first game.  Randomly pick a seat and then move the dealer button to the next player.
            Random r = new Random();
            dealerPosition = r.nextInt(numSeats) + 1;
        }

        // Move the buttons (dealer, small blind, and big blind) and set the currentAction position.
        incrementDealerPosition();

        // Pull small blind
        seats[smallBlindPosition-1].getPlayer().adjustStack((bigBlind/2) * -1);
        seats[smallBlindPosition-1].increasePlayerBet(bigBlind/2);

        // Pull big blind
        seats[bigBlindPosition-1].getPlayer().adjustStack(bigBlind * -1);
        seats[bigBlindPosition-1].increasePlayerBet(bigBlind);

        // Set the currnetBet to the big blind
        currentBet = bigBlind;

        // Set the current action to the player to the left of the big blind
        currentAction = findNextPlayer(bigBlindPosition);

        // Set the current bet position to the big blind position
        currentBetPosition = bigBlindPosition;

        // Initialize a new deck
        deck.shuffle();

        // Deal 2 cards to each player
        for (int i=0; i<2; i++) {
            Seat seatPtr = seats[dealerPosition-1].getNext();
            while (seatPtr.getSeatNum() != dealerPosition) {
                // If there is a player in this seat and they are in the hand, deal a card
                if (seatPtr.getPlayer() != null &&
                    seatPtr.getInHand().equals(true)) {
                    seatPtr.addCard(i, deck.getCard());
                }
                seatPtr = seatPtr.getNext();
            }
            // Don't forget to deal a card to the dealer
            seatPtr.addCard(i, deck.getCard());
        }

        // Deal board cards
        // Burn a card
        deck.getCard();
        // Flop
        board[0] = deck.getCard();
        board[1] = deck.getCard();
        board[2] = deck.getCard();
        // Burn a card
        deck.getCard();
        // Turn
        board[3] = deck.getCard();
        // Burn a card
        deck.getCard();
        // River
        board[4] = deck.getCard();

        // Notify all players that the table has been updates
        sendTableStateChangeNotification("ALL");
    }

    /**
     * Move the action to the next player still in the hand. Skip the players that are All In.
     */
    public void advanceAction() {
        try {
            // Check to make sure there are at least 2 players still in the hand and not all in.
            // Otherwise, advance the round.
            //if (getPlayersInHandNotAllIn() <= 1) {
            //    advanceRound();
            //    return;
            //}

            //int newAction = findNextPlayerNotAllIn(currentAction);

            // No players that are not all in were found, so move on to the next round.
            //if (newAction == 0) {
            //    advanceRound();
            //}

            //int newAction = findNextPlayer(currentAction);

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
                else if (seats[newAction-1].getIsAllIn() != true) {
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

        // Gather up all the bets and put them into the pot
        for (int i=0; i<seats.length; i++) {
            pot += seats[i].getPlayerBet();
            seats[i].setPlayerBet(0);
        }

        // Set the table's current bet back to 0;
        currentBet = 0;
    }

    /**
     * Manually advance the round to the next state.
     *
     * @return New round state.
     */
    public RoundState advanceRound() {
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
                //currentAction = findNextPlayer(dealerPosition);

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
                currentAction = 0;
                determineWinner();
                break;
            case SHOWDOWN:
                finishGame();
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
        if (seatNum <=0 || seatNum > numSeats) {
            throw new Exception("Invalid seatNum '" + seatNum + "' passed into findNextPlayer()");
        }

        // Find the next seat with a player in it.
        Seat nextSeat = seats[seatNum-1].getNext();

        // Keep going around until either the next player is found,
        // or we've circled the whole table and are back to the current position.
        while (nextSeat != seats[seatNum-1]) {
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
        return 0;
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
            if (!seats[newSeatNum-1].getIsAllIn()) {
                return newSeatNum;
            }
            // Otherwise, move on to the next player
            else {
                newSeatNum = findNextPlayer(newSeatNum);
            }
        }

        // No additional player that's not All In was found.  Return 0.
        return 0;
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

//            PlayerDatabase.getInstance().createPlayer("Claire");
            PlayerDatabase.getInstance().login("Claire");
//            PlayerDatabase.getInstance().getPlayer("Claire").buyIn(160);
//            PlayerDatabase.getInstance().createPlayer("Traci");
            PlayerDatabase.getInstance().login("Traci");
//            PlayerDatabase.getInstance().getPlayer("Traci").buyIn(160);
//            PlayerDatabase.getInstance().createPlayer("Zoe");
            PlayerDatabase.getInstance().login("Zoe");
//            PlayerDatabase.getInstance().getPlayer("Zoe").buyIn(160);

            myTable.sitDown("Claire", 1);
            myTable.sitDown("Traci", 2);
            myTable.sitDown("Zoe", 3);

            myTable.dealerPosition = 3;

            myTable.newRound();

            myTable.call("Claire");
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
