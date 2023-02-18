package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.databinding.DetailsteamcheckListBinding

class DetailsTeamCheckAdapter(private val listener: DetailsTeamCheckItemClickListener) :
    RecyclerView.Adapter<DetailsTeamCheckAdapter.DetailsTeamCheckViewHolder>() {

    private val items = mutableListOf<Drivers>()

    private val itemsStint = mutableListOf<Stint>()

    private var positionDefault = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailsTeamCheckViewHolder(
        DetailsteamcheckListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DetailsTeamCheckViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val driverItem = items[position]

        var weightPlus : Double? = null

        var kartNumber : Int? = null

        for (element in itemsStint) {
            if (element.numberStint == position + 1) {
                weightPlus = element.plusWeight
                kartNumber = element.kartNumber
                break
            }
        }

        val weight = driverItem.weight?.plus(weightPlus.toString().toDouble())

        holder.binding.tvNameDriver.text = driverItem.nameDriver
        holder.binding.tvWeight.text = "$weight kg"

        holder.binding.tvKartNumberCheck.text = "Gokart: " + kartNumber.toString()

        holder.binding.tvWeightDetails.text = "(Plusz súly: $weightPlus kg)"
    }

    override fun getItemCount(): Int = items.size

    interface DetailsTeamCheckItemClickListener {
    }

    fun addItem(item: Drivers) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: Drivers) {
        items.remove(item)
        notifyItemRemoved(positionDefault)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(driversItems: List<Drivers>) {
        items.clear()
        items.addAll(driversItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(driversItems: MutableList<Drivers>) {
        items.clear()
        items.addAll(driversItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateStint(detailsStintItems: List<Stint>) {
        itemsStint.clear()
        itemsStint.addAll(detailsStintItems)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateStint2(detailsStintItems: MutableList<Stint>) {
        itemsStint.clear()
        itemsStint.addAll(detailsStintItems)
    }

    inner class DetailsTeamCheckViewHolder(val binding: DetailsteamcheckListBinding) : RecyclerView.ViewHolder(binding.root)
}