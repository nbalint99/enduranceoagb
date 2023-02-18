package hu.bme.aut.android.enduranceoagb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "doneStint")
data class DoneStint(
    @ColumnInfo(name = "numberOfStint") @PrimaryKey(autoGenerate = false) var numberOfStint: Int, //az etap száma/hányadik etap
    @ColumnInfo(name = "hasStintDone") var hasStintDone: Boolean, //befejeződött-e már a komplett csere
    @ColumnInfo(name = "hasDetailsStintReady") var hasDetailsStintReady: Boolean, //inicializálódott-e már az adott csere elemei
    @ColumnInfo(name = "zeroToUp") var zeroToUp: String, //csere időtartalma 0-tól 3 óra fele
    @ColumnInfo(name = "upToZero") var upToZero: String //csere időtartalma 3 órától 0-ig
)