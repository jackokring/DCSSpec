package uk.co.kring.android.dcs.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "public")
    public String pubKey;

    @ColumnInfo(name = "private")
    public String priKey;
}