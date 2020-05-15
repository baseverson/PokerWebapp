package com.sevdev;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    private RoundState roundState = RoundState.UNDEFINED;
    private Card board[];
    private Seat seats[];

    public Integer getTableId() {
        return tableId;
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
     * @return String containing the current state of the table (JSON formatted string)
     */
    public String getTableStateAsJSON(String playerName) {

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
        tableState.setRoundState(roundState);
        tableState.setBoard(board);
        tableState.setSeats(seats);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tableState);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting TableState object to JSON.";
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
     * @param seatNum - Seat the player wants to leave
     * @return String indicating the outcome of the request to leave the table
     */
    public String leaveTable(int seatNum) {

        // TODO: Check to make sure the player is really in the seat
        if (seats[seatNum - 1].getPlayer() != null) {
            seats[seatNum - 1].setPlayer(null);
            numPlayers--;
            // TODO - other cleanup for player leaving? (e.g. update DB for remaining chips)

            // Notify all players that the table has been updates
            sendTableStateChangeNotification("ALL");

            return "The player has successfully left seat " + seatNum;
        } else {
            return " No player in seat " + seatNum + ".";
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
        // Verify the action is on the player requesting the action
        if (!actionOnPlayer(playerName)) {
            return ("Out of turn move. Action is not currently on " + playerName);
        }

        // Get which seat the player is in.
        int seatNum = getPlayerSeatNum(playerName);

        // Mark the seat as not in the current hand.
        seats[seatNum - 1].setInHand(false);

        // Clear this seat's cards.
        seats[seatNum - 1].clearCards();

        // If all players but one have folded, end the game.  The single remaining player is the winner.
        if (getPlayersInHand() == 1) {
            roundState = CLEAN_UP;
            currentAction = 0;
            finishRound();
        }
        else {
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
        if (currentBet > seats[seatNum - 1].getPlayer().getStackSize()) {
            betAmount = seats[seatNum - 1].getPlayer().getStackSize();
        }

        try {
            // Deduct the chips from the players stack and set the seat's current bet to the new bet.
            seats[seatNum-1].getPlayer().deductChipsFromStack(betAmount);

            // Update the current bet for the seat
            seats[seatNum-1].setPlayerBet(betAmount);

            // If the bet is an initial bet or raise, update the currentBet for the table as well as what seat number made the bet.
            if (betAmount > currentBet) {
                currentBet = betAmount;
                currentBetPosition = seatNum;
            }

            // Check to see if the player is All In. If so, set the seat state as such.
            if (seats[seatNum-1].getPlayer().getStackSize() == 0) {
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
        if (callAmount > seats[seatNum-1].getPlayer().getStackSize()) {
            callAmount = seats[seatNum-1].getPlayer().getStackSize();
        }

        try {
            // Deduct the difference between the table's current bet and the player's current bet from the player's stack
            seats[seatNum-1].getPlayer().deductChipsFromStack(callAmount);
            seats[seatNum-1].increasePlayerBet(callAmount);

            // Check to see if the player stack is now 0. If so, they are all in.
            if (seats[seatNum-1].getPlayer().getStackSize() == 0) {
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
            int betSize = seats[seatNum-1].getPlayer().getStackSize();

            // Set the seat's player bet to the player's entire stack
            seats[seatNum-1].increasePlayerBet(betSize);

            // Set the player's stack size to zero

            seats[seatNum-1].getPlayer().deductChipsFromStack(betSize);

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
        for (int i=0; i<numSeats; i++) {
            if (seats[i] != null && seats[i].getInHand()) {
                playersInHand++;
            }
        }
        return playersInHand;
    }

    /**
     * Reset the table to finish a round and prepare for the next round.
     */
    public void finishRound() {
        // Reset the board
        for (int i=0; i<5; i++) {
            board[i] = null;
        }

        // TODO - award the pot to the winner
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
            if (seats[i].getPlayer() != null && seats[i].getPlayer().getStackSize() > 0) {
                seats[i].setInHand(true);
            }
        }

        if (getPlayersInHand() < 2) {
            Exception e = new Exception("Not enough players to start a new round.  Need a minimum of 2.");
            throw e;
        }

        // Reset the round state
        roundState = PRE_FLOP;

        // Move the dealer button
        if (dealerPosition==0) {
            // This must be the very first game.  Randomly pick a seat and then move the dealer button to the next player.
            Random r = new Random();
            dealerPosition = r.nextInt(numSeats) + 1;
        }

        // Move the buttons (dealer, small blind, and big blind) and set the currentAction position.
        incrementDealerPosition();

        // Pull small blind
        seats[smallBlindPosition-1].getPlayer().deductChipsFromStack(bigBlind/2);
        seats[smallBlindPosition-1].increasePlayerBet(bigBlind/2);

        // Pull big blind
        seats[bigBlindPosition-1].getPlayer().deductChipsFromStack(bigBlind);
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
            // TODO: handle the action rotation accounting for all in players
            //int newAction = findNextPlayerNotAllIn(currentAction);
            int newAction = findNextPlayer(currentAction);

            // No players that are not all in were found, so move on to the next round.
            if (newAction == 0) {
                advanceRound();
            }

            if (newAction == currentBetPosition) {
                // Special case for pre-flop, if the big blind bet was called all the way around, then the
                // big blind position has an option to raise.
                if (roundState == PRE_FLOP && newAction == bigBlindPosition && currentBet == bigBlind) {
                    currentBetPosition = findNextPlayerNotAllIn(currentBetPosition);
                    currentAction = newAction;
                }
                else {
                    advanceRound();
                }
            }
            else {
                currentAction = newAction;
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
     * Manually advance the round to the next state.
     *
     * @return New round state.
     */
    public RoundState advanceRound() {

        // Gather up all the bets and put them into the pot
        for (int i=0; i<seats.length; i++) {
            pot += seats[i].getPlayerBet();
            seats[i].setPlayerBet(0);
        }

        // Set the table's current bet back to 0;
        currentBet = 0;

        try {
            // Set the action to the first player after the dealer that is not All In
            // TODO: Don't think I need to worry about all in players here
            //currentAction = findNextPlayerNotAllIn(dealerPosition);
            currentAction = findNextPlayer(dealerPosition);

            // Set the current bet position to the first action
            currentBetPosition = currentAction;
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
                // TODO: Declare and display winner
                break;
            case SHOWDOWN:
                roundState = CLEAN_UP;
                currentAction = 0;
                finishRound();
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

            PlayerDatabase.getInstance().createPlayer("Brandt");
            PlayerDatabase.getInstance().getPlayer("Brandt").buyIn(160);
            PlayerDatabase.getInstance().createPlayer("Traci");
            PlayerDatabase.getInstance().getPlayer("Traci").buyIn(160);
            PlayerDatabase.getInstance().createPlayer("Zoe");
            PlayerDatabase.getInstance().getPlayer("Zoe").buyIn(160);

            myTable.sitDown("Brandt", 1);
            myTable.sitDown("Traci", 2);
            myTable.sitDown("Zoe", 3);

            myTable.dealerPosition = 3;

            myTable.newRound();

            myTable.call("Brandt");
            myTable.call("Traci");
            myTable.call("Zoe");

            myTable.advanceRound(); // to TURN
            myTable.advanceRound(); // to RIVER
            myTable.advanceRound(); // to SHOWDOWN
            myTable.advanceRound(); // to CLEAN_UP

            myTable.newRound();

            System.out.println("");
            System.out.println("");
            System.out.println(myTable.getTableStateAsJSON("Brandt"));
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
