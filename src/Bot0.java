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

    private ArrayList<Integer> word_score = new ArrayList<>();
    private ArrayList<String> words = new ArrayList<>();

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
        String word = "";
        if (board.isFirstPlay())
        {
            //Put tiles from frame into a word  :(
           word = frameToString();

        }

        String command = "";
        if (word != "")
        {
            command += "H8 ";
            command += "A ";
            command += word;
        }
        System.out.println(command);

        return command;
    }


    //Functions
    //1. Place Best Word
    public String getCommand() {
        // Add your code here to input your commands
        // Your code must give the command NAME <botname> at the start of the game
        String command = "";

        /*switch (turnCount) {
            case 0:
                command = PlaceFirstWord();//"NAME Bot0"; //why do i suck lol
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

        return command;
    }

    /* Checks if there are any empty valuable squares and returns the coordinates if found */
    public String checkValuableSquare()
    {
        String coordinates = "";

        //Check for empty triple word scores
        for(int i=0; i<15; i++)
        {
            for(int j = 0; j<15; j++)
            {
                if(board.getSquareCopy(i,j).isTripleWord() && !board.getSquareCopy(i,j).isOccupied())
                {
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }

        //Check for empty double word scores
        for(int i=0; i<15; i++)
        {
            for(int j = 0; j<15; j++)
            {
                if(board.getSquareCopy(i,j).isDoubleWord() && !board.getSquareCopy(i,j).isOccupied())
                {
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }

        //Check for empty triple letter scores
        for(int i=0; i<15; i++)
        {
            for(int j = 0; j<15; j++)
            {
                if(board.getSquareCopy(i,j).isTripleLetter() && !board.getSquareCopy(i,j).isOccupied())
                {
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }


        //Check for double letter scores
        for(int i=0; i<15; i++)
        {
            for(int j = 0; j<15; j++)
            {
                if(board.getSquareCopy(i,j).isDoubleLetter() && !board.getSquareCopy(i,j).isOccupied())
                {
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


    public boolean isPlaceable(int i, int j)
    {
        if(     !board.getSquareCopy(i+1, j).isOccupied()    ||
                !board.getSquareCopy(i-1, j).isOccupied()    ||
                !board.getSquareCopy(i, j+1).isOccupied()    ||
                !board.getSquareCopy(i, j).isOccupied()
        ) return true;
        else return false;
    }

    public boolean isConnectingHorizontal(int i, int j)
    {
        if(     board.getSquareCopy(i+1, j).isOccupied()    ||
                board.getSquareCopy(i-1, j).isOccupied()    ||
                board.getSquareCopy(i, j+1).isOccupied()
        ) return true;
        else return false;
    }

    public boolean isConnectingVertical(int i, int j)
    {
        if(     board.getSquareCopy(i+1, j).isOccupied()    ||
                board.getSquareCopy(i, j-1).isOccupied()    ||
                board.getSquareCopy(i, j+1).isOccupied()
        ) return true;
        else return false;
    }


    private boolean randomDirection() //return true for horizontal / false for vertical
    {
        boolean isHorizontal = false; //Matching boolean variable from the word placement function
        Random rand = new Random();
        int direction = rand.nextInt(2);
        if(direction-1 == 1)
            return isHorizontal;
        else return !isHorizontal;
    }

    /*
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

     */


    private String getHighestWord(int row, int col)
    {
        char highValSquare = board.getSquareCopy(row,col).getTile().getLetter(); // The high value tile from board

        String possibleWord = highValSquare + frameToString(); //String for frame + tile from board

        //Store all permutations of string in words arraylist
        words = getPermutation("abc","", words);


        //Populate word_score array with corresponding scores
        for(int i =0; i< words.size(); i++)
        {
            String curr = words.get(i);
            int score = 0;
            for(int j =0; j<curr.length(); j++)
            {
                Tile getValue = new Tile(curr.charAt(j));
                score += getValue.getValue();
            }
            word_score.add(score);
            score = 0;
        }


        String highestWord = getHighWordScore();

        //Try to place the highest word - if not go to the second highest word

        boolean validWordFlag = false;
        while(!validWordFlag)
        {
            if(isValidMove(highestWord,row,col) != "")
                break;
            else highestWord = getHighWordScore();
        }


        //Find highest score then check if it is a legal move

        return "";
    }

    private String getHighestWord()
    {
        String possibleWord = frameToString();
        ArrayList<String> words = new ArrayList<>();

        //Get all valid possible words
        System.out.println(words.toString());

        ArrayList<Integer> word_score = new ArrayList<>();
        for(int i=0; i< words.size(); i++) //Populate word_score array with corresponding scores
        {
            String curr = words.get(i);
            int score = 0;
            for(int j =0; j<curr.length(); j++)
            {
                Tile getValue = new Tile(curr.charAt(j));
                score += getValue.getValue();
            }
            word_score.add(score);
            score = 0;
        }

        //Find highest scoring word
        int maxScore = 0;
        int tempIndex = 0;

        for(int i =0; i< word_score.size(); i++)
        {
            if(i == 0) maxScore = word_score.get(i);
            else
            {
                if(maxScore < word_score.get(i))
                {
                    tempIndex = i;
                    maxScore = word_score.get(i);
                }
            }
        }

        System.out.println(word_score.toString());
        //Find highest score then check if it is a legal move

        return "";
    }

    public String makeBestMove()
    {
        String command = "";
        command = PlaceFirstWord();

        if (isChallenge()){
            return "CHALLENGE";
        }


        if(command == "")
            command = checkValuableSquare();
        else return command;

        if(command == "")
           command = placeValTile();
        else
        {

        }

        return command;
    }

    public String frameToString()
    {
        String user_frame = me.getFrameAsString();
        String frameToString = "";
        for(int i =0; i<user_frame.length(); i++)
        {
            if(user_frame.charAt(i) != '[' &&
                    user_frame.charAt(i) != ']' &&
                    user_frame.charAt(i) != ' ' &&
                    user_frame.charAt(i) != ']' &&
                    user_frame.charAt(i) != ',')
                    frameToString += user_frame.charAt(i);
        }
        return frameToString.toUpperCase();
    }


    private ArrayList<String> getPermutation(String str, String ans, ArrayList<String> words)
    {

        // If string is empty
        if (str.length() == 0) {

            //only add valid words
            ArrayList<Word> wordList = new ArrayList<>();
            wordList.add(stringToWord(ans));
            if(dictionary.areWords(wordList))
                words.add(ans);

            return words;
        }

        for (int i = 0; i < str.length(); i++) {
            // ith character of str
            char ch = str.charAt(i);

            // Rest of the string after excluding
            // the ith character
            String ros = str.substring(0, i) +
                    str.substring(i + 1);

            getPermutation(ros, ans + ch, words);
        }
        return words;
    }



    private Word stringToWord(String str)
    {
        return new Word(0,0,true, str);
    }

    public String placeValTile()
    {

        String coordinates ="";

        //check for tiles with value 10
        for (int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                if (board.getSquareCopy(i,j).getTile().getValue() == 10){
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }

        //check for tiles with value 8
        for (int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                if (board.getSquareCopy(i,j).getTile().getValue() == 8){
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }

        //check for tiles with value 5
        for (int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                if (board.getSquareCopy(i,j).getTile().getValue() == 5){
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }

        //check for tiles with value 4
        for (int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                if (board.getSquareCopy(i,j).getTile().getValue() == 4){
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }

        //check for tiles with value 3
        for (int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                if (board.getSquareCopy(i,j).getTile().getValue() == 3){
                    coordinates += i;
                    coordinates += " ";
                    coordinates += j;
                    return coordinates;
                }
            }
        }
        return coordinates;
    }

    private String isValidMove(String word, int col, int row)
    {
        //Find index of string where the tile from the board is located in the word
        char valTile = board.getSquareCopy(row,col).getTile().getLetter();

        int index=0;
        for(int i =0; i<word.length(); i++)
        {
            if(word.charAt(i) == valTile)
                index = i;
        }

        //Convert String word to Word object
        Word wordObj = new Word(row,col,true,word);
        Frame frameObj = new Frame();
        ArrayList<Tile> tileArray = new ArrayList<>();
        for(int i=0; i< word.length(); i++)
            tileArray.add(new Tile(word.charAt(i)));

        frameObj.addTiles(tileArray);

        if(board.isLegalPlay(frameObj,wordObj))
        {
            return col + " " + row + " " + wordObj.getLetters();
        }
        else


        return "";
    }



    private String getHighWordScore()
    {
        //Find highest scoring word
        int maxScore = 0;
        int tempIndex = 0;

        for(int i =0; i< word_score.size(); i++)
        {
            if(i == 0) maxScore = word_score.get(i);
            else
            {
                if(maxScore < word_score.get(i))
                {
                    tempIndex = i;
                    maxScore = word_score.get(i);
                }
            }
        }

        String tempWord = words.get(tempIndex);

        words.remove(tempIndex); //Remove word from array in case it cannot be placed
        return tempWord;

    }
    private boolean isChallenge(){
        if (turnCount % 4 == 0) {
            return true;
        }
        return false;
    }

}
