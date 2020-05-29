package uk.co.kring.android.dcs.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"name", "pubKey"})})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "pubKey")
    public String pubKey;

    /* @ColumnInfo(name = "priKey")
    public String priKey; */
}