import javafx.geometry.Orientation;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.CheckedOutputStream;

public class Main {
    public static Scanner scanner;
    public static Random rnd;
    public static char[][] guessingBoard;
    public static char[][] userBoard;
    public static char[][] computerBoard;
    public static int[] shipCount;
    public static final char FILLER_CHAR = '-';
    public static final char SPACE_CHAR = ' ';
    public static final char SHIP_CHAR = '#';
    public static final char HIT_CHAR = 'V';
    public static final char MISS_CHAR = 'X';
    public static final char HIT_SHIP_CHAR = 'X';

    /**
     * Receives from the user the size of the board and returns it.
     * @return the size of the board as an int array with a size of 2.
     */
    public static int[] getBoardSize(){
        System.out.println("Enter the board size");
        String res = scanner.nextLine();
        String parts[] = res.split("X");
        int size[] = new int[2];
        size[0]=Integer.parseInt(parts[0]);
        size[1]=Integer.parseInt(parts[1]);
        return size;
    }

    /**
     * Receives from the user the sizes of the battleships and returns them.
     * @return an int array of the sizes of the battleships.
     */
    public static int[] getBattleshipsSizes(){
        System.out.println("Enter the battleships sizes");
        String res = scanner.nextLine();
        String[] size_parts = res.split(" ");
        int battleshipsCount = 0;
        for (int i=0; i<size_parts.length; i++) {
            battleshipsCount += Integer.parseInt(size_parts[i].split("X")[0]);
        }
        int[] battleships = new int[battleshipsCount];
        int index = 0;
        for(int i = 0; i<size_parts.length;i++) {
            for(int j = 0; j< Integer.parseInt(size_parts[i].split("X")[0]); j++){
               battleships[index] = Integer.parseInt(size_parts[i].split("X")[1]);
               index++;
            }
        }
        return battleships;
    }

    /**
     * Checks if the orientation of the ship is valid.
     * @param subStr the substring that represents the orientation
     * @return true if valid, else returns false.
     */
    public static boolean checkOrientation(String subStr){
        if(subStr!="0" || subStr!="1"){
            System.out.println("Illegal orientation, try again!");
            return false;
        }
        return true;
    }

    /**
     * Checks if the tile of the placement of the ship is valid.
     * @param iX the string that represents the x coordinate of the tile.
     * @param iY the string that represents the y coordinate of the tile.
     * @return true if valid, else returns false.
     */
    public static boolean checkTile(int iX, int iY){
        if(iX<0 || iX>=userBoard[0].length || iY<0 || iY>=userBoard.length){
            System.out.println("Illegal tile, try again!");
            return false;
        }
        return true;
    }

    /**
     * Check if the placement of the boat exceeds the boundaries of the game board.
     * @param iX the x coordinate of the boat placement.
     * @param iY the y coordinate of the boat placement.
     * @param iOrientation the orientation of the boat.
     * @return true if valid, else false.
     */
    public static boolean checkBoundaries(int iX,int iY, int iOrientation, int size){
        int xEnd = iX+size*(1-iOrientation);
        int yEnd = iY+size*iOrientation;
        if(xEnd<0 || xEnd>=userBoard[0].length || yEnd<0 || yEnd>=userBoard.length){
            System.out.println("Battleship exceeds the boundaries of the board, try again!");
            return false;
        }
        return true;
    }

    /**
     * Checks if there is any overlap between the current ships and the others.
     * @param iX the x coordinate of the placement tile of the boat.
     * @param iY the y coordinate of the placement tile of the boat.
     * @param iOrientation the orientation of the placement of the boat.
     * @param size the size of the boat.
     * @param isUser is the function done for the user or the computer
     * @return true if valid, else false.
     */
    public static boolean checkOverlap(int iX, int iY, int iOrientation, int size, boolean isUser){
        for(int i=0;i<size;i++){
            int x=iX+i*(1-iOrientation);
            int y=iY+i*iOrientation;
            if(isUser)
                if(userBoard[x][y]==SHIP_CHAR)
                    return false;
            else
                if(computerBoard[x][y]==SHIP_CHAR)
                    return false;
        }
        return true;
    }

