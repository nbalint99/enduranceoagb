package hu.bme.aut.android.enduranceoagb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class Drivers(
    @ColumnInfo(name = "nameDriver") @PrimaryKey(autoGenerate = false) var nameDriver: String,
    @ColumnInfo(name = "weight") var weight: Double? = null,
    @ColumnInfo(name = "races") var races: Int? = null,
    @ColumnInfo(name = "joker") var joker: Boolean? = null
)
