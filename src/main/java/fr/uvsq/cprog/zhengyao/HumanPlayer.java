package fr.uvsq.cprog.zhengyao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import fr.uvsq.cprog.zhengyao.controller.ControleurDeJeu;
import fr.uvsq.cprog.zhengyao.controller.VerificateurCombinaison;

public class HumanPlayer implements Joueur {
    private final String name;
    private List<Card> cards;
    private int score = 0;
    private boolean aPasse = false;
    private static final Scanner scanner = new Scanner(System.in);
    private List<Card> main;
    private ControleurDeJeu controleurDeJeu;
    private List<List<Card>> combinaisonsPossibles; // List of possible combinations
    private String color; // New attribute for color

    public HumanPlayer(String name, List<Card> cards, ControleurDeJeu controleurDeJeu) {
        this.name = name;
        this.cards = cards;
        this.main = new ArrayList<>(cards);
        this.controleurDeJeu = controleurDeJeu;

    }

    public HumanPlayer(String name, List<Card> cards) {
        this.name = name;
        this.cards = new ArrayList<>(cards);
        this.main = new ArrayList<>(cards);
        this.combinaisonsPossibles = new ArrayList<>(); // Initialize it once
        this.controleurDeJeu = controleurDeJeu;

    }

    @Override
    public String getName() {
        return name;
    }

    // Getter for color
    public String getColor() {
        return color;
    }

    // Setter for color
    public void setColor(String color) {
        this.color = color; // Store the chosen color
    }

    // private void handleQuit() {
    //     System.out.println(getColoredText(name + " has chosen to quit the game."));
    //     // Logic to remove the player or end the game can be added here
    //     // Example: notify other players, update game state, etc.
    //     // If this is the last player, the game might need to terminate
    //     controleurDeJeu.endGameForPlayer(this); // Assuming `game.endGameForPlayer` handles player quitting
    // }

    // private void playSingleCard() {

    //     if (cards.isEmpty()) {
    //         System.out.println(getColoredText("You have no cards to play."));
    //         return;
    //     }

    //     // Ask for the single card choice
    //     System.out.print(getColoredText("Please select a card to play (e.g., '7 of Hearts'): "));
    //     String cardChoice = scanner.nextLine().trim().toLowerCase();

    //     // Find the card the player selected
    //     Optional<Card> cardToPlay = cards.stream()
    //             .filter(card -> card.toString().toLowerCase().equals(cardChoice))
    //             .findFirst();

    //     if (cardToPlay.isPresent()) {
    //         // If the card is valid, play it

    //         Card singleCard = cards.remove(0);
    //         System.out.println(getColoredText(name + " played: " + singleCard));
    //         controleurDeJeu.setDerniereCombinaisonJouee(Collections.singletonList(singleCard));

    //     } else {
    //         // If the card isn't in the player's hand, prompt again
    //         System.out.println(getColoredText(" desolé , mais la cart  non trouvé. essayer autre cart ."));
    //         playSingleCard();
    //     }
    // }

    @Override
    public void setpasserTour(boolean pass) {
        this.aPasse = pass;
    }

    private void playCombination(List<Card> combination) {
        cards.removeAll(combination);
        System.out.println(getColoredText(name + " played: " + combination));
        controleurDeJeu.setDerniereCombinaisonJouee(combination);
    }

    @Override
    public int getTotalCardValue() {
        return cards.stream()
                .mapToInt(Card::getValue) // Assuming Card has a getValue() method
                .sum();
    }

