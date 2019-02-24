package com.example.kogorkus.dbadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DataParser extends AsyncTask<Void,Void,Boolean> {

    Context c;
    String jsonData;
    ListView lv;
    JSONArray products = null;
    ProgressDialog pd;
    ArrayList<String> spacecrafts=new ArrayList<>();

    public DataParser(Context c, String jsonData, ListView lv) {
        this.c = c;
        this.jsonData = jsonData;
        this.lv = lv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd=new ProgressDialog(c);
        pd.setTitle("Parse");
        pd.setMessage("Pasring..Please wait");
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return this.parseData();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        pd.dismiss();
        if(result)
        {
            ArrayAdapter adapter=new ArrayAdapter(c,android.R.layout.simple_list_item_1,spacecrafts);

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(c, spacecrafts.get(position), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Boolean parseData()
    {

        try {
            JSONObject jsonObj = new JSONObject(jsonData); // Создаем переменную типа JSONObject

            // Получаем массив продуктов из нашего объекта
            products = jsonObj.getJSONArray("BARCODES");
            Log.d("tag", products.toString());
            // Проходим по всем продуктом циклом, для создания ассоциативного массива
            for (int i = 0; i < products.length(); i++) {
                JSONObject p = products.getJSONObject(i);
                //Создаем некоторые переменные для дальнейшей работы
                String CODE = p.getString("CODE"); // достаем номер продукта
                String NAME = p.getString("NAME"); //достаем название продукта
                String LENGTH = p.getString("LENGTH"); //достаем описание продукта

                // Временная переменная hashmap для одиночной записи продукта
                HashMap<String, String> product = new HashMap<String, String>();

                // Добовляем значения в наш HashMap в виде key => value
                product.put("CODE", CODE);
                product.put("NAME", NAME);
                product.put("LENGTH", LENGTH);

                Log.d("tag", product.toString());
                // Добовляем продукты в наш лист продуктов
                spacecrafts.add(String.valueOf(product));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
