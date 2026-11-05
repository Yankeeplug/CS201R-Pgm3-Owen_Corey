import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

//NAME: Owen Corey
//ASSIGNMENT: 3
//LAB SECTION: 6
//LECTURE SECTION: 7

public class Main{

    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<SentList> sentList = new ArrayList<SentList>();
        ArrayList<SentList> posList = new ArrayList<SentList>();
        ArrayList<SentList> negList = new ArrayList<SentList>();
        ArrayList<Words> wordList = new ArrayList<Words>();

        //load sentiment, positive words and negative words arraylists
        readSentimentList(sentList, posList, negList);

        //read reviews
        //load ArrayList wordList that will contain original review & pos & neg
        String inFileName;
        String outFileName = "reviews.txt";
        PrintWriter outFile = new PrintWriter(outFileName);

        // open input file adding review + number + ".txt" to review
        // if not able to open, print a message and continue
        // else process the file
        // if the file can be read properly, print the results
        for (int i = 1; i <= 8; i++)
        {
            inFileName = "review" + i + ".txt";

            try
            {
                FileReader inFile = new FileReader(inFileName);

                outFile.println("PROCESSING FILE: " + inFileName + "\n");
                readReview(wordList, sentList, posList, negList, inFileName);
                printReview(wordList, sentList, inFileName, outFile);
                printEditedReview(wordList, sentList, inFileName, outFile);
                outFile.println("\n\n");
            }
            
            catch (Exception e) 
            {
                System.out.println("No file found!");
            }
        }

