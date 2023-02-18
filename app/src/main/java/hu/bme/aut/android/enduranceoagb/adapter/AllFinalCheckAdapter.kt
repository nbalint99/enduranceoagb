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
import kotlin.math.roundToInt


class AllFinalCheckAdapter(private val listener: AllFinalCheckItemClickListener) :
    RecyclerView.Adapter<AllFinalCheckAdapter.AllFinalCheckViewHolder>() {

    private val items = mutableListOf<Teams>()

    private var positionDefault = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllFinalCheckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.allcheck_list, parent, false)
        return AllFinalCheckViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AllFinalCheckViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val teamItem = items[position]

        holder.bind(teamItem)
    }

    override fun getItemCount(): Int = items.size

    interface AllFinalCheckItemClickListener {
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

    inner class AllFinalCheckViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
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

            val avg = item?.avgWeight!! / item?.stintsDone.toString().toDouble()

            if ((item?.avgWeight!! / item?.stintsDone.toString().toDouble()) < 90.0) {
                binding.tvWeight.setTextColor(Color.RED)
                val animationZoomIn = AnimationUtils.loadAnimation(binding.root.context, R.anim.zoom_in_normal)
                binding.tvWeight.startAnimation(animationZoomIn)
            }
            else if ((item?.avgWeight!! / item?.stintsDone.toString().toDouble()) >= 90.0) {
                binding.tvWeight.setTextColor(Color.GRAY)
            }

            binding.tvWeight.text = ((avg * 100.0).roundToInt() / 100.0).toString() + " kg"


            binding.tvNumberOfTeam.text = "${item?.teamNumber}. csapat"
        }
    }
}
