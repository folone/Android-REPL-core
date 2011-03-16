package com.folone.replcore;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String DATABASE_NAME = "repl.db";
    private static final int DATABASE_VERSION = 1;

    public static final String SCRIPTS_TABLE_NAME = "scripts";

    public static final String SCRIPTS_TEXT = "text";
    public static final String SCRIPTS_ENV_NAME = "env_name";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(scriptsCreate());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SCRIPTS_TABLE_NAME);
        onCreate(db);
    }

    public boolean persistScript(String script, String env) {
        try {
            ContentValues values = new ContentValues(2);
            values.put("text", script);
            values.put("env_name", env);
            SQLiteDatabase db = getWritableDatabase();
            db.insert(SCRIPTS_TABLE_NAME, "", values);
            db.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean removeScript(String script, String env) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(SCRIPTS_TABLE_NAME, "text=? AND env_name=?",
                    new String[] { script, env });
            db.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public Map<String, String> getHistory() {
        Map<String, String> history = new LinkedHashMap<String, String>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + SCRIPTS_TABLE_NAME + ";", null);
        while (cursor.moveToNext()) {
            history.put(cursor.getString(0), cursor.getString(1));
        }
        cursor.close();
        db.close();
        return history;
    }

    public boolean clearDB() {
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION);
        db.close();

        return true;
    }

    private String scriptsCreate() {
        StringBuilder query = new StringBuilder("CREATE TABLE ");
        return query.append(SCRIPTS_TABLE_NAME).append(" (").append(
                SCRIPTS_TEXT).append(" TEXT NOT NULL, ").append(
                SCRIPTS_ENV_NAME).append(
                " TEXT NOT NULL, UNIQUE(" + SCRIPTS_TEXT + ", "
                        + SCRIPTS_ENV_NAME + ") ON CONFLICT REPLACE);")
                .toString();
    }

}