    /**
     * Checks if the boat placement is at least 1 tile apart from other boats.
     * @param iX the x coordinate of the placement tile of the boat.
     * @param iY the y coordinate of the placement tile of the boat.
     * @param iOrientation the orientation of the placement of the boat.
     * @param size the size of the boat.
     * @param isUser is the function done for the user or the computer
     * @return true if valid, else false.
     */
    public static boolean checkDistance(int iX, int iY, int iOrientation, int size, boolean isUser){
        char[][] board;
        if(isUser)
            board=userBoard;
        else
            board=computerBoard;

        for(int i=0;i<size;i++){
            int x=iX+i*(1-iOrientation);
            int y=iY+i*iOrientation;
            int i1ToCheck=-1,i2ToCheck=-1;
            if(iOrientation>0){ //if the ship is vertical
                i1ToCheck=x+1;
                i2ToCheck=x-1;
                if(!checkTile(i1ToCheck,y) || !checkTile(i2ToCheck,y) || board[i1ToCheck][y]==SHIP_CHAR ||
                        board[i2ToCheck][y]==SHIP_CHAR)
                    return false;
            }
            else{ //if the ship is horizontal
                i1ToCheck=y+1;
                i2ToCheck=y-1;
                if(!checkTile(x,i1ToCheck) || !checkTile(x,i2ToCheck) || board[x][i1ToCheck]==SHIP_CHAR ||
                        board[x][i2ToCheck]==SHIP_CHAR)
                    return false;
            }
        }
        return true;
    }

    /**
     * Marks a boat with '#' on the board.
     * @param iX the x coordinate of the placement tile of the boat.
     * @param iY the y coordinate of the placement tile of the boat.
     * @param iOrientation the orientation of the placement of the boat.
     * @param size the size of the boat.
     * @param isUser is the function done for the user or the computer
     */
    public static void putInBoard(int iX, int iY, int iOrientation, int size, boolean isUser){
        for(int i=0; i<size; i++){
            int x=iX+i*(1-iOrientation);
            int y=iY+i*iOrientation;
            if(isUser)
                userBoard[x][y]=SHIP_CHAR;
            else
                computerBoard[x][y]=SHIP_CHAR;
        }
    }

    /**
     * Returns if the boat placement is valid, and places the boat on the relevant board if it is.
     * @param size the size of the boat.
     * @param isUser is the function done for the user or the computer
     * @return true if the boat placement is valid, else returns false.
     */
    public static boolean placeBoat(int size, boolean isUser){
        int iX,iY;
        String orientation;

        if(isUser) {
            System.out.println(String.format("Enter location and orientation for battleship of size %d", size));
            String[] parts = scanner.nextLine().split(", ");
            iX = Integer.parseInt(parts[0]);
            iY = Integer.parseInt(parts[1]);
            orientation = parts[2];
        }
        else{
            iX = rnd.nextInt(computerBoard.length);
            iY = rnd.nextInt(computerBoard[0].length);
            orientation = Integer.toString(rnd.nextInt(2));
        }

        if(checkOrientation(orientation)){
            int iOrientation=Integer.parseInt(orientation);
            if(checkTile(iX,iY) && checkBoundaries(iX,iY,iOrientation,size) &&
                    checkOverlap(iX,iY,iOrientation,size,isUser) && checkDistance(iX,iY,iOrientation,size,isUser)) {
                putInBoard(iX,iY,iOrientation,size,isUser);
                return true;
            }
        }
        return false;
    }

    /**
     * Places the boats on the board.
     * @param battleshipsSizes the array of the battleships sizes.
     * @param isUser is the function done for the user or the computer
     */
    public static void placeBoats(int[] battleshipsSizes, boolean isUser){
        for(int i=0; i<battleshipsSizes.length; i++){
            while(!placeBoat(battleshipsSizes[i], isUser));
            if(isUser)
                printBoard(userBoard);
        }
    }

    /**
     * fills the empty board with filler chars.
     * @param board Empty board
     */
    public static void initializeBoard(char[][] board){
        for(int i = 0; i < board.length; i++)
            for(int j = 0; j < board.length; j++)
                board[i][j] = FILLER_CHAR;
    }

    /**
     * repeats a string an amount of times
     * @param str
     * @param times the amount of times
     * @return the new repeated string
     */
    public static String repeatString(String str, int times){
        String output = "";
        for(int i = 0; i < times; i++){
            output += str;
        }
        return output;
    }

    /**
     * prints the board
     * @param board
     */
    public static void printBoard(char[][] board) {
        int nLen = String.valueOf(board.length).length();
        String output = repeatString(String.valueOf(SPACE_CHAR), nLen);
        for (int i = 0; i < board[0].length; i++) {
            output += " " + i;
        }
        output += "\n";
        for (int i = 0; i < board.length; i++) {
            output += repeatString(String.valueOf(SPACE_CHAR), nLen - String.valueOf(i).length());
            output += String.valueOf(i);
            for (int j = 0; j < board[0].length; j++) {
                output += " " + board[i][j];
            }
            output += "\n";
        }
    }

