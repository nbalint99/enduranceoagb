package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ResultListBinding

class ResultAdapter(private val listener: ResultItemClickListener) :
    RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    private val items = mutableListOf<Teams>()

    private val results = mutableListOf<String>()

    private var positionDefault = -1

    private var done = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.result_list, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val teamItem = items[position]


        holder.binding.tvName.text = results[position]

        holder.bind(teamItem)

        holder.binding.tvPlace.text = "${position+1}."

    }

    override fun getItemCount(): Int = items.size

    interface ResultItemClickListener {
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

    @SuppressLint("NotifyDataSetChanged")
    fun update3(resultItems: MutableList<String>) {
        results.clear()
        results.addAll(resultItems)
        notifyDataSetChanged()
    }

    inner class ResultViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = ResultListBinding.bind(itemView)
        var item: Teams? = null

        init {
            binding.root.setOnClickListener { listener.onItemClick(item?.nameTeam, item?.teamNumber.toString(), item?.gp2) }
        }

        fun bind(newItem: Teams) {
            item = newItem

            /*if (item?.gp2 == true) {
                binding.tvName.text = item?.nameTeam + " (GP2)"
            }
            else {
                binding.tvName.text = item?.nameTeam
            }*/
        }
    }
}