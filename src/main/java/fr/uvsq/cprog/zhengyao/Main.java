package fr.uvsq.cprog.zhengyao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;


import fr.uvsq.cprog.zhengyao.controller.ControleurDeJeu;

public enum Main {
    APPLICATION;

    private final List<Joueur> players = new ArrayList<>();
    private GameEngine basicGameEngine; // Declare but do not initialize yet
    private GameEngine advancedGameEngine; // Declare but do not initialize yet

    public void run(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize the AnsiConsole
        AnsiConsole.systemInstall();

        ControleurDeJeu controleurDeJeu = new ControleurDeJeu();

        // Colors for players
        List<String> colors = Arrays.asList("blue", "green", "red", "magenta", "black");
        int colorIndex = 0;

        // Get human player details
        System.out.print(ansi().fgBrightBlue().a("Entrez votre nom: ").reset());
        String playerName = scanner.nextLine();
        Joueur humanPlayer = new HumanPlayer(playerName, new ArrayList<>(), controleurDeJeu); // Initialize with an
                                                                                              // empty hand
        humanPlayer.setColor(colors.get(colorIndex++ % colors.size())); // Assign color using setColor
        players.add(humanPlayer);

        // Get the number of virtual players
        System.out.print(ansi().fgBrightBlue().a("Entrez le nombre de joueurs virtuels (1-3): ").reset());
        int numVirtualPlayers = scanner.nextInt();


        // Create virtual players and assign colors
        for (int i = 0; i < numVirtualPlayers; i++) {
            GameEngine engine = (i % 2 == 0) ? basicGameEngine : advancedGameEngine;
            Joueur virtualPlayer = new VirtualPlayer("VirtualPlayer" + (i + 1), new ArrayList<>(), engine,
                    controleurDeJeu);
            virtualPlayer.setColor(colors.get(colorIndex++ % colors.size())); // Assign color using setColor
            players.add(virtualPlayer);
        }

        // Distribute cards
        distributeCards(); // Distribute cards after all players have been added
        controleurDeJeu.setJoueurs(players);

        // Initialize game engines
        basicGameEngine = new SimpleGameEngine(controleurDeJeu);
        advancedGameEngine = new StrategicGameEngine(controleurDeJeu);
        controleurDeJeu.setGameEngines(basicGameEngine, advancedGameEngine);

        // Check if there are players before starting the game
        if (players.isEmpty()) {
            System.out.println(ansi().fgRed().a("Aucun joueur n'a été ajouté. Le jeu ne peut pas commencer.").reset());
            return;
        }

        // Start the game
        controleurDeJeu.demarrerJeu();

        // Display actions and scores in colors
        for (Joueur player : players) {
            System.out.println(ansi()
                    .fg(Ansi.Color.valueOf(player.getColor().toUpperCase())) // Use getColor to retrieve the player's
                                                                             // color
                    .a(player.getName() + ": Action ou Score Affiché ici.")
                    .reset());
        }

        System.out.println(ansi().fgBrightBlue().a("La partie est terminée !").reset());
        displayScores();

        AnsiConsole.systemUninstall();
    }

    private void distributeCards() {
        Deck deck = new DeckFactory().createDeck();
        deck.shuffle();

        // Ensure there are players before distributing cards
        if (players.isEmpty()) {
            System.out.println("Aucun joueur pour distribuer des cartes.");
            return; // Return if no players exist
        }

        int cardsPerPlayer = 52 / players.size();
        for (Joueur player : players) {
            List<Card> hand = new ArrayList<>();
            for (int i = 0; i < cardsPerPlayer; i++) {
                if (!deck.isEmpty()) {
                    hand.add(deck.drawCard());
                }
            }
            player.setCards(hand); // Assume setHand method exists in Joueur class
        }
    }

    private void displayScores() {
        System.out.println("Scores finaux:");
        for (Joueur player : players) {
            System.out.println(player.getName() + ": " + player.getScore());
        }
    }

    public static void main(String[] args) {
        APPLICATION.run(args);
    }
}
