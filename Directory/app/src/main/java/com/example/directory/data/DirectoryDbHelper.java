package com.example.directory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.directory.data.DirectoryContract.*;

import androidx.annotation.Nullable;

public class DirectoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "directory.db";
    private static final int DATABASE_VERSION = 2;

    private static final String OPEN_PAR = " (";
    private static final String CLOSE_PAR = ")";
    private static final String COMMA = ", ";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String FOREIGN_KEY = " FOREIGN KEY ";
    private static final String REFERENCES = " REFERENCES ";

    private static final String SQL_CREATE_ENTERPRISE_TABLE =
            CREATE_TABLE + EnterpriseEntry.TABLE_NAME + OPEN_PAR +
            EnterpriseEntry._ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA +
            EnterpriseEntry.COLUMN_ENTERPRISE_NAME + TEXT_TYPE + COMMA +
            EnterpriseEntry.COLUMN_CONTACT_PHONE + TEXT_TYPE + COMMA +
            EnterpriseEntry.COLUMN_CONTACT_EMAIL + TEXT_TYPE + COMMA +
            EnterpriseEntry.COLUMN_WEB_SITE + TEXT_TYPE + COMMA +
            EnterpriseEntry.COLUMN_PRODUCTS_SERVICES + TEXT_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_CLASSIFICATIONSENTERPRISE_TABLE =
            CREATE_TABLE + ClassificationsEnterprise.TABLE_NAME + OPEN_PAR +
            ClassificationsEnterprise.COLUMN_ENTERPRISE_ID + INTEGER_TYPE + COMMA +
            ClassificationsEnterprise.COLUMN_CLASSIFICATION_ID + INTEGER_TYPE + COMMA +
            FOREIGN_KEY + OPEN_PAR + ClassificationsEnterprise.COLUMN_ENTERPRISE_ID + CLOSE_PAR +
            REFERENCES + EnterpriseEntry.TABLE_NAME + OPEN_PAR + EnterpriseEntry._ID + CLOSE_PAR + CLOSE_PAR;

    private static final String SQL_DELETE_ENTERPRISE_TABLE =
            DROP_TABLE + EnterpriseEntry.TABLE_NAME;

    private static final String SQL_DELETE_CLASSIFICATIONSENTERPRISE_TABLE =
            DROP_TABLE + ClassificationsEnterprise.TABLE_NAME;


    public DirectoryDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTERPRISE_TABLE);
        db.execSQL(SQL_CREATE_CLASSIFICATIONSENTERPRISE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CLASSIFICATIONSENTERPRISE_TABLE);
        db.execSQL(SQL_DELETE_ENTERPRISE_TABLE);
        onCreate(db);
    }
}
