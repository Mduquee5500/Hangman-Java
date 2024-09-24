// Student: Mateo Duque Escobar
// Student ID: 300352135 
// File Name: HangManGame.java
// Description: Hangman Game that lets the user guess for the letters of the hidden word selected from a file called englishWords.txt. 
//The program hanfles incorrect or invalid input and also counts the incorrect guesses while showing them in alphabetic order. 
//The program lets the user exit the game without saving or save his score with his name in a file called topPlayers.txt, after ending the game, the program will prompt the top players in the file.

import java.io.*;
import java.util.*;

public class HangManGame {
    public void fileReader(String fileName) throws Exception {
        File words = new File(fileName);
        Scanner sc = new Scanner(words);

        ArrayList<String> wordsArray = new ArrayList<>(); // Words from file
        ArrayList<Character> correctArray = new ArrayList<>(); // Correct guesses made by user
        ArrayList<Character> wrongArray = new ArrayList<>(); // Incorrect guesses made by user
        ArrayList<Players> topPlayers = new ArrayList<>(); // Saved players

        while (sc.hasNext()) {
            wordsArray.add(sc.nextLine());
        }

        int randomNum = (int) (Math.random() * wordsArray.size());
        String selectedWord = wordsArray.get(randomNum);
        char[] selectedWordInCharacters = selectedWord.toCharArray();
        String hiddenWordLines = "";
        int guessesLeft = 7;
        int winStreak = 0;
        boolean isWrong = false;

        for (int i = 0; i < selectedWord.length(); i++) {
            hiddenWordLines += ("_ ");
        }
        System.out.println("Hidden Word: " + hiddenWordLines);

        while (true) {
            Scanner inputGuess = new Scanner(System.in);
            System.out.print("Enter guess: ");
            try {
                String guess = inputGuess.nextLine();
                if (guess.length() != 1 || !Character.isLetter(guess.charAt(0))) { // Checks if the input is valid
                    throw new IllegalArgumentException("\nPlease enter a valid input");
                }
                System.out.println();
                char guessToChar = guess.charAt(0);
        
                if (selectedWord.contains(guess)) {
                    int count = 0;
                    for (int i = 0; i < selectedWord.length(); i++) {
                        if (selectedWordInCharacters[i] == guessToChar && !correctArray.contains(guessToChar)) {
                            hiddenWordLines = hiddenWordLines.substring(0, 2 * i) + guessToChar + hiddenWordLines.substring(2 * i + 1);
                            count++;
                        }
                    }
                    if (count > 0) {
                        winStreak += count * 10;
                        correctArray.add(guessToChar); // Add the letter to correctArray after counting it
                    }
                } else {
                    if (!wrongArray.contains(guessToChar)) {
                        wrongArray.add(guessToChar);
                        Collections.sort(wrongArray); // Sort incorrect letters
                        guessesLeft--;
                        isWrong = true;
                    }
                }
        
                String itemsInsideWrongArray = "";
                for (int i = 0; i < wrongArray.size(); i++) {
                    itemsInsideWrongArray += wrongArray.get(i) + ", ";
                }
        
                if (guessesLeft == 0) {
                    System.out.println("YOU LOSE!");
                    System.out.println("The word was: " + selectedWord);
                    while (true) {
                        System.out.println("Select one option: \n1. Save score \n2. Exit without saving");
                        Scanner inputEndSelection = new Scanner(System.in);
                        try {
                            int endSelection = inputEndSelection.nextInt();
                            if (endSelection == 1 || endSelection == 2) {
                                if (endSelection == 1) {
                                    System.out.print("Enter your name: ");
                                    Scanner inputName = new Scanner(System.in);
                                    String name = inputName.nextLine();
                                    Players player = new Players(name, winStreak);
                                    topPlayers.add(player);
                                    
                                    // Read existing content from the file
                                    ArrayList<Players> filePlayers = new ArrayList<>();
                                    try (Scanner fileScanner = new Scanner(new File("topPlayers.txt"))) {
                                        while (fileScanner.hasNextLine()) {
                                            String line = fileScanner.nextLine();
                                            String[] parts = line.split(" - ");
                                            String playerName = parts[0];
                                            int playerScore = Integer.parseInt(parts[1]);
                                            Players filePlayer = new Players(playerName, playerScore);
                                            filePlayers.add(filePlayer);
                                        }
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    
                                    // Add new players to the list
                                    for (Players filePlayer : filePlayers) {
                                        topPlayers.add(filePlayer);
                                    }
                                    
                                    Collections.sort(topPlayers, new PlayerComparator()); // Sort the list of players by score
                                    
                                    // Write the sorted list to the file
                                    try (PrintWriter pw = new PrintWriter(new FileWriter("topPlayers.txt"))) {
                                        for (Players p : topPlayers) {
                                            pw.println(p.getName() + " - " + p.getScore());
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    
                                    try (Scanner fileScanner = new Scanner(new File("topPlayers.txt"))) {
                                        System.out.println("\nTop Players:");
                                        ArrayList<String> topPlayersLines = new ArrayList<>();
                                        while (fileScanner.hasNextLine()) {
                                            topPlayersLines.add(fileScanner.nextLine());
                                        }
                                        
                                        // Prints the first 5 players on the list
                                        int count = 0;
                                        for (String playerLine : topPlayersLines) {
                                            System.out.println(playerLine);
                                            count++;
                                            if (count == 5) {
                                                break;
                                            }
                                        }
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    
                                    System.exit(0); // Exits the program after saving
                                } else {
                                    System.out.println("\nThanks for playing :)");
                                    System.exit(0); // Exits the program without saving
                                }
                            } else {
                                throw new IllegalArgumentException("\nPlease enter a valid option");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("\nPlease enter a valid option");
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                } else if (!hiddenWordLines.contains("_")) {
                    winStreak += 100;
                    if (guessesLeft <= 7) {
                        winStreak += guessesLeft * 30;
                    }
                }
        
                System.out.println("WRITE THIS WORD: " + selectedWord); // This line is just for verification since it shows the hidden word to guess
                System.out.println("Hidden Word: " + hiddenWordLines); // Prints the guessing lines for each letter in the word
        
                if (!wrongArray.isEmpty()) {
                    System.out.println("Wrong guesses: " + itemsInsideWrongArray.substring(0, itemsInsideWrongArray.length() - 2));
                }
        
                System.out.println("Guesses left: " + guessesLeft);
                System.out.println("Score: " + winStreak);
        
                if (isWrong == true) {
                    System.out.println("Sorry, there were no " + guessToChar + "'s");
                    isWrong = false;
                }
        
                if (!hiddenWordLines.contains("_")) { // Selects a new word
                    System.out.println("Correct! The word was: " + selectedWord);
                    System.out.println();
                    randomNum = (int) (Math.random() * wordsArray.size());
                    selectedWord = wordsArray.get(randomNum);
                    selectedWordInCharacters = selectedWord.toCharArray();
                    hiddenWordLines = "";
                    correctArray.clear();
                    wrongArray.clear();
        
                    for (int i = 0; i < selectedWord.length(); i++) {
                        hiddenWordLines += ("_ ");
                    }
                    System.out.println("Hidden Word: " + hiddenWordLines);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        HangManGame app = new HangManGame();
        app.fileReader("englishWords.txt");
    }

    static class PlayerComparator implements Comparator<Players> {
        @Override
        public int compare(Players player1, Players player2) {
            return Integer.compare(player2.getScore(), player1.getScore());  // Sort in descending order (from highest to lowest) according to the score
        }
    }
}
