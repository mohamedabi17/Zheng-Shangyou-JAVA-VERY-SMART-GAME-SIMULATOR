package fr.uvsq.cprog.zhengyao;

import java.util.*;
import java.util.stream.Collectors;

import fr.uvsq.cprog.zhengyao.controller.ConditionsDeFin;
import fr.uvsq.cprog.zhengyao.controller.ControleurDeJeu;
import fr.uvsq.cprog.zhengyao.controller.VerificateurCombinaison;

public class StrategicGameEngine extends GameEngine {

    private final ControleurDeJeu controleur;

    public StrategicGameEngine(ControleurDeJeu controleur) {
        this.controleur = controleur;
    }

    @Override
    public List<Card> executeTurn(Joueur player, ConditionsDeFin conditionsDeFin) {
        List<Card> hand = player.getCards();

        if (hand.isEmpty()) {
            System.out.println(player.getColoredText(player.getName() + " has no cards left to play."));
            return new ArrayList<>(); // Return an empty list when there are no cards left
        }

        // Sort cards by rank from strongest to weakest for easier strategizing
        hand.sort(Collections.reverseOrder());

        List<Card> playedCards = new ArrayList<>();

        // Try to find a stronger combination to play
        // List<List<Card>> combinations = VerificateurCombinaison.trouverCombinaison(hand, hand.size());
        List<List<Card>> combinations = generatePlayableCombinations(hand);

        Optional<List<Card>> strongerCombination = combinations.stream()
                .filter(combination -> VerificateurCombinaison.estCombinaisonPlusForte(
                        combination,
                        conditionsDeFin.getLastPlayedCombinaison()))
                .findFirst();

        if (strongerCombination.isPresent()) {
            // Play the stronger combination
            List<Card> combination = strongerCombination.get();
            playedCards = combination;
            // hand.removeAll(playedCards); // Remove cards from hand after playing
            System.out.println(player.getColoredText(player.getName() + " plays combination: " + playedCards));
        } else {
            // Fallback to playing a single card stronger than the last single card
            Optional<Card> strongerSingleCard = findStrongerSingleCard(hand, conditionsDeFin);

            if (strongerSingleCard.isPresent()) {
                Card singleCard = strongerSingleCard.get();
                playedCards = List.of(singleCard); // Wrap the card in a list
                // hand.remove(singleCard); // Remove the card from hand after playing
                System.out.println(player.getColoredText(player.getName() + " plays single card: " + playedCards));
            } else {
                // No valid play, the player passes
                System.out.println(player.getColoredText(player.getName() + "with strategicEngine passes their turn."));
            }
        }

        return playedCards; // Return the cards played during this turn
    }



    private void passTurn(Joueur player) {
        System.out.println(player.getColoredText(player.getName() + " passes their turn."));
        player.passerTour();
        controleur.passerTour();

    }

    

    /**
     * Finds the best stronger combination to play based on the game rules.
     *
     * @param hand            List of cards in the player's hand.
     * @param conditionsDeFin The game state to compare with.
     * @return Optional of the stronger combination to play.
     */
    private Optional<List<Card>> findStrongerCombination(List<Card> hand, ConditionsDeFin conditionsDeFin) {
        // Find all combinations (pairs, triplets, etc.)
        List<Card> pairs = findPairs(hand);
        List<Card> triplets = findTriplets(hand);

        // Check if these combinations are stronger than the previous one
        if (!pairs.isEmpty() && VerificateurCombinaison.estCombinaisonPlusForte(pairs,conditionsDeFin.getLastPlayedCombinaison())) {
            return Optional.of(pairs);
        }

        if (!triplets.isEmpty() && VerificateurCombinaison.estCombinaisonPlusForte(
                triplets,
                conditionsDeFin.getLastPlayedCombinaison())) {
            return Optional.of(triplets);
        }

        // If no stronger combination is found, return empty
        return Optional.empty();
    }

