package fr.uvsq.cprog.zhengyao.controller;

import fr.uvsq.cprog.zhengyao.Joueur;
import fr.uvsq.cprog.zhengyao.Card;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour verifier les condition de fin de jeux.
 */
public class ConditionsDeFin {

    private static final int SCORE_MAX = 500; // Points needed to end the game

 
    private List<Joueur> joueurs;
    private List<Card> lastPlayedCombination = new ArrayList<>();

    public ConditionsDeFin(List<Joueur> joueurs) {
        this.joueurs = joueurs;
    }

    /**
     * Checks if the game is over based on a player's score.
     */
    public boolean verifierFinPartie() {
        for (Joueur joueur : joueurs) {
            if (joueur.getScore() >= SCORE_MAX) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the last played combination of cards.
     *
     * @param combination The combination to set as the last played.
     */
    public void setLastPlayedCombination(List<Card> combination) {
        this.lastPlayedCombination = new ArrayList<>(combination);
    }

    /**
     * Clear the last Played Combinaison.
     */
    public void clearLastStrongerPlayedCombination() {
        this.lastPlayedCombination.clear();
    }

    public List<Card> getLastPlayedCombinaison() {
        return lastPlayedCombination;
    }

    
}
