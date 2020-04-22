import java.util.ArrayList;
import java.util.Random;

public class Bot0 implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects

    private PlayerAPI me;
    private OpponentAPI opponent;
    private BoardAPI board;
    private UserInterfaceAPI info;
    private DictionaryAPI dictionary;
    private int turnCount;
    private boolean firstMove = true;
    private boolean firstWord = true;
    private int turnCounter = 0;
    private boolean connectingWords = false;

    private ArrayList<Integer> word_score = new ArrayList<>(); //Stores corresponding score for each permuation
    private ArrayList<String> words = new ArrayList<>(); //Stores all the permutations

    private boolean exchangeFlag = false;

    Bot0(PlayerAPI me, OpponentAPI opponent, BoardAPI board, UserInterfaceAPI ui, DictionaryAPI dictionary) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.info = ui;
        this.dictionary = dictionary;
        turnCount = 0;
    }

    //1. Check valid moves
    //2. Check if a word can be placed over Triple Word Score -> Double -> Triple letter -> double
    //3. Check the board for a high value letter
    //4. Combine letter from board with tiles in frame and consult dictionary for the highest possible word
    //5. Repeat for all possible moves until the highest one is found
    //6. Place word.
    //7. If word cannot be placed, pass


    public String PlaceFirstWord() {
            System.out.println(frameToString());
            String frame = "";
            if (/*board.isFirstPlay() */ firstMove) {
                //Put tiles from frame into a word
                frame = frameToString();
                //frame = frame.substring(1);
                firstMove = false;
            }

            int numBlanks = getNumBlanks(frame); //find number of blank tiles in the frame

            if(numBlanks > 0) //Remove blank tiles
                frame = frame.replaceAll("_", "A");

            return placeWord(frame,numBlanks);




    }
    public String placeWordValTile(String coordinates)
    {

        if(firstMove)  //Skip when it's the first move
            return "";

        //Get coordinates (row and column) and store in integers

//        if(coordinates == "")
//        {
//            return "";
//        }
        String[] coordsArr = coordinates.split(" ");
        String colStr = coordsArr[1];
        String rowStr = coordsArr[0];
        int col = Integer.parseInt(colStr);
        int row = Integer.parseInt(rowStr);


        //Combine the frame and the high val tile to a string and find permutations
        String valTile = String.valueOf(board.getSquareCopy(row-1,col-1).getTile().getLetter());
        String frame = frameToString();

        String word = frame + valTile;

        int numBlanks = getNumBlanks(word);
        if(numBlanks > 0) //Remove blank tiles
            word = word.replaceAll("_", "B");

        //get word permutations and score
        words = getPermutation(word,"",words);
        String tempWord = word;
        while(words.size() == 0 && tempWord.length() > 0)
        {
            words = getPermutation(tempWord,"",words);
            tempWord = removeLowestLetter(tempWord,valTile.charAt(0));
        }


        if(words.size() == 0) return "";

        int highValIndex = 0;
        String placement = "";
        for(int i = 0; i < words.size(); i++)
        {
            String tryWord = words.get(i);  //ith valid word
            for(int j = 0; j < tryWord.length(); j++)
            {
                String currChar = String.valueOf(tryWord.charAt(j));
                if(currChar.equals(valTile))
                {
                    highValIndex = j;
                    break;
                }
            }
            try {
                placement = findValidPlacement(row, col, tryWord, highValIndex); //Get a valid word placement
            }catch(IndexOutOfBoundsException e){}
            if(placement != "")
                break;          //If there was no valid word placement, check next word
        }


        if(numBlanks > 0) //Add the blanks back
        {
            placement = ReplaceBlank(placement);
//            placement.replaceAll("B","_");
              System.out.println(placement + " <---");
            placement += " ";
        }


        if(numBlanks >0 && placement.length() > 1)
        for (int i = 0; i < placement.length(); i++)
        {
            if (placement.charAt(i) == Tile.BLANK)
                placement += "B";
             else
                continue;
        }

        resetArrayLists();
        return placement;
    }


    //Functions
    //1. Place Best Word
    public String getCommand() {
        // Add your code here to input your commands
        // Your code must give the command NAME <botname> at the start of the game
        String command = "";
        /*
        switch (turnCount) {
            case 0:
               // command = PlaceFirstWord();//"NAME Bot0";
                command = "PASS";
                break;
            case 1:
                command = "PASS";
                break;
            case 2:
                command = "HELP";
                break;
            case 3:
                command = "SCORE";
                break;
            case 4:
                command = "POOL";
                break;
            default:
                command = "H8 A AN";
                break;
        }
        turnCount++;

        */
        //getHighestWord();
        command = makeBestMove();
        if(command != "")
            turnCounter++;

        System.out.println(command);
        return command;
    }


    public String makeBestMove()
    {
        String command = "";
        command = PlaceFirstWord();

        if(firstWord && command == "")
            firstMove = true;
        else firstWord = false;


        if(command == "")
            command = placeValTile();
        else return command;

        if (command == "" && exchangeFlag == true){
            command = exchange();

        }else return command;


        if(command == "")
            return command;
            // command = placeValTile();
        else
        {

        }
        return command;
    }



    private String exchange() {

        String letters = "";
        String stringRack = frameToString();

        int tilesToExchange = Math.round(((float)Math.random()*3)+2); //takes random tiles from 1-5

        for (int i = 0; i < tilesToExchange; i++){
            letters += stringRack.charAt(i); //put the characters into letters
        }
        exchangeFlag = false;

        return "EXCHANGE " + letters;

    }

    /* Checks if there are any empty valuable squares and returns the coordinates if found */
    public String checkValuableSquare() {
        String coordinates = "";

        //Check for empty triple word scores
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.getSquareCopy(i, j).isTripleWord() && !board.getSquareCopy(i, j).isOccupied()) {
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }

        //Check for empty double word scores
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.getSquareCopy(i, j).isDoubleWord() && !board.getSquareCopy(i, j).isOccupied()) {
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }

        //Check for empty triple letter scores
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.getSquareCopy(i, j).isTripleLetter() && !board.getSquareCopy(i, j).isOccupied()) {
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }


        //Check for double letter scores
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.getSquareCopy(i, j).isDoubleLetter() && !board.getSquareCopy(i, j).isOccupied()) {
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }

        //Else return blank
        return coordinates;
    }


    public boolean isPlaceable(int i, int j) {
        if (!board.getSquareCopy(i + 1, j).isOccupied() ||
                !board.getSquareCopy(i - 1, j).isOccupied() ||
                !board.getSquareCopy(i, j + 1).isOccupied() ||
                !board.getSquareCopy(i, j).isOccupied()
        ) return true;
        else return false;
    }

    public boolean isConnectingHorizontal(int i, int j) {
        if (board.getSquareCopy(i + 1, j).isOccupied() ||
                board.getSquareCopy(i - 1, j).isOccupied() ||
                board.getSquareCopy(i, j + 1).isOccupied()
        ) return true;
        else return false;
    }

    public boolean isConnectingVertical(int i, int j) {
        if (board.getSquareCopy(i + 1, j).isOccupied() ||
                board.getSquareCopy(i, j - 1).isOccupied() ||
                board.getSquareCopy(i, j + 1).isOccupied()
        ) return true;
        else return false;
    }


    private boolean randomDirection() //return true for horizontal / false for vertical
    {
        boolean isHorizontal = false; //Matching boolean variable from the word placement function
        Random rand = new Random();
        int direction = rand.nextInt(2);
        if (direction - 1 == 1)
            return isHorizontal;
        else return !isHorizontal;
    }


    private String highestWord(int length)
    {
        String user_frame = me.getFrameAsString();

        String word = "";
        if(length == 7)
            return user_frame;
        else
        {

            while(user_frame.length() != (7-length))
            {
                int max =0;
                char maxLetter = user_frame.charAt(0);
                for(int i=0; i< user_frame.length(); i++)
                {
                    Tile tile = new Tile(user_frame.charAt(i));
                        if (tile.getValue() > max)
                        {
                            max = tile.getValue();
                            maxLetter = user_frame.charAt(i);
                        }
                }
                for(int i=0; i< user_frame.length(); i++)
                {

                }
            }
        }
        return word;
    }



    private String getLongestWord(int i, int j)
    {
        boolean isHorizontal = true;
        int row = i;
        int col = j;
        int max_length = 7;
        int curr_length = 0;
        ArrayList<Integer> longest = new ArrayList<>();

        //Check if the longest word is horizontal with the first letter beginning on the square
        while( !board.getSquareCopy(row,col).isOccupied() && col <15 && curr_length != 7)
        {
            col++;
            curr_length++;
        }
        if(curr_length == 7 && isConnectingHorizontal(row,col)) //Found longest possible word
        {

        }
        else
        {
            longest.add(curr_length);
            curr_length = 0;
            col = j;
        }

        //Check if the longest word is vertical with the first letter beginning on the square
        while( !board.getSquareCopy(row,col).isOccupied() && row <15 && curr_length != 7 )
        {
            row++;
            curr_length++;
        }
        if(curr_length == 7 && isConnectingVertical(row,col)) //Found longest possible word
        {

        }
        else
        {
            longest.add(curr_length);
            curr_length =0;
            row = i;
        }

        //Find max
        int temp = longest.get(i);
        int highestIndex =0;
        for(int index = 1; index<longest.size(); index++)
        {
            if(longest.get(index) > temp)
            {
                temp = longest.get(index);
                highestIndex = index;
            }
        }
        String word = highestWord(longest.get(highestIndex));

        return "";
    }




    private String getHighestWord(int row, int col) {
        char highValSquare = board.getSquareCopy(row, col).getTile().getLetter(); // The high value tile from board

        String possibleWord = highValSquare + frameToString(); //String for frame + tile from board

        words = getPermutation("abc", "", words); //Store all permutations of string in words arraylist

        //populateWordScore(); //Populate word score array with corresponding word values

        String highestWord = getHighWordScore(); //Get current highest word

        boolean validWordFlag = false;
        while (!validWordFlag) { //Try to place the highest word - if not go to the next highest
            if (isValidMove(highestWord, row, col) != "")
                break;
            else highestWord = getHighWordScore();
        }


        return "";
    }


    public String frameToString() {
        String user_frame = me.getFrameAsString();
        String frameToString = "";
        for (int i = 0; i < user_frame.length(); i++) {
            if (user_frame.charAt(i) != '[' &&
                    user_frame.charAt(i) != ']' &&
                    user_frame.charAt(i) != ' ' &&
                    user_frame.charAt(i) != ']' &&
                    user_frame.charAt(i) != ',')
                frameToString += user_frame.charAt(i);
        }
        return frameToString.toUpperCase();
    }


    private ArrayList<String> getPermutation(String word, String perm, ArrayList<String> words) {

        // If string is empty
        if (word.length() == 0) {

            //only add valid words
            ArrayList<Word> wordList = new ArrayList<>();
            wordList.add(stringToWord(perm));
            if (dictionary.areWords(wordList))
                words.add(perm);

            return words;
        }

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            String ros = word.substring(0, i) +
                    word.substring(i + 1);

            getPermutation(ros, perm + ch, words);
        }
        populateWordScore();
        return words;
    }


    private Word stringToWord(String str) {
        return new Word(0, 0, true, str);
    }

    public String placeValTile() {



        //check for tiles with value 10
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if(board.getSquareCopy(i,j).isOccupied())
                if (board.getSquareCopy(i, j).getTile().getValue() == 10) {
                    String coordinates = "";
                    coordinates += i+1;
                    coordinates += " ";
                    coordinates += j+1;

                    String isPossible = placeWordValTile(coordinates);
                    if(isPossible != "")
                    {
                        return isPossible;
                    }
                }
            }
        }

        //check for tiles with value 8
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if(board.getSquareCopy(i,j).isOccupied())
                    if (board.getSquareCopy(i, j).getTile().getValue() == 8) {
                    String coordinates = "";
                    coordinates += i+1;
                    coordinates += " ";
                    coordinates += j+1;
                    String isPossible = placeWordValTile(coordinates);
                    if(isPossible != "")
                    {
                        return isPossible;
                    }
                }
            }
        }

        //check for tiles with value 5
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if(board.getSquareCopy(i,j).isOccupied())

                    if (board.getSquareCopy(i, j).getTile().getValue() == 5) {
                    String coordinates = "";
                    coordinates += i+1;
                    coordinates += " ";
                    coordinates += j+1;
                    String isPossible = placeWordValTile(coordinates);
                    if(isPossible != "")
                    {
                        return isPossible;
                    }
                }
            }
        }

        //check for tiles with value 4
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if(board.getSquareCopy(i,j).isOccupied())

                    if (board.getSquareCopy(i, j).getTile().getValue() == 4) {
                    String coordinates = "";
                    coordinates += i+1;
                    coordinates += " ";
                    coordinates += j+1;
                    String isPossible = placeWordValTile(coordinates);
                    if(isPossible != "")
                    {
                        return isPossible;
                    }
                }
            }
        }

        //check for tiles with value 3
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if(board.getSquareCopy(i,j).isOccupied())

                    if (board.getSquareCopy(i, j).getTile().getValue() == 3) {
                    String coordinates = "";
                    coordinates += i+1;
                    coordinates += " ";
                    coordinates += j+1;
                    String isPossible = placeWordValTile(coordinates);
                    if(isPossible != "")
                    {
                        return isPossible;
                    }
                }
            }
        }
        //check for tiles with value 2
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if(board.getSquareCopy(i,j).isOccupied())

                    if (board.getSquareCopy(i, j).getTile().getValue() == 2) {
                        String coordinates = "";
                        coordinates += i+1;
                        coordinates += " ";
                        coordinates += j+1;
                        String isPossible = placeWordValTile(coordinates);
                        if(isPossible != "")
                        {
                            return isPossible;
                        }
                    }
            }
        }

        //check for tiles with value 1
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.getSquareCopy(i, j).isOccupied())

                    if (board.getSquareCopy(i, j).getTile().getValue() == 1) {
                        String coordinates = "";
                        coordinates += i + 1;
                        coordinates += " ";
                        coordinates += j + 1;
                        String isPossible = placeWordValTile(coordinates);
                        if(isPossible != "")
                        {
                            return isPossible;
                        }
                    }
            }
        }
        return "";
    }

    private String isValidMove(String word, int col, int row) {
        //Find index of string where the tile from the board is located in the word
        char valTile = board.getSquareCopy(row, col).getTile().getLetter();

        int index = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == valTile)
                index = i;
        }

        //Convert String word to Word objectf
        Word wordObj = new Word(row, col, true, word);
        Frame frameObj = new Frame();
        ArrayList<Tile> tileArray = new ArrayList<>();
        for (int i = 0; i < word.length(); i++)
            tileArray.add(new Tile(word.charAt(i)));

        frameObj.addTiles(tileArray);

        if (board.isLegalPlay(frameObj, wordObj)) {
            return col + " " + row + " " + wordObj.getLetters();
        } else


            return "";
    }


    private String getHighWordScore() //Returns highest scoring word
    {

        int maxScore = 0;
        int tempIndex = 0;

        for (int i = 0; i < word_score.size(); i++) {
            if (i == 0) maxScore = word_score.get(i);
            else {
                if (maxScore < word_score.get(i)) {
                    tempIndex = i;
                    maxScore = word_score.get(i);
                }
            }
        }

        String tempWord = words.get(tempIndex);

        words.remove(tempIndex); //Remove word from array in case it cannot be placed
        return tempWord;
    }

    public void populateWordScore()
    {
        //Populate word_score array with corresponding scores
        for (int i = 0; i < words.size(); i++) {
            String curr = words.get(i);
            int score = 0;
            for (int j = 0; j < curr.length(); j++) {
                Tile getValue = new Tile(curr.charAt(j));
                score += getValue.getValue();
            }
            word_score.add(score);
            score = 0;
        }
    }

    private boolean isChallenge(){
        if (turnCount % 4 == 0) {
            return true;
        }
        return false;
    }


    private int getNumBlanks(String word)
    {
        int count = 0;
        for(int i=0; i<word.length(); i++)
        {
            if(word.charAt(i) == '_')
                count++;
        }
        return count;
    }


