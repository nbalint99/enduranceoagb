package hu.bme.aut.android.enduranceoagb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch")
data class Watch(
    @ColumnInfo(name = "teamNumber") var teamNumber: Int,
    @ColumnInfo(name = "time") var time: Double,
    @ColumnInfo(name = "hasDone") var hasDone: Boolean,
    @ColumnInfo(name = "initialTime") var initialTime: Double
)
