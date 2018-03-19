package com.jayeen.driver.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.jayeen.driver.newmodels.DriverModel;
import com.jayeen.driver.utills.AppLog;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by nPlus on 12/20/16.
 */

public class DatabaseAdapter {
    DatabaseHelper databaseHelper;
    Dao<DriverModel, Integer> userDao = null;

    public DatabaseAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }


    public void creatUser(DriverModel userone) {
        try {
            deleteUser();
            userDao = databaseHelper.getUserDao();
            userDao.create(userone);
        } catch (SQLException e) {
            AppLog.Log("DatabaseAdapter", e.getMessage());
        }
    }

    public void deleteUser() {
        try {
//            TableUtils.clearTable(databaseHelper.getConnectionSource(), Userone.class);
            databaseHelper.deleteUser();
        } catch (SQLException e) {
            AppLog.Log("DatabaseAdapter", e.getMessage());
        }
    }

    public DriverModel getUser() {
        DriverModel user = null;
        try {

            userDao = databaseHelper.getUserDao();
            List<DriverModel> users = userDao.queryForAll();
            if (users.size() > 0) {
                user = users.get(0);
            }

        } catch (SQLException e) {
            AppLog.Log("DatabaseAdapter", e.getMessage());
        }
        return user;
    }
}
