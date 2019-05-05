package com.example.kogorkus.dbadmin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private Context context;
    private String DB_NAME = "barcodes.db";

    private SQLiteDatabase db;

    private static DBManager dbManager;

    public static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    private DBManager(Context context) {
        this.context = context;
        db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        createTablesIfNeedBe();
    }

    private void createTablesIfNeedBe() {
        db.execSQL("CREATE TABLE IF NOT EXISTS BARCODES (_id INTEGER PRIMARY KEY AUTOINCREMENT, CODE TEXT, NAME TEXT, LENGTH TEXT);");
    }

    void addBarcode(String barcode, String name, String length) {
        ContentValues barcodeValue = new ContentValues();
        barcodeValue.put("CODE", barcode);
        barcodeValue.put("NAME", name);
        barcodeValue.put("LENGTH", length);
        db.insert("BARCODES", null, barcodeValue);

    }

    Cursor getAllResults() {
        return db.rawQuery("SELECT * FROM BARCODES ORDER BY NAME;", null);
    }

    void clearTable()
    {
        db.delete("BARCODES", null, null);
    }

    void deleteBarcode(String CODE)
    {
        db.delete("BARCODES", "_id=?", new String[]{CODE});
    }
}
