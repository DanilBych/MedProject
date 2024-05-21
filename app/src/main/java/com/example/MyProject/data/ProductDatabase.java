package com.example.MyProject.data;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Product.class}, version = 1)
public abstract class ProductDatabase extends RoomDatabase {
    private static final String DB_NAME = "database_room.db";
    private static ProductDatabase instance = null;

    public static ProductDatabase newInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(application, ProductDatabase.class, DB_NAME)
                    .build();
        }
        return instance;
    }

    public abstract ProductDao productDao();
}
