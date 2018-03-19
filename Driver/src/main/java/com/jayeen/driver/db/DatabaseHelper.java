package com.jayeen.driver.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jayeen.driver.newmodels.DriverModel;

import java.sql.SQLException;

/**
 * Created by nPlus on 12/19/16.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "UberClientForX";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_LOCATION = "table_location";
    public static final String USER_TABLE = "User";

    private Dao<DriverModel, Integer> mUserDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, DriverModel.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, DriverModel.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* User  This holds all the data on Userone object as getDao is a built in method*/

    public Dao<DriverModel, Integer> getUserDao() throws SQLException {
        if (mUserDao == null) {
            mUserDao = getDao(DriverModel.class);
        }

        return mUserDao;
    }
    public void deleteUser() throws SQLException {
        TableUtils.clearTable(getConnectionSource(), DriverModel.class);
    }

    @Override
    public void close() {
        mUserDao = null;
        super.close();
    }
}
