package com.example.smartbudget;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ZaznamOperations {

    // Database fields
    private DataBase dbHelper;
    private String[] ZAZNAM_TABLE_COLUMNS = { DataBase.ZAZNAM_ID,
                                                DataBase.ZAZNAM_TYP,
                                                DataBase.ZAZNAM_DATUM,
                                                DataBase.ZAZNAM_CASTKA,
                                                DataBase.ZAZNAM_KATEGORIE};
    private SQLiteDatabase database;

    public ZaznamOperations(Context context) {
        dbHelper = new DataBase(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Zaznam addZaznam(String typ, String datum, double castka, String kategorie) {

        ContentValues values = new ContentValues();

        values.put(DataBase.ZAZNAM_TYP, typ);
        values.put(DataBase.ZAZNAM_DATUM, datum);
        values.put(DataBase.ZAZNAM_CASTKA, castka);
        values.put(DataBase.ZAZNAM_KATEGORIE, kategorie);

        long zaznamId = database.insert(DataBase.ZAZNAMY, null, values);

        Cursor cursor = database.query(DataBase.ZAZNAMY,
                ZAZNAM_TABLE_COLUMNS, DataBase.ZAZNAM_ID + " = "
                        + zaznamId, null, null, null, null);

        cursor.moveToFirst();

        Zaznam newComment = parseZaznam(cursor);
        cursor.close();
        return newComment;
    }

    public List getAllZaznamy() {
        List zaznamy = new ArrayList();

        Cursor cursor = database.query(DataBase.ZAZNAMY,
                ZAZNAM_TABLE_COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Zaznam zaznam = parseZaznam(cursor);
            zaznamy.add(zaznam);
            cursor.moveToNext();
        }

        cursor.close();
        return zaznamy;
    }
    public void deleteZaznam(long id) {
        database.delete(DataBase.ZAZNAMY, DataBase.ZAZNAM_ID
                + " = " + id, null);
    }
    public void updateZaznam(long id, String typ, String datum, double castka, String kategorie){
        ContentValues values = new ContentValues();
        values.put(DataBase.ZAZNAM_TYP, typ);
        values.put(DataBase.ZAZNAM_DATUM, datum);
        values.put(DataBase.ZAZNAM_CASTKA, castka);
        values.put(DataBase.ZAZNAM_KATEGORIE, kategorie);

        database.update(DataBase.ZAZNAMY, values, DataBase.ZAZNAM_ID + "=" + id,null);
    }
    private Zaznam parseZaznam(Cursor cursor) {
        Zaznam zaznam = new Zaznam();
        zaznam.setId((cursor.getInt(0)));
        zaznam.setTyp(cursor.getString(1));
        zaznam.setDatum(cursor.getString(2));
        zaznam.setCastka(cursor.getDouble(3));
        zaznam.setKategorie(cursor.getString(4));
        return zaznam;
    }
}