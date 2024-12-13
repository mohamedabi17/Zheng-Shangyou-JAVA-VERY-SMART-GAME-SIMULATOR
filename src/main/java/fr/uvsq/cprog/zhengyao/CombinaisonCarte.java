package fr.uvsq.cprog.zhengyao;

import java.util.Comparator;
import java.util.List;

/**
 * Représente les combinaisons de cartes et fournit des méthodes pour valider
 * ces combinaisons.
 */
public class CombinaisonCarte {

    // Enumération des différentes combinaisons possibles

    /**
     * // Enumération des différentes combinaisons possibles.
     *
     */
    public enum TypeCombinaison {
        SIMPLE, // A single card
        PAIRE, // Pair
        BRELAN, // Three of a kind
        CARRE, // Four of a kind
        SUITE_PAIRES, // Sequence of pairs
        SUITE_BRELANS, // Sequence of triples
        SUITE_CARRES, // Sequence of quads
        FULL, // Full house
        SUITE_COULEUR, // Five cards of the same suit in sequence (flush)
        STRAIGHT_FLUSH, // Five consecutive cards of the same suit
        ROYAL_FLUSH, // 10, Jack, Queen, King, Ace of the same suit
        BOMBE, // Special bomb combinations,
        CARTE_SEULE,

    }

    /**
     * Validates if the given cards form a bombe (e.g., a powerful four of a kind).
     *
     * @param cards the list of cards to check
     * @return true if it's a bombe, false otherwise
     */
    public static boolean estBombe(List<Card> cards) {
        return cards.size() == 4 && cards.stream().allMatch(card -> card.getValue() == cards.get(0).getValue());
    }

