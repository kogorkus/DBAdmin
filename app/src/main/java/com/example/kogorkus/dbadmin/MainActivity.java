package com.example.kogorkus.dbadmin;


import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.Menu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> productList;
    private ListView listView;
    private String jsonData;
    private String GETurlAddress = "http://phasmid-helmet.000webhostapp.com/package/android.php";
    private String POSTurlAddress = "http://phasmid-helmet.000webhostapp.com/package/post2.php";
    private String DELETEurlAddress = "http://phasmid-helmet.000webhostapp.com/package/delete.php";
    private DBManager dbManager;
    private SimpleCursorAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.List);
        dbManager = DBManager.getInstance(this);
        Cursor cursor = dbManager.getAllResults();

        myAdapter = new SimpleCursorAdapter(this, R.layout.listitem, cursor, new String[]{"_id", "NAME", "LENGTH"}, new int[]{R.id.IdTV, R.id.NameTV, R.id.LengthTV}, 0);
        listView.setAdapter(myAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = findViewById(R.id.IdTV);
                dbManager.deleteBarcode(tv.getText().toString());
                Cursor cursor = dbManager.getAllResults();
                myAdapter.changeCursor(cursor);
                myAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editLocalDB) {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.DownloadDB) {
            dbManager.clearTable();
            new Downloader().execute();

        } else if (item.getItemId() == R.id.UploadLocalDB) {
            Cursor cursor = dbManager.getAllResults();
            String CODE, NAME, LENGTH;
            new Deleter().execute();
            while (cursor.moveToNext()) {
                CODE = cursor.getString(cursor.getColumnIndex("CODE"));
                NAME = cursor.getString(cursor.getColumnIndex("NAME"));
                LENGTH = cursor.getString(cursor.getColumnIndex("LENGTH"));
                new Sender(CODE, NAME, LENGTH).execute();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Cursor cursor = dbManager.getAllResults();
        myAdapter.changeCursor(cursor);
        myAdapter.notifyDataSetChanged();
        super.onResume();
    }


    class Downloader extends AsyncTask<Void, Void, String> {

        private JSONArray barcodes = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection connection = Connector.connect(GETurlAddress);
            if (connection.toString().startsWith("Error")) {

                return connection.toString();
            }

            try {
                InputStream is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line;
                StringBuffer jsonDataSB = new StringBuffer();


                while ((line = br.readLine()) != null) {
                    jsonDataSB.append(line);
                }
                br.close();
                is.close();
                jsonData = jsonDataSB.toString();
                return jsonDataSB.toString();

            } catch (IOException e) {
                e.printStackTrace();
                return "Error " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonData) {
            if (jsonData.startsWith("Error")) {
                Log.e("tag", "error in downloader");
            } else {
                try {
                    JSONObject jsonObj = new JSONObject(jsonData);

                    barcodes = jsonObj.getJSONArray("BARCODES");
                    for (int i = 0; i < barcodes.length(); i++) {
                        JSONObject p = barcodes.getJSONObject(i);

                        String CODE = p.getString("CODE");
                        String NAME = p.getString("NAME");
                        String LENGTH = p.getString("LENGTH");

                        dbManager.addBarcode(CODE, NAME, LENGTH);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Cursor cursor = dbManager.getAllResults();
                myAdapter.changeCursor(cursor);
                myAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"Успешно загружено", Toast.LENGTH_SHORT);
            }
        }
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

            /*

            if (response == null) {
                Toast.makeText(MainActivity.this, "Unsuccessful,Null returned", Toast.LENGTH_SHORT).show();
            } else {
                if (response == "Bad Response") {
                    Toast.makeText(MainActivity.this, "Unsuccessful,Bad Response returned", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "Successfully Saved", Toast.LENGTH_SHORT).show();

                }
            }

            */
        }

        private String send() {
            HttpURLConnection con = Connector.connect(POSTurlAddress);

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

    class Deleter extends  AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection con = Connector.connect(DELETEurlAddress);
            try {
                InputStream is = new BufferedInputStream(con.getInputStream());
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
