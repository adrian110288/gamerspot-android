package com.adrianlesniak.gamerspot.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Adrian on 10-Jun-14.
 */
public class GamerSpotDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "GamerSpot.db";

    private static final String COMMA_SEP = ", ";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";

    public static final String SQL_CREATE_NEWSFEEDS_TABLE = "CREATE TABLE " + DatabaseContract.NewsFeedTable.TABLE_NAME + " (" +
        DatabaseContract.NewsFeedTable.COLUMN_NAME_ID + " TEXT PRIMARY KEY UNIQUE," +
        DatabaseContract.NewsFeedTable.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
        DatabaseContract.NewsFeedTable.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
        DatabaseContract.NewsFeedTable.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
        DatabaseContract.NewsFeedTable.COLUMN_NAME_DATE + INTEGER_TYPE + COMMA_SEP +
        DatabaseContract.NewsFeedTable.COLUMN_NAME_CREATOR + TEXT_TYPE + COMMA_SEP +
        DatabaseContract.NewsFeedTable.COLUMN_NAME_PROVIDER + TEXT_TYPE + COMMA_SEP +
        DatabaseContract.NewsFeedTable.COLUMN_NAME_PLATFORM + INTEGER_TYPE + " )";

    public static final String SQL_CREATE_SEARCH_PHRASES_TABLE = "CREATE TABLE " + DatabaseContract.SearchPhrasesTable.TABLE_NAME + " (" +
            DatabaseContract.SearchPhrasesTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.SearchPhrasesTable.COLUMN_NAME_PHRASE + TEXT_TYPE + " )";

    public static final String SQL_CREATE_FAVOURITE_FEEDS_TABLE = "CREATE TABLE " + DatabaseContract.FavouriteFeedsTable.TABLE_NAME + " (" +
            DatabaseContract.FavouriteFeedsTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.FavouriteFeedsTable.COLUMN_FAVOURITE_FEED_ID + TEXT_TYPE + " )";

    public static final String SQL_DELETE_NEWSFEEDS_TABLE = "DROP TABLE IF EXISTS " + DatabaseContract.NewsFeedTable.TABLE_NAME;
    public static final String SQL_DELETE_SEARCH_PHRASES_TABLE = "DROP TABLE IF EXISTS " + DatabaseContract.SearchPhrasesTable.TABLE_NAME;
    public static final String SQL_DELETE_FAVOURITE_FEEDS_TABLE = "DROP TABLE IF EXISTS " + DatabaseContract.FavouriteFeedsTable.TABLE_NAME;

    public GamerSpotDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_NEWSFEEDS_TABLE);
        Log.i("DB" , "NewsFeed table created");
        db.execSQL(SQL_CREATE_SEARCH_PHRASES_TABLE);
        Log.i("DB" , "SearchPhrases table created");
        db.execSQL(SQL_CREATE_FAVOURITE_FEEDS_TABLE);
        Log.i("DB" , "FavouriteFeeds table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_NEWSFEEDS_TABLE);
        db.execSQL(SQL_DELETE_SEARCH_PHRASES_TABLE);
        db.execSQL(SQL_DELETE_FAVOURITE_FEEDS_TABLE);
        onCreate(db);
    }
}
