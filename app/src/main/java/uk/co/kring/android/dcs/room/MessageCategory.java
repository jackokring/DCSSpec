package uk.co.kring.android.dcs.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"message", "category", "madeBy"}, unique = true),
        @Index(value = {"category", "madeBy"}), @Index(value = {"madeBy", "category"})})
public class MessageCategory {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "madeBy")
    public int madeBy;

    @ColumnInfo(name = "category")
    public int category;

    @ColumnInfo(name = "message")
    public int message;
}