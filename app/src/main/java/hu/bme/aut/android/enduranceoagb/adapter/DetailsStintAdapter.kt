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


class DetailsStintAdapter(private val listener: DetailsStintItemClickListener) :
    RecyclerView.Adapter<DetailsStintAdapter.DetailsStintViewHolder>() {


    private val items = mutableListOf<Stint>()

    private val itemsTeams = mutableListOf<Teams>()

    private val itemsDrivers = mutableListOf<Drivers>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailsStintViewHolder(
        DetailsstintListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DetailsStintViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val detailsStintItem = items[position]

        holder.binding.tvTeamName.text = detailsStintItem.teamName

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
        }


        holder.binding.ibDone.setOnClickListener {
            listener.onNewStintListener(position, detailsStintItem.teamNumber, detailsStintItem.teamName, detailsStintItem.hasStintDone, detailsStintItem.driverName, detailsStintItem.plusWeight)
        }
    }

    override fun getItemCount(): Int = items.size

    interface DetailsStintItemClickListener {
        fun onNewStintListener(position: Int, teamNumber: Int, teamName: String, stintDone: Boolean, driverName: String?, plusWeight: Double?)
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

    inner class DetailsStintViewHolder(val binding: DetailsstintListBinding) : RecyclerView.ViewHolder(binding.root)
}