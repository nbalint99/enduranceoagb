package hu.bme.aut.android.enduranceoagb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boxTime")
data class BoxTime(
    @ColumnInfo(name = "teamNumber") var teamNumber: Int,
    @ColumnInfo(name = "initialTime") var initialTime: Double,
    @ColumnInfo(name = "hasDone") var hasDone: Boolean,
    @ColumnInfo(name = "prevPenaltyTime") var prevPenaltyTime: Double? = null,
    @ColumnInfo(name = "actualTime") var actualTime: Double? = null,
    @ColumnInfo(name = "penaltyTime") var penaltyTime: Double? = null,
    @ColumnInfo(name = "nextTime") var nextTime: Double? = null
)
