package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.AllcheckListBinding
import hu.bme.aut.android.enduranceoagb.databinding.TeamscheckListBinding
import kotlin.math.roundToInt

class AllCheckAdapter(private val listener: AllCheckItemClickListener) :
    RecyclerView.Adapter<AllCheckAdapter.AllCheckViewHolder>() {

    private val items = mutableListOf<Teams>()

    private var positionDefault = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllCheckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.allcheck_list, parent, false)
        return AllCheckViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AllCheckViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val teamItem = items[position]

        holder.bind(teamItem)
    }

    override fun getItemCount(): Int = items.size

    interface AllCheckItemClickListener {
        fun onItemClick(nameTeam: String?, teamNumber: String?, gp2: Boolean?)
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
    fun update2(teamsItems: MutableList<Teams>) {
        items.clear()
        items.addAll(teamsItems)
        notifyDataSetChanged()
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 10.00
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

    inner class AllCheckViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = AllcheckListBinding.bind(itemView)
        var item: Teams? = null

        init {
            binding.root.setOnClickListener { listener.onItemClick(item?.nameTeam, item?.teamNumber.toString(), item?.gp2) }
        }

        fun bind(newItem: Teams) {
            item = newItem

            if (item?.gp2 == true) {
                binding.tvTeamName.text = item?.nameTeam + " (GP2)"
            }
            else {
                binding.tvTeamName.text = item?.nameTeam
            }

            val avg = (item?.avgWeight!! / item?.stintsDone.toString().toDouble()) + 0.09

            if (avg < 90.0) {
                binding.tvWeight.setTextColor(Color.RED)
                val animationZoomIn = AnimationUtils.loadAnimation(binding.root.context, R.anim.zoom_in_normal)
                binding.tvWeight.startAnimation(animationZoomIn)
            }
            else if (avg >= 90.0) {
                binding.tvWeight.setTextColor(Color.GRAY)
            }

            binding.tvWeight.text = ((avg * 100.0).roundToInt() / 100.0).toString() + " kg"


            binding.tvNumberOfTeam.text = "${item?.teamNumber}. csapat"
        }
    }
}