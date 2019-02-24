package com.example.kogorkus.dbadmin;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends Activity {

    private ListView listView;
    private String urlAddress="http://phasmid-helmet.000webhostapp.com/package/android.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.List);
        new Downloader(MainActivity.this,urlAddress,listView).execute();
    }


    public void toAddAct(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }
}
