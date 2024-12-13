package fr.uvsq.cprog.zhengyao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import fr.uvsq.cprog.zhengyao.controller.VerificateurCombinaison;


public class EstPlusFortCombinaisonTest {

    // Helper method to create cards
    private Card createCard(Rank rank, Suit suit) {
        return new Card(rank, suit, false); // Passing false for joker
    }

       @Test
    public void testSingleCardVsSingleCard() {
        List<Card> card1 = List.of(createCard(Rank.TWO, Suit.HEARTS));
        List<Card> card2 = List.of(createCard(Rank.QUEEN, Suit.SPADES));

        assertTrue(VerificateurCombinaison.estCombinaisonPlusForte(card1, card2)); // TWO > QUEEN
        assertFalse(VerificateurCombinaison.estCombinaisonPlusForte(card2, card1)); // ACE < TWO
    }

    @Test
    public void testSingleCardVsCombination() {
        List<Card> card = List.of(createCard(Rank.TWO, Suit.HEARTS));
        List<Card> pair = List.of(
                 createCard(Rank.ACE, Suit.HEARTS),
                 createCard(Rank.ACE, Suit.SPADES));

        assertFalse(VerificateurCombinaison.estCombinaisonPlusForte(card, pair)); // Single card < Pair
    }

    @Test
    public void testCombinationVsCombinationSameType() {
        List<Card> pair1 = List.of(
                createCard(Rank.TWO, Suit.HEARTS),
                createCard(Rank.TWO, Suit.SPADES));
        List<Card> pair2 = List.of(
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.CLUBS));

        assertTrue(VerificateurCombinaison.estCombinaisonPlusForte(pair1, pair2)); // Pair of TWO > Pair of ACE
        assertFalse(VerificateurCombinaison.estCombinaisonPlusForte(pair2, pair1)); // Pair of ACE < Pair of TWO
    }

    @Test
    public void testCombinationVsCombinationDifferentTypes() {
        List<Card> pair = List.of(
                createCard(Rank.TWO, Suit.HEARTS),
                createCard(Rank.TWO, Suit.SPADES));
        List<Card> brelan = List.of(
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.CLUBS),
                createCard(Rank.ACE, Suit.DIAMONDS));

        assertFalse(VerificateurCombinaison.estCombinaisonPlusForte(pair, brelan)); // PAIRE < BRELAN
        assertTrue(VerificateurCombinaison.estCombinaisonPlusForte(brelan, pair)); // BRELAN > PAIRE
    }

    @Test
    public void testCombinationVsCombinationDifferentTypesBoombe() {
        List<Card> pair = List.of(
                createCard(Rank.TWO, Suit.HEARTS),
            createCard(Rank.TWO, Suit.SPADES));
        List<Card> boombe = List.of(
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.CLUBS),
                createCard(Rank.ACE, Suit.CLUBS),
                createCard(Rank.ACE, Suit.DIAMONDS));

        assertFalse(VerificateurCombinaison.estCombinaisonPlusForte(boombe, pair)); // PAIRE < BOOMBE
        assertTrue(VerificateurCombinaison.estCombinaisonPlusForte(pair, boombe)); // BOOMBE > PAIRE
    }

    @Test
    public void testNullOrEmptyLastCombination() {
        List<Card> card = List.of(createCard(Rank.TWO, Suit.HEARTS));
        List<Card> empty = List.of();

        assertTrue(VerificateurCombinaison.estCombinaisonPlusForte(card, null)); // Single card > null
        assertTrue(VerificateurCombinaison.estCombinaisonPlusForte(card, empty)); // Single card > empty
    }
}



