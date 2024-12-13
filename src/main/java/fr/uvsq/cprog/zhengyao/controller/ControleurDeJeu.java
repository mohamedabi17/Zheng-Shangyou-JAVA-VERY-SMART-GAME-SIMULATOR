package fr.uvsq.cprog.zhengyao.controller;

import fr.uvsq.cprog.zhengyao.GameEngine;
import fr.uvsq.cprog.zhengyao.GameHistory;
import fr.uvsq.cprog.zhengyao.GameHistoryManager;
import fr.uvsq.cprog.zhengyao.HumanPlayer;
import fr.uvsq.cprog.zhengyao.Joueur;
import fr.uvsq.cprog.zhengyao.Suit;
import fr.uvsq.cprog.zhengyao.Card;
import fr.uvsq.cprog.zhengyao.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Main game controller for Zheng Shangyou.
 * Manages game flow, turn control, and endgame conditions.
 */
public class ControleurDeJeu {
    private List<Joueur> joueurs;
    private Joueur currentPlayer; // Field to hold the current player
    private int tourActuel;
    private Optional<Joueur> joueurFort = Optional.empty();
    private boolean mancheTerminee;
    private boolean premierPli = true;
    private ConditionsDeFin conditionsDeFin; // Instance of ConditionsDeFin
    private List<Joueur> winners = new ArrayList<>(); // Track winners
    private boolean allPlayersFinished = false; // Track if all players have finished their cards
    private GameEngine basicGameEngine;
    private GameEngine advancedGameEngine;
    private GameHistory gameHistory;
    private List<Joueur> ordreElimination = new ArrayList<>();

    /**
     *  Default constructor without players .
     */
    public ControleurDeJeu() {
        this.joueurs = new ArrayList<>();
        this.conditionsDeFin = new ConditionsDeFin(this.joueurs); // Empty list initially
        this.gameHistory = new GameHistory(); // Historique initialisé

    }

    /**
     * ControleurDeJeu constructor.
     * 
     * @param joueurs kles joueurs
     */    
    public ControleurDeJeu(List<Joueur> joueurs) {
        if (joueurs == null || joueurs.isEmpty()) {
            throw new IllegalArgumentException("La liste des joueurs ne peut pas être null ou vide.");
        }
        this.joueurs = new ArrayList<>(joueurs);
        this.conditionsDeFin = new ConditionsDeFin(joueurs); // Pass required parameters

        // Set the starting player based on the lowest card
        initialiserOrdre();
    }

    /**
     *Setter for the joueurs list.
     * 
     * @param joueurs kles joueurs
     */
    public void setJoueurs(List<Joueur> joueurs) {
        if (joueurs == null || joueurs.isEmpty()) {
            throw new IllegalArgumentException("La liste des joueurs ne peut pas être null ou vide.");
        }
        this.joueurs = new ArrayList<>(joueurs);
        gameHistory.addAction("Début de la partie de Zheng Shangyou  les  joueurs  sont : " + joueurs);

        this.conditionsDeFin = new ConditionsDeFin(joueurs); // Update conditions with new players
        initialiserOrdre(); // Initialize order based on new players
    }

    public void demarrerJeu() {
        System.out.println("Début de la partie de Zheng Shangyou !");
        while (checkGameStatus()) { // Check the overall game status
            demarrerManche();
        }
        gameHistory.addAction("La partie est terminée ! : ");

        System.out.println("La partie est terminée !");
        afficherGagnants();

    }

    /**
     * demarrerManche function.
     */
    private void demarrerManche() {
        mancheTerminee = false;
        premierPli = true;
        gameHistory.addAction("Début de la manche ");
        int count = 0;

        while (!mancheTerminee || !allPlayersFinished) {
            gameHistory.addAction("la manche numéro " + count + 1);
            count = count + 1;
            gererPli();
            passerTour();
        }
        afficherGagnants();
        gameHistory.addAction("Fin de la manche " + count + 1);
        System.out.println("Fin de la manche !");
    }