    /**
     * Complete User turn
     * @param computerShipCount the amount of the computer's ships still standing
     * @return if the user has sunken a ship or not
     */
    public static boolean UserAttack(int computerShipCount){
        System.out.println("Your current guessing board:");
        printBoard(guessingBoard);
        int[] attackCords = new int[2];
        do {
            attackCords = attackInput();
        } while(!Attack(attackCords));

        if(guessingBoard[attackCords[0]][attackCords[1]] == HIT_CHAR && isShipSunken(computerBoard,attackCords)){
            System.out.println("The computer's battleship has been drowned, "
                    + (computerShipCount - 1) + " more battleships to go!");
            return true;
        }
        return false;
    }

    /**
     * checks if a ship was sunk this attack
     * @param enemyBoard the enemy's board
     * @param attackCords
     * @return true if a ship has sunk this attack, false otherwise
     */
    public static boolean isShipSunken(char[][] enemyBoard,int[] attackCords){
        if(enemyBoard[attackCords[0] + 1][attackCords[1]] != SHIP_CHAR ||
            enemyBoard[attackCords[0] - 1][attackCords[1]] != SHIP_CHAR ||
            enemyBoard[attackCords[0]][attackCords[1] + 1] != SHIP_CHAR ||
            enemyBoard[attackCords[0]][attackCords[1] - 1] != SHIP_CHAR){
            return true;
        }
        return false;
    }


    /**
     * gets user attack Cords in the format of "x, y"
     * @return user's attack cords
     */
    public static int[] attackInput() {
        System.out.println("Enter a tile to attack");
        String[] res = scanner.nextLine().split(", ");
        return new int[]{Integer.parseInt(res[0]), Integer.parseInt(res[1])};
    }

    /**
     * executes the attack, returns false if the attack is illegal
     * @param attackCords the user's attack cords
     * @return if the attack was legal or not
     */
    public static boolean Attack (int[] attackCords){
        if(0 > attackCords[0] || attackCords[0] >= guessingBoard.length ||
                0 > attackCords[1] || attackCords[1] >= guessingBoard[0].length){
            System.out.println("Illegal tile, try again!");
            return false;
        }
        if(guessingBoard[attackCords[0]][attackCords[1]] != FILLER_CHAR){
            System.out.println("Tile already attacked, try again!");
            return false;
        }
        if(computerBoard[attackCords[0]][attackCords[1]] == SHIP_CHAR){
            System.out.println("That is a hit!");
            computerBoard[attackCords[0]][attackCords[1]] = HIT_SHIP_CHAR;
            guessingBoard[attackCords[0]][attackCords[1]] = HIT_CHAR;
        }
        else{
            System.out.println("That is a miss!");
            guessingBoard[attackCords[0]][attackCords[1]] = MISS_CHAR;
        }
        return true;
    }

    public static boolean computerAttack(int userShipCount){
        int[] attackCords = new int[2];
        do {
            attackCords = attackInput();
        } while(!Attack(attackCords));

        if(guessingBoard[attackCords[0]][attackCords[1]] == HIT_CHAR && isShipSunken(computerBoard,attackCords)){
            System.out.println("The computer's battleship has been drowned, "
                    + (userShipCount - 1) + " more battleships to go!");
            return true;
        }
        return false;
    }

    public static int Turn(){ //0 if no one won, 1 if user won, -1 if computer won
        if(UserAttack(shipCount[1]))
            shipCount[1]--;
        // TODO: continue the function
    }

    public static void battleshipGame() {
        // TODO: Add your code here (and add more methods).
        int[] boardSize = getBoardSize(); //get the size of the board
        int[] battleshipsSizes = getBattleshipsSizes(); //get the sizes of the battleships

        placeBoats(battleshipsSizes,true); //place the boats of the user
        placeBoats(battleshipsSizes,false); //place the boats of the computer

        while(true){
            int res=Turn();
            if(res>0){
                System.out.println("You won the game!");
            } else if (res<0) {
                System.out.println("You lost ):");
            }
        }

        //TODO: continue the function

        // user attack:
        // print your current guessing board
        // enter a tile to attack
        //     enter input while checking validity
        // if missed print this is a miss
        // if hit print hit
        //     if sunk print sunk
        // update user guessing board

        // computer attack:
        // random tile to attack
        //     random input while checking validity
        // if missed print this is a miss
        // if hit print hit
        //     if sunk print sunk
        // update computer guessing board

        // print user game board

        // if all user boats or all computer boats sunk
        //     end game
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