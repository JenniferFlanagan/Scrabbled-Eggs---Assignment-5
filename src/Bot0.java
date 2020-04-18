
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
        switch (turnCount) {
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



    public String makeBestMove()
    {
        String command = "";
        command = PlaceFirstWord();


        if(command == "")
            command = checkValuableSquare();
        else return command;
/*
        if(command == "")
            command = placeValTile();
        else
        {

        }
*/
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
        return frameToString;
    }

}
