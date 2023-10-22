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
    @ColumnInfo(name = "racesTeam") var racesTeam: Int,
    @ColumnInfo(name = "totalPoints") var totalPoints: Int? = null,
    @ColumnInfo(name = "totalGp2Points") var totalGp2Points: Int? = null,
    @ColumnInfo(name = "one") var one: Int? = null,
    @ColumnInfo(name = "two") var two: Int? = null,
    @ColumnInfo(name = "three") var three: Int? = null,
    @ColumnInfo(name = "four") var four: Int? = null,
    @ColumnInfo(name = "five") var five: Int? = null,
    @ColumnInfo(name = "six") var six: Int? = null,
    @ColumnInfo(name = "seven") var seven: Int? = null,
    @ColumnInfo(name = "eight") var eight: Int? = null,
    @ColumnInfo(name = "nine") var nine: Int? = null,
    @ColumnInfo(name = "ten") var ten: Int? = null,
    @ColumnInfo(name = "eleven") var eleven: Int? = null,
    @ColumnInfo(name = "twelve") var twelve: Int? = null,
    @ColumnInfo(name = "thirteen") var thirteen: Int? = null,
    @ColumnInfo(name = "fourteen") var fourteen: Int? = null,
    @ColumnInfo(name = "fifteen") var fifteen: Int? = null,
    @ColumnInfo(name = "oneGp2") var oneGp2: Int? = null,
    @ColumnInfo(name = "twoGp2") var twoGp2: Int? = null,
    @ColumnInfo(name = "threeGp2") var threeGp2: Int? = null,
    @ColumnInfo(name = "fourGp2") var fourGp2: Int? = null,
    @ColumnInfo(name = "fiveGp2") var fiveGp2: Int? = null,
    @ColumnInfo(name = "sixGp2") var sixGp2: Int? = null,
    @ColumnInfo(name = "sevenGp2") var sevenGp2: Int? = null,
    @ColumnInfo(name = "eightGp2") var eightGp2: Int? = null,
    @ColumnInfo(name = "nineGp2") var nineGp2: Int? = null,
    @ColumnInfo(name = "tenGp2") var tenGp2: Int? = null,
    @ColumnInfo(name = "elevenGp2") var elevenGp2: Int? = null,
    @ColumnInfo(name = "twelveGp2") var twelveGp2: Int? = null,
    @ColumnInfo(name = "thirteenGp2") var thirteenGp2: Int? = null,
    @ColumnInfo(name = "fourteenGp2") var fourteenGp2: Int? = null,
    @ColumnInfo(name = "fifteenGp2") var fifteenGp2: Int? = null


)
