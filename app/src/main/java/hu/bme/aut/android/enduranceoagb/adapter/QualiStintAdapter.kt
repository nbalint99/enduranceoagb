package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.adapter.ResultAdapter.ResultViewHolder
import hu.bme.aut.android.enduranceoagb.data.BoxTime
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.DetailsqualistintfragmentBinding
import hu.bme.aut.android.enduranceoagb.databinding.DetailsstintListBinding
import hu.bme.aut.android.enduranceoagb.databinding.DetailsstintfragmentListBinding
import hu.bme.aut.android.enduranceoagb.databinding.ResultListBinding
import java.util.Locale
import kotlin.math.roundToInt


class QualiStintAdapter(private val listener: QualiStintItemClickListener) :
    RecyclerView.Adapter<QualiStintAdapter.QualiStintViewHolder>() {

    private val itemsTeams = mutableListOf<Teams>()

    private val itemsDrivers = mutableListOf<Drivers>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualiStintViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detailsqualistintfragment, parent, false)
        return QualiStintViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: QualiStintViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val detailsStintItem = itemsTeams[position]

        if (detailsStintItem.shortTeamName == null || detailsStintItem.shortTeamName == "null") {
            holder.binding.tvTeamName.text = detailsStintItem.nameTeam
        }
        else {
            holder.binding.tvTeamName.text = detailsStintItem.shortTeamName
        }




        holder.binding.tvStintDriver.text = "Versenyző neve:"
        holder.binding.tvStintPlusWeight.text = "Plusz súly:"

        holder.binding.tvStintDriver2.text = "Versenyző neve:"
        holder.binding.tvStintPlusWeight2.text = "Plusz súly:"

        if (detailsStintItem.qualiName1 == null || detailsStintItem.qualiName1 == "null") {
            holder.binding.tvStintDriverName.text = ""
            holder.binding.tvStintPlusWeightValue.text = ""
        }
        else {
            holder.binding.tvStintDriverName.text = detailsStintItem.qualiName1
            holder.binding.tvStintPlusWeightValue.text = detailsStintItem.qualiWeight1.toString() + " kg"
        }

        if (detailsStintItem.qualiName2 == null || detailsStintItem.qualiName2 == "null") {
            holder.binding.tvStintDriverName2.text = ""
            holder.binding.tvStintPlusWeightValue2.text = ""
            holder.binding.tvTeamNumber.text = "Eddigi súly: ${detailsStintItem.qualiTotalWeight} kg"
        }
        else {
            holder.binding.tvStintDriverName2.text = detailsStintItem.qualiName2
            holder.binding.tvStintPlusWeightValue2.text = detailsStintItem.qualiWeight2.toString() + " kg"
            val avgWeight = (detailsStintItem.qualiTotalWeight?.plus(0.09))?.div(2.0)
            if (avgWeight != null) {
                val roundedValue = String.format(Locale.US, "%.1f", avgWeight).toDouble()
                if (avgWeight < 90.0) {
                    holder.binding.tvTeamNumber.text = "Átlag: $roundedValue kg"
                }
                else {
                    holder.binding.tvTeamNumber.setTextColor(Color.GREEN)
                    holder.binding.tvTeamNumber.text = "Átlag: $roundedValue kg"
                }
            }

        }
        holder.binding.tvKartNumberStint.text = "Gokart: ${detailsStintItem.startKartNumber}"


        holder.binding.ibDone.setOnClickListener {
            listener.onNewStintListener(1, detailsStintItem.nameTeam, detailsStintItem.shortTeamName)
        }
        holder.binding.ibDone2.setOnClickListener {
            listener.onNewStintListener(2, detailsStintItem.nameTeam, detailsStintItem.shortTeamName)
        }

    }

    override fun getItemCount(): Int = itemsTeams.size

    interface QualiStintItemClickListener {
        fun onNewStintListener(stint: Int, teamName: String, shortTeamName: String?)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(detailsStintItems: MutableList<Teams>) {
        itemsTeams.clear()
        itemsTeams.addAll(detailsStintItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(detailsStintItems: MutableList<Teams>) {
        itemsTeams.clear()
        itemsTeams.addAll(detailsStintItems)
        notifyDataSetChanged()
    }

    fun addItemStints(item: Teams) {
        itemsTeams.add(item)
        notifyItemInserted(itemsTeams.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun teams(detailsTeamItems: MutableList<Teams>) {
        itemsTeams.clear()
        itemsTeams.addAll(detailsTeamItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun drivers(detailsDriverItems: MutableList<Drivers>) {
        itemsDrivers.clear()
        itemsDrivers.addAll(detailsDriverItems)
    }


    inner class QualiStintViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = DetailsqualistintfragmentBinding.bind(itemView)
    }
}