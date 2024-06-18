package hu.bme.aut.android.enduranceoagb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "races")
data class Races(
    @ColumnInfo(name = "id_r") @PrimaryKey(autoGenerate = false) var id_r: String? = null,
    @ColumnInfo(name = "nameR") var nameR: String,
    @ColumnInfo(name = "location") var location: String,
    @ColumnInfo(name = "numberOfTeams") var numberOfTeams: Int,
    @ColumnInfo(name = "allStintNumber") var allStintNumber: Int, //mindig eggyel több mint a felette levő
    @ColumnInfo(name = "hasStintReady") var hasStintReady: Boolean,
    @ColumnInfo(name = "hasRaceDone") var hasRaceDone: Boolean,
    @ColumnInfo(name = "petrolDone") var petrolDone: Boolean,
    @ColumnInfo(name = "hasTeamsDone") var hasTeamsDone: Int,
    @ColumnInfo(name = "hasResultsDone") var hasResultsDone: Boolean,
    @ColumnInfo(name = "hasRaceDone") var hasQualiDone: Int,
    @ColumnInfo(name = "numberOfRace") var numberOfRace: Int? = null,
    @ColumnInfo(name = "hasGroupDone") var hasGroupDone: Boolean? = null,
    @ColumnInfo(name = "secondGroup") var secondGroup: Int? = null,
    @ColumnInfo(name = "firstMore") var firstMore: Boolean? = null,
    @ColumnInfo(name = "secondMore") var secondMore: Boolean? = null,
    @ColumnInfo(name = "equalGroup") var equalGroup: Boolean? = null,
    @ColumnInfo(name = "hasTeamsCreated") var hasTeamsCreated: Boolean? = null,
    @ColumnInfo(name = "hasFinalTeamsCreated") var hasFinalTeamsCreated: Boolean? = null

)
