package com.example.tim.chess;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /**
     *
     * @author Tim Gassaway, Nick Prezioso
     *
     */

    public static GridView gridview;
    public static Piece[][] board = new Piece[8][8];
    public static Piece[][] undoBoard = new Piece[8][8];

    public static int turnCounter = 0;
    public static String input;
    public static ArrayList<Integer[]> replay;
    String repName = "";
    boolean check;
    boolean checkmate;
    boolean stalemate;
    boolean currentPlayer;  //true=white
    boolean drawRequested;

    boolean targeting;

    int sourceX;
    int sourceY;
    int targetX;
    int targetY;

    int pX;
    int pY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isTaskRoot()){
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart(){
        super.onStart();
        initBoard();
    }

    @Override
    protected void onResume(){
        super.onResume();
        try {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"tempboard");
            if(f.exists()){
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                board = (Piece[][])ois.readObject();
                ois.close();
                fis.close();
                String choice = getIntent().getStringExtra("choice");
                //String choice = data.getStringExtra("choice");
                char color = (pY==7)?'w':'b';
                switch(choice){
                    case "queen":{
                        board[pY][pX] = new Queen(pY, pX, color, true);
                    }
                    case "knight":{
                        board[pY][pX] = new Knight(pY, pX, color, true);
                    }
                    case "rook":{
                        board[pY][pX] = new Rook(pY, pX, color, true);
                    }
                    case "bishop":{
                        board[pY][pX] = new Bishop(pY, pX, color, true);
                    }
                }
                printBoard();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void draw(View v){
        if (drawRequested){
            endGame();
            return;
        }
        drawRequested = !drawRequested;

    }

    public void AI(View v){
        if(checkmate || stalemate)
            return;

        char color = currentPlayer?'w':'b';
        ArrayList<Piece> pieces = new ArrayList<Piece>();
        ArrayList<Integer[]> legalMoves = new ArrayList<Integer[]>();
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(board[i][j].color==color)
                    pieces.add(board[i][j]);
            }
        }

        for(Piece p: pieces){
            for(int i=0; i<8; i++){
                for(int j=0; j<8; j++){
                    if(p.canMove(i, j)){
                        Piece[][] tempBoard = copyBoard(board);
                        move(p, board[j][i]);
                        if(!isCheck(!currentPlayer)){
                            Integer[] temp = {p.x, p.y, i, j};
                            legalMoves.add(temp);
                        }
                        turnCounter--;
                        board = tempBoard;
                    }
                }
            }
        }

        Integer[] choice = legalMoves.get((int)(Math.random()*legalMoves.size()));
        undoBoard = copyBoard(board);
        move(board[choice[1]][choice[0]], board[choice[3]][choice[2]]);
        replay.add(choice);
        drawRequested=false;

        printBoard();
        check = isCheck(currentPlayer);
        checkmate = isCheckmate(currentPlayer);
        stalemate = isStalemate(currentPlayer);
        currentPlayer = !currentPlayer;
        if (checkmate || stalemate) {
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                }
            });
            endGame();
        }
    }

    public void resign(View v){
        //update text field
        endGame();
    }

    public void replay(View v){
        //select replay



        try
        {
            FileInputStream fis = new FileInputStream(repName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            replay = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
    }

    public void undo(View v){
        if(turnCounter == 0){
            return;
        }
        if(board != undoBoard) {
            turnCounter--;
            currentPlayer = !currentPlayer;
            board = undoBoard;
            printBoard();
        }
    }

    public void endGame(){
        Intent intent = new Intent(this, SaveReplayActivity.class);
        startActivityForResult(intent, 1);
        initBoard();
    }

    /**
     * Promotes a pawn to the chosen piece based on input.
     * <p>
     * Defaults to queen if unspecified.
     * @param p the piece to be promoted
     */
    public void promote(Piece p){
        pX = p.x;
        pY = p.y;

        FileOutputStream fos= null;
        try {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"tempboard");
            f.createNewFile();
            fos = new FileOutputStream(f);
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(board);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this, PromoteActivity.class);
        intent.putExtra("color", p.color+"");
        startActivityForResult(intent, 2);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1) {
            if(resultCode==RESULT_OK) {
                repName = data.getStringExtra("repname");
                try{
                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+repName);
                    FileOutputStream fos= new FileOutputStream(f);
                    ObjectOutputStream oos= new ObjectOutputStream(fos);
                    oos.writeObject(replay);
                    oos.close();
                    fos.close();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
        else if(requestCode==2){
            if(resultCode==RESULT_OK){
                try {
                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"tempboard");
                    FileInputStream fis = new FileInputStream(f);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    board = (Piece[][])ois.readObject();
                    ois.close();
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                String choice = data.getStringExtra("choice");
                char color = (pY==7)?'w':'b';
                switch(choice){
                    case "queen":{
                        board[pY][pX] = new Queen(pY, pX, color, true);
                    }
                    case "knight":{
                        board[pY][pX] = new Knight(pY, pX, color, true);
                    }
                    case "rook":{
                        board[pY][pX] = new Rook(pY, pX, color, true);
                    }
                    case "bishop":{
                        board[pY][pX] = new Bishop(pY, pX, color, true);
                    }
                }
                printBoard();
            }
        }
    }


    /**
     * Checks to see if a space is being threatened by an opposing piece.
     * <p>
     * "Threatened" here means that an opposing piece can capture a piece
     * at this location in the current player's possession on the next turn.
     * @param x - x-coordinate of space to be checked.
     * @param y - y-coordinate of space to be checked.
     * @param color - color of the opposing pieces that can threaten the space.
     * @return true if space is being threatened, false if not.
     */
    public static boolean threatened(int x, int y, char color){
        for(int i = 0; i<=7; i++){
            for (int j = 0; j<=7; j++){
                Piece test = board[i][j];
                if(!board[y][x].isBlank() && test.color != color){
                    continue;
                }
                else if(test instanceof King){
                    if(test.color==color && Math.abs(test.x-x)<=1 && Math.abs(test.y-y)<=1)
                        return true;
                }
                else if(test.color==color && test.canMove(x, y)){
                    if(test instanceof Pawn){
                        if(((Pawn) test).x == x){
                            continue;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks to see if a space is being threatened by an opposing piece that isn't a king.
     * <p>
     * "Threatened" here means that an opposing piece can capture a piece
     * at this location in the current player's possession on the next turn.
     * @param x - x-coordinate of space to be checked.
     * @param y - y-coordinate of space to be checked.
     * @param color - color of the opposing pieces that can threaten the space.
     * @return true if space is being threatened, false if not.
     */
    public boolean threatenedByNonKings(int x, int y, char color){
        for(int i = 0; i<=7; i++){
            for (int j = 0; j<=7; j++){
                Piece test = board[i][j];
                if(!board[y][x].isBlank() && test.color != color){
                    continue;
                }
                else if(test instanceof King){
                    continue;
                }
                else if(test.color==color && test.canMove(x, y)){
                    Piece[][] temp = copyBoard(board);
                    move(test, board[y][x]);
                    boolean b = color!='w';
                    if(isCheck(b)){
                        board = temp;
                        turnCounter--;
                        return false;
                    }
                    board=temp;
                    turnCounter--;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a move by the player put the opponent in checkmate
     * @param player - the player who moved the piece in question
     * @return whether the player has put the opponent in checkmate
     */
    public boolean isCheckmate(boolean player){
        if(!isCheck(player))
            return false;
        //king cannot move
        char enemyColor = player ? 'w':'b';
        char color = player ? 'b':'w' ;
        Piece k = new Piece(0, 0, 'w', false);	//temporary value
        boolean b = false;
        for (Piece[] row : board){
            for (Piece p : row){
                if(p.color == color && p instanceof King){
                    k = (King)p;
                    boolean canMove =
                            k.canMove(k.x, k.y+1)	||
                                    k.canMove(k.x+1, k.y)	||
                                    k.canMove(k.x+1, k.y+1)	||
                                    k.canMove(k.x, k.y-1)	||
                                    k.canMove(k.x-1, k.y)	||
                                    k.canMove(k.x-1, k.y-1)	||
                                    k.canMove(k.x-1, k.y+1)	||
                                    k.canMove(k.x+1, k.y-1);
                    if(canMove){
                        return false;
                    }
                    b=true;
                    break;
                }
            }
            if(b)
                break;
        }
        //cannot capture or block
        //get threatening piece
        b = false;
        for (Piece[] row : board){
            for (Piece p : row){
                if(p.color == enemyColor && p.canMove(k.x, k.y)){
                    //get all spaces through which it threatens king (including its own space)
                    ArrayList<Integer> xList = new ArrayList<Integer>();
                    ArrayList<Integer> yList = new ArrayList<Integer>();
                    if(p instanceof Queen){
                        //cardinal
                        if((p.x!=k.x && p.y==k.y) || (p.x==k.x && p.y!=k.y)){
                            //horizontal
                            if(p.x!=k.x){
                                //on left
                                if(p.x<k.x){
                                    for(int i=p.x; i<k.x; i++){
                                        xList.add(i);
                                        yList.add(p.y);
                                    }
                                }
                                //on right
                                else{
                                    for(int i=p.x; i>k.x; i--){
                                        xList.add(i);
                                        yList.add(p.y);
                                    }
                                }
                            }
                            //vertical
                            else{
                                //below
                                if(p.y<k.y){
                                    for(int i=p.y; i<k.y; i++){
                                        xList.add(p.x);
                                        yList.add(i);
                                    }
                                }
                                //above
                                else{
                                    for(int i=p.y; i>k.y; i--){
                                        xList.add(p.x);
                                        yList.add(i);
                                    }
                                }
                            }
                        }
                        //diagonal
                        else{
                            //above on left
                            if(p.x<k.x && p.y>k.y){
                                for(int x=p.x, y=p.y; x<k.x && y>k.y; ){
                                    xList.add(x);
                                    yList.add(y);
                                    x++;
                                    y--;
                                }
                            }
                            //above on right
                            else if(p.x>k.x && p.y>k.y){
                                for(int x=p.x, y=p.y; x>k.x && y>k.y; ){
                                    xList.add(x);
                                    yList.add(y);
                                    x--;
                                    y--;
                                }
                            }
                            //below on left
                            else if(p.x<k.x && p.y<k.y){
                                for(int x=p.x, y=p.y; x<k.x && y<k.y; ){
                                    xList.add(x);
                                    yList.add(y);
                                    x++;
                                    y++;
                                }
                            }
                            //below on right
                            else{
                                for(int x=p.x, y=p.y; x>k.x && y<k.y; ){
                                    xList.add(x);
                                    yList.add(y);
                                    x--;
                                    y++;
                                }
                            }
                        }
                    }
                    else if(p instanceof Bishop){
                        //above on left
                        if(p.x<k.x && p.y>k.y){
                            for(int x=p.x, y=p.y; x<k.x && y>k.y; ){
                                xList.add(x);
                                yList.add(y);
                                x++;
                                y--;
                            }
                        }
                        //above on right
                        else if(p.x>k.x && p.y>k.y){
                            for(int x=p.x, y=p.y; x>k.x && y>k.y; ){
                                xList.add(x);
                                yList.add(y);
                                x--;
                                y--;
                            }
                        }
                        //below on left
                        else if(p.x<k.x && p.y<k.y){
                            for(int x=p.x, y=p.y; x<k.x && y<k.y; ){
                                xList.add(x);
                                yList.add(y);
                                x++;
                                y++;
                            }
                        }
                        //below on right
                        else{
                            for(int x=p.x, y=p.y; x>k.x && y<k.y; ){
                                xList.add(x);
                                yList.add(y);
                                x--;
                                y++;
                            }
                        }
                    }
                    else if(p instanceof Rook){
                        //horizontal
                        if(p.x!=k.x){
                            //on left
                            if(p.x<k.x){
                                for(int i=p.x; i<k.x; i++){
                                    xList.add(i);
                                    yList.add(p.y);
                                }
                            }
                            //on right
                            else{
                                for(int i=p.x; i>k.x; i--){
                                    xList.add(i);
                                    yList.add(p.y);
                                }
                            }
                        }
                        //vertical
                        else{
                            //below
                            if(p.y<k.y){
                                for(int i=p.y; i<k.y; i++){
                                    xList.add(p.x);
                                    yList.add(i);
                                }
                            }
                            //above
                            else{
                                for(int i=p.y; i>k.y; i--){
                                    xList.add(p.x);
                                    yList.add(i);
                                }
                            }
                        }
                    }
                    else if(p instanceof Knight){
                        xList.add(p.x);
                        yList.add(p.y);
                    }
                    else if(p instanceof Pawn){
                        xList.add(p.x);
                        yList.add(p.y);
                    }
                    //see if there is a piece of our color that threatens any of those spaces
                    for(int i=0; i<xList.size(); i++){
                        if(threatenedByNonKings(xList.get(i), yList.get(i), color))
                            return false;
                    }
                    b=true;
                    break;
                }
            }
            if(b)
                break;
        }
        return true;
    }

    /**
     * Checks if a move by the player put the opponent in check
     * @param player - the player who moved the piece in question
     * @return whether the player has put the opponent in check
     */
    public static boolean isCheck(boolean player){
        char color = player? 'b':'w';
        for (Piece[] row : board){
            for (Piece p : row){
                if(p.color == color && p instanceof King){
                    return ((King) p).isChecked();
                }
            }
        }
        return false;
    }

    /**
     * Checks for stalemate.
     * @param player - the current player
     * @return whether the given player is in stalemate
     */
    public boolean isStalemate(boolean player){
        char color = player? 'b':'w';
        for (Piece[] row : board){
            for (Piece p : row){
                if(p.isBlank() || p.color!=color)
                    continue;
                for(int i=0; i<7; i++){
                    for(int j=0; j<7; j++){
                        Piece test = board[i][j];
                        if(p.canMove(test.x, test.y)){
                            Piece[][] temp = copyBoard(board);
                            move(p, test);
                            if(!isCheck(p.color!='w')){
                                board=temp;
                                turnCounter--;
                                return false;
                            }
                            board=temp;
                            turnCounter--;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Moves a piece according to user input, assumes legality.
     * @param p1 - Piece to be moved.
     * @param p2 - Piece being moved to.
     */
    public void move(Piece p1, Piece p2){
        if(p1 instanceof Pawn){
            boolean isEnPassant = ((Pawn) p1).isLegalEnPassant(p2.x, p2.y);
            int oldY = p1.y;
            board[p2.y][p2.x] = new Pawn(p2.x, p2.y, p1.color, true);
            ((Pawn)board[p2.y][p2.x]).lastMovedTurn = turnCounter;
            ((Pawn)board[p2.y][p2.x]).lastMoveWasDouble = Math.abs(p1.y-p2.y)==2 && p1.x==p2.x;
            if(isEnPassant){
                char color = oldY%2 == p2.x%2 ? 'b':'w';
                board[oldY][p2.x] = new Piece(oldY, p2.x, color, false);
            }
            if ((p1.color == 'w' && p2.y == 7) || (p1.color == 'b' && p2.y == 0))
                promote(board[p2.y][p2.x]);
        }
        else if(p1 instanceof Rook)
            board[p2.y][p2.x] = new Rook(p2.x, p2.y, p1.color, true);
        else if(p1 instanceof Knight)
            board[p2.y][p2.x] = new Knight(p2.x, p2.y, p1.color, true);
        else if(p1 instanceof Bishop)
            board[p2.y][p2.x] = new Bishop(p2.x, p2.y, p1.color, true);
        else if(p1 instanceof Queen)
            board[p2.y][p2.x] = new Queen(p2.x, p2.y, p1.color, true);
        else if(p1 instanceof King){
            boolean castle = (p1.y==0 || p1.y==7) && p1.y==p2.y && Math.abs(p1.x-p2.x)==2;
            if(castle){
                boolean queenSide = p2.x<p1.x;
                if(queenSide){
                    move(board[p1.y][0], board[p1.y][p1.x-1]);
                }
                else{
                    move(board[p1.y][7], board[p1.y][p1.x+1]);
                }
            }
            board[p2.y][p2.x] = new King(p2.x, p2.y, p1.color, true);
        }
        board[p1.y][p1.x] = (p1.x)%2==(p1.y)%2? new Piece(p1.x,p1.y,'b', false):new Piece(p1.x,p1.y,'w', false);
        turnCounter++;
    }

    /**
     * Initializes the board state to the start of a new game.
     */
    public void initBoard(){
        check = false;
        checkmate = false;
        stalemate = false;
        currentPlayer = true;   //white
        drawRequested = false;
        turnCounter = 0;
        replay = new ArrayList<Integer[]>();

        targeting=false;

        sourceX=0;
        sourceY=0;
        targetX=0;
        targetY=0;

        pX=0;
        pY=0;

        gridview = (GridView)findViewById(R.id.gridview);

        board[0][0] = new Rook(0,0,'w', false);
        board[0][1] = new Knight(1,0,'w', false);
        board[0][2] = new Bishop(2,0,'w', false);
        board[0][3] = new Queen(3,0,'w', false);
        board[0][4] = new King(4,0,'w', false);
        board[0][5] = new Bishop(5,0,'w', false);
        board[0][6] = new Knight(6,0,'w', false);
        board[0][7] = new Rook(7,0,'w', false);

        for(int i = 0;i<=7;i++){
            board[1][i] = new Pawn(i,1,'w', false);
        }

        for(int i = 0;i<=7;i++){
            for(int j = 2;j<=5;j++){
                if(i%2==j%2)
                    board[j][i] = new Piece(i,j,'b', false);
                else
                    board[j][i] = new Piece(i,j,'w', false);
            }
        }

        for(int i = 0;i<=7;i++){
            board[6][i] = new Pawn(i,6,'b', false);
        }

        board[7][0] = new Rook(0,7,'b', false);
        board[7][1] = new Knight(1,7,'b', false);
        board[7][2] = new Bishop(2,7,'b', false);
        board[7][3] = new Queen(3,7,'b', false);
        board[7][4] = new King(4,7,'b', false);
        board[7][5] = new Bishop(5,7,'b', false);
        board[7][6] = new Knight(6,7,'b', false);
        board[7][7] = new Rook(7,7,'b', false);

        printBoard();
    }

    /**
     * Prints the current state of the board in ASCII art.
     */
    public void printBoard(){
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if(targeting){
                    targetX=getX(position);
                    targetY=getY(position);

                    ImageView img = (ImageView)gridview.getChildAt(getPos(sourceX, sourceY));
                    img.setColorFilter(null);

                    if(targetX==sourceX && targetY==sourceY){
                        //unselect
                    }
                    else if(board[sourceY][sourceX].color != (currentPlayer?'w':'b')){
                        //error
                    }
                    else if(board[sourceY][sourceX].canMove(targetX, targetY)){
                        Piece[][] temp = copyBoard(board);

                        //move
                        undoBoard = copyBoard(board);
                        move(board[sourceY][sourceX], board[targetY][targetX]);

                        if(isCheck(!currentPlayer)){
                            board=temp;
                            turnCounter--;
                            printBoard();
                        }
                        else{
                            Integer[] arr = {sourceY,sourceX,targetX,targetY};
                            replay.add(arr);
                            printBoard();
                            check = isCheck(currentPlayer);
                            checkmate = isCheckmate(currentPlayer);
                            stalemate = isStalemate(currentPlayer);
                            currentPlayer = !currentPlayer;
                            drawRequested=false;
                            if (checkmate || stalemate) {
                                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                    }
                                });
                                endGame();
                            }
                        }
                    }
                    else{
                        //error
                    }
                    targeting=false;
                }
                else{
                    sourceX=getX(position);
                    sourceY=getY(position);
                    if(!board[sourceY][sourceX].isBlank() && board[sourceY][sourceX].color==(currentPlayer?'w':'b')){
                        ImageView img = (ImageView)gridview.getChildAt(getPos(sourceX, sourceY));
                        img.setColorFilter(Color.argb(100,0,255,125));
                        targeting=true;
                    }
                }
            }
        });
    }

    public static Piece[][] copyBoard(Piece[][] b){
        Piece[][] temp = new Piece[8][8];
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                Piece old = b[i][j];
                if(old instanceof Bishop){
                    temp[i][j] = new Bishop(old.x, old.y, old.color, old.hasMoved);
                }
                else if(old instanceof King){
                    temp[i][j] = new King(old.x, old.y, old.color, old.hasMoved);
                }
                else if(old instanceof Knight){
                    temp[i][j] = new Knight(old.x, old.y, old.color, old.hasMoved);
                }
                else if(old instanceof Pawn){
                    temp[i][j] = new Pawn(old.x, old.y, old.color, old.hasMoved);
                }
                else if(old instanceof Queen){
                    temp[i][j] = new Queen(old.x, old.y, old.color, old.hasMoved);
                }
                else if(old instanceof Rook){
                    temp[i][j] = new Rook(old.x, old.y, old.color, old.hasMoved);
                }
                else{
                    temp[i][j] = new Piece(old.x, old.y, old.color, old.hasMoved);
                }
            }
        }
        return temp;
    }

    public static int getImg(String str){
        switch(str){
            case "  ":
            case "##":{
                return R.drawable.blank;
            }
            case "wB":{
                return R.drawable.bishopwhite;
            }
            case "bB":{
                return R.drawable.bishopblack;
            }
            case "wK":{
                return R.drawable.kingwhite;
            }
            case "bK":{
                return R.drawable.kingblack;
            }
            case "wN":{
                return R.drawable.knightwhite;
            }
            case "bN":{
                return R.drawable.knightblack;
            }
            case "wp":{
                return R.drawable.pawnwhite;
            }
            case "bp":{
                return R.drawable.pawnblack;
            }
            case "wQ":{
                return R.drawable.queenwhite;
            }
            case "bQ":{
                return R.drawable.queenblack;
            }
            case "wR":{
                return R.drawable.rookwhite;
            }
            case "bR":{
                return R.drawable.rookblack;
            }
            default:{
                return -1;
            }
        }
    }

    public int getX(int pos){
        return pos%8;
    }

    public int getY(int pos){
        return 7-(pos/8);
    }

    public int getPos(int x, int y){
        return 56+x-(8*y);
    }
}

