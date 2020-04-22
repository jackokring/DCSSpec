package uk.co.kring.android.dcs.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"madeBy"}), @Index(value = {"location", "when"}),
        @Index(value = {"when", "location"})})
public class Message {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "madeBy")
    public int madeBy;

    @ColumnInfo(name = "location")
    public int location;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "when")
    public long when;//some time conversion
}