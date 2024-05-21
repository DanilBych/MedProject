package com.example.MyProject.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MyNoteDao {

    @Query("SELECT * FROM notes")
    Single<List<MyNote>> getProducts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable add(MyNote product);

    @Query("DELETE FROM notes WHERE id =:id")
    Completable remove(int id);

    @Query("UPDATE notes SET proven =:proven, title =:title, url =:url, note =:note, description =:description WHERE id =:id")
    Completable update(String title, String note, String description, String url, int proven, int id);

}