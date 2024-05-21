package com.example.MyProject.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Objects;

@Entity(tableName = "notes")
public class MyNote extends Product{
    @ColumnInfo(name = "note")
    String note;
    @ColumnInfo(name = "description")
    String description;



    public MyNote(String title, int proven, String url, String note, String description) {
        super(title, proven, url);
        this.note = note;
        this.description = description;
    }

    public String getNote() {
        return note;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "MyProduct{" +
                "note='" + note + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MyNote myNote = (MyNote) o;
        return Objects.equals(note, myNote.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), note);
    }
}
