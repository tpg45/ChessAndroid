package com.example.tim.chess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class PickReplayActivity extends AppCompatActivity {

    boolean nameDesc;
    boolean dateDesc;
    ArrayList<FileWrapper> arr;

    private class FileWrapper{
        File f;

        public FileWrapper(File f){
            this.f=f;
        }

        public String toString(){
            return f.getName()+"\t"+new Date(f.lastModified());
        }
    }

    public String getExtension(String s){
        String extension = "";
        int i = s.lastIndexOf('.');
        if (i > 0) {
            extension = s.substring(i+1);
        }
        return extension;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_replay);

        loadReplays();
    }

    public void loadReplays(){
        nameDesc=true;
        dateDesc=true;
        //populate listview
        arr = new ArrayList<FileWrapper>();
        File folder = new File(getFilesDir().getAbsolutePath());
        File[] listOfFiles = folder.listFiles();
        for(File f: listOfFiles){
            if(getExtension(f.getName()).equals("rep"))
                arr.add(new FileWrapper(f));
        }
        arr.sort((FileWrapper f1, FileWrapper f2)->f1.f.getName().compareTo(f2.f.getName()));
        ListView lv = (ListView)findViewById(R.id.listview);
        ArrayAdapter<FileWrapper> arrayAdapter = new ArrayAdapter<FileWrapper>(
                this,
                android.R.layout.simple_list_item_1,
                arr );

        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                pick(position);
            }
        });
    }

    public void pick(int pos){
        Intent intent = new Intent(this, PlayReplayActivity.class);
        ListView lv = (ListView)findViewById(R.id.listview);
        intent.putExtra("replay", ((FileWrapper)lv.getItemAtPosition(pos)).f);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1) {
            if(resultCode==RESULT_OK) {
                loadReplays();
            }
        }
    }

    public void nameSort(View v){
        dateDesc=true;
        if(nameDesc){
            nameDesc=false;
            arr.sort((FileWrapper f1, FileWrapper f2)->f2.f.getName().compareTo(f1.f.getName()));
            ListView lv = (ListView)findViewById(R.id.listview);
            ArrayAdapter<FileWrapper> arrayAdapter = new ArrayAdapter<FileWrapper>(
                    this,
                    android.R.layout.simple_list_item_1,
                    arr );

            lv.setAdapter(arrayAdapter);
        }
        else{
            nameDesc=true;
            arr.sort((FileWrapper f1, FileWrapper f2)->f1.f.getName().compareTo(f2.f.getName()));
            ListView lv = (ListView)findViewById(R.id.listview);
            ArrayAdapter<FileWrapper> arrayAdapter = new ArrayAdapter<FileWrapper>(
                    this,
                    android.R.layout.simple_list_item_1,
                    arr );

            lv.setAdapter(arrayAdapter);
        }
    }

    public void dateSort(View v){
        nameDesc=true;
        if(dateDesc){
            dateDesc=false;
            arr.sort((FileWrapper f1, FileWrapper f2)->Long.compare(f2.f.lastModified(), f1.f.lastModified()));
            ListView lv = (ListView)findViewById(R.id.listview);
            ArrayAdapter<FileWrapper> arrayAdapter = new ArrayAdapter<FileWrapper>(
                    this,
                    android.R.layout.simple_list_item_1,
                    arr );

            lv.setAdapter(arrayAdapter);
        }
        else{
            dateDesc=true;
            arr.sort((FileWrapper f1, FileWrapper f2)->Long.compare(f1.f.lastModified(), f2.f.lastModified()));
            ListView lv = (ListView)findViewById(R.id.listview);
            ArrayAdapter<FileWrapper> arrayAdapter = new ArrayAdapter<FileWrapper>(
                    this,
                    android.R.layout.simple_list_item_1,
                    arr );

            lv.setAdapter(arrayAdapter);
        }
    }
}
