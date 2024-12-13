package fr.uvsq.cprog.zhengyao.controller;

import fr.uvsq.cprog.zhengyao.Card;
import fr.uvsq.cprog.zhengyao.CombinaisonCarte;
import fr.uvsq.cprog.zhengyao.Rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class pour la verification des combinaison la collection des combinaison et
 * calcule de score de chaque une
 */
public class VerificateurCombinaison {

    /**
     * This method calculates the score based on the type of combination and the
     * cards involved.
     *
     * @param type        The type of combination (e.g., SIMPLE, PAIRE, BRELAN,
     *                    etc.)
     * @param combinaison The list of cards in the combination
     * @return The calculated score for the given combination
     */
    public static int scoreParType(CombinaisonCarte.TypeCombinaison type, List<Card> combinaison) {
        return switch (type) {
            case SIMPLE -> 1;
            case PAIRE -> {
                if (combinaison.size() == 2 && sontMemeValeur(combinaison)) {
                    int score = 2 * calculerScoreCarteSeule(combinaison.subList(0, 1));
                    yield score;
                } else {
                    throw new IllegalArgumentException("Invalid PAIRE combination.");
                }
            }
            case BRELAN -> {
                if (combinaison.size() == 3 && sontMemeValeur(combinaison)) {
                    int score = 3 * calculerScoreCarteSeule(combinaison.subList(0, 1));
                    yield score;
                } else {
                    throw new IllegalArgumentException("Invalid BRELAN combination.");
                }
            }
            case CARRE -> 50;
            case SUITE_PAIRES -> combinaison.size() / 2 * 5;
            case SUITE_BRELANS -> combinaison.size() / 3 * 6;
            case FULL -> 8; // Assuming FULL has a fixed score
            case SUITE_COULEUR -> combinaison.size() * 9; // Assuming score scales with the number of cards
            case CARTE_SEULE -> calculerScoreCarteSeule(combinaison);
            default -> 0;
        };
    }

    /**
     * This method calculates the score for a single card.
     *
     * @param combinaison The list containing exactly one card
     * @return The score for the single card
     */
    public static int calculerScoreCarteSeule(List<Card> combinaison) {
        if (combinaison.size() != 1) {
            throw new IllegalArgumentException("La combinaison CARTE_SEULE doit contenir exactement une carte.");
        }

        Card carte = combinaison.get(0);
        Rank rank = carte.getRank();

        return switch (rank) {
            case TWO -> 13; // 2 est la plus forte
            case ACE -> 12; // As
            case KING -> 11; // Roi
            case QUEEN -> 10; // Dame
            case JACK -> 9; // Valet
            case TEN -> 8;
            case NINE -> 7;
            case EIGHT -> 6;
            case SEVEN -> 5;
            case SIX -> 4;
            case FIVE -> 3;
            case FOUR -> 2;
            case THREE -> 1; // 3 est la plus faible
        };
    }

  

    /**
     * @param candidate      cart
     * @param lastPlayedCard cart a comparer avec
     * @return boolean of isStrongerCard
     */
    public static boolean isStrongerCard(Card candidate, Card lastPlayedCard) {
        if (lastPlayedCard == null) {
            return true; // If there's no last played card, any card is stronger
        }

        // Compare card ranks based on the game's ranking rules
        return candidate.getRank().getValue() > lastPlayedCard.getRank().getValue();
    }

    /**
     * Verifies if the combination is valid based on the defined rules. *
     * 
     * @param cartes listes of cartes
     * @return boolean of estCombinaisonLegale ou pas
     */
    public static boolean estCombinaisonLegale(List<Card> cartes) {
        if (cartes == null || cartes.isEmpty()) {
            return false; // Invalid if null or empty
        }

        int size = cartes.size();
        if (size == 1) {
            return true; // Single card is always valid
        } else if (size == 2) {
            return sontMemeValeur(cartes); // Valid only if it's a pair
        } else if (size == 3) {
            return sontMemeValeur(cartes) || estSuite(cartes); // Triplet or a valid straight
        } else if (size == 4) {
            return sontMemeValeur(cartes); // Four of a kind is valid
        } else if (size >= 5) {
            return estSuite(cartes) || estTripletEtDeuxIdentiques(cartes) || estSuiteDeBrelans(cartes);
            // Valid if it's a straight, triplet + 2 identical, or a sequence of triplets
        }
        return false; // Fallback
    }

