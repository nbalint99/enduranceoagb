package hu.bme.aut.android.enduranceoagb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class Teams(
    @ColumnInfo(name = "nameTeam") @PrimaryKey(autoGenerate = false) var nameTeam: String,
    @ColumnInfo(name = "people") var people: Int,
    @ColumnInfo(name = "teamNumber") var teamNumber: Int? = null,
    @ColumnInfo(name = "avgWeight") var avgWeight: Double? = null,
    @ColumnInfo(name = "hasDriversDone") var hasDriversDone: Int,
    @ColumnInfo(name = "startKartNumber") var startKartNumber: Int? = null,
    @ColumnInfo(name = "hasQualiDone") var hasQualiDone: Boolean,
    @ColumnInfo(name = "stintsDone") var stintsDone: Int? = null,
    @ColumnInfo(name = "gp2") var gp2: Boolean? = null,
    @ColumnInfo(name = "points") var points: Int? = null,
    @ColumnInfo(name = "shortTeamName") var shortTeamName: String? = null,
    @ColumnInfo(name = "group") var group: Int? = null,
    @ColumnInfo(name = "hasQualiResultDone") var hasQualiResultDone: Boolean? = null
)