        outFile.close();
    }

    //PRE:  accept the empty ArrayLists created in main
    //POST: the arrays are loaded with the proper words and information
    public static void readSentimentList(ArrayList<SentList> sentList,
                                         ArrayList<SentList> posList,
                                         ArrayList<SentList> negList){
        String csvFilePath = "sentiment.txt";
        String line;
        double value;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            while ((line = br.readLine()) != null) {
                // Split the line by commas into an array of strings
                String[] trimmedLines = line.trim().split(",");
                line = trimmedLines[0];
                value = Double.parseDouble(trimmedLines[1]);
                 
                //Create object SentList & add to sentList arraylist
                SentList listToAdd = new SentList(line, value);
                sentList.add(listToAdd);

                //if word values are pos add to posList
                //if word values are neg, add to neglist
                if (listToAdd.value >= 0) 
                {
                    posList.add(listToAdd); 
                }

                else if (listToAdd.value < 0)
                {
                    negList.add(listToAdd);
                }
            }

        }

        catch (IOException e) {
            System.out.println("Error reading the file.");
            e.printStackTrace();
        }
    }

    //PRE: accept the word lists and file name to open
    //POST: read the file while the line is not null
    //      each word is edited (to lower case without punctuation)
    //      the sentiment value is accessed
    //      if the word is positive - update to a random word in the negative list & update the word value
    //      if the word is negative - update to a random word in the positive list & update the word value

    public static void readReview(ArrayList<Words> wordList,
                                  ArrayList<SentList> sentList,
                                  ArrayList<SentList> posList,
                                  ArrayList<SentList> negList,
                                  String inFile) {

        
        try (BufferedReader br = new BufferedReader(new FileReader(inFile))) 
        {
            String line;
            while ((line = br.readLine()) != null) 
                {
                // Split the line by spaces into an array of strings
                String[] values = line.split(" ");
                 
                //for each word
                for (String word : values) 
                {
                    //  strip off punctuation at the end of word set charWord to null or value of punctuation
                    String originalWord = word.replaceAll("[\\p{Punct}]", "");
                    
                    //  save origWord, posWord, negWord
                    String posWord = "";
                    String negWord = "";
                    
                    //  change to lower case  (set editWord)
                    String editWord = originalWord.toLowerCase();

                    //  look up word in sentList (set origValue, posValue, negValue)
                    double tempValue = getSentiment(sentList, editWord);
                    double posValue = 0;
                    double negValue = 0;

                    //  if positive enough, find new negword in negList & reset negWord & NegValue, reset negFlag
                    if (tempValue >= 1.25)
                    {
                        int negIndex = (int)(Math.random() * negList.size());
                        negWord = negList.get(negIndex).word;
                        negValue = getSentiment(sentList, negWord);
                    }

                    //  if negative enough, find new posword in posList & reset posWord & posValue, reset posFlag
                    else if (tempValue <= -1.25)
                    {
                        int posIndex = (int)(Math.random() * posList.size());
                        posWord = posList.get(posIndex).word;
                        posValue = getSentiment(sentList, posWord);
                    }

                    Words wordToAdd = new Words(originalWord, editWord, posWord, negWord, tempValue, posValue, negValue);

                    wordList.add(wordToAdd);
                }
            }

            sortWordList(wordList);

            br.close();
        }

        catch (IOException e) {
            System.out.println("Error reading the file: " + inFile);
        }
    }

    //PRE:  accept the updated wordlist
    //POST: loop through word list, create a string that will be the original, positive & negative reviews
    //      print each review
    public static void printReview(ArrayList<Words> wordList, ArrayList<SentList> sentList, String inFile, 
                                  PrintWriter outFile)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(inFile))) 
        {
            String line;
            double sentiment = 0;
            int charCount = 0;

            while ((line = br.readLine()) != null) 
            {
                String[] words = line.split(" ");

                for (String word : words) 
                {
                    double wordValue = getSentiment(sentList, word);

                    sentiment += wordValue;
                    charCount += word.length() + 1;
                    
                    if (charCount > 80)
                    {
                        charCount = 0;
                        outFile.println();
                    }

                    outFile.print(word + " ");
                }
            }
            
            String formattedSentiment = String.format("%.2f", sentiment);

            outFile.println("\n\nFINAL SENTIMENT: " + formattedSentiment + "\n");
        }

        catch (IOException e) 
        {
            System.out.println("Error reading the file: " + inFile);
        }
    }

    //PRE:  accept the updated wordlist
    //POST: loop through word list, create a string that will be the original, positive & negative reviews
    //      print each review
    public static void printEditedReview(ArrayList<Words> wordList, ArrayList<SentList> sentList, String inFile, 
                                  PrintWriter outFile)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(inFile))) 
        {
            String line;
            double sentiment = 0;
            int charCount = 0;
            ArrayList<String> editedWords = new ArrayList<String>();

            //While there is a line to read:
            while ((line = br.readLine()) != null) 
            {
                //Split the line into separate words and add to words array.
                String[] words = line.split(" ");

                //For each word in words.
                for (String word : words) 
                {
                    boolean capitalized = false;

                    //Gets the last character in the string.
                    char punctuation = word.charAt(word.length() - 1);

                    //Identifies if the first character is capitalized.
                    if (isCaps(word.charAt(0)))
                    {
                        capitalized = true;
                    }

                    String tempWord = word.replaceAll("[\\p{Punct}]", "");
                    Words foundWord = getWord(wordList, tempWord);
                    String wordStr = tempWord;
                    double value = 0;
                    double tempValue = 0;

                    //If the word exists in the word list:
                    if (!(foundWord == null))
                    {
                        wordStr = foundWord.origWord;
                        value = foundWord.sentOrigValue;
                        tempValue = foundWord.sentOrigValue;
                    }

                    //Prints a new line once a string has surpassed 80 characters.
                    if (charCount > 80)
                    {
                        charCount = 0;
                        outFile.println();
                    }

                    //  if positive enough, find new negword in negList & reset negWord & NegValue, reset negFlag
                    if (value >= 1.25)
                    {
                        wordStr = foundWord.negWord;    
                        value = foundWord.sentNegValue;
                        editedWords.add(String.format("%-25s%-25s", tempWord + " --- " + tempValue, wordStr + " --- " + value));
                    }

                    //  if negative enough, find new posword in posList & reset posWord & posValue, reset posFlag
                    else if (value <= -1.25)
                    {
                        wordStr = foundWord.posWord;
                        value = foundWord.sentPosValue;
                        editedWords.add(String.format("%-25s%-25s", tempWord + " --- " + tempValue, wordStr + " --- " + value));
                    }

                    //If the first letter was capitalized, make the replacement capitalized.
                    if (capitalized)
                    {
                        wordStr = wordStr.substring(0, 1).toUpperCase() + wordStr.substring(1);
                    }

                    //If string had punctuation, add it to the replacement.
                    if (isPunctuation(punctuation))
                    {
                        wordStr = wordStr + punctuation;
                    }

                    outFile.print(wordStr + " ");
                    
                    sentiment += value;
                    charCount += word.length() + 1;
                }
            }
            
            String formattedSentiment = String.format("%.2f", sentiment);

            outFile.println("\n\nREPLACED WORDS:");

            //Prints all words that were changed and what they were changed into.
            for (String word : editedWords) 
            {
                outFile.println(word);
            }

            outFile.println("\nFINAL SENTIMENT: " + formattedSentiment);
        }

        catch (IOException e) 
        {
            System.out.println("Error reading the file: " + inFile);
        }
    }

    //PRE:  accept a character
    //POST: return true if this character is punctuation; false otherwise
    //      There may be a better way to write this!
    static boolean isPunctuation (char ch) {
        if (ch == '!' || ch == '\"' || ch == '#' || ch == '$' || ch == '%' || ch == '&' || ch == '\'' || ch == '(' || ch == ')' || ch == '*' || ch == '+' || ch == ',' || ch == '-' || ch == '.' || ch == '/' || ch == ':' || ch == ';' || ch == '<' || ch == '=' || ch == '>' || ch == '?' || ch == '@' || ch == '[' || ch == '\\' || ch == ']' || ch == '^' || ch == '`' || ch == '{' || ch == '|' || ch == '}')
          return true;
        return false;
    }

    //PRE: accepts a character
    //POST: return true if the first letter is upper case; false otherwise
    static boolean isCaps (char ch) {
        boolean bool = false;

        if (Character.isUpperCase(ch))
        {
            bool = true;
        }

        return bool;
    }


    //PRE:  accepts the sentList and a word to find.
    //POST: returns the index if word was found; -1 otherwise    
    
    static int sentSearch(ArrayList<SentList> sentList, String word)
    {
        int start = 0;
        int end = sentList.size() - 1;
        int index = -1;

        while (start <= end)
        {
            int middle = (start + end) / 2;
            
            //Compares the middle string and the word to find.
            int compare = sentList.get(middle).word.compareTo(word.toLowerCase());

            if (compare == 0)
            {
                index = middle;
                return index;
            }

            else if (compare > 0)
            {
                end = middle - 1;
            }

            else
            {
                start = middle + 1;
            }
        }

        return index;
    }

    //PRE:  accept the sentiment words list and a word to find
    //POST: return the value of the sentiment if found, 0 otherwise
    static double getSentiment (ArrayList<SentList> sentList, String eWord) 
    {
        int index = sentSearch(sentList, eWord);

        if (index == -1)
        {
            return 0.0;
        }
        
        return sentList.get(index).value;
    }


    //PRE:  accepts the wordList and a word to find.
    //POST: returns the index if word was found; -1 otherwise

    static int wordSearch(ArrayList<Words> wordList, String word)
    {
        int start = 0;
        int end = wordList.size() - 1;
        int index = -1;

        while (start <= end)
        {
            int middle = (start + end) / 2;
            //Compares the middle string and the word to find.
            int compare = wordList.get(middle).getEditWord().compareTo(word.toLowerCase());

            if (compare == 0)
            {
                index = middle;
                return index;
            }

            else if (compare > 0)
            {
                end = middle - 1;
            }

            else
            {
                start = middle + 1;
            }
        }

        return index;
    }

    //PRE:  accept the words list and a word to find
    //POST: return the word if found, null otherwise
    static Words getWord (ArrayList<Words> wordList, String eWord) 
    {
        int index = wordSearch(wordList, eWord);
        
        if (index == -1)
        {
            return null;
        }

        return wordList.get(index);
    }

    static void sortWordList(ArrayList<Words> wordList)
    {
        Collections.sort(wordList, new Comparator<Words>() 
        {
            @Override public int compare(Words w1, Words w2) 
            {
                return w1.getEditWord().compareTo(w2.getEditWord());
            }
        });
    }
}