    /**
     * // Check if the new combination is stronger than the last one. *
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static boolean estCombinaisonPlusForte(List<Card> nouvelleCombinaison, List<Card> derniereCombinaison) {
        if (derniereCombinaison == null || derniereCombinaison.size() == 0) {
            return true; // If there's no last combination, the new one is automatically stronger
        }

        CombinaisonCarte.TypeCombinaison typeNouvelle = CombinaisonCarte.typeCombinaison(nouvelleCombinaison);
        CombinaisonCarte.TypeCombinaison typeDerniere = CombinaisonCarte.typeCombinaison(derniereCombinaison);

        // Calculate scores for both combinations based on their type
        int scoreNouvelle = scoreParType(typeNouvelle, nouvelleCombinaison);
        int scoreDerniere = scoreParType(typeDerniere, derniereCombinaison);

        // If the new combination has a higher score, it's stronger
        if (scoreNouvelle >= scoreDerniere) {
            return true;
        } else  {
            return false;
        }

    }
  
    /**
     * Calculate the total value of the cards in the provided combination.*
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static int calculerValeurTotale(List<Card> cartes) {
        int total = 0;
        for (Card card : cartes) {
            total += card.getRankValue(); // Use method to get card's value
        }
        return total;
    }

    /**
     * Checks if there is a triplet with 2 identical cards in the list.
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static boolean estTripletEtDeuxIdentiques(List<Card> cartes) {
        Map<Integer, List<Card>> rankGroups = new HashMap<>();
        for (Card card : cartes) {
            rankGroups.computeIfAbsent(card.getRankValue(), k -> new ArrayList<>()).add(card);
        }

        boolean hasTriplet = false;
        boolean hasPair = false;

        for (List<Card> group : rankGroups.values()) {
            if (group.size() >= 3) {
                hasTriplet = true; // Found a triplet
            } else if (group.size() >= 2) {
                hasPair = true; // Found a pair
            }
        }
        return hasTriplet && hasPair; // Must have both a triplet and a pair
    }

    /**
     * Checks if all cards in the given list have the same rank.
     *
     * @param cartes The list of cards to check
     * @return true if all cards have the same rank, false otherwise
     */
    private static boolean sontMemeValeur(List<Card> cartes) {
        if (cartes == null || cartes.isEmpty()) {
            return false;
        }

        Rank firstRank = cartes.get(0).getRank();
        for (Card card : cartes) {
            if (card.getRank() != firstRank) {
                return false;
            }
        }
        return true;
    }

