package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.BoxTime
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.DetailsstintListBinding
import hu.bme.aut.android.enduranceoagb.databinding.DetailsstintfragmentListBinding
import kotlin.math.roundToInt


class DetailsStintAdapter(private val listener: DetailsStintItemClickListener) :
    RecyclerView.Adapter<DetailsStintAdapter.DetailsStintViewHolder>() {


    private val items = mutableListOf<Stint>()

    private val itemsTeams = mutableListOf<Teams>()

    private val itemsDrivers = mutableListOf<Drivers>()

    private val numberOfStint = mutableListOf<Int>()

    private val itemsBox = mutableListOf<BoxTime>()

    private val itemsTime = mutableListOf<Double>()

    private var positionDefault = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailsStintViewHolder(
        DetailsstintListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DetailsStintViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val detailsStintItem = items[position]

        if (detailsStintItem.shortTeamName == null || detailsStintItem.shortTeamName == "null") {
            holder.binding.tvTeamName.text = detailsStintItem.teamName
        }
        else {
            holder.binding.tvTeamName.text = detailsStintItem.shortTeamName
        }

        holder.binding.tvTeamNumber.text = detailsStintItem.teamNumber.toString() + ". csapat"

        holder.binding.tvStintDriver.text = "Versenyző neve:"
        holder.binding.tvStintPlusWeight.text = "Plusz súly:"


        if (detailsStintItem.numberStint != 1) {
                        holder.binding.tvTeamName.setOnClickListener {
                for (i in itemsTeams) {
                    if (i.teamNumber == detailsStintItem.teamNumber) {
                        listener.onTeamListener(detailsStintItem.teamName, detailsStintItem.teamNumber.toString(), i.gp2)
                        break
                    }
                }
            }
        }

        if (detailsStintItem.hasStintDone) {
            //holder.binding.tvInfoText.text = "Megjegyzés: "
            holder.binding.tvStintDriverName.text = detailsStintItem.driverName
            holder.binding.tvStintPlusWeightValue.text = detailsStintItem.plusWeight.toString() + " kg"
            holder.binding.tvKartNumberStint.text = "Gokart: ${detailsStintItem.kartNumber}"
            /*if (detailsStintItem.info == "") {
                holder.binding.tvInfo.text = "-"
            }
            else {
                holder.binding.tvInfo.text = detailsStintItem.info
                holder.binding.tvInfo.setTextColor(Color.RED)
            }*/
            /*for (i in itemsTeams) {
                if (detailsStintItem.teamNumber == i.teamNumber) {
                    if (detailsStintItem.numberStint == 1) {
                        val avgWeight = detailsStintItem.plusWeight?.let {
                            detailsStintItem.driverWeight?.plus(
                                it
                            )
                        }

                        if (avgWeight != null) {
                            holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                        }
                        else {
                            holder.binding.tvAvgWeight.text = "Átlag súly: - "
                        }
                        break
                    }
                    else {
                        if (detailsStintItem.prevAvgWeight.toString() != "null" || detailsStintItem.prevAvgWeight != null) {
                            val prevAvgWeight = detailsStintItem.prevAvgWeight
                            for (element in itemsDrivers) {
                                if (element.nameDriver == detailsStintItem.driverName) {
                                    val driverWeight = element.weight!!
                                    val plusWeight = detailsStintItem.plusWeight
                                    val newWeight = driverWeight + plusWeight!!
                                    val totalWeight = prevAvgWeight?.plus(newWeight)
                                    val avgWeight = totalWeight?.div(i.stintsDone.toString().toDouble())

                                    if (avgWeight != null) {
                                        holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                                    }
                                    else {
                                        holder.binding.tvAvgWeight.text = "Átlag súly: - "
                                    }
                                    break
                                }
                            }
                        }
                        else {
                            val avgWeight = i.avgWeight?.div(i.stintsDone.toString().toDouble())

                            if (avgWeight != null) {
                                holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Átlag súly: - "
                            }
                            break
                        }
                    }

                }
            }*/
        }
        else if (!detailsStintItem.hasStintDone) {
            //holder.binding.tvInfoText.text = "Előző megjegyzése: "
            holder.binding.tvStintDriverName.text = ""
            holder.binding.tvStintPlusWeightValue.text = ""
            holder.binding.tvKartNumberStint.text = "Gokart: ${detailsStintItem.expectedKartNumber}"
            /*if (detailsStintItem.previousInfo == "" || detailsStintItem.previousInfo == null) {
                holder.binding.tvInfo.text = "-"
            }
            else {
                holder.binding.tvInfo.text = detailsStintItem.previousInfo
                holder.binding.tvInfo.setTextColor(Color.RED)
            }
            for (i in itemsTeams) {
                if (detailsStintItem.teamNumber == i.teamNumber) {
                    if (detailsStintItem.prevAvgWeight.toString() != "null" || detailsStintItem.prevAvgWeight != null) {
                        val avgWeight = detailsStintItem.prevAvgWeight?.div(i.stintsDone.toString().toDouble())
                        if ((numberOfStint[0] == detailsStintItem.numberStint) && !detailsStintItem.hasStintDone) {
                            val requiredWeight = 90.0 * detailsStintItem.numberStint
                            val needWeight = requiredWeight - i.avgWeight!!
                            if (needWeight < 0) {
                                holder.binding.tvAvgWeight.text = "Már megvan a minimum súly!"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Szükséges súly: ${((needWeight * 100.0).roundToInt() / 100.0)} kg"
                                holder.binding.tvAvgWeight.setTextColor(Color.RED)
                            }
                            break
                        }
                        else {
                            if (avgWeight != null) {
                                holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Átlag súly: - "
                            }
                            break
                        }
                    }
                    else {
                        val avgWeight = i.avgWeight?.div(i.stintsDone.toString().toDouble())
                        if ((numberOfStint[0] == detailsStintItem.numberStint) && !detailsStintItem.hasStintDone) {
                            val requiredWeight = 90.0 * detailsStintItem.numberStint
                            val needWeight = requiredWeight - i.avgWeight!!
                            if (needWeight < 0) {
                                holder.binding.tvAvgWeight.text = "Már megvan a minimum súly!"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Szükséges súly: ${((needWeight * 100.0).roundToInt() / 100.0)} kg"
                                holder.binding.tvAvgWeight.setTextColor(Color.RED)
                            }
                            break
                        }
                        else {
                            if (avgWeight != null) {
                                holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Átlag súly: - "
                            }
                            break
                        }
                    }

                }
            }*/
        }


        holder.binding.ibDone.setOnClickListener {
            listener.onNewStintListener(position, detailsStintItem.teamNumber, detailsStintItem.teamName, detailsStintItem.hasStintDone, detailsStintItem.driverName, detailsStintItem.plusWeight, detailsStintItem.shortTeamName, detailsStintItem.driverWeight, detailsStintItem.prevAvgWeight)
        }

    }

    override fun getItemCount(): Int = items.size

    interface DetailsStintItemClickListener {
        fun onNewStintListener(position: Int, teamNumber: Int, teamName: String, stintDone: Boolean, driverName: String?, plusWeight: Double?, shortTeamName: String?, driverWeight: Double?, prevTotalWeight: Double?)
        fun onTeamListener(teamName: String?, number: String?, gp2: Boolean?)
        fun dataChanged(position: Int, initTime: Double)
        fun dataChangedBool(position: Int)
        fun onNewBoxListener(position: Int, teamNumber: Int, stintDone: Boolean, nameTeam: String)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(detailsStintItems: List<Stint>) {
        items.clear()
        items.addAll(detailsStintItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(detailsStintItems: MutableList<Stint>) {
        items.clear()
        items.addAll(detailsStintItems)
        notifyDataSetChanged()
    }

    fun addItemStints(item: Stint) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun teams(detailsTeamItems: MutableList<Teams>) {
        itemsTeams.clear()
        itemsTeams.addAll(detailsTeamItems)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun drivers(detailsDriverItems: MutableList<Drivers>) {
        itemsDrivers.clear()
        itemsDrivers.addAll(detailsDriverItems)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun stints(stint: MutableList<Int>) {
        numberOfStint.clear()
        numberOfStint.addAll(stint)
    }

    fun addItem(item: BoxTime) {
        itemsBox.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: BoxTime) {
        itemsBox.remove(item)
        notifyItemRemoved(positionDefault)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2Box(itemsWatch: MutableList<BoxTime>) {
        itemsBox.clear()
        itemsBox.addAll(itemsWatch)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update3time(time: MutableList<Double>) {
        itemsTime.clear()
        itemsTime.addAll(time)
        notifyDataSetChanged()
    }

    inner class DetailsStintViewHolder(val binding: DetailsstintListBinding) : RecyclerView.ViewHolder(binding.root) {
        var item: BoxTime? = null

        //var countDownTimer : CountDownTimer? = null

        fun bind(newItem: BoxTime) {
            item = newItem
        }
    }
}