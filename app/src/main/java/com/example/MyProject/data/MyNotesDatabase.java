package com.example.MyProject.data;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {MyNote.class}, version = 1)
public abstract class MyNotesDatabase extends RoomDatabase {
    private static final String DB_NAME = "my_notes.db";
    private static MyNotesDatabase instance = null;

    public static MyNotesDatabase newInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(application, MyNotesDatabase.class, DB_NAME)
                    .build();
        }
        return instance;
    }

    public abstract MyNoteDao productDao();
}