package hu.bme.aut.android.enduranceoagb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "allteams")
data class AllTeams(
    @ColumnInfo(name = "nameTeam") @PrimaryKey(autoGenerate = false) var nameTeam: String,
    @ColumnInfo(name = "people") var people: Int?,
    @ColumnInfo(name = "joker") var joker: Int? = null,
    @ColumnInfo(name = "hasJokerRaced") var hasJokerRaced: Boolean? = null,
    @ColumnInfo(name = "points") var points: Int? = null,
    @ColumnInfo(name = "oldPoints") var oldPoints: Int? = null,
    @ColumnInfo(name = "gp2Points") var gp2Points: Int? = null,
    @ColumnInfo(name = "oldGp2Points") var oldGp2Points: Int? = null,
    @ColumnInfo(name = "gp2") var gp2: Boolean? = null,
    @ColumnInfo(name = "racesTeam") var racesTeam: Int
)