    public void gererPli() {
        // Ensure the current tour index is valid
        if (tourActuel < 0 || tourActuel >= joueurs.size()) {
            throw new IllegalStateException("Index du tour actuel est hors des limites.");
        }

        Joueur joueur = joueurs.get(tourActuel);
        if (premierPli == true) {
            System.out.println(joueur.getColoredText(
                    "C'est au tour de le joueur avec le 3 tréflé ou la carte la plus fort " + joueur.getName()));
        } else {
            System.out.println(joueur.getColoredText(
                    "C'est au tour de le joueur " + joueur.getName()));
        }
        System.out.println(joueur.getColoredText(
                "C'est au tour de le joueur  " + joueur.getName()));
        gameHistory.addAction("C'est au tour de " + joueur.getName());

        List<Card> cartesJouees = jouerTourJoueur(joueur);

        if (premierPli == true) {
            premierPli = false;
        } else if (cartesJouees.isEmpty() || !VerificateurCombinaison.estCombinaisonPlusForte(cartesJouees,
                conditionsDeFin.getLastPlayedCombinaison())) {
            System.out.println(joueur.getColoredText(joueur.getName() + " passe son tour."));
            gameHistory.addAction(joueur.getColoredText(joueur.getName() + " passe son tour."));
            // joueur.passerTour();
            // passerTour();
        } else {
            // Handle the case where the player plays cards
            System.out.println(joueur.getColoredText(joueur.getName() + " a joué : " + cartesJouees));

            gameHistory.addAction(joueur.getColoredText(joueur.getName() + " a joué : " + cartesJouees));

            traiterCombinaison(cartesJouees, joueur);

            // Check if the player has finished their cards
            if (!joueur.hasCards()) {
                System.out.println(joueur.getColoredText(joueur.getName() + " a terminé toutes ses cartes !"));
                gameHistory.addAction(joueur.getColoredText(joueur.getName() + " a terminé toutes ses cartes !"));
                // joueur.passerTour();// Mark player as passed
                // passerTour();

                if (joueurFort.isEmpty()) {
                    joueurFort = Optional.of(joueur); // Set the first winner
                }
            }

        }

        // Display remaining cards for each player
        afficherCartesRestantes();

        if (!joueur.hasCards()) {
            System.out.println(joueur.getColoredText(joueur.getName() + " a terminé toutes ses cartes !"));
            gameHistory.addAction(joueur.getColoredText(joueur.getName() + " a terminé toutes ses cartes !"));

            // winners.add(joueur); // Add player to winners list

            if (joueurFort.isEmpty()) {
                joueurFort = Optional.of(joueur); // Set the first winner
            }
        }

        // // Check if all players have finished their cards
        // allPlayersFinished = !joueurs.stream().allMatch(Joueur::hasCards); // Set to false if any player has cards left
        // gameHistory.addAction(joueur.getColoredText("allPlayersFinished !"));

        // if (allPlayersFinished == true) {
        //     afficherGagnants();
        // }

        // Check if all players have passed
        if (tousLesJoueursOntPasse()) {
            System.out.println("tout les joueurs on passe fin de pli ");
            finDuPli();
        }
        // joueur.passerTour();

    }

    private void afficherCartesRestantes() {
        System.out.println("Cartes restantes pour chaque joueur :");
        gameHistory.addAction("Cartes restantes pour chaque joueur :");

        int count = 0;

        for (Joueur joueur : joueurs) {
            System.out.println(
                    joueur.getColoredText(joueur.getName() + " : " + joueur.getCards().size() + " cartes restantes."));
            if (joueur.getCards().size() == 0) {
                joueur.passerTour();
                joueur.setpasserTour(true);
                passerTour();
                System.out.println(
                        joueur.getColoredText(
                                joueur.getName() + " a passe tour : " + joueur.aPasseLeTour()));
            }
            count = count + joueur.getCards().size();
            gameHistory.addAction(joueur.getName() + " : " + joueur.getCards().size() + " cartes restantes.");

        }

        if (count == 0) {
            afficherGagnants();
            allPlayersFinished = true;
        }
    }

    private void afficherGagnants() {
        if (winners.isEmpty()) {
            System.out.println("Aucun gagnant n'a été déterminé.");
            gameHistory.addAction("Aucun gagnant n'a été déterminé.");

        } else {
            System.out.println("Gagnants : ");
            // int count = 0;
            for (Joueur winner : winners) {
                System.out.println(winner.getColoredText("- " + winner.getName()));
                // if (count == 0) {
                gameHistory.addAction(winner.getColoredText("- " + winner.getName()));
                gameHistory.addWinner(winner.getName());
                // }

            }

            String basePath = System.getProperty("user.dir");

            GameHistoryManager.saveHistory(gameHistory, "historique_partie.ser");
            GameHistoryManager.showInterface(basePath);
            // gameHistory.displayHistory();

        }
    }

