package com.example.MyProject.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM products")
    Single<List<Product>> getProducts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable add(Product product);

    @Query("DELETE FROM products WHERE id =:id")
    Completable remove(int id);

    @Query("UPDATE products SET proven =:proven, title =:title, url =:url WHERE id =:id")
    Completable update(String title,String url,int proven, int id);

}
