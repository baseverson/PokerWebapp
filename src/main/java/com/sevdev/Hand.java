package com.sevdev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Hand implements Comparable {

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
    // Store card ranks for Full House, Trips, Two Pair, and One Pair
    private Integer multiCardRankNum1=0, multiCardRankNum2=0;

    public Integer getSeatNum() { return this.seatNum; }
    public HandType getType() { return this.type; }
    public Integer getNumberRank() { return this.numberRank; }
    public List<Card> getCardList() { return this.cardList; }

    /**
     * Constructor
     */
    public Hand(int seatNum) {
        this.seatNum = seatNum;
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
     * Add an array of cards to the list
     *
     * @param cards - array of cards to add
     */
    public void addCards(Card[] cards) {
        for (Card card : cards) {
            cardList.add(new Card(card));
        }
    }

    /**
     * Convert the hand into a printable string.
     *
     * @return string containing hand details
     */
    public String toString() {
        String output = "";
        switch (type) {
            case STRAIGHT_FLUSH:
            case FLUSH:
            case STRAIGHT:
            case HIGH_CARD:
                output += type + " { ";
                for (Card card : handCards) {
                    output += card.getRank() + "(" + card.getNumberRank() + ")-" + card.getSuit() + " ";
                }
                output += "}";
                break;
            case QUADS:
            case TRIPS:
            case ONE_PAIR:
                output += type.toString() + " (" + multiCardRankNum1 + "'s), kicker(s): ";
                for (Card card : handCards) {
                    output += card.getRank() + "(" + card.getNumberRank() + ")-" + card.getSuit() + " ";
                }
                break;
            case FULL_HOUSE:
                output += type.toString() + " (" + multiCardRankNum1 + "'s full of " + multiCardRankNum2 + "'s)";
                break;
//            case FLUSH:
//                output += type + " { ";
//                for (Card card : handCards) {
//                    output += card.getRank() + "(" + card.getNumberRank() + ")-" + card.getSuit() + " ";
//                }
//                output += "}";
//                break;
//                // TODO
//                break;
//            case STRAIGHT:
//                // TODO
//                break;
//            case TRIPS:
//                // TODO
//                break;
            case TWO_PAIR:
                output += type.toString() + " (" + multiCardRankNum1 + "'s and " + multiCardRankNum2 + "'s)";
                output += ", kicker(s): ";
                for (Card card : handCards) {
                    output += card.getRank() + "(" + card.getNumberRank() + ")-" + card.getSuit() + " ";
                }
                break;
//            case ONE_PAIR:
//                // TODO
//                break;
//            case HIGH_CARD:
//                // TODO
//                break;
            case UNDEFINED:
            case WIN_BY_FOLD:
                output += type.toString();
        }
        return output;
    }

    /**
      * Evaluate the cards to determine what hand they make.
      */
    public void evaluate() {
        // Sort the cards so the higest ranked cards are first
        Collections.sort(cardList, Collections.<Card>reverseOrder());

        System.out.println("*** Evaluating Hand - Seat " + seatNum + " ***");
        for (Card card : cardList) {
            System.out.println("   " + card.getRank() + " (" +card.getNumberRank() + ") " + card.getSuit());
        }

        // Start checking to see if the cards make a hand, starting with the highest hand
        //============================
        // Straight Flush
        //============================
        if (isStraightFlush()) {
            type = HandType.STRAIGHT_FLUSH;
            numberRank = 9;
        }
        //============================
        // Quads
        //============================
        else if (isQuads()) {
            type = HandType.QUADS;
            numberRank = 8;
        }
        //============================
        // Full House
        //============================
        else if (isFullHouse()) {
            type = HandType.FULL_HOUSE;
            numberRank = 7;
        }
        //============================
        // Flush
        //============================
        else if (isFlush()) {
            type = HandType.FLUSH;
            numberRank = 6;
        }
        //============================
        // Straight
        //============================
        else if (isStraight()) {
            type = HandType.STRAIGHT;
            numberRank = 5;

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
            numberRank = 4;

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
            numberRank = 3;

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
            numberRank = 2;

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
            numberRank = 1;

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
        System.out.println("");
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

    /**
     * Compare this hand against another.
     *
     * @param obj - Hand to compare this hand against.
     *
     * @return Positive value if this hand is better, negative if worse, zero if they are equal
     */
    @Override
    public int compareTo(Object obj) {
        Hand compareHand = (Hand)obj;

        if ((this.numberRank - compareHand.numberRank) > 0) {
            // This hand has the better rank.  Return 1 to indicate it is better.
            return 1;
        }
        else if ((this.numberRank - compareHand.numberRank) < 0) {
            // This hand has the lesser rank. Return -1 to indicate it is worse.
            return -1;
        }
        else {
            // The hand ranks are the same.  Need to do deeper comparisons based on the hand type.
            switch (type) {
                case STRAIGHT_FLUSH:
                    // Compare the highest card of each straight flush
                    return this.handCards.get(0).getNumberRank() - compareHand.handCards.get(0).getNumberRank();
                case QUADS:
                    // Compare the value of the Quads
                    if (this.multiCardRankNum1 != compareHand.multiCardRankNum1) {
                        // The Quad card ranks are different. Return the difference.
                        return this.multiCardRankNum1 - compareHand.multiCardRankNum1;
                    }
                    else {
                        // Quad card values are the same.  Check the kicker.
                        return this.handCards.get(0).getNumberRank() - compareHand.handCards.get(0).getNumberRank();
                    }
                case FULL_HOUSE:
                    // Check the Trips first
                    if (this.multiCardRankNum1 != compareHand.multiCardRankNum1) {
                        // Trips are different. Return the difference.
                        return this.multiCardRankNum1 - compareHand.multiCardRankNum1;
                    }
                    else {
                        // Trips are the same.  Compare the pair.
                        return this.multiCardRankNum2 - compareHand.multiCardRankNum2;
                    }
                case FLUSH:
                case HIGH_CARD:
                    // Logic for comparing Flush and High Card are the same.

                    // Compare each card in the hand.
                    for (int i=0; i<this.handCards.size(); i++) {
                        if (this.handCards.get(i).getNumberRank() != compareHand.handCards.get(i).getNumberRank()) {
                            return this.handCards.get(i).getNumberRank() - compareHand.handCards.get(i).getNumberRank();
                        }
                    }

                    // No differences found.  Return 0.
                    return 0;
                case STRAIGHT:
                    // Compare the highest card of each flush
                    return this.handCards.get(0).getNumberRank() - compareHand.handCards.get(0).getNumberRank();
                case TRIPS:
                case ONE_PAIR:
                    // Logic for comparing Trips and Pairs is the same.

                    // Check the Pair first
                    // Check the Trips/Pair first
                    if (this.multiCardRankNum1 != compareHand.multiCardRankNum1) {
                        // Trips/pair are different. Return the difference.
                        return this.multiCardRankNum1 - compareHand.multiCardRankNum1;
                    }
                    else {
                        // Trips/pair are the same.  Check the kickers.
                        for (int i=0; i<this.handCards.size(); i++) {
                            if (this.handCards.get(i).getNumberRank() != compareHand.handCards.get(i).getNumberRank()) {
                                return this.handCards.get(i).getNumberRank() - compareHand.handCards.get(i).getNumberRank();
                            }
                        }
                    }

                    // No differences found.  Return 0.
                    return 0;
                case TWO_PAIR:
                    // Check the Second pair first. Because of how the hand building algorithm works,
                    // the pair with the greater rank is in multiCardRank2.
                    if (this.multiCardRankNum1 != compareHand.multiCardRankNum1) {
                        // Pair is different. Return the difference.
                        return this.multiCardRankNum1 - compareHand.multiCardRankNum1;
                    }
                    else if (this.multiCardRankNum2 != compareHand.multiCardRankNum2) {
                        // Pair is different. Return the difference.
                        return this.multiCardRankNum2 - compareHand.multiCardRankNum2;
                    }
                    else {
                        // Both pairs are the same.  Check the kicker.
                        return this.handCards.get(0).getNumberRank() - compareHand.handCards.get(0).getNumberRank();
                    }
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.shuffle();

        // Test straight flush
        Hand straightFlush = new Hand(1);
        straightFlush.addCard(new Card("clubs", "8"));
        straightFlush.addCard(new Card("clubs", "5"));
        straightFlush.addCard(new Card("clubs", "K"));
        straightFlush.addCard(new Card("clubs", "7"));
        straightFlush.addCard(new Card("clubs", "2"));
        straightFlush.addCard(new Card("clubs", "6"));
        straightFlush.addCard(new Card("clubs", "4"));
        straightFlush.evaluate();

        // Test Quads
        Hand quads = new Hand(2);
        quads.addCard(new Card("clubs", "4"));
        quads.addCard(new Card("diamonds", "4"));
        quads.addCard(new Card("hearts", "4"));
        quads.addCard(new Card("spades", "K"));
        quads.addCard(new Card("clubs", "2"));
        quads.addCard(new Card("clubs", "6"));
        quads.addCard(new Card("clubs", "4"));
        quads.evaluate();

        // Test Quads
        Hand quads2 = new Hand(2);
        quads2.addCard(new Card("clubs", "5"));
        quads2.addCard(new Card("diamonds", "5"));
        quads2.addCard(new Card("hearts", "5"));
        quads2.addCard(new Card("spades", "K"));
        quads2.addCard(new Card("clubs", "2"));
        quads2.addCard(new Card("clubs", "6"));
        quads2.addCard(new Card("clubs", "5"));
        quads2.evaluate();

        // Test Quads
        Hand quads3 = new Hand(2);
        quads3.addCard(new Card("clubs", "5"));
        quads3.addCard(new Card("diamonds", "5"));
        quads3.addCard(new Card("hearts", "5"));
        quads3.addCard(new Card("spades", "A"));
        quads3.addCard(new Card("clubs", "2"));
        quads3.addCard(new Card("clubs", "6"));
        quads3.addCard(new Card("clubs", "5"));
        quads3.evaluate();

        // Test Full House
        Hand boat = new Hand(3);
        boat.addCard(new Card("clubs", "3"));
        boat.addCard(new Card("diamonds", "Q"));
        boat.addCard(new Card("hearts", "3"));
        boat.addCard(new Card("spades", "3"));
        boat.addCard(new Card("spades", "2"));
        boat.addCard(new Card("clubs", "Q"));
        boat.addCard(new Card("clubs", "4"));
        boat.evaluate();

        // Test Full House
        Hand boat1 = new Hand(3);
        boat1.addCard(new Card("clubs", "A"));
        boat1.addCard(new Card("diamonds", "4"));
        boat1.addCard(new Card("hearts", "A"));
        boat1.addCard(new Card("spades", "A"));
        boat1.addCard(new Card("spades", "2"));
        boat1.addCard(new Card("clubs", "4"));
        boat1.addCard(new Card("clubs", "3"));
        boat1.evaluate();

        // Test Full House
        Hand boat2 = new Hand(3);
        boat2.addCard(new Card("clubs", "3"));
        boat2.addCard(new Card("diamonds", "K"));
        boat2.addCard(new Card("hearts", "3"));
        boat2.addCard(new Card("spades", "3"));
        boat2.addCard(new Card("spades", "2"));
        boat2.addCard(new Card("clubs", "K"));
        boat2.addCard(new Card("clubs", "4"));
        boat2.evaluate();

        // Test Full House
        Hand boat3 = new Hand(3);
        boat3.addCard(new Card("clubs", "2"));
        boat3.addCard(new Card("diamonds", "A"));
        boat3.addCard(new Card("hearts", "2"));
        boat3.addCard(new Card("spades", "3"));
        boat3.addCard(new Card("spades", "2"));
        boat3.addCard(new Card("clubs", "A"));
        boat3.addCard(new Card("clubs", "4"));
        boat3.evaluate();

        // Test flush
        Hand flush = new Hand(4);
        flush.addCard(new Card("clubs", "A"));
        flush.addCard(new Card("clubs", "Q"));
        flush.addCard(new Card("clubs", "10"));
        flush.addCard(new Card("clubs", "8"));
        flush.addCard(new Card("clubs", "6"));
        flush.addCard(new Card("clubs", "4"));
        flush.addCard(new Card("clubs", "2"));
        flush.evaluate();

        // Test straight
        Hand straight = new Hand(5);
        straight.addCard(new Card("clubs", "8"));
        straight.addCard(new Card("spades", "5"));
        straight.addCard(new Card("diamonds", "K"));
        straight.addCard(new Card("diamonds", "7"));
        straight.addCard(new Card("diamonds", "2"));
        straight.addCard(new Card("spades", "6"));
        straight.addCard(new Card("hearts", "4"));
        straight.evaluate();

        // Test wheel
        Hand wheel = new Hand(6);
        wheel.addCard(new Card("clubs", "8"));
        wheel.addCard(new Card("spades", "5"));
        wheel.addCard(new Card("diamonds", "K"));
        wheel.addCard(new Card("diamonds", "A"));
        wheel.addCard(new Card("diamonds", "2"));
        wheel.addCard(new Card("spades", "3"));
        wheel.addCard(new Card("hearts", "4"));
        wheel.evaluate();

        // Test wheel
        Hand wheelFlush = new Hand(7);
        wheelFlush.addCard(new Card("spades", "2"));
        wheelFlush.addCard(new Card("clubs", "6"));
        wheelFlush.addCard(new Card("spades", "5"));
        wheelFlush.addCard(new Card("diamonds", "K"));
        wheelFlush.addCard(new Card("spades", "A"));
        wheelFlush.addCard(new Card("spades", "3"));
        wheelFlush.addCard(new Card("spades", "4"));
        wheelFlush.evaluate();

        // Test Trips
        Hand trips = new Hand(8);
        trips.addCard(new Card("spades", "5"));
        trips.addCard(new Card("clubs", "6"));
        trips.addCard(new Card("spades", "5"));
        trips.addCard(new Card("diamonds", "2"));
        trips.addCard(new Card("hearts", "A"));
        trips.addCard(new Card("spades", "5"));
        trips.addCard(new Card("spades", "4"));
        trips.evaluate();

        // Test Trips
        Hand trips1 = new Hand(8);
        trips1.addCard(new Card("spades", "7"));
        trips1.addCard(new Card("clubs", "6"));
        trips1.addCard(new Card("spades", "7"));
        trips1.addCard(new Card("diamonds", "2"));
        trips1.addCard(new Card("hearts", "A"));
        trips1.addCard(new Card("spades", "7"));
        trips1.addCard(new Card("spades", "4"));
        trips1.evaluate();

        // Test Trips
        Hand trips2 = new Hand(8);
        trips2.addCard(new Card("spades", "7"));
        trips2.addCard(new Card("clubs", "6"));
        trips2.addCard(new Card("spades", "7"));
        trips2.addCard(new Card("diamonds", "2"));
        trips2.addCard(new Card("hearts", "Q"));
        trips2.addCard(new Card("spades", "7"));
        trips2.addCard(new Card("spades", "4"));
        trips2.evaluate();

        // Test Two Pair
        Hand twoPair = new Hand(9);
        twoPair.addCard(new Card("spades", "5"));
        twoPair.addCard(new Card("clubs", "6"));
        twoPair.addCard(new Card("spades", "5"));
        twoPair.addCard(new Card("diamonds", "2"));
        twoPair.addCard(new Card("hearts", "A"));
        twoPair.addCard(new Card("spades", "6"));
        twoPair.addCard(new Card("spades", "4"));
        twoPair.evaluate();

        // Test Two Pair
        Hand twoPair1 = new Hand(9);
        twoPair1.addCard(new Card("spades", "3"));
        twoPair1.addCard(new Card("clubs", "6"));
        twoPair1.addCard(new Card("spades", "5"));
        twoPair1.addCard(new Card("diamonds", "3"));
        twoPair1.addCard(new Card("hearts", "A"));
        twoPair1.addCard(new Card("spades", "6"));
        twoPair1.addCard(new Card("spades", "4"));
        twoPair1.evaluate();

        // Test Two Pair
        Hand twoPair2 = new Hand(9);
        twoPair2.addCard(new Card("spades", "Q"));
        twoPair2.addCard(new Card("clubs", "Q"));
        twoPair2.addCard(new Card("spades", "5"));
        twoPair2.addCard(new Card("diamonds", "3"));
        twoPair2.addCard(new Card("hearts", "2"));
        twoPair2.addCard(new Card("spades", "6"));
        twoPair2.addCard(new Card("spades", "2"));
        twoPair2.evaluate();

        // Test Two Pair
        Hand twoPair3 = new Hand(9);
        twoPair3.addCard(new Card("spades", "Q"));
        twoPair3.addCard(new Card("clubs", "Q"));
        twoPair3.addCard(new Card("spades", "5"));
        twoPair3.addCard(new Card("diamonds", "3"));
        twoPair3.addCard(new Card("hearts", "2"));
        twoPair3.addCard(new Card("spades", "J"));
        twoPair3.addCard(new Card("spades", "2"));
        twoPair3.evaluate();

        // Test Two Pair
        Hand twoPair4 = new Hand(9);
        twoPair4.addCard(new Card("spades", "Q"));
        twoPair4.addCard(new Card("clubs", "Q"));
        twoPair4.addCard(new Card("spades", "5"));
        twoPair4.addCard(new Card("diamonds", "3"));
        twoPair4.addCard(new Card("hearts", "J"));
        twoPair4.addCard(new Card("spades", "J"));
        twoPair4.addCard(new Card("spades", "2"));
        twoPair4.evaluate();

        // Test One Pair
        Hand onePair = new Hand(10);
        onePair.addCard(new Card("spades", "Q"));
        onePair.addCard(new Card("clubs", "6"));
        onePair.addCard(new Card("spades", "5"));
        onePair.addCard(new Card("diamonds", "2"));
        onePair.addCard(new Card("hearts", "A"));
        onePair.addCard(new Card("spades", "6"));
        onePair.addCard(new Card("spades", "4"));
        onePair.evaluate();

        // Test One Pair
        Hand highCard = new Hand(11);
        highCard.addCard(new Card("spades", "Q"));
        highCard.addCard(new Card("clubs", "6"));
        highCard.addCard(new Card("spades", "5"));
        highCard.addCard(new Card("diamonds", "2"));
        highCard.addCard(new Card("hearts", "9"));
        highCard.addCard(new Card("spades", "A"));
        highCard.addCard(new Card("spades", "4"));
        highCard.evaluate();

        // Random
        Hand random = new Hand(12);
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.addCard(deck.getCard());
        random.evaluate();

        List<Hand> handList = new ArrayList<Hand>();
        handList.add(random);
        handList.add(twoPair);
        handList.add(onePair);
        handList.add(trips1);
        handList.add(highCard);
        handList.add(wheel);
        handList.add(wheelFlush);
        handList.add(straight);
        handList.add(twoPair1);
        handList.add(twoPair3);
        handList.add(twoPair4);
        handList.add(trips2);
        handList.add(straightFlush);
        handList.add(flush);
        handList.add(quads);
        handList.add(twoPair2);
        handList.add(quads2);
        handList.add(quads3);
        handList.add(boat);
        handList.add(boat1);
        handList.add(boat2);
        handList.add(boat3);
        handList.add(trips);

        System.out.println("\n\n");

        Collections.sort(handList, Collections.<Hand>reverseOrder());
        for (Hand hand : handList) {
            System.out.print("Seat: " + hand.seatNum + " - ");
            System.out.println(hand.toString());
        }


    }

}