    /**
     * jouerTourJoueur function.
     * 
     * @param joueur joueur concerne
     */   
     private List<Card> jouerTourJoueur(Joueur joueur) {
        if (premierPli) {
            // Handle the first turn
            List<Card> cartes = joueur.getCards();
            Card carteChoisie = null;

            // Check for 3 of clubs or 3 of diamonds
            for (Card carte : cartes) {
                if (carte.getRank() == Rank.THREE &&
                        (carte.getSuit() == Suit.CLUBS || carte.getSuit() == Suit.DIAMONDS)) {
                    carteChoisie = carte;
                    break;
                }
            }

            if (carteChoisie == null) {
                carteChoisie = cartes.stream()
                        .min(Card::compareTo)
                        .orElse(null);
            }

            // Remove the chosen card(s) from the player's hand and return it
            if (carteChoisie != null) {
                cartes.remove(carteChoisie);
                premierPli = false; // End the first round flag
                return List.of(carteChoisie); // Return the played card as a single-item list
            }
        } else {
            if (joueur instanceof HumanPlayer && !premierPli) {
                return gererPliHumain(joueur);
            } else {
                // Decide whether to use basic or advanced engine for the virtual player
                GameEngine engine = (joueur.getName().contains("1")) ? basicGameEngine : advancedGameEngine;
                return (engine.executeTurn(joueur, conditionsDeFin)); // Uses the selected game engine to handle the
                                                                      // turn
            }
        }
        return new ArrayList<>(); // Fallback in case no card is played
    }

    private List<Card> gererPliHumain(Joueur joueur) {
        List<Card> cartesJouees;
        List<List<Card>> combinaisons = trouverCombinaisons(joueur.getCards(),
                conditionsDeFin.getLastPlayedCombinaison());
        joueur.setCombinaisonsPossibles(combinaisons);

        if (joueur.getCardCount() == 0) {

            return cartesJouees = new ArrayList<>();
        } // Set the combinations for the player

        if (!combinaisons.isEmpty()) {
            System.out.println(joueur.getColoredText("Combinaisons disponibles pour " + joueur.getName() + ":"));
            gameHistory.addAction(joueur.getColoredText("Combinaisons disponibles pour " + joueur.getName() + ":"));

            for (int i = 0; i < combinaisons.size(); i++) {
                System.out.println((i + 1) + ": " + combinaisons.get(i));
            }

            int choix = joueur.choisirCombinaison(combinaisons.size());

            if (choix < 0 || choix > combinaisons.size()) {
                System.out.println(joueur.getColoredText("Choix invalide. Veuillez réessayer."));
                return gererPliHumain(joueur); // Allow the player to retry
            }

            // Handle the single card selection
            if (choix == 0) {
                List<Card> mainActuelle = joueur.getCards();
                System.out.println(joueur.getColoredText("Choisissez une carte à jouer depuis votre main :"));
                for (int i = 0; i < mainActuelle.size(); i++) {
                    System.out.println((i + 1) + ": " + mainActuelle.get(i));
                }

                // Get the card index from the player
                int indexCarte = joueur.choisirCarte(mainActuelle.size());
                if (indexCarte < 1 || indexCarte > mainActuelle.size()) {
                    System.out.println("Choix invalide. Veuillez réessayer.");
                    return gererPliHumain(joueur); // Retry if the selection is out of bounds
                }

                cartesJouees = new ArrayList<>();
                cartesJouees.add(mainActuelle.get(indexCarte - 1)); // Adjust index since user input is 1-based
                traiterCombinaison(cartesJouees, joueur);

            } else {
                cartesJouees = new ArrayList<>(combinaisons.get(choix - 1)); // Select combination
                traiterCombinaison(cartesJouees, joueur);

                // joueur.removeCards(cartesJouees); // Remove selected combination from hand
            }

        } else {
            System.out.println(
                    joueur.getColoredText(joueur.getName() + " n'a pas de combinaison valide et plus fort.\n"));
            cartesJouees = joueur.getCards();

            System.out
                    .println(joueur.getColoredText("vous avez " + cartesJouees.size()
                            + " cartes restant, les cartes restons sont \n " + cartesJouees + "\n"));
            gameHistory.addAction(joueur.getName() + "have " + cartesJouees.size()
                    + "cartes restant, les cartes restons sont \n " + cartesJouees + "\n");

            cartesJouees = joueur.jouerCartes();

            return cartesJouees; // Return empty if no combinations

        }
        return cartesJouees;
    }

