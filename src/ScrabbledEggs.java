import java.util.ArrayList;
import java.util.Random;

public class ScrabbledEggs implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects


    /**
     * Scrabbled Eggs Bot
     * Our bot does the following:
     *
     * On the first move, it will try to place the highest scoring word starting with 7 letters (the whole frame) if a
     * word cannot be placed, it will then remove the lowest value letter from the word and try again (6 letters this
     * time). This continues until either a word is found or there's only one letter left.  In this scenario, the bot
     * exchanges, randomly, 1-5 letters from the frame and then tries again. If the worst case occurs, the bot exchanges
     * twice and then places a two letter word from the frame (the bot is aware the game will end if it doesn't place
     * a word within three moves).
     *
     * When there are already tiles on the board, the bot checks to see if there is a vacant high value square (e.g
     * triple word score). It will attempt all possible combinations of words to include that square and will place a
     * word. If it cannot it will find another vacant high value square. If all are occupied it procedes to the next
     * option.
     *
     * When there are already tiles on the borad, the bot then scans to see if it can find a tile worth 10 points. If it
     * finds a tile, it tries all possible combinations of words including that tile.  If a word cannot be placed, it
     * checks the next high value tile. This repeats from 10 point tiles down to 1 points (worst case)
     *
     * If the bot cannot place a word under these circumstances it will exchange tiles from its hand
     *
     * Every four moves the bot will challenge (functionality complete but not implemented currently for testing purposes)
     *
     * During the end game, when there are no tiles left in the pool, if the bot can't place a word, it's immediate
     * response is to exchange. This causes the error "there are no tiles left in the pool to exchange", in this case
     * the bot has been programmed to attempt the exchange three times and then "PASS" which will complete the game.
     *
     * Note: sometimes the bot doesn't get off to a good start trying to place words a the beginning, rarely, the bot
     * won't be able to find a word to place even after exchanging three times and the game will end. We politely ask
     * just to neglect that attempt and rerun the program.
     *
     * Lastly, we used a separate respository for assignment 5 so that it wouldn't clash with our own work from our own
     * repository, since we wish to work and complete our rendition of scrabble over the summer. The link to the assignment
     * 5 repo is here:
     *https://github.com/JenniferFlanagan/Scrabbled-Eggs---Assignment-5?fbclid=IwAR1VSyWmGzEvzIFgycj_y-THHT6lfTldFOG6XR4atueAfcM1ZS-HY-wtJF0
     * */

    //Global variables
    private PlayerAPI me;
    private OpponentAPI opponent;
    private BoardAPI board;
    private UserInterfaceAPI info;
    private DictionaryAPI dictionary;
    private int turnCount;
    private boolean firstMove = true; //If its the first move
    private boolean firstWord = true; //If its the first word being placed (true when board is empty)
    private int turnCounter = 0;
    private boolean connectingWords = false;
    private int onBoardCounter = 0; //Counts how many designated letters already exist on the board
    private int noPlacementCount = 0; //Number of times the bot has bot hasn't placed a word in a row
    private boolean placeFlag = false; //If a command places a word, true
    private int exchangeCounter = 0; //Amount of times the bot has exchagned in a row

    private ArrayList<Integer> word_score = new ArrayList<>(); //Stores corresponding score for each permuation
    private ArrayList<String> words = new ArrayList<>(); //Stores all the permutations
    private boolean exchangeFlag = false; //If a word cannot be placed, turn true


    //the i wanna be really smart array
    int[] commonLetters = {2,11,10,11,1,11,11,11,4,11,11,9,11,7,5,11,
            11,3,8,6,11,11,11,11,11,11}; //Rank of commonality for each letter of the alphabet
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    ScrabbledEggs(PlayerAPI me, OpponentAPI opponent, BoardAPI board, UserInterfaceAPI ui, DictionaryAPI dictionary) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.info = ui;
        this.dictionary = dictionary;
        turnCount = 0;
    }


    //1. Place Best Word
    public String getCommand() {
        // Add your code here to input your commands
        // Your code must give the command NAME <botname> at the start of the game
        String command = "";

        //Get best move
        command = makeBestMove();

        if(command != "")
            turnCounter++;

        if(placeFlag) //If a word is placed reset noPlacemenCount to 0
            noPlacementCount = 0;

      //  System.out.println(command);
        if(!(command.contains("EXCHANGE"))) //Every time the bot doesn't exchange reset the exchange counter
            exchangeCounter = 0;

        if(exchangeCounter == 3) //In the case where there are no tiles left in the pool to exchange - pass
            return "PASS";


        return command;
    }

    private String getAnyWord() { //Worst case, try and make a valid word placement

        String frame = "";
        StringBuilder appendLetter = new StringBuilder();
        String vowel = "";
        String consonant = "";
        //String word = "";
        frame = frameToString();
        String placement = "H8 A ";
        for(int i = 0; i < 7; i++)
        {
            char temp = frame.charAt(i);
            //Check current char against each char in the frame and return two letter word

            for(int j = 0; j< 7; j++)
            {
                if(i != j) {
                    String strWord =  Character.toString(temp) + Character.toString(frame.charAt(j));
                    Word word = new Word(7,7,true,strWord);
                    ArrayList<Word> checkDic = new ArrayList<>();
                    checkDic.add(word);

                    if(dictionary.areWords(checkDic))
                    {
                        placeFlag = true;
                        return placement += strWord;
                    }
                }
            }
        }


//        for (int i = 0; i < frameToString().length() - 1; i++) {
//            char current = frameToString().charAt(i);//finds the vowel in the frame
//            char next = frameToString().charAt(i + 1);//finds the next vowel in the frame
//            try {
//                if (isVowel(Character.toString(current)))
//                    vowel = Character.toString(current);
//                else
//                    consonant = Character.toString(current);
//
//                if (isVowel(vowel) && !isVowel(consonant)) {
//                    word += consonant + vowel;
//                    Word wordObj = new Word(7, 7, true, word);
//                    ArrayList<Word> wordList = new ArrayList<>();
//                    wordList.add(wordObj);
//                    if (dictionary.areWords(wordList)) {
//                        return "H8 A " + word;
//                    }
//                }

//                if (isVowel(current)) {
//                    frame = frameToString().concat(frame);
//                } else {
//                    appendLetter.append(next);
//                    appendLetter.append(next + 1);
//                }
//            } catch (IndexOutOfBoundsException e) {}
//            //frame += appendLetter.append(frame.charAt(i)).toString();
//
//        }
        placeFlag = false;
        return "PASS";
    }
    public String PlaceFirstWord() {
        // out.println(frameToString());
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
            if(placement.length() > 1) {
                placement = ReplaceBlank(placement, 'B', numBlanks);
                //placement.replaceAll("B","_");
              //  System.out.println(placement + " <---");
                placement += " ";
            }
        }


        if(numBlanks >0 && placement.length() > 1)
            for (int i = 0; i < placement.length(); i++)
            {
                if (placement.charAt(i) == Tile.BLANK){
                    placement += "B";
                    //break;
                }
            }


        resetArrayLists();
        return placement;
    }


    private String placeWordValSquare(String coordinates)
    {
        //Store coordinates in row and col arrays;
        String[] coordsArr = coordinates.split(" ");
        String colStr = coordsArr[1];
        String rowStr = coordsArr[0];

        int col = Integer.parseInt(colStr);
        int row = Integer.parseInt(rowStr);






        return "";
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


    public String makeBestMove() {

        String command = "";

        //If first word and the bot hasn't placed a word twice in a row, place a two character word
        if (noPlacementCount >= 2 && firstWord) {
            placeFlag = true;
            return getAnyWord();
        }

        //Otherwise, find the highest value first word
        command = PlaceFirstWord();

        //If word is not found and the first word hasn't been placed, resst firstMove variable
        if (firstWord && command == "")
            firstMove = true;
        else firstWord = false;

        //If not the first move, place the highest value word
        if (command == "")
            command = placeValTile();
        else {
            placeFlag = true;
            return command;
        }

        //If word could not be placed, exchange
        if (command == "" && exchangeFlag == true && noPlacementCount < 2) {
            noPlacementCount++;
            command = exchange();

        }

//        }else{
//            placeFlag = true;
//            return command;
//        }


//        if (command == "" && turnCount % 4 == 0){
//
//        }else{
//            placeFlag = true;
//            return command;
//        }
        if(command != "")
            exchangeCounter++;
        return command;
    }

    private boolean isChallenge(){
        if (turnCount % 4 == 0) {
            return true;
        }
        return false;
    }

    private String exchange() {

        //Exchange random amoutn of letters from 1-5
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



    public boolean isPlaceable(int i, int j) {
        if (!board.getSquareCopy(i + 1, j).isOccupied() ||
                !board.getSquareCopy(i - 1, j).isOccupied() ||
                !board.getSquareCopy(i, j + 1).isOccupied() ||
                !board.getSquareCopy(i, j).isOccupied()
        ) return true;
        else return false;
    }

    public boolean isConnectingHorizontal(int i, int j) { //Checks to see if a letter is adjacent to another letter horizontally
        if (board.getSquareCopy(i + 1, j).isOccupied() ||
                board.getSquareCopy(i - 1, j).isOccupied() ||
                board.getSquareCopy(i, j + 1).isOccupied()
        ) return true;
        else return false;
    }

    public boolean isConnectingVertical(int i, int j) {//Checks to see if a letter is adjacent to another letter vertically
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


    private String highestWord(int length) //Functionality not implemented yet
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






//Functionality not implemented yet
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

    //Gets all valid permutations of words from a player's frame that exist in the dictionary
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

    //Find the highest value tile on the board and see if th ebot can place a word including that tile
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

    //Functionality not implemented yet
    public String checkValuableSquare() {
        //Check for empty triple word scores
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.getSquareCopy(i, j).isTripleWord() && !board.getSquareCopy(i, j).isOccupied()) {
                    String coordinates = "";
                    coordinates += i+1;
                    coordinates += " ";
                    coordinates += j+1;

                    String isPossible = placeWordValSquare(coordinates);
                    if(isPossible != "")
                        return isPossible;
                }
            }
        }

        //Check for empty double word scores
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.getSquareCopy(i, j).isDoubleWord() && !board.getSquareCopy(i, j).isOccupied()) {
                    String coordinates = "";
                    coordinates += i+1;
                    coordinates += " ";
                    coordinates += j+1;

                    String isPossible = placeWordValSquare(coordinates);
                    if(isPossible != "")
                        return isPossible;
                }
            }
        }

        //Check for empty triple letter scores
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.getSquareCopy(i, j).isTripleLetter() && !board.getSquareCopy(i, j).isOccupied()) {
                    String coordinates = "";
                    coordinates += i+1;
                    coordinates += " ";
                    coordinates += j+1;

                    String isPossible = placeWordValSquare(coordinates);
                    if(isPossible != "")
                        return isPossible;
                }
            }
        }


        //Check for double letter scores
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.getSquareCopy(i, j).isDoubleLetter() && !board.getSquareCopy(i, j).isOccupied()) {
                    String coordinates = "";
                    coordinates += i+1;
                    coordinates += " ";
                    coordinates += j+1;

                    String isPossible = placeWordValSquare(coordinates);
                    if(isPossible != "")
                        return isPossible;
                }
            }
        }
        return "";
    }

    //Checks to see if the bot can place a word in a specific position on the hboard
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

    //Gets the highest word from the permutations array
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


    //Helper function that popoulates the word score arraylist
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


    //Returns number of balnks in a wiord
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



    //removes the lowest value letter from the word, (bot will then try to compute a word without the letter)
    private String removeLowestLetter(String word)
    {
        if(word.length() == 0)
        {
            exchangeFlag = true; //If no word, exchange
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
        for(int i = 0; i < word.length(); i++) //Create newword without min character
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

    //Placing best word
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
            word = ReplaceBlank(word, 'A', numBlanks);
        }
        String command = "";
        if (frame != "") {
            command += "H8 ";
            command += "A ";
            command += word + " ";


            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == Tile.BLANK) {
                    command += "A";
                    break;
                }
            }
        }


       // System.out.println(command);
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
        int horCol = col - tileIndex; //Get the starting col index (the column left of tile index)
        Word horWord = new Word(row-1,  horCol-1, true, word, "B");


        if (board.isLegalPlay(frameObj, horWord)) {
            sRow = getLetterRow(horCol);
            //
            // System.out.println("SRow is " + sRow);
            return sRow + row + " A " + horWord.getLetters();
        }


        //Try vertical
        int verCol = col; //Get the starting row index (above tile index)
        int verRow = row - tileIndex;
        Word verWord = new Word(verRow-1, verCol-1, false, word, "B");

        if (board.isLegalPlay(frameObj, verWord)) {
            sRow = getLetterRow(verCol);
           // System.out.println("SRow is " + sRow);
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

    //Replaces the string with to include blanks from the frame
    private String ReplaceBlank(String placement, char designation, int numBlanks)
    {
        String newPlacement = "";
        for(int i = 0; i < placement.length(); i++)
        {
            if(placement.charAt(i) != designation ) {
                newPlacement += placement.charAt(i);
                numBlanks--;
            }
            else
            if(i != 0 && numBlanks!=0) {
                if(onBoardCounter >0)
                {
                    newPlacement += 'B';
                    onBoardCounter--;
                }
                else
                    newPlacement += '_';
            }
        }
        return newPlacement;
    }


    //checks to see if the designated character is already on the board (we use 'B' for the designated character when its not the first move)
    private void onBoardCheck(Word word )
    {
        int row = word.getRow();
        int col = word.getColumn();

        for(int i = 0; i < word.getLetters().length(); i++)
        {
            if(word.getLetters().charAt(i) == 'B' && word.isHorizontal())
                if(board.getSquareCopy(row,col).isOccupied() && board.getSquareCopy(row + i, col).getTile().getLetter() == 'B')
                {
                    onBoardCounter++;
                }
                else if(word.getLetters().charAt(i) == 'B' && !word.isHorizontal())
                    if(board.getSquareCopy(row,col).isOccupied() && board.getSquareCopy(row, col+i).getTile().getLetter() == 'B')
                    {
                        onBoardCounter++;
                    }
        }
    }
}
