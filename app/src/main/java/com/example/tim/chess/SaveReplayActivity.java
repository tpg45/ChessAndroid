package com.example.tim.chess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SaveReplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_replay);
        TextView textView = (TextView)findViewById(R.id.resultdisplay);
        textView.setText(getIntent().getStringExtra("status"));
    }

    public void saveReplay(View v){
        EditText name = (EditText)findViewById(R.id.name);
        String str = name.getText().toString();
        String d = "";
        if(str!=null && !str.equals("")){
            ArrayList<Character[]> replay = (ArrayList<Character[]>) getIntent().getSerializableExtra("replay");
            for(Character[] a: replay){
                d+=(a[0]+"")+(a[1]+"")+(a[2]+"")+(a[3]+"");
                if(a.length==5)
                    d+=(a[4]+"");
            }
            File f = new File(getFilesDir(), str+".rep");
            PrintWriter writer = null;
            try {
                f.createNewFile();
                writer = new PrintWriter(f.getAbsolutePath(), "UTF-8");
                writer.print(d);
                writer.flush();
                writer.close();
                String absolutePath = f.getAbsolutePath();
                System.out.println(absolutePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
