package com.sevdev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Hand {

    // Holds the 7 cards that can make up a hand
    private List<Card> cardList = new ArrayList<Card>();
    // The seat number this hand belongs to
    private Integer seatNum = 0;
    // The type of hand made (enum)
    private HandType type = HandType.UNDEFINED;
    // The numerical rank value for this hand type (used for comparison)
    private Integer numberRank = 0;

    // Card sets for straight flush, flush, and straight
    private List<Card> handCards = new ArrayList<Card>();

    private Integer multiCardRankNum1=0, multiCardRankNum2=0;

    /**
     * Constructor
     */
    public void Hand() {
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
      * Evaluate the cards to determine what hand they make.
      */
    public void evaluate() {
        // Sort the cards so the higest ranked cards are first
        Collections.sort(cardList, Collections.<Card>reverseOrder());

        System.out.println("***Evaluating Hand***");
        for (Card card : cardList) {
            System.out.println("   " + card.getRank() + " (" +card.getNumberRank() + ") " + card.getSuit());
        }

        // Start checking to see if the cards make a hand, starting with the highest hand
        //============================
        // Straight Flush
        //============================
        if (isStraightFlush()) {
            type = HandType.STRAIGHT_FLUSH;

            System.out.print("The hand is a " + type + " { ");
            for (Card card : handCards) {
                System.out.print(card.getRank() + "(" + card.getNumberRank() + ")-");
                System.out.print(card.getSuit() + " ");
            }
            System.out.println("}");
        }
        //============================
        // Quads
        //============================
        else if (isQuads()) {
            type = HandType.QUADS;

            System.out.println("The hand is a " + type + " : " + multiCardRankNum1);
            System.out.print("The kicker is: ");
            for (Card card : handCards) {
                System.out.print(card.getRank() + "(" + card.getNumberRank() + ")-");
                System.out.print(card.getSuit() + " ");
            }
        }
        //============================
        // Full House
        //============================
        else if (isFullHouse()) {
            type = HandType.FULL_HOUSE;

            System.out.println("The hand is a " + type + " : " + multiCardRankNum1 + ", " + multiCardRankNum2);
        }
        //============================
        // Flush
        //============================
        else if (isFlush()) {
            type = HandType.FLUSH;

            System.out.print("The hand is a " + type + " { ");
            for (Card card : handCards) {
                System.out.print(card.getRank() + "(" + card.getNumberRank() + ")-");
                System.out.print(card.getSuit() + " ");
            }
            System.out.println("}");
            System.out.println("flushSuit: " + handCards.get(0).getSuit());
            System.out.println("flushHighCard: " + handCards.get(0).getNumberRank());
        }
        //============================
        // Straight
        //============================
        else if (isStraight()) {
            type = HandType.STRAIGHT;

            System.out.print("The hand is a " + type + " { ");
            for (Card card : handCards) {
                System.out.print(card.getRank() + "(" + card.getNumberRank() + ")-");
                System.out.print(card.getSuit() + " ");
            }
            System.out.println("}");
        }
        //============================
        // Trips
        //============================
        else if (isTrips()) {
            type = HandType.TRIPS;

            System.out.println("The hand is a " + type + " : " + multiCardRankNum1);
            System.out.print("The kickers are: ");
            for (Card card : handCards) {
                System.out.print(card.getRank() + "(" + card.getNumberRank() + ")-");
                System.out.print(card.getSuit() + " ");
            }
        }
        //============================
        // Two Pair
        //============================
        else if (isTwoPair()) {
            type = HandType.TWO_PAIR;

            System.out.println("The hand is a " + type + " : " + multiCardRankNum1 + ", " + multiCardRankNum2);
            System.out.print("The kickers are: ");
            for (Card card : handCards) {
                System.out.print(card.getRank() + "(" + card.getNumberRank() + ")-");
                System.out.print(card.getSuit() + " ");
            }
        }
        //============================
        // One Pair
        //============================
        else if (isOnePair()) {
            type = HandType.ONE_PAIR;

            System.out.println("The hand is a " + type + " : " + multiCardRankNum1);
            System.out.print("The kickers are: ");
            for (Card card : handCards) {
                System.out.print(card.getRank() + "(" + card.getNumberRank() + ")-");
                System.out.print(card.getSuit() + " ");
            }
        }
        //============================
        // High Card
        //============================
        else {
            type = HandType.HIGH_CARD;
            // Pull the top 5 cards
            handCards.clear();
            for (int i=0; i<5; i++) {
                handCards.add(cardList.get(i));
            }

            System.out.print("The hand is a " + type + " { ");
            for (Card card : handCards) {
                System.out.print(card.getRank() + "(" + card.getNumberRank() + ")-");
                System.out.print(card.getSuit() + " ");
            }
            System.out.println("}");
        }
    }

    public int cardCountByRank(Integer rankNum) {
        // Find the number of cards of the specified rank
        int cardCount = 0;
        // Check each card to see if it matches the rank
        for (Card card : cardList) {
            if (card.getNumberRank() == rankNum) {
                // The card matches the suit.
                // Increment the card count.
                cardCount++;
            }

        }
        return cardCount;
    }

    /**
     * Is this hand a Royal Flush?
     * @return True if the hand is a Royal Flush
     */
    private boolean isStraightFlush() {
        // Clear the list of cards to make this hand.  Start with a clean slate.
        handCards.clear();

        // Clear the list of straight flush cards
        handCards.clear();

        // Loop through each suit looking for a straight flush
        for (String suit : Card.suits) {

            // Loop through the sorted cards to look for a sequence without gap within just this suit
            int numCardsInSequence = 0;
            int lastCardRank = 0;
            for (Card card : cardList) {

                // We only care about cards in the suit. If the card is not in the suit, skip it.
                if (card.getSuit() == suit) {

                    if (lastCardRank == 0) {
                        // This is the first card. Set the sequence count to 1.
                        numCardsInSequence = 1;
                        // Add this as the first card in the straight sequence
                        handCards.add(card);
                    }
                    else if (card.getNumberRank() == (lastCardRank - 1)) {
                        // This card is one rank down from the previous.  It's in sequence.
                        // Increment the sequence count
                        numCardsInSequence++;
                        // Add this as the next card in the straight sequence
                        handCards.add(card);
                    }
                    else {
                        // Next card is not in sequence. Restarting the sequence.
                        // Restart the sequence count.
                        numCardsInSequence = 1;
                        // Clear the straight cards.
                        handCards.clear();
                        // Add this new card as the start of the new sequence
                        handCards.add(card);
                    }
                    // Check to see if we have made our straight. If so, return true.
                    if (numCardsInSequence >= 5) {
                        return true;
                    }

                    // No straight made yet. Update the last card rank for the next comparison.
                    lastCardRank = card.getNumberRank();
                }
            }

            // Special case - check for the wheel (5-A straight)
            if (lastCardRank == 2 && cardList.get(0).getNumberRank() == 14 && cardList.get(0).getSuit() == suit) {
                // The last card was a 2 and the first card in the hand is an Ace, add the Ace to the end of straight sequence.
                handCards.add(cardList.get(0));
                numCardsInSequence++;

                // Check to see if this made the straight
                if (numCardsInSequence >= 5) {
                    return true;
                }
            }
        }

        // Checks are done and no straight flush. Return false.
        return false;
    }

    /**
     * Is this hand Quads?
     * @return True if the hand is Quads
     */
    private boolean isQuads() {
        // Clear the list of cards to make this hand.  Start with a clean slate.
        handCards.clear();

        // Find the number of cards of the same rank
        // Check the card count in each rank
        for (Integer rankNum : Card.rankNums) {
            int cardCount = cardCountByRank(rankNum);

            // After checking all cards, check to see if we have 4 matching rank.
            if (cardCount >= 4)
            {
                // The hand is Quads. Save the rank.
                multiCardRankNum1 = rankNum;

                // Populate handCards with the remaining cards (kickers)
                for (Card card : cardList) {
                    if (card.getNumberRank() != multiCardRankNum1) {
                        handCards.add(card);
                    }

                    // Only need one kicker for Quads
                    if (handCards.size() >= 1) {
                        // All done. Return true;
                        return true;
                    }
                }
            }
        }

        // No quads. Return false.
        return false;
    }

    /**
     * Is this hand a Full House?
     * @return True if the hand is a Full House
     */
    private boolean isFullHouse() {
        // Clear the list of cards to make this hand.  Start with a clean slate.
        handCards.clear();

        // Find the number of cards of the same rank
        // Check the card count in each rank
        for (Integer rankNum : Card.rankNums) {
            int cardCount = cardCountByRank(rankNum);

            // After checking all cards, check to see if we have 3 matching rank.
            if (cardCount >= 3 && rankNum > multiCardRankNum1)
            {
                // This set is higher rank than the previously found set (if there is one). But the previously
                // found set may be better than the previously found pair (if there is one). Check if that is
                // the case and upgrade the pair.
                if (multiCardRankNum1 > multiCardRankNum2) {
                    multiCardRankNum2 = multiCardRankNum1;
                }

                // At least 3 cards of this rank were found and better than the previous set.
                // Set the first rank var for the full house.
                multiCardRankNum1 = rankNum;
            }
            else if (cardCount >= 2 && rankNum > multiCardRankNum2) {
                // At least 2 cards of this rank were found. Set the second rank var for the full house.
                multiCardRankNum2 = rankNum;

            }
        }

        // Check to see if we made a full house.  If so, return true.
        if (multiCardRankNum1 != 0 && multiCardRankNum2 !=0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Is this hand a Flush?
     * @return True if the hand is a Flush
     */
    private boolean isFlush() {
        // Clear the list of cards to make this hand.  Start with a clean slate.
        handCards.clear();

        // Find the number of cards of the same suit
        // Check the card count in each suit
        for (String suit : Card.suits) {
            int cardCount = 0;
            // Check each card to see if it matches the suit
            for (Card card : cardList) {
                if (card.getSuit() == suit) {
                    // The card matches the suit.
                    // Increment the card count.
                    cardCount++;
                    // Add the card to the set of flush cards
                    handCards.add(card);

                    // Check to see if we have made a flush.  5 (or more) cards of the same suit is a flush.
                    if (cardCount >= 5)
                    {
                        // The hand is a flush. Return true.
                        return true;
                    }
                }
            }
        }

        // None of the suit counts are 5 or more, no flush.  Reset the flush cards and return false.
        handCards.clear();
        return false;
    }

    /**
     * Is this hand a Straight?
     * @return True if the hand is a Straight
     */
    private boolean isStraight() {
        // Clear the list of cards to make this hand.  Start with a clean slate.
        handCards.clear();

        // Clear the list of straight cards
        handCards.clear();

        // Loop through the sorted cards to look for a sequence without gap
        int numCardsInSequence = 0;
        int lastCardRank = 0;
        for (Card card : cardList) {
            if (lastCardRank == 0) {
                // This is the first card. Set the sequence count to 1.
                numCardsInSequence = 1;
                // Add this as the first card in the straight sequence
                handCards.add(card);
            }
            else if (lastCardRank == card.getNumberRank()) {
                // Same as the last card. Skip - do nothing.
            }
            else if (card.getNumberRank() == (lastCardRank - 1)) {
                // This card is one rank down from the previous.  It's in sequence.
                // Increment the sequence count
                numCardsInSequence++;
                // Add this as the next card in the straight sequence
                handCards.add(card);
            }
            else {
                // Next card is not in sequence. Restarting the sequence.
                // Restart the sequence count.
                numCardsInSequence = 1;
                // Clear the straight cards.
                handCards.clear();
                // Add this new card as the start of the new sequence
                handCards.add(card);
            }
            // Check to see if we have made our straight. If so, return true.
            if (numCardsInSequence >= 5) {
                return true;
            }

            // No straight made yet. Update the last card rank for the next comparison.
            lastCardRank = card.getNumberRank();
        }

        // Special case - check for the wheel (5-A straight)
        if (lastCardRank == 2 && cardList.get(0).getNumberRank() == 14) {
            // The last card was a 2 and the first card in the hand is an Ace, add the Ace to the end of straight sequence.
            handCards.add(cardList.get(0));
            numCardsInSequence++;

            // Check to see if this made the straight
            if (numCardsInSequence >= 5) {
                return true;
            }
        }

        // Checks are done and no straight. Return false.
        return false;
    }

    /**
     * Is this hand Trips?
     * @return True if the hand is Trips
     */
    private boolean isTrips() {
        // Clear the list of cards to make this hand.  Start with a clean slate.
        handCards.clear();

        // Find the number of cards of the same rank
        // Check the card count in each rank
        for (Integer rankNum : Card.rankNums) {
            int cardCount = cardCountByRank(rankNum);

            // After checking all cards, check to see if we have 4 matching rank.
            if (cardCount >= 3)
            {
                // The hand is Quads. Save the rank and Return true.
                multiCardRankNum1 = rankNum;

                // Populate handCards with the remaining cards (kickers)
                for (Card card : cardList) {
                    if (card.getNumberRank() != multiCardRankNum1) {
                        handCards.add(card);
                    }

                    // Two kickers for Trips
                    if (handCards.size() >= 2) {
                        // All done. Return true;
                        return true;
                    }
                }
            }
        }

        // No Trips. Return false.
        return false;
    }

    /**
     * Is this hand Two Pair?
     * @return True if the hand is Two Pair
     */
    private boolean isTwoPair() {
        // Clear the list of cards to make this hand.  Start with a clean slate.
        handCards.clear();

        // Clear variables from previous checks
        multiCardRankNum1 = 0;
        multiCardRankNum2 = 0;

        // Find the number of cards of the same rank
        // Check the card count in each rank
        for (Integer rankNum : Card.rankNums) {
            int cardCount = cardCountByRank(rankNum);

            // After checking all cards, check to see if we have 2 matching rank.
            if (cardCount >= 2 && rankNum > multiCardRankNum1)
            {
                // This set is higher rank than the previously found set (if there is one). But the previously
                // found set may be better than the previously found pair (if there is one). Check if that is
                // the case and upgrade the pair.
                if (multiCardRankNum1 > multiCardRankNum2) {
                    multiCardRankNum2 = multiCardRankNum1;
                }

                // At least 2 cards of this rank were found and better than the previous pair.
                // Set the first rank var for the two pair.
                multiCardRankNum1 = rankNum;
            }
            else if (cardCount >= 2 && rankNum > multiCardRankNum2) {
                // At least 2 cards of this rank were found. Set the second rank var for the two pair.
                multiCardRankNum2 = rankNum;

            }
        }

        // Check to see if we made two pair.  If so, return true.
        if (multiCardRankNum1 != 0 && multiCardRankNum2 !=0) {

            // Populate handCards with the remaining cards (kickers)
            for (Card card : cardList) {
                if (card.getNumberRank() != multiCardRankNum1 && card.getNumberRank() != multiCardRankNum2) {
                    handCards.add(card);
                }

                // Only need one kicker for Two Pair
                if (handCards.size() >= 1) {
                    // All done. Return true;
                    return true;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Is this hand One Pair?
     * @return True if the hand is One Pair
     */
    private boolean isOnePair() {
        // Clear the list of cards to make this hand.  Start with a clean slate.
        handCards.clear();

        // Find the number of cards of the same rank
        // Check the card count in each rank
        for (Integer rankNum : Card.rankNums) {
            int cardCount = cardCountByRank(rankNum);

            // After checking all cards, check to see if we have 4 matching rank.
            if (cardCount >= 2)
            {
                // The hand is Quads. Save the rank and Return true.
                multiCardRankNum1 = rankNum;

                // Populate handCards with the remaining cards (kickers)
                for (Card card : cardList) {
                    if (card.getNumberRank() != multiCardRankNum1) {
                        handCards.add(card);
                    }

                    // Need three kickers for One Pair
                    if (handCards.size() >= 3) {
                        // All done. Return true;
                        return true;
                    }
                }
                return true;
            }
        }

        // No Trips. Return false.
        return false;
    }

    // TODO: comparison functions

    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.shuffle();

        // Test straight flush
        Hand straightFlush = new Hand();
        straightFlush.addCard(new Card("clubs", "8"));
        straightFlush.addCard(new Card("clubs", "5"));
        straightFlush.addCard(new Card("clubs", "K"));
        straightFlush.addCard(new Card("clubs", "7"));
        straightFlush.addCard(new Card("clubs", "2"));
        straightFlush.addCard(new Card("clubs", "6"));
        straightFlush.addCard(new Card("clubs", "4"));
        straightFlush.evaluate();

        // Test Quads
        Hand quads = new Hand();
        quads.addCard(new Card("clubs", "4"));
        quads.addCard(new Card("diamonds", "4"));
        quads.addCard(new Card("hearts", "4"));
        quads.addCard(new Card("spades", "K"));
        quads.addCard(new Card("clubs", "2"));
        quads.addCard(new Card("clubs", "6"));
        quads.addCard(new Card("clubs", "4"));
        quads.evaluate();

        // Test Full House
        Hand boat = new Hand();
        boat.addCard(new Card("clubs", "3"));
        boat.addCard(new Card("diamonds", "Q"));
        boat.addCard(new Card("hearts", "3"));
        boat.addCard(new Card("spades", "3"));
        boat.addCard(new Card("spades", "2"));
        boat.addCard(new Card("clubs", "Q"));
        boat.addCard(new Card("clubs", "4"));
        boat.evaluate();

        // Test flush
        Hand flush = new Hand();
        flush.addCard(new Card("clubs", "A"));
        flush.addCard(new Card("clubs", "Q"));
        flush.addCard(new Card("clubs", "10"));
        flush.addCard(new Card("clubs", "8"));
        flush.addCard(new Card("clubs", "6"));
        flush.addCard(new Card("clubs", "4"));
        flush.addCard(new Card("clubs", "2"));
        flush.evaluate();

        // Test straight
        Hand straight = new Hand();
        straight.addCard(new Card("clubs", "8"));
        straight.addCard(new Card("spades", "5"));
        straight.addCard(new Card("diamonds", "K"));
        straight.addCard(new Card("diamonds", "7"));
        straight.addCard(new Card("diamonds", "2"));
        straight.addCard(new Card("spades", "6"));
        straight.addCard(new Card("hearts", "4"));
        straight.evaluate();

        // Test wheel
        Hand wheel = new Hand();
        wheel.addCard(new Card("clubs", "8"));
        wheel.addCard(new Card("spades", "5"));
        wheel.addCard(new Card("diamonds", "K"));
        wheel.addCard(new Card("diamonds", "A"));
        wheel.addCard(new Card("diamonds", "2"));
        wheel.addCard(new Card("spades", "3"));
        wheel.addCard(new Card("hearts", "4"));
        wheel.evaluate();

        // Test wheel
        Hand wheelFlush = new Hand();
        wheelFlush.addCard(new Card("spades", "2"));
        wheelFlush.addCard(new Card("clubs", "6"));
        wheelFlush.addCard(new Card("spades", "5"));
        wheelFlush.addCard(new Card("diamonds", "K"));
        wheelFlush.addCard(new Card("spades", "A"));
        wheelFlush.addCard(new Card("spades", "3"));
        wheelFlush.addCard(new Card("spades", "4"));
        wheelFlush.evaluate();

        // Test Two Pair
        Hand twoPair = new Hand();
        twoPair.addCard(new Card("spades", "2"));
        twoPair.addCard(new Card("clubs", "6"));
        twoPair.addCard(new Card("spades", "5"));
        twoPair.addCard(new Card("diamonds", "2"));
        twoPair.addCard(new Card("hearts", "A"));
        twoPair.addCard(new Card("spades", "6"));
        twoPair.addCard(new Card("spades", "4"));
        twoPair.evaluate();

        // Test Two Pair
        Hand onePair = new Hand();
        onePair.addCard(new Card("spades", "Q"));
        onePair.addCard(new Card("clubs", "6"));
        onePair.addCard(new Card("spades", "5"));
        onePair.addCard(new Card("diamonds", "2"));
        onePair.addCard(new Card("hearts", "A"));
        onePair.addCard(new Card("spades", "6"));
        onePair.addCard(new Card("spades", "4"));
        onePair.evaluate();



        Hand random = new Hand();
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.evaluate();
    }
}
