package uk.co.kring.android.dcs.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"madeBy"}), @Index(value = {"kind", "madeBy"}, unique = true)})
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "madeBy")
    public int madeBy;

    @ColumnInfo(name = "kind")
    public String kind;
}