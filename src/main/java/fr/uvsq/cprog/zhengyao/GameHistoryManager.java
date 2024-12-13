package fr.uvsq.cprog.zhengyao;

import java.io.*;
import java.util.*;

import org.fusesource.jansi.Ansi;

public class GameHistoryManager {

    // Save game history to a file with a unique identifier
    public static void saveHistory(GameHistory gameHistory, String baseFileName) {
        String uniqueFileName = baseFileName.replace(".ser", "_" + System.currentTimeMillis() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(uniqueFileName))) {
            oos.writeObject(gameHistory);
            System.out.println("Game history saved to " + uniqueFileName);
        } catch (IOException e) {
            System.out.println("Failed to save game history: " + e.getMessage());
        }
    }

    public static List<GameHistory> loadAllHistories(String directoryPath) {
        List<GameHistory> histories = new ArrayList<>();
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Invalid directory path: " + directoryPath);
            return histories;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".ser"));
        if (files != null) {
            for (File file : files) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    GameHistory history = (GameHistory) ois.readObject();
                    histories.add(history);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Failed to load history from file: " + file.getName());
                }
            }
        }
        return histories;
    }

    // Display histories and wins
    public static void displayHistoriesAndWins(List<GameHistory> histories) {
        Map<String, Integer> winsCount = new HashMap<>();

        System.out.println("======== ALL GAME HISTORIES ========");
        for (int i = 0; i < histories.size(); i++) {
            System.out.println("Partie " + (i + 1) + ":");
            GameHistory history = histories.get(i);
            history.displayHistory();

            // Count winners for this history
            for (String winner : history.getWinners()) {
                winsCount.put(winner, winsCount.getOrDefault(winner, 0) + 1);
            }
        }

        // Display win counts
        System.out.println("\n======== WIN COUNTS ========");
        if (winsCount.isEmpty()) {
            System.out.println("No winners recorded.");
        } else {
            winsCount.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " wins"));
        }
    }

    // Interactive interface
    public static void showInterface(String directoryPath) {
        List<GameHistory> histories = loadAllHistories(directoryPath);
        if (histories.isEmpty()) {
            System.out.println("No game histories found.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n======= GAME HISTORY INTERFACE =======");
            System.out.println("1. View all game histories");
            System.out.println("2. View total wins per player");
            System.out.println("3. View specific game history");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    displayHistoriesAndWins(histories);
                    break;
                case 2:
                    displayWinCounts(histories);
                    break;
                case 3:
                    // Show list of available histories to the user
                    System.out.println("Select a game history to view:");
                    for (int i = 0; i < histories.size(); i++) {
                        System.out.println((i + 1) + ". Game " + (i + 1)); // Display index as a game number
                    }
                    System.out.print("Enter the number of the game you want to view: ");

                    int gameChoice;
                    try {
                        gameChoice = Integer.parseInt(scanner.nextLine()) - 1; // Subtract 1 for zero-indexed list
                        if (gameChoice < 0 || gameChoice >= histories.size()) {
                            System.out.println("Invalid selection. Please choose a valid game number.");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue;
                    }

                    // Display the selected game's history
                    GameHistory selectedHistory = histories.get(gameChoice);
                    selectedHistory.displayHistory();
                    break;
                case 4:
                    System.out.println(Ansi.ansi()
                            .fgBrightGreen()
                            .bold()
                            .a("SEE YOU NEXT TIME, PLEASE COME BACK AS SOON AS POSSIBLE :) ")
                            .reset());
                    System.exit(0); // Terminates the entire application

                    return;
                default:
                    System.out.println("Invalid choice. Please choose again.");
            }
            // scanner.close();

        }
    }

    // Helper method to display only win counts
    private static void displayWinCounts(List<GameHistory> histories) {
        Map<String, Integer> winsCount = new HashMap<>();
        for (GameHistory history : histories) {
            for (String winner : history.getWinners()) {
                winsCount.put(winner, winsCount.getOrDefault(winner, 0) + 1);
            }
        }

        System.out.println("\n======== WIN COUNTS ========");
        if (winsCount.isEmpty()) {
            System.out.println("No winners recorded.");
        } else {
            winsCount.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " wins"));
        }
    }
}