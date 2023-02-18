package hu.bme.aut.android.enduranceoagb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "result")
data class Result(
    @ColumnInfo(name = "nameTeam") @PrimaryKey(autoGenerate = false) var nameTeam: String,
    @ColumnInfo(name = "gp2") var gp2: Boolean? = null,
    @ColumnInfo(name = "result") var result: Int
)