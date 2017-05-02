package com.example.tim.chess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class PromoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote);

        if(getIntent().getStringExtra("color").equals("w")){
            ImageView q = (ImageView)findViewById(R.id.q);
            ImageView k = (ImageView)findViewById(R.id.k);
            ImageView r = (ImageView)findViewById(R.id.r);
            ImageView b = (ImageView)findViewById(R.id.b);

            q.setImageResource(R.drawable.queenwhite);
            k.setImageResource(R.drawable.knightwhite);
            r.setImageResource(R.drawable.rookwhite);
            b.setImageResource(R.drawable.bishopwhite);
        }
    }

    public void queen(View v){
        Intent intent = new Intent();
        intent.putExtra("choice", "queen");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void knight(View v){
        Intent intent = new Intent();
        intent.putExtra("choice", "knight");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void rook(View v){
        Intent intent = new Intent();
        intent.putExtra("choice", "rook");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void bishop(View v){
        Intent intent = new Intent();
        intent.putExtra("choice", "bishop");
        setResult(RESULT_OK, intent);
        finish();
    }
}
