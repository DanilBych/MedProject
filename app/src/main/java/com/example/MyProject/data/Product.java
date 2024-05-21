package com.example.MyProject.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "products")
public class Product implements Serializable {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "title")
    private final String title;
    @ColumnInfo(name = "proven")
    private final int proven;
    @ColumnInfo(name = "url")
    private final String url;

    public Product(String title, int proven, String url) {
        this(0, title, proven, url);
    }

    @Ignore
    public Product(int id, String title, int proven, String url) {
        this.id = id;
        this.title = title;
        this.proven = proven;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getProven() {
        return proven;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Product{" +
                "title='" + title + '\'' +
                ", proven='" + proven + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Objects.equals(title, product.title) && Objects.equals(proven, product.proven) && Objects.equals(url, product.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, proven, url);
    }
}
