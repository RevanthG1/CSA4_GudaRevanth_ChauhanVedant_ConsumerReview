import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class Review {

  
  private static HashMap<String, Double> sentiment = new HashMap<>();
  private static ArrayList<String> posAdjectives = new ArrayList<>();
  private static ArrayList<String> negAdjectives = new ArrayList<>();

  
  static {
    
    try (Scanner input = new Scanner(new File("cleanSentiment.csv"))) {
      while (input.hasNextLine()) {
        String[] temp = input.nextLine().split(",");
        if (temp.length >= 2) {
          sentiment.put(temp[0], Double.parseDouble(temp[1]));
        }
      }
    } catch (Exception e) {
      System.out.println("Error reading or parsing cleanSentiment.csv");
    }

    
    try (Scanner input = new Scanner(new File("positiveAdjectives.txt"))) {
      while (input.hasNextLine()) posAdjectives.add(input.nextLine().trim());
    } catch (Exception e) {
      System.out.println("Error reading or parsing postitiveAdjectives.txt\n" + e);
    }
    try (Scanner input = new Scanner(new File("negativeAdjectives.txt"))) {
      while (input.hasNextLine()) negAdjectives.add(input.nextLine().trim());
    } catch (Exception e) {
      System.out.println("Error reading or parsing negativeAdjectives.txt");
    }
  }

  
  public static double sentimentVal(String word) {
    try { return sentiment.get(word.toLowerCase()); }
    catch (Exception e) { return 0; }
  }

  public static String removePunctuation(String word) {
    while (word.length() > 0 && !Character.isAlphabetic(word.charAt(0))) {
      word = word.substring(1);
    }
    while (word.length() > 0 && !Character.isAlphabetic(word.charAt(word.length() - 1))) {
      word = word.substring(0, word.length() - 1);
    }
    return word;
  }

  
  public static List<String> readLines(String fileName) {
    List<String> lines = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (!line.trim().isEmpty()) lines.add(line.trim());
      }
    } catch (IOException e) {
      System.err.println("Failed to read " + fileName + ": " + e.getMessage());
    }
    return lines;
  }

  
  public static int starRatingFromTotal(double total) {
    if (total < 0) return 1;
    else if (total < 5) return 2;
    else if (total < 10) return 3;
    else if (total < 15) return 4;
    else return 5;
  }

  
  public static String starsAsAsterisks(int stars) {
    if (stars < 1) stars = 1;
    if (stars > 5) stars = 5;
    return "*".repeat(stars);
  }

  
  public static void analyzeOneReview(String text) {
    System.out.println("=== Sentiment Analysis Demo ===\n");
    System.out.println("Analyzing: " + text + "\n");

   
    double total = 0.0;
    String[] tokens = text.split("\\s+");
    for (String tok : tokens) {
      String base = removePunctuation(tok).toLowerCase();
      if (!base.isEmpty()) total += sentimentVal(base);
    }

    int stars = starRatingFromTotal(total);
    System.out.printf("Total Sentiment Value: %.16f%n", total); 
    System.out.println("Star Rating: " + starsAsAsterisks(stars));
    System.out.println();

    
    System.out.println("==== Word-by-Word Breakdown ====\n");
    for (String tok : tokens) {
      String base = removePunctuation(tok).toLowerCase();
      if (base.isEmpty()) continue;
      double val = sentimentVal(base);
      System.out.printf("%s: % .4f%n", base, val);
    }
  }

  
  public static void main(String[] args) {
    List<String> lines = readLines("reviews.txt");
    if (lines.isEmpty()) {
      System.out.println("No lines found in reviews.txt (or file missing).");
      return;
    }

    int lineNum;
    if (args.length > 0) {
      
      try {
        lineNum = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        System.out.println("First argument must be a line number. Example: java Review 23");
        return;
      }
    } else {
      
      Scanner sc = new Scanner(System.in);
      System.out.println("reviews.txt has " + lines.size() + " non-empty lines.");
      System.out.print("Enter a line number to analyze (1-" + lines.size() + "): ");
      String s = sc.nextLine().trim();
      try {
        lineNum = Integer.parseInt(s);
      } catch (NumberFormatException e) {
        System.out.println("Not a number. Exiting.");
        return;
      }
    }

    if (lineNum < 1 || lineNum > lines.size()) {
      System.out.println("Line number out of range. Must be between 1 and " + lines.size() + ".");
      return;
    }

    String chosen = lines.get(lineNum - 1);
    analyzeOneReview(chosen);
  }
}
