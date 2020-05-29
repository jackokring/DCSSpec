package uk.co.kring.android.dcs.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"madeBy", "firebaseID"})})
public class Self {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "madeBy")
    public int madeBy;

    @ColumnInfo(name = "firebaseID")
    public String FBID;

    @ColumnInfo(name = "priKey")
    public String priKey;
}