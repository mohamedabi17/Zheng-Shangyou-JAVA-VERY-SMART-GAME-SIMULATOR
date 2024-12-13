package fr.uvsq.cprog.zhengyao;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * Gère l'historique des actions d'une partie.
 */
public class GameHistory implements Serializable {
    private final List<String> actions;
    private List<String> winners;
    private static final long serialVersionUID = 1L; // Ensure compatibility during serialization

    public GameHistory() {
        this.actions = new ArrayList<>();
        this.winners = new ArrayList<>();

    }

    public void addAction(String action) {
        actions.add(action);
    }

    public List<String> getActions() {
        return new ArrayList<>(actions);
    }

    /**
     * Ajoute un gagnant à l'historique.
     *
     * @param winner Le nom du gagnant.
     */
    public void addWinner(String winner) {
        winners.add(winner);
    }

    /**
     * Retourne une copie des gagnants enregistrés.
     *
     * @return Liste des gagnants.
     */
    public List<String> getWinners() {
        return new ArrayList<>(winners);
    }

    public void displayHistory() {
        // Initialize Jansi
        AnsiConsole.systemInstall();

        // Get terminal size (example dimensions; adjust for your environment)
        int terminalWidth = 80;

        // Create a bold and decorative header
        String header = "======================= HISTORIQUE DE LA PARTIE ==========================";
        int headerPadding = Math.max((terminalWidth - header.length()) / 2, 0);
        String centeredHeader = " ".repeat(headerPadding) + header;

        // Print a visually striking header
        System.out.println(Ansi.ansi()
                .eraseScreen()
                .fgBrightYellow()
                .bold()
                .a("\n" + "=".repeat(terminalWidth)) // Top border
                .reset());

        System.out.println(Ansi.ansi()
                .fgBrightYellow()
                .bold()
                .a(centeredHeader)
                .reset());

        System.out.println(Ansi.ansi()
                .fgBrightYellow()
                .bold()
                .a("=".repeat(terminalWidth)) // Bottom border
                .reset());

        // Print each action in bold, centered
        for (String action : actions) {
            int actionPadding = Math.max((terminalWidth - action.length()) / 2, 0);
            String centeredAction = " ".repeat(actionPadding) + action;

            System.out.println(Ansi.ansi()
                    .fgBrightGreen()
                    .bold()
                    .a(centeredAction)
                    .reset());
        }

        // Print winners
        if (!winners.isEmpty()) {
            System.out.println(Ansi.ansi()
                    .fgBrightBlue()
                    .bold()
                    .a("\nGagnants:")
                    .reset());
            for (String winner : winners) {
                System.out.println(Ansi.ansi()
                        .fgBrightMagenta()
                        .bold()
                        .a(" - " + winner)
                        .reset());
            }
        }

        // Print bottom spacing and cleanup
        System.out.println(Ansi.ansi()
                .fgBrightYellow()
                .bold()
                .a("\n" + "=".repeat(terminalWidth)) // Closing border
                .reset());

        // Cleanup Jansi
        AnsiConsole.systemUninstall();
    }

}
