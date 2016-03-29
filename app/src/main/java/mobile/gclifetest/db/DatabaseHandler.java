package mobile.gclifetest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;

import java.util.List;

import mobile.gclifetest.PojoGson.EventsPojo;

/**
 * Created by goodworklabs on 08/02/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GCLIFE_DB";
    private static final String TABLE_NEWS = "News";
    private static final String TABLE_NOTICE = "Notice";
    private static final String TABLE_IDEAS = "Ideas";
    private static final String TABLE_PHOTOS = "Photos";
    private static final String TABLE_VIDEOS = "Videos";
    private static final String TABLE_IMPCONTACTS = "ImpContacts";
    private static final String TABLE_FRIENDS = "Friends";
    private static final String TABLE_MAILRECV = "MAILRECV";
    private static final String TABLE_MAILSENT = "MAILSENT";
    private static final String TABLE_MEMSVERIFI = "MEMSVERIFI";
    private static final String EVENT_LIST = "EVENTS";
    private static final String EVENT_ID = "EVENT_ID";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + "(" + EVENT_ID + " INTEGER PRIMARY KEY," + EVENT_LIST + " TEXT" + ")";
        String CREATE_NOTICE_TABLE = "CREATE TABLE " + TABLE_NOTICE + "(" + EVENT_ID + " INTEGER PRIMARY KEY," + EVENT_LIST + " TEXT" + ")";
        String CREATE_IDEAS_TABLE = "CREATE TABLE " + TABLE_IDEAS + "(" + EVENT_ID + " INTEGER PRIMARY KEY," + EVENT_LIST + " TEXT" + ")";
        String CREATE_PHOTOS_TABLE = "CREATE TABLE " + TABLE_PHOTOS + "(" + EVENT_ID + " INTEGER PRIMARY KEY," + EVENT_LIST + " TEXT" + ")";
        String CREATE_VIDEOS_TABLE = "CREATE TABLE " + TABLE_VIDEOS + "(" + EVENT_ID + " INTEGER PRIMARY KEY," + EVENT_LIST + " TEXT" + ")";
        String CREATE_IMPCONTS_TABLE = "CREATE TABLE " + TABLE_IMPCONTACTS + "(" + EVENT_ID + " INTEGER PRIMARY KEY," + EVENT_LIST + " TEXT" + ")";
        String CREATE_FRIENDS_TABLE = "CREATE TABLE " + TABLE_FRIENDS + "(" + EVENT_ID + " INTEGER PRIMARY KEY," + EVENT_LIST + " TEXT" + ")";
        String CREATE_MAILRECV_TABLE = "CREATE TABLE " + TABLE_MAILRECV + "(" + EVENT_ID + " INTEGER PRIMARY KEY," + EVENT_LIST + " TEXT" + ")";
        String CREATE_MAILSENT_TABLE = "CREATE TABLE " + TABLE_MAILSENT + "(" + EVENT_ID + " INTEGER PRIMARY KEY," + EVENT_LIST + " TEXT" + ")";
        String CREATE_MEMSVERIFY_TABLE = "CREATE TABLE " + TABLE_MEMSVERIFI + "(" + EVENT_ID + " INTEGER PRIMARY KEY," + EVENT_LIST + " TEXT" + ")";
        db.execSQL(CREATE_NEWS_TABLE);
        db.execSQL(CREATE_NOTICE_TABLE);
        db.execSQL(CREATE_IDEAS_TABLE);
        db.execSQL(CREATE_PHOTOS_TABLE);
        db.execSQL(CREATE_VIDEOS_TABLE);
        db.execSQL(CREATE_IMPCONTS_TABLE);
        db.execSQL(CREATE_FRIENDS_TABLE);
        db.execSQL(CREATE_MAILRECV_TABLE);
        db.execSQL(CREATE_MAILSENT_TABLE);
        db.execSQL(CREATE_MEMSVERIFY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IDEAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMPCONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAILRECV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAILSENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMSVERIFI);
        onCreate(db);
    }

    public void addEventNews(JSONArray eventNews, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVENT_LIST, eventNews.toString());
        db.insert(tableName, null, values);
        db.close();
    }

    public String getEventNews(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(1);
        } else
            return "null";
    }
    public void updateEventNews(JSONArray eventNews, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVENT_LIST, eventNews.toString());
        db.update(tableName, values, null, null);
    }
}
