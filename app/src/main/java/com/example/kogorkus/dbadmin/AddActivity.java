package com.example.kogorkus.dbadmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AddActivity extends Activity {

    private EditText CodeET, NameET, LengthET;
    private Button AddButton;
    private String urlAddress="http://phasmid-helmet.000webhostapp.com/package/post2.php";
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        final Bundle extras = getIntent().getExtras();
        dbManager = DBManager.getInstance(this);
        CodeET = findViewById(R.id.editText);
        NameET = findViewById(R.id.editText2);
        LengthET = findViewById(R.id.editText3);
        AddButton = findViewById(R.id.button4);
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sender s=new Sender(AddActivity.this, urlAddress, CodeET , NameET, LengthET);
                //s.execute();
                dbManager.addBarcode(CodeET.getText().toString(), NameET.getText().toString(), LengthET.getText().toString());
                CodeET.setText("");
                NameET.setText("");
                LengthET.setText("");
            }
        });
        if (extras != null) {
            CodeET.setText(extras.getString("code"));
        }

    }

    public void Scan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }




}
