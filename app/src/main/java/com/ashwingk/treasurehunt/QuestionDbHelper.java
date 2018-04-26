package com.ashwingk.treasurehunt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ashman on 07-10-2017.
 *
 */

public class QuestionDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "questionDb", TABLE_QUESTIONS = "questionTable",
        KEY_QUESTION = "question", KEY_ANSWERED = "answered", KEY_TIMESTAMP = "timestamp",
        KEY_ANSWER = "answer";
    public QuestionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create = "CREATE TABLE "+TABLE_QUESTIONS+"(" +
                KEY_QUESTION+" VARCHAR(3) PRIMARY KEY, "+KEY_ANSWERED+
                " INTEGER, "+KEY_ANSWER+" VARCHAR(20), "+KEY_TIMESTAMP+" INTEGER)";
        sqLiteDatabase.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_QUESTIONS);
        onCreate(sqLiteDatabase);
    }
    void addQuestion(String question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION, question);
        values.put(KEY_ANSWERED, 0);
        db.insert(TABLE_QUESTIONS, null, values);
        db.close();
    }
    ArrayList<String> getLastEntry() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_QUESTIONS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<String> q = new ArrayList<>();
        if(cursor.moveToLast()) {
            q.add(cursor.getString(cursor.getColumnIndex(KEY_QUESTION)));
            q.add(cursor.getString(cursor.getColumnIndex(KEY_ANSWERED)));
            cursor.close();
        }
        db.close();
        return q;
    }
    void answerQuestion(String question, long answered, String code) {
        String TAG = "DbHelper";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ANSWERED, 1);
        cv.put(KEY_TIMESTAMP, answered);
        cv.put(KEY_ANSWER, code);
        Log.d(TAG, "answerQuestion: question is "+question);
        db.update(TABLE_QUESTIONS, cv, KEY_QUESTION+"='"+question+"'", null);
        db.close();
    }
    ArrayList<String> getAnsweredQuestions() {
        ArrayList<String> questions = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_QUESTIONS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                String answered = cursor.getString(cursor.getColumnIndex(KEY_ANSWERED));
                if(answered.equals("1"))
                    questions.add(cursor.getString(cursor.getColumnIndex(KEY_QUESTION)));
            } while(cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return questions;
    }
    ArrayList<Answer> getAnswers() {
        ArrayList<Answer> questions = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_QUESTIONS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                String answered = cursor.getString(cursor.getColumnIndex(KEY_ANSWERED));
                if(answered.equals("1"))
                    questions.add(new Answer(cursor.getString(cursor.getColumnIndex(KEY_QUESTION)),
                            cursor.getString(cursor.getColumnIndex(KEY_ANSWER)),
                            cursor.getLong(cursor.getColumnIndex(KEY_TIMESTAMP))));
            } while(cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return questions;
    }
}
