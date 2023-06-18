package hu.bme.aut.android.enduranceoagb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stint")
data class Stint(
    @ColumnInfo(name = "teamName") var teamName: String, //csapatnév
    @ColumnInfo(name = "teamNumber") var teamNumber: Int, //csapatsorszám
    @ColumnInfo(name = "driverName") var driverName: String? = null, //versenyző sorszáma
    @ColumnInfo(name = "numberStint") var numberStint: Int, //az etap száma/hányadik etap
    @ColumnInfo(name = "shortTeamName") var shortTeamName: String? = null,
    @ColumnInfo(name = "plusWeight") var plusWeight: Double? = null,
    @ColumnInfo(name = "info") var info: String? = null, //plusz infó, megjegyzés
    @ColumnInfo(name = "previousInfo") var previousInfo: String? = null, //előző etap plusz infója, megjegyzése
    @ColumnInfo(name = "hasStintDone") var hasStintDone: Boolean,
    @ColumnInfo(name = "prevAvgWeight") var prevAvgWeight: Double? = null,
    @ColumnInfo(name = "driverWeight") var driverWeight: Double? = null,
    @ColumnInfo(name = "kartNumber") var kartNumber: Int? = null,
    @ColumnInfo(name = "expectedKartNumber") var expectedKartNumber: Int? = null,
    @ColumnInfo(name = "prevDriverName") var prevDriverName: String? = null,
    @ColumnInfo(name = "prevPlusWeight") var prevPlusWeight: Double? = null,
    @ColumnInfo(name = "prevKartNumber") var prevKartNumber: Int? = null
)
