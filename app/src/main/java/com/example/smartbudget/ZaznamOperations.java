package com.example.smartbudget;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ZaznamOperations {

    private DataBase dbHelper;
    private String[] ZAZNAM_TABLE_COLUMNS = { DataBase.ZAZNAM_ID,
                                                DataBase.ZAZNAM_TYP,
                                                DataBase.ZAZNAM_DATUM_DEN,
                                                DataBase.ZAZNAM_DATUM_MESIC,
                                                DataBase.ZAZNAM_DATUM_ROK,
                                                DataBase.ZAZNAM_CASTKA,
                                                DataBase.ZAZNAM_KATEGORIE,
                                                DataBase.ZAZNAM_OBRAZEK};
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

    public Zaznam addZaznam(String typ,
                            int datumDen, int datumMesic,
                            int datumRok, double castka,
                            String kategorie, String obrazek) {

        ContentValues values = new ContentValues();

        values.put(DataBase.ZAZNAM_TYP, typ);
        values.put(DataBase.ZAZNAM_DATUM_DEN, datumDen);
        values.put(DataBase.ZAZNAM_DATUM_MESIC, datumMesic);
        values.put(DataBase.ZAZNAM_DATUM_ROK, datumRok);
        values.put(DataBase.ZAZNAM_CASTKA, castka);
        values.put(DataBase.ZAZNAM_KATEGORIE, kategorie);
        if(!obrazek.equals("")&&(obrazek!=null)) {
            values.put(DataBase.ZAZNAM_OBRAZEK, obrazek);
        }

        long zaznamId = database.insert(DataBase.ZAZNAMY, null, values);

        Cursor cursor = database.query(DataBase.ZAZNAMY,
                ZAZNAM_TABLE_COLUMNS, DataBase.ZAZNAM_ID + " = "
                        + zaznamId, null, null, null, null);

        cursor.moveToFirst();

        Zaznam newComment = parseZaznam(cursor);
        cursor.close();
        return newComment;
    }

    public List getAllZaznamy(boolean isChecked, int aktualMesic, int aktualRok) {
        List zaznamy = new ArrayList();
        Cursor cursor;

        if(isChecked) {
            cursor = database.query(DataBase.ZAZNAMY,
                    ZAZNAM_TABLE_COLUMNS, DataBase.ZAZNAM_DATUM_MESIC + " = ? AND " + DataBase.ZAZNAM_DATUM_ROK + " = ?",
                    new String[]{String.valueOf(aktualMesic), String.valueOf(aktualRok)}, null, null,
                    DataBase.ZAZNAM_DATUM_ROK + " DESC, " + DataBase.ZAZNAM_DATUM_MESIC + " DESC, " + DataBase.ZAZNAM_DATUM_DEN + " DESC");
        }else{
            cursor = database.query(DataBase.ZAZNAMY,
                    ZAZNAM_TABLE_COLUMNS, null, null, null, null,
                    DataBase.ZAZNAM_DATUM_ROK + " DESC, " + DataBase.ZAZNAM_DATUM_MESIC + " DESC, " + DataBase.ZAZNAM_DATUM_DEN + " DESC");
        }

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
    public void updateZaznam(long id, String typ,
                             int datumDen, int datumMesic, int datumRok,
                             double castka, String kategorie,
                             String obrazek){
        ContentValues values = new ContentValues();
        values.put(DataBase.ZAZNAM_TYP, typ);
        values.put(DataBase.ZAZNAM_DATUM_DEN, datumDen);
        values.put(DataBase.ZAZNAM_DATUM_MESIC, datumMesic);
        values.put(DataBase.ZAZNAM_DATUM_ROK, datumRok);
        values.put(DataBase.ZAZNAM_CASTKA, castka);
        values.put(DataBase.ZAZNAM_KATEGORIE, kategorie);
        if((!obrazek.isEmpty())&&(obrazek!=null)) {
            values.put(DataBase.ZAZNAM_OBRAZEK, obrazek);
        }else{
            values.put(DataBase.ZAZNAM_OBRAZEK, "");
        }

        database.update(DataBase.ZAZNAMY, values, DataBase.ZAZNAM_ID + "=" + id,null);
    }

    public List getVydajeZaRok(String rok, Context context){
        List vydajeList = new ArrayList();

        Cursor cursor = database.query(DataBase.ZAZNAMY,
                new String[]{DataBase.ZAZNAM_DATUM_MESIC, "SUM (" + DataBase.ZAZNAM_CASTKA + ") AS vydajZaMesic"},
                DataBase.ZAZNAM_DATUM_ROK + " = ? AND " + DataBase.ZAZNAM_TYP + " = ?", new String[]{rok, context.getString(R.string.switch_databaseVydaj)},
                DataBase.ZAZNAM_DATUM_MESIC,
                null,
                DataBase.ZAZNAM_DATUM_MESIC + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            int mesic = cursor.getInt(cursor.getColumnIndexOrThrow(DataBase.ZAZNAM_DATUM_MESIC));
            double celkoveVydaje = cursor.getDouble(cursor.getColumnIndexOrThrow("vydajZaMesic"));
            String mesicNazev;
            switch (mesic) {
                case 1: mesicNazev = context.getString(R.string.mesic1); break;
                case 2: mesicNazev = context.getString(R.string.mesic2); break;
                case 3: mesicNazev = context.getString(R.string.mesic3); break;
                case 4: mesicNazev = context.getString(R.string.mesic4); break;
                case 5: mesicNazev = context.getString(R.string.mesic5); break;
                case 6: mesicNazev = context.getString(R.string.mesic6); break;
                case 7: mesicNazev = context.getString(R.string.mesic7); break;
                case 8: mesicNazev = context.getString(R.string.mesic8); break;
                case 9: mesicNazev = context.getString(R.string.mesic9); break;
                case 10: mesicNazev = context.getString(R.string.mesic10); break;
                case 11: mesicNazev = context.getString(R.string.mesic11); break;
                case 12: mesicNazev = context.getString(R.string.mesic12); break;
                default: mesicNazev = ""; break;
            }
            vydajeList.add(mesicNazev + ": " + celkoveVydaje);
            cursor.moveToNext();
        }

        cursor.close();
        return vydajeList;
    }
    private Zaznam parseZaznam(Cursor cursor) {
        Zaznam zaznam = new Zaznam();
        zaznam.setId((cursor.getInt(0)));
        zaznam.setTyp(cursor.getString(1));
        zaznam.setDatumDen(cursor.getInt(2));
        zaznam.setDatumMesic(cursor.getInt(3));
        zaznam.setDatumRok(cursor.getInt(4));
        zaznam.setCastka(cursor.getDouble(5));
        zaznam.setKategorie(cursor.getString(6));
        zaznam.setObrazek(cursor.getString(7));
        return zaznam;
    }
}