    /**
     * Finds the next strongest single card to play.
     *
     * @param hand            List of cards in the player's hand.
     * @param conditionsDeFin The game state to compare with.
     * @return Optional of the stronger single card to play.
     */
    private Optional<Card> findStrongerSingleCard(List<Card> hand, ConditionsDeFin conditionsDeFin) {
        List<Card> lastPlayedCombination = conditionsDeFin.getLastPlayedCombinaison();

        // Ensure last played combination is a single card
        if (lastPlayedCombination.size() != 1) {
            return Optional.empty();
        }

        Card lastPlayedCard = lastPlayedCombination.get(0);

        return hand.stream()
                .filter(card -> VerificateurCombinaison.isStrongerCard(card, lastPlayedCard))
                .findFirst();
    }

    /**
     * Finds pairs in the hand and returns them if found.
     *
     * @param hand List of cards.
     * @return List of paired cards.
     */
    private List<Card> findPairs(List<Card> hand) {
        Map<Rank, List<Card>> groupedCards = hand.stream()
                .collect(Collectors.groupingBy(Card::getRank));

        return groupedCards.values().stream()
                .filter(group -> group.size() >= 2) // Ensure at least 2 cards in the group for a pair
                .flatMap(group -> group.stream().limit(2)) // Limit to 2 cards per pair
                .collect(Collectors.toList());
    }

    /**
     * Finds triplets in the hand and returns them if found.
     *
     * @param hand List of cards.
     * @return List of triplet cards.
     */
    private List<Card> findTriplets(List<Card> hand) {
        Map<Rank, List<Card>> groupedCards = hand.stream()
                .collect(Collectors.groupingBy(Card::getRank));

        return groupedCards.values().stream()
                .filter(group -> group.size() >= 3) // Ensure at least 3 cards in the group for a triplet
                .flatMap(group -> group.stream().limit(3)) // Limit to 3 cards per triplet
                .collect(Collectors.toList());
    }


    private List<List<Card>> generatePlayableCombinations(List<Card> cards) {
        List<List<Card>> combinations = new ArrayList<>();

        // Generate combinations by descending reliability
        combinations.addAll(generateTriplets(cards));
        combinations.addAll(generatePairs(cards));
        combinations.addAll(generateSingleCards(cards));
        combinations.addAll(generateBombe(cards));

        // Sort combinations (strongest first, weakest last)
        combinations.sort(Comparator.comparingInt(this::getCombinationStrength).reversed());

        return combinations;
    }

    private int getCombinationStrength(List<Card> combination) {
        if (combination.size() == 3)
            return 3; // Triplets are strongest
        if (combination.size() == 2)
            return 2; // Pairs are next strongest
        return 1; // Singles are weakest
    }

    private List<List<Card>> generateTriplets(List<Card> cards) {
        List<List<Card>> triplets = new ArrayList<>();
        for (int i = 0; i < cards.size() - 2; i++) {
            if (cards.get(i).getRank() == cards.get(i + 1).getRank()
                    && cards.get(i).getRank() == cards.get(i + 2).getRank()) {
                List<Card> triplet = new ArrayList<>();
                triplet.add(cards.get(i));
                triplet.add(cards.get(i + 1));
                triplet.add(cards.get(i + 2));
                triplets.add(triplet);
            }
        }
        return triplets;
    }

    private List<List<Card>> generatePairs(List<Card> cards) {
        List<List<Card>> pairs = new ArrayList<>();
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getRank() == cards.get(i + 1).getRank()) {
                List<Card> pair = new ArrayList<>();
                pair.add(cards.get(i));
                pair.add(cards.get(i + 1));
                pairs.add(pair);
            }
        }
        return pairs;
    }

    private List<List<Card>> generateBombe(List<Card> cards) {
        List<List<Card>> bombes = new ArrayList<>();
        for (int i = 0; i < cards.size() - 3; i++) {
            if (cards.get(i).getRank() == cards.get(i + 1).getRank() &&
                    cards.get(i).getRank() == cards.get(i + 2).getRank() &&
                    cards.get(i).getRank() == cards.get(i + 3).getRank()) {
                List<Card> bombe = new ArrayList<>();
                bombe.add(cards.get(i));
                bombe.add(cards.get(i + 1));
                bombe.add(cards.get(i + 2));
                bombe.add(cards.get(i + 3));
                bombes.add(bombe);
            }
        }
        return bombes;
    }

    private List<List<Card>> generateSingleCards(List<Card> cards) {
        List<List<Card>> singles = new ArrayList<>();
        for (Card card : cards) {
            singles.add(Collections.singletonList(card));
        }
        return singles;
    }
}
