package fr.uvsq.cprog.zhengyao;

import fr.uvsq.cprog.zhengyao.controller.ConditionsDeFin;
import fr.uvsq.cprog.zhengyao.controller.ControleurDeJeu;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;


public class ConditionsDeFinTest {

    private ConditionsDeFin conditionsDeFin;
    private ControleurDeJeu controleurDeJeu; // Mock or real controller used in the test

    // Helper method to create cards
    private Card createCard(Rank rank, Suit suit) {
        return new Card(rank, suit, false); // Passing false for joker
    }

    // Helper method to create a hand with cards
    private List<Card> createHand(Card... cards) {
        return Arrays.asList(cards);
    }

    @BeforeEach
    public void setUp() {
        controleurDeJeu = new ControleurDeJeu(); // Assuming a simple constructor for the game controller

        // Initialize players with HumanPlayer subclass (which inherits from Joueur)
        Joueur player1 = new HumanPlayer("Player 1", createHand(
                createCard(Rank.TWO, Suit.HEARTS),
                createCard(Rank.THREE, Suit.CLUBS)), controleurDeJeu);

        Joueur player2 = new HumanPlayer("Player 2", createHand(
                createCard(Rank.FIVE, Suit.HEARTS),
                createCard(Rank.SEVEN, Suit.CLUBS)), controleurDeJeu);

        List<Joueur> players = Arrays.asList(player1, player2);
        conditionsDeFin = new ConditionsDeFin(players); // Initialize the game end conditions
    }

   
}