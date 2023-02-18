package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.TeamscheckListBinding


class TeamCheckAdapter(private val listener: TeamCheckItemClickListener) :
    RecyclerView.Adapter<TeamCheckAdapter.TeamCheckViewHolder>() {

    private val items = mutableListOf<Teams>()

    private var positionDefault = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamCheckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.teamscheck_list, parent, false)
        return TeamCheckViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamCheckViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val teamItem = items[position]

        holder.bind(teamItem)

    }

    override fun getItemCount(): Int = items.size

    interface TeamCheckItemClickListener {
        fun onTeamSelected(position: String?, number: String?, gp2: Boolean?)
        fun onItemClick(position: String?, number: String?, gp2: Boolean?)
    }

    fun addItem(item: Teams) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: Teams) {
        items.remove(item)
        notifyItemRemoved(positionDefault)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(teamsItems: List<Teams>) {
        items.clear()
        items.addAll(teamsItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(teamItems: MutableList<Teams>) {
        items.clear()
        items.addAll(teamItems)
        notifyDataSetChanged()
    }

    inner class TeamCheckViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = TeamscheckListBinding.bind(itemView)
        var item: Teams? = null

        init {
            binding.root.setOnClickListener { listener.onItemClick(item?.nameTeam, item?.teamNumber.toString(), item?.gp2) }
        }

        fun bind(newItem: Teams) {
            item = newItem

            if (item?.gp2 == true) {
                binding.tvName.text = item?.nameTeam + " (GP2)"
            }
            else {
                binding.tvName.text = item?.nameTeam
            }

            binding.tvNumberOfTeam.text = item?.teamNumber.toString() + ". csapat"


            binding.ibForward.setOnClickListener {
                listener.onTeamSelected(item?.nameTeam, item?.teamNumber.toString(), item?.gp2)
            }
        }
    }
}