//    private ArrayList<String> getValidPermuations()
//    {
//
//    }

    private String removeLowestLetter(String word)
    {
        if(word.length() == 0)
        {
            exchangeFlag = true;
        }
        int min = 11;
        int minIndex = 0;
        String minChar = "";
        for(int i = 0; i < word.length(); i++) // Remove min character
        {
            Tile getVal = new Tile(word.charAt(i));
            if(min > getVal.getValue() ) {
                min = getVal.getValue();
                minChar = String.valueOf(word.charAt(i));
                minIndex = i;
            }
        }
        String newWord = "";
        for(int i = 0; i < word.length(); i++)
        {
            if(minChar != "")
            {
                if(word.charAt(i) !=  minChar.charAt(0))
                    newWord += word.charAt(i);
            }

        }
        return newWord;
    }

    private String removeLowestLetter(String word, char noRemove) //With a character you don't want removed
    {
        if(word.length() == 0)
        {
            exchangeFlag = true;
        }
        int min = 11;
        int minIndex = 0;
        String minChar = "";
        for(int i = 0; i < word.length(); i++) // Remove min character
        {
            Tile getVal = new Tile(word.charAt(i));
            if(min > getVal.getValue() && getVal.getLetter() != noRemove) {
                min = getVal.getValue();
                minChar = String.valueOf(word.charAt(i));
                minIndex = i;
            }
        }
        String newWord = "";
        for(int i = 0; i < word.length(); i++)
        {
            if(minChar != "")
            {
                if(word.charAt(i) !=  minChar.charAt(0))
                    newWord += word.charAt(i);
            }

        }
        return newWord;
    }
    private String placeWord(String frame,  int numBlanks)
    {
        words = getPermutation(frame,"",words); //Global word array - get all permutations for the letters in the frame

        String temp = frame;
        int counter = 0; //Counter for removing a letter from the perumation string if no valid words can be found
        while(words.size() == 0 && !exchangeFlag)
        {
            frame = removeLowestLetter(frame);
            words = getPermutation(frame, "", words);
        }

        if(exchangeFlag)
        {
            return "";
        }


        String word = getHighWordScore();

        if(numBlanks > 0) //Add the blanks back
        {
            word.replaceAll("A","_");
        }
        String command = "";
        if (frame != "") {
            command += "H8 ";
            command += "A ";
            command += word + " ";


            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == Tile.BLANK) {
                    command += "A";
                } else {
                    continue;
                }
            }

        }
        System.out.println(command);
        resetArrayLists();
        return command;
    }

    private void resetArrayLists()
    {
        words.clear();
        word_score.clear();
    }


    /* Finds a valid placement to place a word (tileIndex is the index of the tile from the board in string word) */
    private String findValidPlacement(int row, int col, String word, int tileIndex)
    {


        //Create frame with bot's frame (for converting String to Word)
        Frame frameObj = new Frame();
        ArrayList<Tile> tileArray = new ArrayList<>();
        for (int i = 0; i < word.length(); i++)
            tileArray.add(new Tile(word.charAt(i)));
        frameObj.addTiles(tileArray);

        String sRow = "";

//        //Try horizontal
        int horCol = col - tileIndex; //Get the starting col index (the colum left of tile index)
        Word horWord = new Word(row-1,  horCol-1, true, word, "B");


        if (board.isLegalPlay(frameObj, horWord)) {
            sRow = getLetterRow(horCol);
            return sRow + row + " A " + horWord.getLetters();
        }


        //Try vertical
        int verCol = col; //Get the starting row index (above tile index)
        int verRow = row - tileIndex;
        Word verWord = new Word(verRow-1, verCol-1, false, word, "B");

        if (board.isLegalPlay(frameObj, verWord)) {
            sRow = getLetterRow(verCol);

            return sRow + verRow +  " D " + horWord.getLetters();
        }



        //otherwise, return blank
        return "";


    }

    private String getLetterRow(int i)
    {
        switch (i)
        {
            case 1: return "A";
            case 2: return "B";
            case 3: return "C";
            case 4: return "D";
            case 5: return "E";
            case 6: return "F";
            case 7: return "G";
            case 8: return "H";
            case 9: return "I";
            case 10: return "J";
            case 11: return "K";
            case 12: return "L";
            case 13: return "M";
            case 14: return "N";
            case 15: return "O";


            default: return "";
        }
    }

    private String ReplaceBlank(String placement)
    {
        String newPlacement = "";
        for(int i = 0; i < placement.length(); i++)
        {
            if(placement.charAt(i) != 'B')
                newPlacement += placement.charAt(i);
            else
                newPlacement += '_';
        }
        return newPlacement;
    }
}

