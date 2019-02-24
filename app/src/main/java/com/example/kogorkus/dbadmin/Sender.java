package com.example.kogorkus.dbadmin;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class Sender extends AsyncTask<Void, Void, String> {

    private Context c;
    private String urlAddress;
    private EditText CodeET, NameET, LengthET;

    private String CODE, NAME, LENGTH;




    public Sender(Context c, String urlAddress, EditText... editTexts) {
        this.c = c;
        this.urlAddress = urlAddress;

        this.CodeET = editTexts[0];
        this.NameET = editTexts[1];
        this.LengthET = editTexts[2];


        CODE = CodeET.getText().toString();
        NAME = NameET.getText().toString();
        LENGTH = LengthET.getText().toString();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Toast.makeText(c, "Отправляется", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected String doInBackground(Void... params) {
        return this.send();
    }


    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);



        if (response != null) {

            Toast.makeText(c, response, Toast.LENGTH_LONG).show();

            CodeET.setText("");
            NameET.setText("");
            LengthET.setText("");
        } else {
            Toast.makeText(c, "Unsuccessful " + response, Toast.LENGTH_LONG).show();
        }
    }

    private String send() {
        HttpURLConnection con = Connector.connect(urlAddress);

        if (con == null) {
            return null;
        }

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

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