    private void traiterCombinaison(List<Card> cartesJouees, Joueur joueur) {
        // Vérifier si la combinaison jouée est plus forte que la précédente
        if (!VerificateurCombinaison.estCombinaisonPlusForte(cartesJouees,
                conditionsDeFin.getLastPlayedCombinaison())) {
            System.out.println(joueur.getColoredText(
                    joueur.getName()
                            + " a joué une combinaison moins forte que la précédente.a été ignioré passe tour."));
            gameHistory.addAction(joueur.getColoredText(
                    joueur.getName() + " a joué une combinaison moins forte que la précédente. passe tout."));
            // joueur.passerTour();
            // passerTour();
            return;
        }

        // Mettre à jour la dernière combinaison et le joueur le plus fort
        conditionsDeFin.setLastPlayedCombination(cartesJouees);
        joueurFort = Optional.of(joueur);

        // Retirer les cartes jouées de la main du joueur
        joueur.removeCards(cartesJouees);
        System.out.println(joueur.getColoredText(
                joueur.getName() + " a joué une combinaison plus forte" + cartesJouees));
        gameHistory.addAction(joueur.getColoredText(
                joueur.getName() + " a joué une combinaison plus forte : " + cartesJouees));

        // Vérifier si le joueur n'a plus de cartes
        if (!joueur.hasCards()) {
            System.out.println(joueur.getColoredText(
                    joueur.getName() + " a terminé toutes ses cartes et remporte la manche !"));
            gameHistory.addAction(joueur.getColoredText(
                    joueur.getName() + " a terminé toutes ses cartes et remporte la manche !"));

            // Ajouter le joueur à l'ordre d'élimination
            ordreElimination.add(joueur);

            // Vérifier si tous les joueurs sauf un ont terminé leurs cartes
            if (ordreElimination.size() == joueurs.size() - 1) {
                // Ajouter le dernier joueur à l'ordre d'élimination
                for (Joueur restant : joueurs) {
                    if (!ordreElimination.contains(restant)) {
                        ordreElimination.add(restant);
                    }
                }

                // Afficher l'ordre final
                System.out.println("Ordre d'élimination pour cette manche :");
                for (int i = 0; i < ordreElimination.size(); i++) {
                    System.out.println((i + 1) + "e : " + ordreElimination.get(i).getName());
                }
            }
            finDuPli();
            endGameForPlayer(joueur);
        }

        // joueur.passerTour();
        // passerTour();

    }

    public List<List<Card>> trouverCombinaisons(List<Card> main, List<Card> derniereCombinaison) {
        List<List<Card>> combinaisons = new ArrayList<>();


        // Helper function to add only unique combinations
        List<List<Card>> carres = VerificateurCombinaison.trouverCarres(main); // Four of a Kind (Bomb)
        for (List<Card> comb : carres) {
            if (!combinaisons.contains(comb)){
                combinaisons.add(comb);}
        }
        List<List<Card>> suites = VerificateurCombinaison.trouverSuites(main); // Sequences

        for (List<Card> comb : suites) {
            if (!combinaisons.contains(comb)){
                combinaisons.add(comb);}
        }
        List<List<Card>> brelans = VerificateurCombinaison.trouverBrelans(main); // Triplets

        for (List<Card> comb : brelans) {
            if (!combinaisons.contains(comb)){
                combinaisons.add(comb);}
        }

        List<List<Card>> paires = VerificateurCombinaison.trouverPaires(main); // Pairs
        for (List<Card> comb : paires) {
            if (!combinaisons.contains(comb)){
                combinaisons.add(comb);}
        }
        List<List<Card>> tripletEtDeuxIdentiques = VerificateurCombinaison.trouverTripletEtDeuxIdentiques(main);
        for (List<Card> comb : tripletEtDeuxIdentiques) {
            if (!combinaisons.contains(comb)){
                combinaisons.add(comb);}
        }

        // Filter out any combinations that are not legal or not strong enough
        combinaisons.removeIf(combinaison -> !VerificateurCombinaison.estCombinaisonLegale(combinaison) 
        ||!VerificateurCombinaison.estCombinaisonPlusForte(combinaison, derniereCombinaison));

        return combinaisons;
    }

