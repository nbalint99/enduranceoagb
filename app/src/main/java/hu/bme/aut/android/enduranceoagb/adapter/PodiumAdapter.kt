package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.data.Result
import hu.bme.aut.android.enduranceoagb.databinding.PodiumListBinding

class PodiumAdapter(private val listener: PodiumItemClickListener) :
    RecyclerView.Adapter<PodiumAdapter.PodiumViewHolder>() {

    private val items = mutableListOf<Result>()

    private val teams = mutableListOf<String>()

    private val drivers = mutableListOf<String>()

    private var positionDefault = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodiumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.podium_list, parent, false)
        return PodiumViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodiumViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val teamItem = items[position]

        //holder.bind(teamItem)

        holder.binding.tvPlacePodium.text = (position + 1).toString() + ". hely"

        holder.binding.tvNamePodium.text = teams[position]

        holder.binding.tvDriversPodium.text = drivers[position]

        when (position) {
            0 -> {
                holder.binding.tvNamePodium.setTextColor(Color.rgb(255,215,0))
            }
            1 -> {
                holder.binding.tvNamePodium.setTextColor(Color.rgb(192,192,192))
            }
            2 -> {
                holder.binding.tvNamePodium.setTextColor(Color.rgb(160, 121, 89))
            }
        }

    }

    override fun getItemCount(): Int = items.size

    interface PodiumItemClickListener {
    }

    fun addItem(item: Result) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: Result) {
        items.remove(item)
        notifyItemRemoved(positionDefault)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(teamItems: MutableList<Result>) {
        items.clear()
        items.addAll(teamItems)
        notifyDataSetChanged()
    }

    fun update3(teamItems: MutableList<String>) {
        teams.clear()
        teams.addAll(teamItems)
        notifyDataSetChanged()
    }

    fun update4(driversItems: MutableList<String>) {
        drivers.clear()
        drivers.addAll(driversItems)
        notifyDataSetChanged()
    }

    inner class PodiumViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = PodiumListBinding.bind(itemView)
        var item: Result? = null

        /*init {
        }

        fun bind(newItem: Result) {
            item = newItem

        }*/
    }
}