package uk.co.kring.android.dcs.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"madeBy"}),
        @Index(value = {"address", "madeBy"}, unique = true)})
public class Location {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "madeBy")
    public int madeBy;

    @ColumnInfo(name = "lat")
    public float lat;

    @ColumnInfo(name = "lon")
    public float lon;
}