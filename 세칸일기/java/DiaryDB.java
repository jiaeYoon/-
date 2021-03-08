package com.example.mydiary_ver6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.mydiary_ver6.Diary;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DiaryDB extends SQLiteOpenHelper {
    //public static final int DATABASE_VERSION = 1;
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "DiaryDB.db";

    public DiaryDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static class DiaryEntry implements BaseColumns{
        public static final String TABLE_NAME = "diary";

        public static final String COLUMN_NAME_FILENAME = "filename";
        public static final String COLUMN_NAME_DATE = "date";
        //public static final String COLUMN_NAME_EMOJI1 = "emoji1";
        //public static final String COLUMN_NAME_IMAGE = "image";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DiaryEntry.TABLE_NAME + " (" +
                    DiaryEntry._ID + " INTEGER PRIMARY KEY, " +
                    DiaryEntry.COLUMN_NAME_FILENAME + " TEXT, " +
                    DiaryEntry.COLUMN_NAME_DATE + " TEXT)";
//                    DiaryEntry.COLUMN_NAME_IMAGE + " TEXT)";


    public void saveDiary(File file){
        if (file.exists() == false){
            return;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();                 //레코드 추가를 위한 클래스 생성
        values.put(DiaryEntry.COLUMN_NAME_FILENAME, file.getName());
        values.put(DiaryEntry.COLUMN_NAME_DATE, getDate());
//        values.put(DiaryEntry.COLUMN_NAME_IMAGE, ImagePath);

        db.insert(DiaryEntry.TABLE_NAME, null, values);

    }

    public String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String date = simpleDateFormat.format(new Date());
        return date;
    }


    public ArrayList<Diary> loadDiaryList(){
        ArrayList<Diary> diaryArrayList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();          //조회 가능한 SQLiteDatabase 객체 반환

        String[] projection2 = {
                DiaryEntry._ID,
                DiaryEntry.COLUMN_NAME_FILENAME,
                DiaryEntry.COLUMN_NAME_DATE
        };

        //조회된 데이터에 접근할 수 있는 Cursor 객체 반환
        Cursor cursor = db.query(DiaryEntry.TABLE_NAME, projection2, null, null, null, null, null);

        while(cursor.moveToNext()){
            Diary diary = new Diary();
            diary.fileName = cursor.getString(cursor.getColumnIndex(DiaryEntry.COLUMN_NAME_FILENAME));  //getColumnIndex() : 컬럼이 몇 번째 열에 존재하는지 찾아 위치 반환
            diary.date = cursor.getString(cursor.getColumnIndex(DiaryEntry.COLUMN_NAME_DATE));

//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);        //경로를 통해 비트맵으로 전환
//            imageView.setImageBitmap(bitmap);
//            diary.DImage = cursor.getString(cursor.getColumnIndex(DiaryEntry.COLUMN_NAME_IMAGE));

            diaryArrayList.add(diary);
        }
        return diaryArrayList;
    }

    public void removeDiary(File file){
        SQLiteDatabase db = getReadableDatabase();

        String selection = DiaryEntry.COLUMN_NAME_FILENAME + " LIKE ?";
        String[] selectionArgs = {file.getName()};

        db.delete(DiaryEntry.TABLE_NAME, selection, selectionArgs); //파일명 일치하는 레코드 삭제
    }
}
