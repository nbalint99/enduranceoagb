package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.databinding.RacesListBinding

class RaceAdapter(private val listener: RaceItemClickListener) :
    RecyclerView.Adapter<RaceAdapter.RaceViewHolder>() {

    private val items = mutableListOf<Races>()

    private var positionDefault = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.races_list, parent, false)
        return RaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: RaceViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val raceItem = items[position]

        holder.bind(raceItem)

    }

    override fun getItemCount(): Int = items.size

    interface RaceItemClickListener {
        fun onItemClick(race: Races?)
        fun onItemLongClick(race: Races?): Boolean
        fun onModifyRaceListener(key: String, location: String, numberOfTeams: Int, nameRace: String)
    }

    fun addItem(item: Races) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: Races?) {
        items.remove(item)
        notifyItemRemoved(positionDefault)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(raceItems: MutableList<Races>) {
        items.clear()
        items.addAll(raceItems)
        notifyDataSetChanged()
    }

    inner class RaceViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = RacesListBinding.bind(itemView)
        var item: Races? = null

        init {
            binding.root.setOnClickListener { listener.onItemClick(item) }
            binding.root.setOnLongClickListener {
                listener.onItemLongClick(item)
            }
        }

        fun bind(newItem: Races) {
            item = newItem

            binding.tvName.text = item!!.nameR
            binding.tvLocation.text = item!!.location

            binding.ibForward.setOnClickListener {
                listener.onModifyRaceListener(item!!.id_r.toString(), item!!.location, item!!.numberOfTeams, item!!.nameR)
            }
        }
    }
}


