import java.util.Scanner;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that contains helper methods for the Review Lab
 * + a simple dataset analyzer (reads one-review-per-line file and reports
 *   positive/negative counts and an overall take).
 **/
public class Review {

  

  private static HashMap<String, Double> sentiment = new HashMap<String, Double>();
  private static ArrayList<String> posAdjectives = new ArrayList<String>();
  private static ArrayList<String> negAdjectives = new ArrayList<String>();

  static{
    try {
      Scanner input = new Scanner(new File("cleanSentiment.csv"));
      while(input.hasNextLine()){
        String[] temp = input.nextLine().split(",");
        sentiment.put(temp[0],Double.parseDouble(temp[1]));
      }
      input.close();
    }
    catch(Exception e){
      System.out.println("Error reading or parsing cleanSentiment.csv");
    }

    
    try {
      Scanner input = new Scanner(new File("positiveAdjectives.txt"));
      while(input.hasNextLine()){
        posAdjectives.add(input.nextLine().trim());
      }
      input.close();
    }
    catch(Exception e){
      System.out.println("Error reading or parsing postitiveAdjectives.txt\n" + e);
    }

    
    try {
      Scanner input = new Scanner(new File("negativeAdjectives.txt"));
      while(input.hasNextLine()){
        negAdjectives.add(input.nextLine().trim());
      }
      input.close();
    }
    catch(Exception e){
      System.out.println("Error reading or parsing negativeAdjectives.txt");
    }
  }

  
  public static String textToString( String fileName )
  {
    String temp = "";
    try {
      Scanner input = new Scanner(new File(fileName));

      
      while(input.hasNext()){
        temp = temp + input.next() + " ";
      }
      input.close();

    }
    catch(Exception e){
      System.out.println("Unable to locate " + fileName);
    }
    
    return temp.trim();
  }

 
  public static double sentimentVal( String word )
  {
    try
    {
      return sentiment.get(word.toLowerCase());
    }
    catch(Exception e)
    {
      return 0;
    }
  }

  
  public static String getPunctuation( String word )
  {
    String punc = "";
    for(int i=word.length()-1; i >= 0; i--){
      if(!Character.isLetterOrDigit(word.charAt(i))){
        punc = punc + word.charAt(i);
      } else {
        return punc;
      }
    }
    return punc;
  }

  
  public static String removePunctuation( String word )
  {
    while(word.length() > 0 && !Character.isAlphabetic(word.charAt(0)))
    {
      word = word.substring(1);
    }
    while(word.length() > 0 && !Character.isAlphabetic(word.charAt(word.length()-1)))
    {
      word = word.substring(0, word.length()-1);
    }
    return word;
  }

  
  public static String randomPositiveAdj()
  {
    int index = (int)(Math.random() * posAdjectives.size());
    return posAdjectives.get(index);
  }

  
  public static String randomNegativeAdj()
  {
    int index = (int)(Math.random() * negAdjectives.size());
    return negAdjectives.get(index);
  }

  
  public static String randomAdjective()
  {
    boolean positive = Math.random() < .5;
    if(positive){
      return randomPositiveAdj();
    } else {
      return randomNegativeAdj();
    }
  }

  
  public static double totalSentiment(String fileName)
  {
    String reviewText = textToString(fileName);
    double total = 0.0;

    String[] words = reviewText.split(" ");

    for (String word : words)
    {
      String cleanWord = removePunctuation(word);
      total += sentimentVal(cleanWord);
    }
    return total;
  }

  
  public static int starRating(String fileName)
  {
    double total = totalSentiment(fileName);

    if (total < 0) {
      return 1;
    } else if (total < 5) {
      return 2;
    } else if (total < 10) {
      return 3;
    } else if (total < 15) {
      return 4;
    } else {
      return 5;
    }
  }


  public static int analyzeSentiment(String reviewLine) {
    
    String cleaned = reviewLine.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
    String[] tokens = cleaned.split("\\s+"); 

    String[] positive = {
      "good","great","best","amazing","love","fire","10","pretty","super","fast",
      "nice","tender","delicious","awesome","recommend","favorite","staple","memories","go-to","clean"
    };
    String[] negative = {
      "bad","worst","horrible","disgusting","rude","wrong","underwhelming","zero",
      "choking","slow","mess","hate","complain","bone","wait","late","awkward",
      "missing","poorly","cold","soggy","terrible","dirty"
    };

    int score = 0;
    for (String w : positive) if (cleaned.contains(w)) score++;
    for (String w : negative) if (cleaned.contains(w)) score--;

    
    for (String pos : positive) {
      String pattern = "not " + pos;
      if (cleaned.indexOf(pattern) != -1) score -= 2;
    }
    return score;
  }


  public static int[] analyzeReviewLines(String fileName) {
    List<String> lines = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (!line.isEmpty()) lines.add(line);
      }
    } catch (IOException e) {
      System.err.println("Failed to read " + fileName + ": " + e.getMessage());
      return new int[]{0,0,0,0};
    }

    int pos = 0, neg = 0, neu = 0;
    for (String r : lines) {
      int s = analyzeSentiment(r);
      if (s > 0) pos++;
      else if (s < 0) neg++;
      else neu++;
    }

    System.out.println("Lines analyzed: " + lines.size());
    System.out.println("Positive: " + pos);
    System.out.println("Negative: " + neg);
    System.out.println("Neutral : " + neu);
    if (pos > neg) {
      System.out.println("Overall (per-line): People generally like Taco Bell.");
    } else if (neg > pos) {
      System.out.println("Overall (per-line): People generally do not like Taco Bell.");
    } else {
      System.out.println("Overall (per-line): Opinions are mixed.");
    }
    return new int[]{lines.size(), pos, neg, neu};
  }


  public static void main(String[] args) {
    String reviewsFile = (args.length > 0) ? args[0] : "reviews.txt";
    
    

    System.out.println("== Taco Bell Review Dataset Analysis ==");
    analyzeReviewLines(reviewsFile);

    
    int stars = starRating(reviewsFile);
    System.out.println("Lexicon star rating for entire file: " + stars + " star(s)");
  }
}
