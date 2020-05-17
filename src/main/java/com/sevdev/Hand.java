package com.sevdev;

import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> cardList;
    private Integer seatNum = 0;
    private HandType type = HandType.UNDEFINED;
    // TODO: Card Rank listings

    /**
     * Constructor
     */
    public void Hand() {
        cardList = new ArrayList<Card>();
    }

    /**
     * Add a card to the list
     *
     * @param addCard - card to add
     */
    public void addCard(Card addCard) {
        cardList.add(new Card(addCard));
    }

    /**
     * Count the number of cards in the list with the specified suit
     *
     * @param suit - suit to count
     *
     * @return numer of cards with the specified suit
     */
    public int countSuit(String suit) {
        int cardCount = 0;
        for (Card card : cardList) {
            if (card.getSuit() == suit) {
                cardCount++;
            }
        }
        return cardCount;
    }

    /**
      * Evaluate the cards to determine what hand they make.
      */
    public void evaluate() {
        // TODO
    }

    // TODO: comparison functionsa


}