    /**
     * Validates if the given cards form a sequence of fours (Sequence of Carres).
     *
     * @param cards the list of cards to check
     * @return true if it's a sequence of fours, false otherwise
     */
    public static boolean estSequenceCarres(List<Card> cards) {
        if (cards.size() % 4 != 0) {
            return false;
        } // Must be groups of 4

        // Check each group of four cards for same value
        for (int i = 0; i < cards.size(); i += 4) {
            int value = cards.get(i).getValue();
            for (int j = i; j < i + 4; j++) {
                if (cards.get(j).getValue() != value) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Validates if the given cards form a sequence of triples (Sequence of
     * Brelans).
     *
     * @param cards the list of cards to check
     * @return true if it's a sequence of triples, false otherwise
     */
    public static boolean estSequenceBrelans(List<Card> cards) {
        if (cards.size()==3){
            return false;
        }
        if (cards.size() % 3 != 0) {
            return false; // Must be groups of 3
        }
        // Check each group of three cards for same value
        for (int i = 0; i < cards.size(); i += 3) {
            int value = cards.get(i).getValue();
            for (int j = i; j < i + 3; j++) {
                if (cards.get(j).getValue() != value) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Validates if the given cards form a sequence of pairs (Sequence of Paires).
     *
     * @param cards the list of cards to check
     * @return true if it's a sequence of pairs, false otherwise
     */
    public static boolean estSequencePaires(List<Card> cards) {
        if (cards.size()==2){
            return false;
        }
        if (cards.size() % 2 != 0) {
            return false; // Must be groups of 2
        }
        
        // Check each pair for same value
        for (int i = 0; i < cards.size(); i += 2) {
            if (cards.get(i).getValue() != cards.get(i + 1).getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates if the given cards form a flush (same suit, not necessarily in
     * sequence).
     *
     * @param cards the list of cards to check
     * @return true if it's a flush, false otherwise
     */
    public static boolean estFlush(List<Card> cards) {
        if (cards.size()!=5){
            return false;
        }
        Suit suit = cards.get(0).getSuit();
        for (Card card : cards) {
            if (!card.getSuit().equals(suit)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Valide si les cartes données forment une paire.
     *
     * @param cards la liste de cartes à valider
     * @return true si c'est une paire, false sinon
     */
    public static boolean estPaire(List<Card> cards) {
        return cards.size() == 2 && cards.get(0).getValue() == cards.get(1).getValue();
    }

    /**
     * Valide si les cartes données forment un brelan.
     *
     * @param cards la liste de cartes à valider
     * @return true si c'est un brelan, false sinon
     */
    public static boolean estBrelan(List<Card> cards) {
        return ((cards.size() == 3)
                && cards.get(0).getValue() == cards.get(1).getValue()
                && cards.get(1).getValue() == cards.get(2).getValue());
    }

    /**
     * Valide si les cartes données forment un flash.
     *
     * @param cards la liste de cartes à valider
     * @return true si c'est un flash, false sinon
     */
    public static boolean estFlash(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return false;
        } // Check for null and empty list

        Suit firstSuit = cards.get(0).getSuit(); // Use Suit type directly

        for (Card card : cards) {
            if (!card.getSuit().equals(firstSuit)) {
                return false; // If one card has a different suit
            }
        }
        return true;
    }

    /**
     * Valide si les cartes données forment une quinte.
     *
     * @param cards la liste de cartes à valider
     * @return true si c'est une quinte, false sinon
     */
    public static boolean estQuinte(List<Card> cards) {
        if (cards.size() != 5) {
            return false;
        }
        cards.sort(null);
        // Tri des cartes par valeur

        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getValue() + 1 != cards.get(i + 1).getValue()) {
                return false; // Si les valeurs ne sont pas consécutives
            }
        }
        return true;
    }

    /**
     * Valide si les cartes données forment un carré.
     *
     * @param cards la liste de cartes à valider
     * @return true si c'est un carré, false sinon
     */
    public static boolean estCarre(List<Card> cards) {
        return cards.size() == 4
                && cards.get(0).getValue() == cards.get(1).getValue()
                && cards.get(1).getValue() == cards.get(2).getValue()
                && cards.get(2).getValue() == cards.get(3).getValue();
    }

    /**
     * Valide si les cartes données forment un full.
     *
     * @param cards la liste de cartes à valider
     * @return true si c'est un full, false sinon
     */
    public static boolean estFull(List<Card> cards) {
        if (cards.size() != 5) {
            return false;
        }

        // Vérification des combinaisons possibles pour un full
        boolean troisIdentiques = (cards.get(0).getValue() == cards.get(1).getValue()
                && cards.get(1).getValue() == cards.get(2).getValue());
        boolean paire = (cards.get(3).getValue() == cards.get(4).getValue());

        return (troisIdentiques && paire) || (paire && cards.get(0).getValue() == cards.get(1).getValue());
    }

    /**
     * Validates if the given cards form a straight flush (five consecutive cards of
     * the same suit).
     *
     * @param cards the list of cards to check
     * @return true if it's a straight flush, false otherwise
     */
    public static boolean estStraightFlush(List<Card> cards) {
        if (cards.size() != 5) {
            return false;
        }

        cards.sort(Comparator.comparingInt(Card::getValue));
        Suit suit = cards.get(0).getSuit();

        for (int i = 1; i < cards.size(); i++) {
            if (!cards.get(i).getSuit().equals(suit)
                    || cards.get(i).getValue() != cards.get(i - 1).getValue() + 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * return a boolean if the cards played are single card or not.
     *
     * @param cards the list of cards to check
     * @return true if it's a estSimpleCard, false otherwise
     */
    public static boolean estSimpleCard(List<Card> cards) {
        if (cards.size() != 1) {
            return false;
        }
        return true;
    }

    /**
     * return a boolean if the cards played RoyalFlush
     *
     * @param cards the list of cards to check
     * @return true if it's a estRoyalFlush, false otherwise
     */
    public static boolean estRoyalFlush(List<Card> cards) {
        if (cards.size() != 5) {
            return false;
        }

        // Sort by value and check for 10, J, Q, K, A of the same suit
        cards.sort(Comparator.comparingInt(Card::getValue));
        int[] royalValues = { 10, 11, 12, 13, 14 }; // 10 to Ace
        Suit suit = cards.get(0).getSuit();

        for (int i = 0; i < cards.size(); i++) {
            if (!cards.get(i).getSuit().equals(suit) || cards.get(i).getValue() != royalValues[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines the type of combination for the given cards.
     *
     * @param cards the list of cards to check
     * @return the type of combination or SIMPLE if none match
     */
    public static TypeCombinaison typeCombinaison(List<Card> cards) {
        if (estRoyalFlush(cards)) {
            return TypeCombinaison.ROYAL_FLUSH;
        }
        if (estStraightFlush(cards)) {
            return TypeCombinaison.STRAIGHT_FLUSH;
        }
        if (estBombe(cards)) {
            return TypeCombinaison.BOMBE;
        }
        if (estSequenceCarres(cards)) {
            return TypeCombinaison.CARRE;
        }
        if (estSequenceBrelans(cards)) {
            return TypeCombinaison.SUITE_BRELANS;
        }
        if (estSequencePaires(cards)) {
            return TypeCombinaison.SUITE_PAIRES;
        }
        if (estFlush(cards)) {
            return TypeCombinaison.STRAIGHT_FLUSH;
        }
        if (estFull(cards)) {
            return TypeCombinaison.FULL;
        }
        if (estCarre(cards)) {
            return TypeCombinaison.CARRE;
        }
        if (estBrelan(cards)) {
            return TypeCombinaison.BRELAN;
        }
        if (estPaire(cards)) {
            return TypeCombinaison.PAIRE;
        }
        if (estSimpleCard(cards)) {

            return TypeCombinaison.CARTE_SEULE;
        }

        return TypeCombinaison.SIMPLE;
    }
}
