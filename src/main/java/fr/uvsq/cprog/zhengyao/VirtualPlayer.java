package fr.uvsq.cprog.zhengyao;

import fr.uvsq.cprog.zhengyao.controller.ControleurDeJeu;
import fr.uvsq.cprog.zhengyao.controller.VerificateurCombinaison;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

/**
 * Manages the virtual player's actions.
 */
public class VirtualPlayer implements Joueur {

    private final String name;
    private List<Card> cards;
    private String color;
    private final GameEngine gameEngine;
    private int score = 0;
    private boolean passe = false;
    private List<List<Card>> combinaisonsPossibles;
    private ControleurDeJeu controleurDeJeu;
    private Color color1;

    /**
     * Constructor for VirtualPlayer.
     *
     * @param name            the name of the player
     * @param cards           the initial hand of the player
     * @param gameEngine      the game engine associated with the player
     * @param controleurDeJeu the game controller
     */
    public VirtualPlayer(String name, List<Card> cards, GameEngine gameEngine, ControleurDeJeu controleurDeJeu) {
        this.name = name;
        this.cards = new ArrayList<>(cards);
        this.gameEngine = gameEngine;
        this.controleurDeJeu = controleurDeJeu;
        this.combinaisonsPossibles = new ArrayList<>();
    }

    /**
     * Gets the color of the player.
     *
     * @return the color of the player
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of the player.
     *
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Sets the possible combinations for the player.
     *
     * @param combinaisons the list of possible combinations
     */
    public void setCombinaisonsPossibles(List<List<Card>> combinaisons) {
        this.combinaisonsPossibles = new ArrayList<>(combinaisons);
    }

    /**
     * Gets the player's current hand.
     *
     * @return a copy of the player's current hand
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    @Override
    public int choisirCombinaison(int maxChoix) {
        // Select the strongest combination available
        if (!combinaisonsPossibles.isEmpty()) {
            return 0; // Select the first combination (as a simple strategy)
        }
        return -1; // Pass if no valid combination exists
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasCards() {
        return !cards.isEmpty();
    }

    @Override
    public void showCards() {
        System.out.println(getColoredText(name + "'s cards: " + cards));
    }

    @Override
    public int getCardCount() {
        return cards.size();
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void ajouterPoints(int points) {
        score += points;
    }

    @Override
    public int getTotalCardValue() {
        return cards.stream().mapToInt(Card::getValue).sum();
    }

    @Override
    public List<Card> jouerCartes(List<Card> combinaison) {
        if (combinaisonValide(combinaison)) {
            cards.removeAll(combinaison);
            return new ArrayList<>(combinaison);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Card> jouerCartes() {
        List<Card> playedCards = determineCardsToPlay();
        if (!playedCards.isEmpty()) {
            System.out.println(getColoredText(name + " played: " + playedCards));
            cards.removeAll(playedCards);
        } else {
            System.out.println(getColoredText(name + " played no cards."));
        }
        return playedCards;
    }

    /**
     * Determines the cards to play based on strategy.
     *
     * @return the list of cards to play
     */
    public List<Card> determineCardsToPlay() {
        if (!cards.isEmpty()) {
            Collections.sort(cards); // Ensure cards are sorted for combination determination
            for (List<Card> combinaison : combinaisonsPossibles) {
                if (combinaisonValide(combinaison)) {
                    return combinaison;
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Removes a single card from the player's hand.
     *
     * @param card the card to remove
     */
    public void removeCard(Card card) {
        cards.remove(card);
    }

    @Override
    public int choisirCarte(int max) {
        return 0; // Always choose the first card as a simple strategy
    }

    /**
     * Removes multiple cards from the player's hand.
     *
     * @param cardsToRemove the list of cards to remove
     */
    public void removeCards(List<Card> cardsToRemove) {
        cards.removeAll(cardsToRemove);
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param card the card to add
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Checks if the played combination is valid.
     *
     * @param combinaison the combination of cards to validate
     * @return true if the combination is valid, false otherwise
     */
    private boolean combinaisonValide(List<Card> combinaison) {
        List<Card> derniereCombinaison = controleurDeJeu.getDerniereCombinaisonJouee();
        return VerificateurCombinaison.estCombinaisonPlusForte(combinaison, derniereCombinaison);
    }

    @Override
    public boolean aPasseLeTour() {
        return passe;
    }

    @Override
    public void passerTour() {
        this.passe = true;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public void setpasserTour(boolean pass) {
        this.passe = pass;
    }

    /**
     * Resets the pass status for a new round.
     */
    public void resetPassStatus() {
        this.passe = false;
    }

    @Override
    public String getColoredText(String text) {
        if (color == null) {
            return text;
        }

        Color ansiColor;
        switch (color.toLowerCase()) {
            case "blue":
                ansiColor = Color.BLUE;
                break;
            case "green":
                ansiColor = Color.GREEN;
                break;
            case "magenta":
                ansiColor = Color.MAGENTA;
                break;
            case "red":
                ansiColor = Color.RED;
                break;
            case "black":
                ansiColor = Color.BLACK;
                break;
            default:
                ansiColor = Color.DEFAULT;
                break;
        }

        return Ansi.ansi().fg(ansiColor).a(text).reset().toString();
    }

}
