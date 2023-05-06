import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    public static Random rnd;
    public static char[][] guessingBoard;
    public static char[][] userBoard;
    public static char[][] computerBoard;
    public static char[][] computerGuessingBoard;
    public static int[] shipCount;
    public static final char FILLER_CHAR = '–';
    public static final char SPACE_CHAR = ' ';
    public static final char SHIP_CHAR = '#';
    public static final char HIT_CHAR = 'V';
    public static final char MISS_CHAR = 'X';
    public static final char HIT_SHIP_CHAR = 'X';

    /**
     * Receives from the user the size of the board and returns it.
     *
     * @return the size of the board as an int array with a size of 2.
     */
    public static int[] getBoardSize() {
        System.out.println("Enter the board size");
        String res = scanner.nextLine();
        String[] parts = res.split("X");
        int[] size = new int[2];
        size[0] = Integer.parseInt(parts[0]);
        size[1] = Integer.parseInt(parts[1]);
        return size;
    }

    /**
     * creates all the boards and fills them with filler characters.
     *
     * @param boardSize the size of the boards.
     */
    public static void initializeBoards(int[] boardSize) {
        guessingBoard = new char[boardSize[0]][boardSize[1]];
        computerGuessingBoard = new char[boardSize[0]][boardSize[1]];
        userBoard = new char[boardSize[0]][boardSize[1]];
        computerBoard = new char[boardSize[0]][boardSize[1]];
        initializeBoard(guessingBoard);
        initializeBoard(computerGuessingBoard);
        initializeBoard(userBoard);
        initializeBoard(computerBoard);
    }

    /**
     * Receives from the user the sizes of the battleships and returns them.
     *
     * @return an int array of the sizes of the battleships.
     */
    public static int[] getBattleshipsSizes() {
        System.out.println("Enter the battleships sizes");
        String res = scanner.nextLine();
        String[] size_parts = res.split(" ");
        int battleshipsCount = 0;
        for (int i = 0; i < size_parts.length; i++) {
            battleshipsCount += Integer.parseInt(size_parts[i].split("X")[0]);
        }
        int[] battleships = new int[battleshipsCount];
        int index = 0;
        for (int i = 0; i < size_parts.length; i++) {
            for (int j = 0; j < Integer.parseInt(size_parts[i].split("X")[0]); j++) {
                battleships[index] = Integer.parseInt(size_parts[i].split("X")[1]);
                index++;
            }
        }
        return battleships;
    }

    /**
     * Checks if the orientation of the ship is valid.
     *
     * @param subStr the substring that represents the orientation.
     * @return true if valid, else returns false.
     */
    public static boolean checkOrientation(String subStr) {
        if (!subStr.equals("0") && !subStr.equals("1")) {
            System.out.println("Illegal orientation, try again!");
            return false;
        }
        return true;
    }

    /**
     * Checks if the tile of the placement of the ship is valid.
     *
     * @param x      the x coordinate of the tile.
     * @param y      the y coordinate of the tile.
     * @param isUser "true" if the check is for the user and "false" for the computer
     * @return true if valid, else returns false.
     */
    public static boolean checkTile(int x, int y, boolean isUser) {
        if (x < 0 || x >= userBoard.length || y < 0 || y >= userBoard[0].length) {
            if (isUser) System.out.println("Illegal tile, try again!");
            return false;
        }
        return true;
    }

    /**
     * Check if the placement of the boat exceeds the boundaries of the game board.
     *
     * @param x           the x coordinate of the boat placement.
     * @param y           the y coordinate of the boat placement.
     * @param orientation the orientation of the boat.
     * @param size        the size of the boat.
     * @param isUser      "true" if the check is for the user and "false" for the computer
     * @return true if valid, else false.
     */
    public static boolean checkBoundaries(int x, int y, int orientation, int size, boolean isUser) {
        int xEnd = (orientation == 0) ? x : ((x + size) - 1);
        int yEnd = (orientation == 1) ? y : ((y + size) - 1);
        if (xEnd < 0 || xEnd >= userBoard.length || yEnd < 0 || yEnd >= userBoard[0].length) {
            if (isUser) System.out.println("Battleship exceeds the boundaries of the board, try again!");
            return false;
        }
        return true;
    }

    /**
     * Checks if there is any overlap between the current ships and the others.
     *
     * @param x           the x coordinate of the placement tile of the boat.
     * @param y           the y coordinate of the placement tile of the boat.
     * @param orientation the orientation of the placement of the boat.
     * @param size        the size of the boat.
     * @param isUser      is the function done for the user or the computer?
     * @return true if valid, else false.
     */
    public static boolean checkOverlap(int x, int y, int orientation, int size, boolean isUser) {
        for (int i = 0; i < size; i++) {
            int iX = x + i * orientation;
            int iY = y + i * (1 - orientation);
            if (isUser) {
                if (userBoard[iX][iY] == SHIP_CHAR) {
                    System.out.println("Battleship overlaps another battleship, try again!");
                    return false;
                }
            } else {
                if (computerBoard[iX][iY] == SHIP_CHAR)
                    return false;
            }
        }
        return true;
    }

    /**
     * Checks if the boat placement is at least 1 tile apart from other boats.
     *
     * @param x           the x coordinate of the placement tile of the boat.
     * @param y           the y coordinate of the placement tile of the boat.
     * @param orientation the orientation of the placement of the boat.
     * @param size        the size of the boat.
     * @param isUser      is the function done for the user or the computer?
     * @return true if valid, else false.
     */
    public static boolean checkDistance(int x, int y, int orientation, int size, boolean isUser) {
        char[][] board;
        if (isUser)
            board = userBoard;
        else
            board = computerBoard;

        for (int i = 0; i <= size; i++) { //TODO: check code duplication
            int iX = x + i * orientation;
            int iY = y + i * (1 - orientation);
            int i1ToCheck, i2ToCheck;
            if (orientation == 1) { //if the ship is vertical
                i1ToCheck = iY + 1;
                i2ToCheck = iY - 1;
                if ((!checkTile(iX, iY, false) || board[iX][iY] == FILLER_CHAR)
                        && (!checkTile(iX, i1ToCheck, false) || board[iX][i1ToCheck] == FILLER_CHAR)
                        && (!checkTile(iX, i2ToCheck, false) || board[iX][i2ToCheck] == FILLER_CHAR)) {
                    //if adjacent tiles are legal
                    continue;
                }
            } else { //if the ship is horizontal
                i1ToCheck = iX + 1;
                i2ToCheck = iX - 1;
                if ((!checkTile(iX, iY, false) || board[iX][iY] == FILLER_CHAR)
                        && (!checkTile(i1ToCheck, iY, false) || board[i1ToCheck][iY] == FILLER_CHAR)
                        && (!checkTile(i2ToCheck, iY, false) || board[i2ToCheck][iY] == FILLER_CHAR)) {
                    //if adjacent tiles are legal
                    continue;
                }
            }
            if (isUser) System.out.println("Adjacent battleship detected, try again!");
            return false;
        }
        return true;
    }

    /**
     * Marks a boat with '#' on the board.
     *
     * @param x           the x coordinate of the placement tile of the boat.
     * @param y           the y coordinate of the placement tile of the boat.
     * @param orientation the orientation of the placement of the boat.
     * @param size        the size of the boat.
     * @param isUser      is the function done for the user or the computer?
     */
    public static void putInBoard(int x, int y, int orientation, int size, boolean isUser) {
        for (int i = 0; i < size; i++) {
            int iX = x + i * orientation;
            int iY = y + i * (1 - orientation);
            if (isUser)
                userBoard[iX][iY] = SHIP_CHAR;
            else
                computerBoard[iX][iY] = SHIP_CHAR;
        }
    }

    /**
     * Returns if the boat placement is valid, and places the boat on the relevant board if it is.
     *
     * @param size   the size of the boat.
     * @param isUser is the function done for the user or the computer?
     * @return true if the boat placement is valid, else returns false.
     */
    public static boolean placeBoat(int size, boolean isUser) {
        int iX, iY;
        String orientation;

        if (isUser) {
            String[] parts = scanner.nextLine().split(", ");
            iX = Integer.parseInt(parts[0]);
            iY = Integer.parseInt(parts[1]);
            orientation = parts[2];
        } else {
            iX = rnd.nextInt(computerBoard.length);
            iY = rnd.nextInt(computerBoard[0].length);
            orientation = Integer.toString(rnd.nextInt(2));
        }

        if (checkOrientation(orientation)) {
            int iOrientation = Integer.parseInt(orientation);
            if (checkTile(iX, iY, isUser) && checkBoundaries(iX, iY, iOrientation, size, isUser) &&
                    checkOverlap(iX, iY, iOrientation, size, isUser) && checkDistance(iX, iY, iOrientation, size, isUser)) {
                putInBoard(iX, iY, iOrientation, size, isUser);
                return true;
            }
        }
        return false;
    }

    /**
     * Places the boats on the board.
     *
     * @param battleshipsSizes the array of the battleships sizes.
     * @param isUser           is the function done for the user or the computer?
     */
    public static void placeBoats(int[] battleshipsSizes, boolean isUser) {
        for (int i = 0; i < battleshipsSizes.length; i++) {
            if (isUser) {
                System.out.printf("Enter location and orientation for battleship of size %d%n"
                        , battleshipsSizes[i]);
            }
            while (!placeBoat(battleshipsSizes[i], isUser)) ;
            if (isUser) {
                System.out.println("Your current game board:");
                printBoard(userBoard);
            }
        }
    }

    /**
     * fills the empty board with filler chars.
     *
     * @param board Empty board.
     */
    public static void initializeBoard(char[][] board) {
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                board[i][j] = FILLER_CHAR;
    }

    /**
     * repeats a string an amount of times.
     *
     * @param str   the string to be repeated.
     * @param times the amount of times.
     * @return the new repeated string.
     */
    public static String repeatString(String str, int times) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < times; i++) {
            output.append(str);
        }
        return output.toString();
    }

    /**
     * prints the board.
     *
     * @param board the board to print.
     */
    public static void printBoard(char[][] board) {
        int nLen = String.valueOf(board.length).length();
        StringBuilder output = new StringBuilder(repeatString(String.valueOf(SPACE_CHAR), nLen));
        for (int i = 0; i < board[0].length; i++) {
            output.append(" ").append(i);
        }
        output.append("\n");
        for (int i = 0; i < board.length; i++) {
            output.append(repeatString(String.valueOf(SPACE_CHAR), nLen - String.valueOf(i).length()));
            output.append(i);
            for (int j = 0; j < board[0].length; j++) {
                output.append(" ").append(board[i][j]);
            }
            output.append("\n");
        }
        System.out.println(output);
    }

    /**
     * Complete user/computer turn.
     *
     * @param isUser true if it's the user's turn, false if it's the computers.
     * @return if the user has sunken a ship or not.
     */
    public static boolean attack(boolean isUser) {
        if (isUser) {
            System.out.println("Your current guessing board:");
            printBoard(guessingBoard);
        }
        int[] attackCords;
        if (isUser) System.out.println("Enter a tile to attack");
        do {
            attackCords = attackInput(isUser);
        } while (!executeAttack(attackCords, isUser));
        if (isUser) {
            if (guessingBoard[attackCords[0]][attackCords[1]] == HIT_CHAR
                    && isShipSunken(computerBoard, attackCords[0], attackCords[1])) {
                System.out.println("The computer's battleship has been drowned, "
                        + (shipCount[1] - 1) + " more battleships to go!");
                return true;
            }
            return false;
        } else {
            if (computerGuessingBoard[attackCords[0]][attackCords[1]] == HIT_CHAR
                    && isShipSunken(userBoard, attackCords[0], attackCords[1])) {
                System.out.println("Your battleship has been drowned, you have left "
                        + (shipCount[0] - 1) + " more battleships!");
                return true;
            }
            return false;
        }

    }

    /**
     * checks if a ship was sunk this attack.
     *
     * @param enemyBoard   the enemy's board.
     * @param xAttackCords the X coordinate of the attack.
     * @param yAttackCords the Y coordinate of the attack.
     * @return true if a ship has sunk this attack, false otherwise.
     */
    public static boolean isShipSunken(char[][] enemyBoard, int xAttackCords, int yAttackCords) {
        if (isElementShip(enemyBoard, xAttackCords + 1, yAttackCords)
                || isElementShip(enemyBoard, xAttackCords - 1, yAttackCords)
                || isElementShip(enemyBoard, xAttackCords, yAttackCords + 1)
                || isElementShip(enemyBoard, xAttackCords, yAttackCords - 1)) {
            return false;
        }
        if (isElementHitShip(enemyBoard, xAttackCords + 1, yAttackCords)) {
            enemyBoard[xAttackCords][yAttackCords] = '+';
            if (!isShipSunken(enemyBoard, xAttackCords + 1, yAttackCords)) {
                enemyBoard[xAttackCords][yAttackCords] = HIT_SHIP_CHAR;
                return false;
            }
        }
        if (isElementHitShip(enemyBoard, xAttackCords - 1, yAttackCords)) {
            enemyBoard[xAttackCords][yAttackCords] = '+';
            if (!isShipSunken(enemyBoard, xAttackCords - 1, yAttackCords)) {
                enemyBoard[xAttackCords][yAttackCords] = HIT_SHIP_CHAR;
                return false;
            }
        }
        if (isElementHitShip(enemyBoard, xAttackCords, yAttackCords + 1)) {
            enemyBoard[xAttackCords][yAttackCords] = '+';
            if (!isShipSunken(enemyBoard, xAttackCords, yAttackCords + 1)) {
                enemyBoard[xAttackCords][yAttackCords] = HIT_SHIP_CHAR;
                return false;
            }
        }
        if (isElementHitShip(enemyBoard, xAttackCords, yAttackCords - 1)) {
            enemyBoard[xAttackCords][yAttackCords] = '+';
            if (!isShipSunken(enemyBoard, xAttackCords, yAttackCords - 1)) {
                enemyBoard[xAttackCords][yAttackCords] = HIT_SHIP_CHAR;
                return false;
            }
        }
        enemyBoard[xAttackCords][yAttackCords] = HIT_SHIP_CHAR;
        return true;
    }

    /**
     * Checks if an element in a gameBoard is a ship.
     *
     * @param gameBoard the gameBoard in question.
     * @param x         the x coordinate of the element.
     * @param y         the y coordinate of the element.
     * @return returns true if the element is a ship, false otherwise.
     */
    public static boolean isElementShip(char[][] gameBoard, int x, int y) {
        return checkTile(x, y, false) && gameBoard[x][y] == SHIP_CHAR;
    }

    /**
     * Checks if an element in a gameBoard is a hit ship.
     *
     * @param gameBoard the gameBoard in question.
     * @param x         the x coordinate of the element.
     * @param y         the y coordinate of the element.
     * @return returns true if the element is a hit ship, false otherwise.
     */
    public static boolean isElementHitShip(char[][] gameBoard, int x, int y) {
        return checkTile(x, y, false) && gameBoard[x][y] == HIT_SHIP_CHAR;
    }

    /**
     * gets user attack Cords in the format of "x, y".
     *
     * @param isUser is the function done for the user or the computer?
     * @return user's attack cords.
     */
    public static int[] attackInput(boolean isUser) {
        if (!isUser) {
            return new int[]{rnd.nextInt(guessingBoard.length), rnd.nextInt(guessingBoard[0].length)};
        }
        String[] res = scanner.nextLine().split(", ");
        return new int[]{Integer.parseInt(res[0]), Integer.parseInt(res[1])};
    }


    /**
     * executes the attack, returns false if the attack is illegal.
     *
     * @param attackCords the user's attack cords.
     * @param isUser      true if user's turn, false otherwise.
     * @return if the attack was legal or not.
     */
    public static boolean executeAttack(int[] attackCords, boolean isUser) {
        if (!checkTile(attackCords[0], attackCords[1], isUser))
            return false;
        if (isUser) {
            if (guessingBoard[attackCords[0]][attackCords[1]] != FILLER_CHAR) {
                System.out.println("Tile already attacked, try again!");
                return false;
            }

            if (computerBoard[attackCords[0]][attackCords[1]] == SHIP_CHAR) {
                System.out.println("That is a hit!");
                computerBoard[attackCords[0]][attackCords[1]] = HIT_SHIP_CHAR;
                guessingBoard[attackCords[0]][attackCords[1]] = HIT_CHAR;
            } else {
                System.out.println("That is a miss!");
                guessingBoard[attackCords[0]][attackCords[1]] = MISS_CHAR;
            }
        } else {
            if (computerGuessingBoard[attackCords[0]][attackCords[1]] != FILLER_CHAR) {
                return false;
            }

            System.out.printf("The computer attacked (%d, %d)%n", attackCords[0], attackCords[1]);
            if (userBoard[attackCords[0]][attackCords[1]] == SHIP_CHAR) {
                System.out.println("That is a hit!");
                userBoard[attackCords[0]][attackCords[1]] = HIT_SHIP_CHAR;
                computerGuessingBoard[attackCords[0]][attackCords[1]] = HIT_CHAR;
            } else {
                System.out.println("That is a miss!");
                computerGuessingBoard[attackCords[0]][attackCords[1]] = MISS_CHAR;
            }
        }
        return true;
    }

    /**
     * completes a Turn: a user attack followed by a computer attack.
     *
     * @return 0 if no one won, 1 if user won, -1 if computer won.
     */
    public static int Turn() {
        if (attack(true)) {
            shipCount[1]--;
            if (shipCount[1] == 0) {
                return 1;
            }
        }

        if (attack(false)) {
            shipCount[0]--;
            if (shipCount[0] == 0) {
                System.out.println("Your current game board:");
                printBoard(userBoard);
                return -1;
            }
        }

        System.out.println("Your current game board:");
        printBoard(userBoard);
        return 0;
    }

    /**
     * The shell function of the game.
     */
    public static void battleshipGame() {
        int[] boardSize = getBoardSize(); //get the size of the board
        int[] battleshipsSizes = getBattleshipsSizes(); //get the sizes of the battleships
        shipCount = new int[]{battleshipsSizes.length, battleshipsSizes.length};

        initializeBoards(boardSize);

        System.out.println("Your current game board:");
        printBoard(userBoard);

        placeBoats(battleshipsSizes, true); //place the boats of the user
        placeBoats(battleshipsSizes, false); //place the boats of the computer

        int res;
        do {
            res = Turn();
            if (res > 0) {
                System.out.println("You won the game!");
            } else if (res < 0) {
                System.out.println("You lost ):");
            }
        } while (res == 0);
    }


    public static void main(String[] args) throws IOException {
        String path = args[0];
        scanner = new Scanner(new File(path));
        int numberOfGames = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Total of " + numberOfGames + " games.");

        for (int i = 1; i <= numberOfGames; i++) {
            scanner.nextLine();
            int seed = scanner.nextInt();
            rnd = new Random(seed);
            scanner.nextLine();
            System.out.println("Game number " + i + " starts.");
            battleshipGame();
            System.out.println("Game number " + i + " is over.");
            System.out.println("------------------------------------------------------------");
        }
        System.out.println("All games are over.");
    }
}