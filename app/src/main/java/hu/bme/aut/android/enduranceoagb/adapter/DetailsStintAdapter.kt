package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.DetailsstintListBinding
import kotlin.math.roundToInt


class DetailsStintAdapter(private val listener: DetailsStintItemClickListener) :
    RecyclerView.Adapter<DetailsStintAdapter.DetailsStintViewHolder>() {


    private val items = mutableListOf<Stint>()

    private val itemsTeams = mutableListOf<Teams>()

    private val itemsDrivers = mutableListOf<Drivers>()

    private val numberOfStint = mutableListOf<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailsStintViewHolder(
        DetailsstintListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DetailsStintViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val detailsStintItem = items[position]

        if (detailsStintItem.shortTeamName == null) {
            holder.binding.tvTeamName.text = detailsStintItem.teamName
        }
        else {
            holder.binding.tvTeamName.text = detailsStintItem.shortTeamName
        }


        holder.binding.tvTeamNumber.text = detailsStintItem.teamNumber.toString() + ". csapat"

        holder.binding.tvStintDriver.text = "Versenyző neve:"
        holder.binding.tvStintPlusWeight.text = "Plusz súly:"


        if (detailsStintItem.numberStint != 1) {
            holder.binding.tvPrevDriver.text = detailsStintItem.prevDriverName
            holder.binding.tvPrevKart.text = "Gokart: " + detailsStintItem.prevKartNumber
            holder.binding.tvPrevWeight.text = detailsStintItem.prevPlusWeight.toString() + " kg"
            holder.binding.tvComing.text = "Érkezik:"
            holder.binding.tvPrevDriver.setTextColor(Color.BLUE)
            holder.binding.tvPrevKart.setTextColor(Color.BLUE)
            holder.binding.tvPrevWeight.setTextColor(Color.BLUE)
            holder.binding.tvComing.setTextColor(Color.BLUE)

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
            holder.binding.tvInfoText.text = "Megjegyzés: "
            holder.binding.tvStintDriverName.text = detailsStintItem.driverName
            holder.binding.tvStintPlusWeightValue.text = detailsStintItem.plusWeight.toString() + " kg"
            holder.binding.tvKartNumberStint.text = "Gokart: ${detailsStintItem.kartNumber}"
            if (detailsStintItem.info == "") {
                holder.binding.tvInfo.text = "-"
            }
            else {
                holder.binding.tvInfo.text = detailsStintItem.info
                holder.binding.tvInfo.setTextColor(Color.RED)
            }
            for (i in itemsTeams) {
                if (detailsStintItem.teamNumber == i.teamNumber) {
                    if (detailsStintItem.prevAvgWeight != null) {
                        val prevAvgWeight = detailsStintItem.prevAvgWeight
                        for (element in itemsDrivers) {
                            if (element.nameDriver == detailsStintItem.driverName) {
                                val driverWeight = element.weight!!
                                val plusWeight = detailsStintItem.plusWeight
                                val newWeight = driverWeight + plusWeight!!
                                val totalWeight = prevAvgWeight?.plus(newWeight)
                                val avgWeight = totalWeight?.div(i.stintsDone.toString().toDouble())
                                /*if ((numberOfStint[0] == detailsStintItem.numberStint) && !detailsStintItem.hasStintDone) {
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
                                else {*/
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
                        /*if ((numberOfStint[0] == detailsStintItem.numberStint) && !detailsStintItem.hasStintDone) {
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
                        else {*/
                        if (avgWeight != null) {
                            holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                        }
                        else {
                            holder.binding.tvAvgWeight.text = "Átlag súly: - "
                        }
                        break
                    }


                    //}
                }
            }
        }
        else if (!detailsStintItem.hasStintDone) {
            holder.binding.tvInfoText.text = "Előző megjegyzése: "
            holder.binding.tvStintDriverName.text = ""
            holder.binding.tvStintPlusWeightValue.text = ""
            holder.binding.tvKartNumberStint.text = "Gokart: ${detailsStintItem.expectedKartNumber}"
            if (detailsStintItem.previousInfo == "" || detailsStintItem.previousInfo == null) {
                holder.binding.tvInfo.text = "-"
            }
            else {
                holder.binding.tvInfo.text = detailsStintItem.previousInfo
                holder.binding.tvInfo.setTextColor(Color.RED)
            }
            for (i in itemsTeams) {
                if (detailsStintItem.teamNumber == i.teamNumber) {
                    if (detailsStintItem.prevAvgWeight != null) {
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
            }
        }


        holder.binding.ibDone.setOnClickListener {
            listener.onNewStintListener(position, detailsStintItem.teamNumber, detailsStintItem.teamName, detailsStintItem.hasStintDone, detailsStintItem.driverName, detailsStintItem.plusWeight, detailsStintItem.shortTeamName)
        }
    }

    override fun getItemCount(): Int = items.size

    interface DetailsStintItemClickListener {
        fun onNewStintListener(position: Int, teamNumber: Int, teamName: String, stintDone: Boolean, driverName: String?, plusWeight: Double?, shortTeamName: String?)
        fun onTeamListener(teamName: String?, number: String?, gp2: Boolean?)
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

    inner class DetailsStintViewHolder(val binding: DetailsstintListBinding) : RecyclerView.ViewHolder(binding.root)
}