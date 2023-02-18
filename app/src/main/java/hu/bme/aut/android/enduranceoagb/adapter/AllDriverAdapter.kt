package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.AlldriversListBinding
import hu.bme.aut.android.enduranceoagb.databinding.DriversListBinding

class AllDriverAdapter(private val listener: AllDriverItemClickListener) :
    RecyclerView.Adapter<AllDriverAdapter.AllDriverViewHolder>() {

    private val items = mutableListOf<Drivers>()

    private var positionDefault = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllDriverViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alldrivers_list, parent, false)
        return AllDriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllDriverViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val driverItem = items[position]

        holder.bind(driverItem)

    }

    override fun getItemCount(): Int = items.size

    interface AllDriverItemClickListener {
        //fun onItemClick(nameDriver: String?, weight: String?)
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

    inner class AllDriverViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = AlldriversListBinding.bind(itemView)
        var item: Drivers? = null

        init {
            //binding.root.setOnClickListener { listener.onItemClick(item!!.nameDriver, item!!.weight.toString()) }
            binding.root.setOnLongClickListener { listener.onItemLongClick(item) }
        }

        @SuppressLint("SetTextI18n")
        fun bind(newItem: Drivers) {
            item = newItem

            if (item!!.joker == true) {
                binding.tvNameAllDriver.setTextColor(Color.RED)
                binding.tvNumberAllDriver.setTextColor(Color.RED)
            }

            binding.tvNameAllDriver.text = newItem.nameDriver

            binding.tvNumberAllDriver.text = "Indulások száma: " + newItem.races
        }
    }
}