    /**
     * Initializes player order by finding the player with the lowest card to start.
     */
    public void initialiserOrdre() {
        Joueur joueurAvecCarteLaPlusBasse = getJoueurAvecCarteLaPlusBasse(joueurs);
        if (joueurAvecCarteLaPlusBasse != null) {
            tourActuel = joueurs.indexOf(joueurAvecCarteLaPlusBasse);
        } else {
            System.out
                    .println("Aucun joueur n'a de carte pour commencer. Le premier joueur sera utilisé par défaut.");

            tourActuel = 0;
        }
    }

    /**
     * Start a new round with the player who won the previous round.
     */
    public void startNewPli(Optional<Joueur> joueurFort) {

        for (Joueur joueur : joueurs) {
            joueur.setpasserTour(false);
        }
        if (joueurFort.isPresent()) {
            System.out.println(joueurFort.get().getName() + " commence ce nouveau pli.");
            gameHistory.addAction(joueurFort.get().getName() + " commence ce nouveau pli.");
            // You can add additional logic here if you want to reset specific player states
            // for the new round
        } else {
            System.out.println("Le pli commence avec le premier joueur.");
            gameHistory.addAction("Le pli commence avec le premier joueur.");
            // Set the first player as the starting player
            // tourActuel = 0;
        }
        int count = 0;

        for (Joueur joueur : joueurs) {

            count = count + joueur.getCards().size();
        }
        if (count == 0) {
            winners.add(joueurFort.get());
            afficherGagnants();
        }
        conditionsDeFin.clearLastStrongerPlayedCombination();
        joueurFort = Optional.empty(); // Reset the strongest player
        gererPli();
    }

    private Joueur getJoueurAvecCarteLaPlusBasse(List<Joueur> joueurs) {
        Joueur joueurAvecCarteBasse = null;
        Card carteBasse = null;

        for (Joueur joueur : joueurs) {
            List<Card> cartes = joueur.getCards();

            if (!cartes.isEmpty()) {
                // Check if the player has the 3 of clubs or 3 of diamonds
                for (Card carte : cartes) {
                    if (carte.getRank() == Rank.THREE 
                    &&(carte.getSuit() == Suit.CLUBS || carte.getSuit() == Suit.DIAMONDS)) {
                        System.out.println(
                                "Carte prioritaire trouvée: " + carte + " pour le joueur: " + joueur.getName());
                        return joueur; // Player with 3 of clubs/diamonds starts
                    }
                }

                // Find the lowest card for the current player
                Card carteCouranteBasse = cartes.stream()
                        .min(Card::compareTo) // Assuming Card has a proper compareTo method
                        .orElse(null);

                System.out.println(
                        "Carte la plus basse pour le joueur " + joueur.getName() + " est: " + carteCouranteBasse);
                joueurFort = Optional.empty(); // No strong player initially

                // Update the global lowest card and corresponding player
                if (carteBasse == null ||
                        (carteCouranteBasse != null && carteCouranteBasse.compareTo(carteBasse) < 0)) {
                    carteBasse = carteCouranteBasse;
                    joueurAvecCarteBasse = joueur;
                }
            }
        }

        if (joueurAvecCarteBasse != null) {
            System.out.println("Le joueur avec la carte la plus basse est: " + joueurAvecCarteBasse.getName() +
                    " avec la carte: " + carteBasse);
            joueurFort = Optional.empty(); // No strong player initially

        }

        return joueurAvecCarteBasse; // Return the player with the lowest card if no priority card is found
    }

    public void setCurrentPlayer(Joueur joueur) {
        this.currentPlayer = joueur;
    }

    public Joueur getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Card> getDerniereCombinaisonJouee() {
        return new ArrayList<>(conditionsDeFin.getLastPlayedCombinaison());
    }

    public void setDerniereCombinaisonJouee(List<Card> combinaison) {
        if (combinaison == null) {
            throw new IllegalArgumentException("La combinaison ne peut pas être nulle");
        }
        conditionsDeFin.setLastPlayedCombination(combinaison);
    }

