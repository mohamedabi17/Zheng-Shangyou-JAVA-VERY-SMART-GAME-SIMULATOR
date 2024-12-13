package fr.uvsq.cprog.zhengyao;

import fr.uvsq.cprog.zhengyao.controller.VerificateurCombinaison;
import fr.uvsq.cprog.zhengyao.controller.CalculateurScore;
import fr.uvsq.cprog.zhengyao.controller.ConditionsDeFin;
import fr.uvsq.cprog.zhengyao.controller.ControleurDeJeu;

import java.util.ArrayList;
import java.util.List;

public class SimpleGameEngine extends GameEngine {
    private final ControleurDeJeu controleur;

    public SimpleGameEngine(ControleurDeJeu controleur) {
        this.controleur = controleur;
    }

    @Override
    public List<Card> executeTurn(Joueur player, ConditionsDeFin conditionsDeFin) {
        List<Card> playerCards = new ArrayList<>(player.getCards());

        if (playerCards.isEmpty()) {
            System.out.println(player.getColoredText(player.getName() + " n'a plus de cartes à jouer."));
            return new ArrayList<>();
        }

        // Always play the first card in hand (childlike behavior)
        List<Card> cardsToPlay = new ArrayList<>();

        // Validate and play the card
        cardsToPlay.add(playerCards.get(0));
        if (VerificateurCombinaison.estCombinaisonLegale(cardsToPlay) &&
                VerificateurCombinaison.estCombinaisonPlusForte(cardsToPlay,
                        conditionsDeFin.getLastPlayedCombinaison())) {
            playCombination(player, cardsToPlay, playerCards, conditionsDeFin);

        } else {
            System.out.println(
                    player.getColoredText(player.getName() + "with simpe Strategie joue avec une carte moin forte:ignioré "+cardsToPlay.add(playerCards.get(0))));

            // passTurn(player);
        }

        // Update player's hand
        player.setCards(playerCards);
        return cardsToPlay; // Return the cards played
    }

    private void playCombination(Joueur player, List<Card> chosenCombination, List<Card> playerCards,
            ConditionsDeFin conditionsDeFin) {
        System.out.println(player.getColoredText(player.getName() + " joue: " + chosenCombination));
        conditionsDeFin.setLastPlayedCombination(playerCards);
        CalculateurScore.ajouterScore(player, chosenCombination);
        // playerCards.removeAll(chosenCombination);
    }
}