    // Checks if the provided combination forms a straight.
    public static boolean estSuite(List<Card> cartes) {
        List<Integer> values = cartes.stream()
                .map(Card::getRankValue)
                .distinct()
                .sorted()
                .toList();

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) != values.get(i - 1) + 1) {
                return false;
            }
        }
        return values.size() >= 5;
    }

    /**
     * // Finds all combinations of a specified size within the player's hand*
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static List<List<Card>> trouverCombinaison(List<Card> cartes, int taille) {
        Map<Integer, List<Card>> rankGroups = new HashMap<>();
        for (Card card : cartes) {
            rankGroups.computeIfAbsent(card.getRankValue(), k -> new ArrayList<>()).add(card);
        }

        List<List<Card>> combinations = new ArrayList<>();
        for (List<Card> group : rankGroups.values()) {
            if (group.size() >= taille) {
                for (int i = 0; i <= group.size() - taille; i++) {
                    combinations.add(new ArrayList<>(group.subList(i, i + taille)));
                }
            }
        }
        return combinations;
    }

    /**
     * // // Removes duplicate combinations.*
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static List<List<Card>> removeDuplicates(List<List<Card>> combinations) {
        List<List<Card>> uniqueCombinations = new ArrayList<>();
        for (List<Card> combination : combinations) {
            if (!uniqueCombinations.contains(combination)) {
                uniqueCombinations.add(combination);
            }
        }
        return uniqueCombinations;
    }

    /**
     * Checks if the provided combination is a sequence of triplets.*
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static boolean estSuiteDeBrelans(List<Card> cartes) {
        List<List<Card>> triplets = trouverCombinaison(cartes, 3);
        if (triplets.size() < 2)
            return false;

        List<Integer> tripletValues = triplets.stream()
                .map(triplet -> triplet.get(0).getRankValue())
                .distinct()
                .sorted()
                .toList();

        for (int i = 1; i < tripletValues.size(); i++) {
            if (tripletValues.get(i) != tripletValues.get(i - 1) + 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds all triplets and pairs in the provided hand.
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static List<List<Card>> trouverTripletEtDeuxIdentiques(List<Card> cartes) {
        List<List<Card>> result = new ArrayList<>();
        result.addAll(trouverCombinaison(cartes, 3)); // Triplets
        result.addAll(trouverCombinaison(cartes, 2)); // Pairs
        return removeDuplicates(result); // Remove duplicates
    }

    /**
     * // Finds all straights of triplets (brelans) in the provided hand.*
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static List<List<Card>> trouverSuitesDeBrelans(List<Card> cartes) {
        List<List<Card>> brelans = trouverCombinaison(cartes, 3);
        List<List<Card>> suitesDeBrelans = new ArrayList<>();

        for (List<Card> triplet : brelans) {
            List<Card> temp = new ArrayList<>(triplet);
            for (Card card : cartes) {
                if (!temp.contains(card) && temp.size() < 6) {
                    temp.add(card);
                }
            }

            if (temp.size() == 6 && estSuite(temp)) {
                suitesDeBrelans.add(temp);
            }
        }

        return removeDuplicates(suitesDeBrelans); // Remove duplicates
    }

    /**
     * // Find pairs in the provided hand.*
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static List<List<Card>> trouverPaires(List<Card> cartes) {
        return trouverCombinaison(cartes, 2);
    }

    /**
     * // Find triplets in the provided hand.
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static List<List<Card>> trouverBrelans(List<Card> cartes) {
        return trouverCombinaison(cartes, 3);
    }

    /**
     * // Find quads (bombs) in the provided hand.
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static List<List<Card>> trouverCarres(List<Card> cartes) {
        return trouverCombinaison(cartes, 4);
    }

    /**
     * // Find straights in the provided hand.*
     * 
     * @param nouvelleCombinaison nouveau combinaisonn
     * @param derniereCombinaison la derniere combinaison forte
     * @return boolean of estCombinaisonPlusForte ou pas
     */
    public static List<List<Card>> trouverSuites(List<Card> cartes) {
        List<List<Card>> suites = new ArrayList<>();
        List<Card> sortedCards = new ArrayList<>(cartes);
        sortedCards.sort((a, b) -> a.getRankValue() - b.getRankValue());

        List<Card> currentStraight = new ArrayList<>();
        for (int i = 0; i < sortedCards.size(); i++) {
            if (currentStraight.isEmpty() || sortedCards.get(i)
                    .getRankValue() == currentStraight.get(currentStraight.size() - 1).getRankValue() + 1) {
                currentStraight.add(sortedCards.get(i)); // Add to current straight
            } else {
                if (currentStraight.size() >= 5) {
                    suites.add(new ArrayList<>(currentStraight)); // Add valid straight
                }
                currentStraight.clear(); // Reset current straight
                currentStraight.add(sortedCards.get(i)); // Start new straight
            }
        }
        if (currentStraight.size() >= 5) {
            suites.add(new ArrayList<>(currentStraight)); // Add last valid straight
        }

        return removeDuplicates(suites); // Remove duplicates
    }
}
