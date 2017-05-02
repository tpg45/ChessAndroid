package com.example.tim.chess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SaveReplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_replay);
    }

    public void saveReplay(View v){
        EditText name = (EditText)findViewById(R.id.name);
        String str = name.getText().toString();
        if(str!=null && !str.equals("")){
            Intent intent = new Intent();
            intent.putExtra("repname", str);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
