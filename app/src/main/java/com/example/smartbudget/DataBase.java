package com.example.smartbudget;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    public static final String ZAZNAMY = "Zaznamy";
    public static final String ZAZNAM_ID = "_id";
    public static final String ZAZNAM_TYP = "_typ";
    public static final String ZAZNAM_DATUM_DEN = "_datumDen";
    public static final String ZAZNAM_DATUM_MESIC = "_datumMesic";
    public static final String ZAZNAM_DATUM_ROK = "_datumRok";
    public static final String ZAZNAM_CASTKA = "_castka";
    public static final String ZAZNAM_KATEGORIE = "_kategorie";
    public static final String ZAZNAM_OBRAZEK = "_obrazek";

    private static final String DATABASE_NAME = "SmartBudgetDB.db";
    private static final int DATABASE_VERSION = 2025032601;

    private static final String DATABASE_CREATE = "create table " + ZAZNAMY
            + "("
            + ZAZNAM_ID + " integer primary key autoincrement, "
            + ZAZNAM_TYP + " text not null, "
            + ZAZNAM_DATUM_DEN + " integer not null, "
            + ZAZNAM_DATUM_MESIC + " integer not null, "
            + ZAZNAM_DATUM_ROK + " integer not null, "
            + ZAZNAM_CASTKA + " double not null, "
            + ZAZNAM_KATEGORIE + " text not null,"
            + ZAZNAM_OBRAZEK + " blob);";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + ZAZNAMY);
        onCreate(db);
    }

}