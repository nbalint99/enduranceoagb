package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.DriversListBinding

class DriverAdapter(private val listener: DriverItemClickListener) :
    RecyclerView.Adapter<DriverAdapter.DriverViewHolder>() {

    private val items = mutableListOf<Drivers>()

    private var positionDefault = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.drivers_list, parent, false)
        return DriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val driverItem = items[position]

        holder.bind(driverItem)

    }

    override fun getItemCount(): Int = items.size

    interface DriverItemClickListener {
        fun onItemClick(nameDriver: String?, weight: String?)
        fun onItemLongClick(team: Drivers?): Boolean
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
    fun update2(driverItems: MutableList<Drivers>) {
        items.clear()
        items.addAll(driverItems)
        notifyDataSetChanged()
    }

    inner class DriverViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = DriversListBinding.bind(itemView)
        var item: Drivers? = null

        init {
            binding.root.setOnClickListener { listener.onItemClick(item!!.nameDriver, item!!.weight.toString()) }
            binding.root.setOnLongClickListener { listener.onItemLongClick(item) }
        }

        fun bind(newItem: Drivers) {
            item = newItem

            binding.tvNameDriver.text = newItem.nameDriver
            if (newItem.weight == null) {
                binding.tvWeightDriver.text = ""
            }
            else {
                binding.tvWeightDriver.text = newItem.weight.toString() + " kg"
            }
        }
    }
}