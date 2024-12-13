package fr.uvsq.cprog.zhengyao;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The {@code Deck} class represents a deck of cards. It is used to create and
 * manage a collection of cards.
 *
 * <p>
 * A deck of cards is created with all the possible combinations of suits and
 * ranks, and can
 * be shuffled and dealt from. The deck keeps track of the number of cards
 * remaining and throws
 * an exception if an attempt is made to deal from an empty deck.
 *
 * @author lyudaio
 * @since 0.0.1
 */
public class Deck implements Comparable<Deck> {

    private final List<Card> cards;
    private final SecureRandom secureRandom;

    public Deck() {
        cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit, false));
            }
        }
        this.secureRandom = new SecureRandom();
    }

    public void shuffle() {
        long seed = secureRandom.nextLong();
        Collections.shuffle(cards, new Random(seed));
    }

    public List<Card> deal(int amount) {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No more cards in the deck");
        }
        if (amount > cards.size()) {
            throw new IllegalStateException("Cannot deal more cards than are in the deck");
        }
        List<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            dealtCards.add(cards.remove(0));
        }
        return dealtCards;
    }

    public Card deal() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No more cards in the deck");
        }
        return cards.remove(0);
    }

    /**
     * Splits the deck into a specified number of hands. If the cards cannot be
     * split evenly, the hands will have different numbers of cards.
     * The extra cards, if any, will be distributed randomly among the players.
     *
     * @param players the number of hands to split the deck into
     * @return a list of hands
     */
    public List<Hand> split(int players) {
        List<Hand> hands = new ArrayList<>();
        for (int i = 0; i < players; i++) {
            hands.add(new Hand());
        }

        // Shuffle the deck to randomize the card dealing
        shuffle();

        // Deal cards evenly
        int cardsPerPlayer = cards.size() / players;
        int remainingCards = cards.size() % players;

        for (int i = 0; i < players; i++) {
            for (int j = 0; j < cardsPerPlayer; j++) {
                hands.get(i).addCard(cards.remove(0));
            }
        }

        // Handle any remaining cards
        for (int i = 0; i < remainingCards; i++) {
            // Randomly select a player to receive the extra card
            int randomPlayerIndex = secureRandom.nextInt(players);
            hands.get(randomPlayerIndex).addCard(cards.remove(0));
        }

        return hands;
    }

    public int findCard(Rank rank, Suit suit) {
        if (cards.isEmpty()) {
            return 0;
        }

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getRank() == rank && card.getSuit() == suit) {
                return i;
            }
        }
        return -1;
    }

    public Card drawCard() {
        return cards.isEmpty() ? null : cards.remove(cards.size() - 1);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Gets a sublist of cards from the deck.
     *
     * @param fromIndex the starting index, inclusive
     * @param toIndex   the ending index, exclusive
     * @return a sublist of cards from the deck
     * @throws IndexOutOfBoundsException if the indices are out of range
     */
    public List<Card> getSubList(int fromIndex, int toIndex) {
        return cards.subList(fromIndex, toIndex);
    }
 
    public List<Integer> findCardsByRank(Rank rank) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getRank() == rank) {
                indices.add(i);
            }
        }
        return indices;
    }

    public List<Integer> findCardsBySuit(Suit suit) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getSuit() == suit) {
                indices.add(i);
            }
        }
        return indices;
    }

    public int getSize() {
        return cards.size();
    }

    public Card getCardAtIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= getSize()) {
            throw new IndexOutOfBoundsException("The specified index is out of range: " + index);
        }
        return cards.get(index);
    }

    @Override
    public int compareTo(Deck o) {
        return Integer.compare(this.cards.size(), o.cards.size());
    }

    public List<Card> getCards() {
        return cards;
    }

    public void sort() {
        Collections.sort(cards);
    }

    public void clear() {
        cards.clear();
    }

    public SecureRandom getSecureRandom() {
        return secureRandom;
    }
}