    @Override
    public boolean hasCards() {
        return !cards.isEmpty();
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
        this.main = new ArrayList<>(cards);
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
    public List<Card> getCards() {
        return cards;
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
    public boolean aPasseLeTour() {
        return aPasse;
    }

    @Override
    public void passerTour() {
        this.aPasse = true;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    // // Method to get the player's current hand
    // public List<Card> getMain() {
    // return new ArrayList<>(main); // Returns a copy to prevent modification
    // }

    @Override
    public List<Card> jouerCartes(List<Card> combinaison) {
        if (combinaisonValide(combinaison)) {
            cards.removeAll(combinaison); // Remove valid cards from hand
            return combinaison; // Return the played combination
        }
        return new ArrayList<>(); // Return an empty list if invalid
    }

    @Override
    public int choisirCombinaison(int maxChoix) {
        if (combinaisonsPossibles == null || combinaisonsPossibles.isEmpty()) {
            System.out.println(getColoredText("Aucune combinaison possible."));
            return 0;
        }

        System.out.println(getColoredText("0: Jouer une carte simple"));
        Scanner scanner = new Scanner(System.in);
        int choix = -1;

        while (choix < 0 || choix > maxChoix) {
            System.out.print(getColoredText("Veuillez choisir une combinaison (0 pour jouer une carte simple) : "));
            String input = scanner.nextLine().trim().toLowerCase();

            try {
                // Attempt to parse a numeric choice directly
                if (input.matches("\\d+")) {
                    choix = Integer.parseInt(input);
                } else {
                    // Handle text-based input like "je choisis combinaison 2"
                    choix = parseTextualChoice(input);
                }
            } catch (NumberFormatException e) {
                System.out.println(getColoredText(
                        "Désolé, choix de combinaison non valide. Veuillez entrer un nombre valide ou une phrase reconnue."));
            }

            if (choix < 0 || choix > maxChoix) {
                System.out.println(getColoredText("Désolé, choix invalide. Veuillez choisir un nombre valide."));
            }
        }

        return choix;
    }

    /**
     * Parses textual input for combination choice.
     *
     * @param input the user's input as a string
     * @return the parsed combination index or -1 if invalid
     */
    private int parseTextualChoice(String input) {
        // Regex to identify phrases like "combinaison 2" or "je choisis 1"
        Pattern pattern = Pattern.compile("(combinaison|choisir|choisis|combo|  choose | put`|)\\s*(\\d+)",
                Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(2)); // Extract and return the numeric choice
            } catch (NumberFormatException e) {
                return -1; // Invalid number format
            }
        }

        return -1; // No valid match found
    }

    public void setCombinaisonsPossibles(List<List<Card>> combinaisons) {
        this.combinaisonsPossibles = combinaisons;
    }

    private boolean combinaisonValide(List<Card> combinaison) {
        // Check if the combination is valid and stronger than the last played
        // combination
        List<Card> derniereCombinaisonJouee = controleurDeJeu.getDerniereCombinaisonJouee();
        return VerificateurCombinaison.estCombinaisonPlusForte(combinaison, derniereCombinaisonJouee);
    }

    /**
     * Removes a single card from the player's hand.
     */
    public void removeCard(Card card) {
        cards.remove(card);
    }

    /**
     * Removes multiple cards from the player's hand.
     */
    public void removeCards(List<Card> cardsToRemove) {
        cards.removeAll(cardsToRemove);
    }

    @Override
    public List<Card> jouerCartes() {
        System.out.println(getColoredText(name
                + ": Que voulez-vous jouer ? (par exemple : 'je mets 3 coeurs' or 'je mets la combinaison 1')"));
        System.out.print(getColoredText("Votre action : "));
        String input = scanner.nextLine().trim().toLowerCase();

        // Handle combination selection
        if (input.startsWith("je mets la combinaison") ||
                input.contains("combinaison") ||
                input.contains("combo") ||
                input.contains("comb")) {
            return handleCombinationChoice(input);
        }
        if (input.startsWith("je passe ") ||
                input.contains("pass") ||
                input.contains("passer") ||
                input.contains("pas")) {
            return new ArrayList<>();
        }

        // Otherwise, assume the input is related to playing cards
        List<Card> playedCards = new ArrayList<>();
        Map<String, Integer> cardRequest = parsePlayerCardInput(input);

        if (cardRequest == null || cardRequest.isEmpty()) {
            System.out.println(getColoredText("Désolé, votre proposition n'est pas claire. Essayez encore."));
            return jouerCartes();
        }

        // Validate card request
        for (Map.Entry<String, Integer> entry : cardRequest.entrySet()) {
            String cardName = entry.getKey();
            int count = entry.getValue();
            List<Card> matchingCards = findMatchingCards(cardName);

            if (matchingCards.size() < count) {
                System.out.println(
                        getColoredText("Désolé " + name + ", mais vous n’avez que " + matchingCards.size() + " "
                                + cardName + "(s)."));
                return jouerCartes();
            }

            playedCards.addAll(matchingCards.subList(0, count)); // Add the requested number of cards
        }

        // Remove played cards from the player's hand
        // cards.removeAll(playedCards);
        return playedCards;
    }

    /**
     * Handles the case where the player selects a combination to play.
     */
    private List<Card> handleCombinationChoice(String input) {
        try {
            // Extract the combination choice (e.g., "combinaison 1")
            Pattern pattern = Pattern.compile("combinaison\\s*(\\d+)", Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                int combinationIndex = Integer.parseInt(matcher.group(1)) - 1;

                // Get the valid combinations
                List<List<Card>> playableCombinations = generatePlayableCombinations();
                if (combinationIndex >= 0 && combinationIndex < playableCombinations.size()) {
                    List<Card> chosenCombination = playableCombinations.get(combinationIndex);
                    playCombination(chosenCombination);
                    return chosenCombination;
                } else {
                    System.out.println(getColoredText("Désolé, cette combinaison n'est pas valide. Essayez encore."));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(getColoredText("Erreur de formatage dans la phrase."));
        }

        return jouerCartes(); // Prompt the player again if the input is invalid
    }

    /**
     * Parses the player's input for card specifications.
     * Handles cases where the player specifies:
     * - Number of cards and type (e.g., "je mets 3 hearts et 2 diamonds").
     * - Specific cards by rank and suit (e.g., "je mets Five of hearts et King of
     * spades").
     */
    private Map<String, Integer> parsePlayerCardInput(String input) {
        Map<String, Integer> cardRequest = new HashMap<>();
        Pattern multipleCardsPattern = Pattern.compile("(\\d+)\\s*(de\\s+)?([a-zé]+)", Pattern.UNICODE_CASE);
        Pattern specificCardsPattern = Pattern.compile("(\\b\\w+\\b)\\s+of\\s+(\\b\\w+\\b)", Pattern.UNICODE_CASE);

        // Match inputs like "3 hearts" or "4 spades"
        Matcher multipleMatcher = multipleCardsPattern.matcher(input);
        while (multipleMatcher.find()) {
            try {
                int count = Integer.parseInt(multipleMatcher.group(1));
                String cardType = multipleMatcher.group(3).toLowerCase();

                // Combine requests for the same type (e.g., "3 hearts and 2 hearts")
                cardRequest.put(cardType, cardRequest.getOrDefault(cardType, 0) + count);
            } catch (NumberFormatException e) {
                System.out.println(getColoredText("Erreur de formatage dans la phrase : " + input));
                return null;
            }
        }

        // Match inputs like "Five of hearts" or "King of spades"
        Matcher specificMatcher = specificCardsPattern.matcher(input);
        while (specificMatcher.find()) {
            String rank = specificMatcher.group(1).toLowerCase();
            String suit = specificMatcher.group(2).toLowerCase();
            String cardName = rank + " of " + suit;

            // Specific cards are treated as "1 card request"
            cardRequest.put(cardName, cardRequest.getOrDefault(cardName, 0) + 1);
        }

        // Return the parsed request or null if no valid input
        if (cardRequest.isEmpty()) {
            System.out.println(getColoredText("Aucune carte valide spécifiée. Essayez encore."));
            return null;
        }

        return cardRequest;
    }

    /**
     * Finds cards in the player's hand matching a specific card name (e.g., "roi"
     * or "8").
     */
    private List<Card> findMatchingCards(String cardName) {
        List<Card> matchingCards = new ArrayList<>();

        for (Card card : cards) {
            if (card.toString().toLowerCase().contains(cardName)) {
                // System.out.println(
                // getColoredText("test of card to string with matching : " + card.toString() +
                // cardName));

                matchingCards.add(card);
            }
        }

        return matchingCards;
    }

    @Override
    public int choisirCarte(int max) {
        Scanner scanner = new Scanner(System.in);
        int choix = -1;

        // Keep prompting until a valid choice is made
        while (choix < 1 || choix > max) {
            System.out.print("Entrez le numéro de la carte que vous souhaitez jouer (1 à " + max + "): ");
            try {
                choix = Integer.parseInt(scanner.nextLine()); // Read user input
            } catch (NumberFormatException e) {
                System.out.println(getColoredText(("Entrée invalide, veuillez entrer un nombre.")));
            }

            if (choix < 1 || choix > max) {
                System.out.println(
                        getColoredText(("Choix invalide. Veuillez choisir un nombre entre 1 et " + max + ".")));
            }
        }

        return choix; // Return the player's choice
    }

    private List<List<Card>> generatePlayableCombinations() {
        List<List<Card>> combinations = new ArrayList<>();

        // Generate combinations by descending reliability
        combinations.addAll(generateTriplets());
        combinations.addAll(generatePairs());
        combinations.addAll(generateSingleCards());
        combinations.addAll(generateBombe());

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

    private List<List<Card>> generateTriplets() {
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

    private List<List<Card>> generatePairs() {
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

    private List<List<Card>> generateBombe() {
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

    private List<List<Card>> generateSingleCards() {
        List<List<Card>> singles = new ArrayList<>();
        for (Card card : cards) {
            singles.add(Collections.singletonList(card));
        }
        return singles;
    }

    public void resetPassStatus() {
        this.aPasse = false;
    }

    @Override
    public String getColoredText(String text) {
        if (color == null) {
            return text; // Return plain text if no color is set
        }

        // Map color names to Jansi Color
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
                ansiColor = Color.DEFAULT; // Fallback for unknown colors
        }
        // Apply color using Jansi's Ansi class
        return Ansi.ansi().fg(ansiColor).a(text).reset().toString();
    }
}
