package com.example.tim.chess;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PlayReplayActivity extends AppCompatActivity {
    private class ImageAdapter2 extends BaseAdapter{
        private Context mContext;

        public ImageAdapter2(Context c) {
                mContext = c;
            }

            public int getCount() {
                return 64;
            }

            public Object getItem(int position) {
                return null;
            }

            public long getItemId(int position) {
                return 0;
            }

            // create a new ImageView for each item referenced by the Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView;
                if (convertView == null) {
                    // if it's not recycled, initialize some attributes
                    imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new GridView.LayoutParams(95, 95));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setPadding(0, 0, 0, 0);
                } else {
                    imageView = (ImageView) convertView;
                }

                //imageView.setImageResource(mThumbIds[position]);
                //img.setImageResource(getImg(board[i][j].toString()));

                int x = position%8;
                int y = 7-(position/8);
                imageView.setImageResource(MainActivity.getImg(board[y][x].toString()));

                return imageView;
            }
        }

    File f;
    String inputs;
    Piece[][] board;
    GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_replay);
        f = (File)getIntent().getSerializableExtra("replay");
        inputs = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            inputs=br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gridview = (GridView)findViewById(R.id.gridview);
        initBoard();
    }

    public void initBoard(){
        board = new Piece[8][8];
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

    public void delete(View v){
        f.delete();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void forward(View v){
        if(inputs.length()==0)
            return;
        int sX = ((int)inputs.charAt(0))-48;
        int sY = ((int)inputs.charAt(1))-48;
        int tX = ((int)inputs.charAt(2))-48;
        int tY = ((int)inputs.charAt(3))-48;
        inputs=inputs.substring(4);
        char pChoice = '\0';
        Piece s = board[sY][sX];
        if(inputs.length()!=0 && Character.isAlphabetic(inputs.charAt(0))){
            pChoice=inputs.charAt(0);
            inputs=inputs.substring(1);
        }
        if(pChoice=='\0'){
            if(s instanceof Bishop)
                board[tY][tX] = new Bishop(tX, tY, s.color, true);
            else if(s instanceof King)
                board[tY][tX] = new King(tX, tY, s.color, true);
            else if(s instanceof Knight)
                board[tY][tX] = new Knight(tX, tY, s.color, true);
            else if(s instanceof Pawn)
                board[tY][tX] = new Pawn(tX, tY, s.color, true);
            else if(s instanceof Queen)
                board[tY][tX] = new Queen(tX, tY, s.color, true);
            else if(s instanceof Rook)
                board[tY][tX] = new Rook(tX, tY, s.color, true);

            char c = (sX%2==sY%2)?'b':'w';
            board[sY][sX] = new Piece(sX, sY, c, false);
        }
        else{
            switch(pChoice){
                case 'q':{
                    board[tY][tX] = new Queen(tX, tY, s.color, true);
                    break;
                }
                case 'k':{
                    board[tY][tX] = new Knight(tX, tY, s.color, true);
                    break;
                }
                case 'r':{
                    board[tY][tX] = new Rook(tX, tY, s.color, true);
                    break;
                }
                case 'b':{
                    board[tY][tX] = new Bishop(tX, tY, s.color, true);
                    break;
                }
            }
            char c = (sX%2==sY%2)?'b':'w';
            board[sY][sX] = new Piece(sX, sY, c, false);
        }
        printBoard();
    }

    public void printBoard(){
        gridview.setAdapter(new ImageAdapter2(this));
    }
}
