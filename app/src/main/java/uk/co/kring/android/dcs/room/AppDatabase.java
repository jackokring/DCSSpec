package uk.co.kring.android.dcs.room;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { User.class, Self.class, Message.class,
        Location.class, Category.class, MessageCategory.class }, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    static AppDatabase db;
    public abstract AppDatabaseDAO appDatabaseDAO();
    public static AppDatabase getInstance(Context c) {
        if(db != null) return db;
        return db = Room.databaseBuilder(c,
                AppDatabase.class, "App").build();
    }
}