    /**
     * Advances to the next player's turn in the player order.
     */
    public void passerTour() {
        tourActuel = (tourActuel + 1) % joueurs.size();
        while (!joueurs.get(tourActuel).hasCards()) {
            tourActuel = (tourActuel + 1) % joueurs.size();
        }
        if (joueurFort.isPresent() && joueurFort.get().equals(joueurs.get(tourActuel))) {
            joueurFort = Optional.empty();
            conditionsDeFin.clearLastStrongerPlayedCombination();
        }

    }

    /**
     * Checks if all players have passed, indicating the end of a round.
     */
    private boolean tousLesJoueursOntPasse() {

        return joueurs.stream().allMatch(Joueur::aPasseLeTour); // Check if all players have passed

    }

    /**
     * Ends the current round, setting the starting player for the next round.
     */
    private void finDuPli() {

        // If no player has played, keep the current player as the starting player
        if (joueurFort.isEmpty()) {
            System.out.println("Aucun joueur n'a remporté le pli. Le même joueur commence le prochain pli.");
            gameHistory.addAction("Aucun joueur n'a remporté le pli. Le même joueur commence le prochain pli.");
            tourActuel = (tourActuel + 1) % joueurs.size(); // Set the starting player to the player with
                                                            // the strongest
            // Keep the current player as the starting player (tourActuel doesn't change)
        } else {
            System.out.println(joueurFort.get().getName() + " commence le prochain pli.");
            gameHistory.addAction(joueurFort.get().getName() + " commence le prochain pli.");
            tourActuel = joueurs.indexOf(joueurFort.get()); // Set the starting player to the player with the strongest
                                                            // combination
        }

        startNewPli(joueurFort); // Start the next round with the winning player

      

    }

    /**
     * Checks if the game should continue based on defined end conditions.
     * 
     * @return true if the game continues, false if it ends.
     */
    private boolean checkGameStatus() {
        boolean gameContinues = !conditionsDeFin.verifierFinPartie();
        System.out.println("entred checkGameStatus with " + (!gameContinues || allPlayersFinished == true));

        if (!gameContinues) {
            System.out.println("La condition de fin de partie est atteinte.");
            return false; // Continue if game hasn't ended, end otherwise

        }

        return true; // Continue if game hasn't ended, end otherwise
    }

    public void setGameEngines(GameEngine basicGameEngine, GameEngine advancedGameEngine) {
        this.basicGameEngine = basicGameEngine;
        this.advancedGameEngine = advancedGameEngine;
    }

    /**
     * Allows a player to quit the game voluntarily.
     * Updates the game state accordingly.
     *
     * @param joueur The player who wants to quit.
     */
    public void endGameForPlayer(Joueur joueur) {
        if (joueur == null || !joueurs.contains(joueur)) {
            System.out.println("Le joueur spécifié est invalide ou ne fait pas partie de la partie.");
            return;
        }

        System.out.println(joueur.getColoredText(joueur.getName() + " a décidé de quitter la partie."));
        gameHistory.addAction(joueur.getColoredText(joueur.getName() + " a décidé de quitter la partie."));

        // Remove the player from the game
        // joueurs.remove(joueur);

        // Check if this affects the game state
        if (joueurs.isEmpty()) {
            System.out.println("Tous les joueurs ont quitté la partie. Le jeu est terminé.");
            gameHistory.addAction("Tous les joueurs ont quitté la partie. Le jeu est terminé.");
            System.exit(0); // End the game if no players are left
        }

        if (!joueur.hasCards()) {
            // If the quitting player had finished their cards, ensure their win status is
            // maintained
            if (!winners.contains(joueur)) {
                winners.add(joueur);
                System.out.println(joueur.getColoredText(joueur.getName() + " a quitté, mais est considéré gagnant."));
                gameHistory
                        .addAction(joueur.getColoredText(joueur.getName() + " a quitté, mais est considéré gagnant."));
            }
        } else {
            // If the player had cards, redistribute or leave them unplayed
            System.out.println(
                    joueur.getColoredText(joueur.getName() + " quitte avec des cartes en main : " + joueur.getCards()));
            gameHistory.addAction(joueur.getName() + " quitte avec des cartes en main : " + joueur.getCards());
        }

        // Adjust the current player index
        if (currentPlayer == joueur) {
            joueur.passerTour(); // Advance to the next player's turn
            passerTour(); // Mark player as passed

        }

        // Update game conditions
        conditionsDeFin = new ConditionsDeFin(joueurs);
    }

}
