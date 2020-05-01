package com.sevdev;

import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Deck {

    private String[] suits = {"spades", "diamonds", "clubs", "hearts"};
    private String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    private Stack<Card> cardStack;

    /**
     * Method to initialize a deck and randomize the contents.
     *
     * @return None.
     */
    public void initializeNewDeck() {
        cardStack = new Stack<Card>();

        for (String suit : suits) {
            for (String rank : ranks) {
                //cardList.add(new Card(suit, rank));
                cardStack.push(new Card(suit, rank));
            }
        }

        Collections.shuffle(cardStack);
    }

    /**
     * Method to return the card off the top of the deck. The card will be removed from the deck.
     *
     * @return Card from the top of the deck.
     */
    public Card getCard() {
        return cardStack.pop();
    }

    /**
     * Method to return the contents of the deck in a json string.
     *
     * @return String that will be returned as a application/json.
     */
    public String getDeckAsJSON() {

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cardStack);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void main(String[] args) {
        Deck myDeck = new Deck();
        myDeck.initializeNewDeck();
        myDeck.getDeckAsJSON();
    }

}
