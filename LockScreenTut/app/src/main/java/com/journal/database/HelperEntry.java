package com.journal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.journal.database.model.Entry;

import java.util.ArrayList;
import java.util.List;

public class HelperEntry extends DBHelper {

    public HelperEntry(Context context) {
        super(context);
    }

    public long insertEntry(String entry) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Entry.COLUMN_TITTLE, entry);

        // insert row
        long id = db.insert(Entry.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Entry getEntry(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Entry.TABLE_NAME,
                new String[]{Entry.COLUMN_ID, Entry.COLUMN_TIMESTAMP, Entry.COLUMN_TITTLE},
                Entry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Entry note = new Entry(
                cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Entry.COLUMN_TIMESTAMP)),
                cursor.getString(cursor.getColumnIndex(Entry.COLUMN_TITTLE)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<Entry> getAllEntries() {
        List<Entry> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Entry.TABLE_NAME + " ORDER BY " +
                Entry.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Entry note = new Entry();
                note.setId(cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_ID)));
                note.setTittle(cursor.getString(cursor.getColumnIndex(Entry.COLUMN_TITTLE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Entry.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getEntriesCount() {
        String countQuery = "SELECT  * FROM " + Entry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateEntry(Entry note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_TITTLE, note.getTittle());

        // updating row
        return db.update(Entry.TABLE_NAME, values, Entry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteEntry(Entry note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Entry.TABLE_NAME, Entry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
