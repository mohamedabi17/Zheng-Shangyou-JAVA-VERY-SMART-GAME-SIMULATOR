package fr.uvsq.cprog.zhengyao;

import java.util.List;

import fr.uvsq.cprog.zhengyao.controller.ConditionsDeFin;

/**
 * Abstract class representing the engine for a game.
 * This class defines the structure for executing turns in the game.
 */
public abstract class GameEngine {
    /**
     * Executes a turn for the specified player.
     *
     * @param player the virtual player whose turn is to be executed
     */
    public abstract List<Card> executeTurn(Joueur player, ConditionsDeFin conditionsDeFin);

    // public abstract void mettreAJourJoueurs(List<Joueur> nouveauxJoueurs);

}
