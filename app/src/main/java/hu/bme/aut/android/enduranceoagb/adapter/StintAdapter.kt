package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.DoneStint
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.databinding.StintListBinding


class StintAdapter(private val listener: StintItemClickListener) :
    RecyclerView.Adapter<StintAdapter.StintViewHolder>() {

    private val items = mutableListOf<DoneStint>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StintViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stint_list, parent, false)
        return StintViewHolder(view)
    }

    override fun onBindViewHolder(holder: StintViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val stintItem = items[position]

        holder.bind(stintItem)

    }

    override fun getItemCount(): Int = items.size

    interface StintItemClickListener {
        fun onStintSelected(position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(stintItems: List<DoneStint>) {
        items.clear()
        items.addAll(stintItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(stintItems: MutableList<DoneStint>) {
        items.clear()
        items.addAll(stintItems)
        notifyDataSetChanged()
    }

    inner class StintViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = StintListBinding.bind(itemView)
        var item: DoneStint? = null

        init {
            binding.root.setOnClickListener { listener.onStintSelected(item!!.numberOfStint) }
        }

        fun bind(newItem: DoneStint) {
            item = newItem

            binding.tvStint.text = item?.numberOfStint.toString() + ". etap"

            binding.tvZeroToUp.text = item?.zeroToUp.toString()
            binding.tvUpToZero.text = item?.upToZero.toString()
        }
    }
}
