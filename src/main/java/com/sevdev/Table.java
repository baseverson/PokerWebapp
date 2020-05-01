package com.sevdev;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;

public class Table {

    private Integer tableId = 0;
    private Deck deck;
    private TableState tableState;

    /**
     * Constructor
     */
    public Table() {
        if (tableId == 0) {
            Random r = new Random();
            tableId = r.nextInt(1000);
        }

        deck = new Deck();
        deck.initializeNewDeck();

        tableState = new TableState();
        tableState.initialize(tableId, 8,2);
    }

    /**
     * Retrieve the Table ID.
     *
     * @return String containing the Table ID.
     */
    public String getTableId() { return tableId.toString(); }

    /**
     * Retrieve the current state of the table.
     *
     * @return String containing the current state of the table (JSON formatted string)
     */
    public String getTableStateAsJSON() {

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tableState);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "{ 'poker': 'error' }";
    }

    /**
     * Set up the table for the next round.
     */
    public void newRound() {
        // Move the dealer button
        tableState.incrementDealerPosition();

        // Initialize a new deck
        deck.initializeNewDeck();

        // TODO - pull small/big blinds

        // TODO - deal player cards

        // Deal board cards
        // Flop
        tableState.dealBoardCard(0, deck.getCard());
        tableState.dealBoardCard(1, deck.getCard());
        tableState.dealBoardCard(2, deck.getCard());
        // Burn a card
        deck.getCard();
        // Turn
        tableState.dealBoardCard(3, deck.getCard());
        // Burn a card
        deck.getCard();
        // River
        tableState.dealBoardCard(4, deck.getCard());

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
        deck.initializeNewDeck();
    }
}
