package com.example.alejandro.practica4aadinmobiliariacontentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alejandro on 26/01/2015.
 */
public class Ayudante extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "inmuebles.db";
    public static final int DATABASE_VERSION = 1;

    public Ayudante(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;
        sql = "create table " + Contrato.TablaInmueble.TABLA +
                " (" + Contrato.TablaInmueble._ID +
                " integer primary key autoincrement, " +
                Contrato.TablaInmueble.CALLE + " text, " +
                Contrato.TablaInmueble.LOCALIDAD + " text, " +
                Contrato.TablaInmueble.TIPO + " text, " +
                Contrato.TablaInmueble.PRECIO + " integer, " +
                Contrato.TablaInmueble.SUBIDO + " integer)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists " + Contrato.TablaInmueble.TABLA;
        db.execSQL(sql);
        onCreate(db);
    }
}