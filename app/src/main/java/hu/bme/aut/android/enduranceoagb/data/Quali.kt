package hu.bme.aut.android.enduranceoagb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Quali")
data class Quali(
    @ColumnInfo(name = "team") @PrimaryKey(autoGenerate = false) var team: String,
    @ColumnInfo(name = "longTeamName") var longTeamName: String,
    @ColumnInfo(name = "gp2") var gp2: Boolean,
    @ColumnInfo(name = "result") var result: Int,
    @ColumnInfo(name = "group") var group: Int? = null
)