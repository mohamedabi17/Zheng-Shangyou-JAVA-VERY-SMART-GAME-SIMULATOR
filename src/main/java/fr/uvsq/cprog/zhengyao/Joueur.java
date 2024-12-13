package fr.uvsq.cprog.zhengyao;

import java.util.List;

public interface Joueur {

    String getName();

    // void playTurn();

    public void setCards(List<Card> cards);

    boolean hasCards();

    public String getColor();

    public int getTotalCardValue();

    String getColoredText(String text);

    public void setColor(String color);

    public int choisirCarte(int max);

    void showCards();

    public abstract void setpasserTour(boolean pass);

    int getCardCount();

    List<Card> getCards();

    // Method to add a card to the player's hand
    public void addCard(Card card);

    void ajouterPoints(int points);

    public abstract int choisirCombinaison(int maxChoix);

    public void setCombinaisonsPossibles(List<List<Card>> combinaisons);

    int getScore();

    /**
     * Removes a single card from the player's hand.
     */
    public void removeCard(Card card);

    /**
     * Removes multiple cards from the player's hand.
     */
    public void removeCards(List<Card> cardsToRemove);

    List<Card> jouerCartes();

    public List<Card> jouerCartes(List<Card> combinaison);

    // Method signatures for aPasseLeTour and passerTour
    boolean aPasseLeTour();

    void passerTour();
}
