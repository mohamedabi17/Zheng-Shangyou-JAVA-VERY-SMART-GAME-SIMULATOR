package fr.uvsq.cprog.zhengyao.controller;

import fr.uvsq.cprog.zhengyao.CombinaisonCarte;
import fr.uvsq.cprog.zhengyao.Joueur;
import fr.uvsq.cprog.zhengyao.Card;
import java.util.List;


/**
 * Classe pour calculer et gérer les scores des joueurs.
 */
public class CalculateurScore {

    /**
     * Ajoute des points au score du joueur en fonction de la taille de la
     * combinaison.
     *
     * @param joueur       Le joueur dont on souhaite mettre à jour le score.
     * @param cartesJouees Les cartes jouées qui forment la combinaison.
     */
    public static void ajouterScore(Joueur joueur, List<Card> cartesJouees) {
        // Determine the type of combination played
        CombinaisonCarte.TypeCombinaison type = CombinaisonCarte.typeCombinaison(cartesJouees);

        // Get the score for the combination type
        int score = VerificateurCombinaison.scoreParType(type, cartesJouees);

        // Add the score to the player's total score
        joueur.ajouterPoints(score);
    }
}
