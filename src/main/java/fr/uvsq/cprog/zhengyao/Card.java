package fr.uvsq.cprog.zhengyao;

import java.util.List;
import java.util.Objects;

/**
 * The {@code Card} class represents a standard playing card with a rank and a
 * suit.
 * This class provides methods to access the rank and suit of a card, as well as
 * to compare
 * cards for equality and to obtain a string representation of a card.
 * 
 */
public class Card implements Comparable<Card> {
    private final Rank rank;
    private final Suit suit;
    private boolean isJoker; // Add this field to indicate if it's a joker
    private List<Card> main; // Assuming 'main' is the hand of cards for this player

    public List<Card> getMain() {
        return main;
    }

    /**
     * Constructs a new {@code Card} with the specified rank and suit.
     *
     * @param rank the rank of the card, cannot be {@code null}
     * @param suit the suit of the card, cannot be {@code null}
     * @throws IllegalArgumentException if rank or suit is {@code null}
     */
    public Card(Rank rank, Suit suit, boolean isJoker) {
        if (rank == null) {
            throw new IllegalArgumentException("Rank cannot be null");
        }
        if (suit == null) {
            throw new IllegalArgumentException("Suit cannot be null");
        }
        this.rank = rank;
        this.suit = suit;
        this.isJoker = isJoker; // Corrected this line
    }

    /**
     * Returns the rank of the card.
     *
     * @return the rank of the card
     */
    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public boolean isJoker() {
        return isJoker; // Return the joker status
    }

    public int getValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

    /**
     * Compares this card with the specified object for equality.
     * Two cards are considered equal if and only if their rank and suit are equal.
     *
     * @param obj the object to compare with this card
     * @return {@code true} if the specified object is equal to this card,
     *         {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Card other)) {
            return false;
        }

        return rank == other.rank && suit == other.suit;
    }

    /**
     * Compares this card with the specified card for order.
     *
     * @param other the card to be compared
     * @return a negative integer, zero, or a positive integer as this card is less
     *         than, equal to, or greater than the specified card
     */
    @Override
    public int compareTo(Card other) {
        int rankCompare = Integer.compare(rank.getValue(), other.rank.getValue());
        if (rankCompare != 0) {
            return rankCompare;
        }
        return suit.compareTo(other.suit);
    }

    /**
     * get the rank of the value.
     *
     * @return a negative integer if it is a jocker else it returns the value
     */
    public int getRankValue() {
        // If the card is a joker, it may have a specific rank value, or you can return
        // a unique value
        if (isJoker) {
            return -1; // Assuming -1 represents a joker; adjust based on game rules
        }
        return rank.getValue();
    }
}
