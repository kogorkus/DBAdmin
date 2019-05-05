package com.example.kogorkus.dbadmin;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AddActivity extends Activity {

    private EditText CodeET, NameET, LengthET;
    private Button AddButton;
    private String urlAddress="http://phasmid-helmet.000webhostapp.com/package/post2.php";

    private FirebaseFirestore db;
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
                //new Sender( CodeET.getText().toString() , NameET.getText().toString(), LengthET.getText().toString()).execute();
                dbManager.addBarcode(CodeET.getText().toString(), NameET.getText().toString(), LengthET.getText().toString());

                Map<String, Object> test1 = new HashMap<>();
                test1.put("Code", CodeET.getText().toString());
                test1.put("Length", LengthET.getText().toString());
                test1.put("Name", NameET.getText().toString());

                db.collection("test")
                        .add(test1)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("123", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("123", "Error adding document", e);

                            }
                        });

                CodeET.setText("");
                NameET.setText("");
                LengthET.setText("");
            }
        });
        db = FirebaseFirestore.getInstance();
        if (extras != null) {
            CodeET.setText(extras.getString("code"));
        }

    }

    public void Scan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    class Sender extends AsyncTask<Void, Void, String> {

        String CODE,  NAME, LENGTH;

        Sender(String CODE, String NAME, String LENGTH){
            this.CODE = CODE;
            this.NAME = NAME;
            this.LENGTH = LENGTH;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(Void... params) {
            return this.send();
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);


            if (response == null) {
                Toast.makeText(AddActivity.this, "Unsuccessful,Null returned", Toast.LENGTH_SHORT).show();
            } else {
                if (response == "Bad Response") {
                    Toast.makeText(AddActivity.this, "Unsuccessful,Bad Response returned", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(AddActivity.this, "Successfully Saved", Toast.LENGTH_SHORT).show();

                }
            }


        }

        private String send() {
            HttpURLConnection con = Connector.connect(urlAddress);

            try {
                OutputStream os = con.getOutputStream();

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                bw.write("CODE=" + CODE + "&" + "NAME=" + NAME + "&" + "LENGTH=" + LENGTH);

                bw.flush();

                bw.close();
                os.close();


                int responseCode = con.getResponseCode();
                if (responseCode == con.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuffer response = new StringBuffer();

                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    br.close();
                    return response.toString();
                } else {
                    return "Bad Response";
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }




}
