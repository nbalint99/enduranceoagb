package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.AllTeams
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.AllteamsListBinding
import hu.bme.aut.android.enduranceoagb.databinding.TeamsListBinding

class AllTeamAdapter(private val listener: AllTeamItemClickListener) :
    RecyclerView.Adapter<AllTeamAdapter.AllTeamViewHolder>() {

    private val items = mutableListOf<AllTeams>()

    private var positionDefault = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllTeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.allteams_list, parent, false)
        return AllTeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllTeamViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val teamItem = items[position]

        holder.bind(teamItem, position)

    }

    override fun getItemCount(): Int = items.size

    interface AllTeamItemClickListener {
        fun onTeamCreated(nameTeam: String, people: Int?, gp2: Boolean)
        fun onTeamSelected(nameTeam: String?, people: Int?, joker: Int?, hasJokerRaced: Boolean?, gp2: Boolean?)
        //fun onItemClick(nameTeam: String?, teamNumber: String?, gp2: Boolean?)
        fun onItemLongClick(team: AllTeams?): Boolean
    }

    fun addItem(item: AllTeams) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: AllTeams) {
        items.remove(item)
        notifyItemRemoved(positionDefault)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(teamItems: MutableList<AllTeams>) {
        items.clear()
        items.addAll(teamItems)
        notifyDataSetChanged()
    }

    inner class AllTeamViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = AllteamsListBinding.bind(itemView)
        var item: AllTeams? = null

        init {
            binding.root.setOnClickListener { listener.onTeamSelected(item?.nameTeam, item?.people, item?.joker, item?.hasJokerRaced, item?.gp2) }
            binding.root.setOnLongClickListener { listener.onItemLongClick(item) }
        }

        fun bind(newItem: AllTeams, position: Int) {
            item = newItem

            val place = position + 1

            if (item?.gp2 == true) {
                binding.tvName.text = item?.nameTeam + " (GP2)"
            }
            else {
                binding.tvName.text = item?.nameTeam
            }

            if (item?.points != null) {
                binding.tvAllPoints.text = place.toString() + ". helyezett - " + item?.points.toString() + " pont"
            }
            else {
                binding.tvAllPoints.text = ""
            }

            if (item?.gp2Points != null) {
                binding.tvAllGP2Points.text = "GP2: " + item?.gp2Points.toString() + " pont"
            }
            else {
                binding.tvAllGP2Points.text = ""
            }

            /*if (item?.hasDriversDone != item?.people) {
                binding.tvWarn.text = "Vedd fel a versenyzőket!"
            }
            else {
                binding.tvWarn.text = ""
            }

            if (item?.teamNumber == null) {
                binding.tvNumberOfTeam.text = "Még nincs csapatszáma a csapatnak"
            }
            else {
                binding.tvNumberOfTeam.text = item?.teamNumber.toString() + ". csapat "
            }

            if (item?.startKartNumber == null) {
                binding.tvKartNumber.text = ""
            }
            else if (item?.teamNumber == null) {
                binding.tvKartNumber.text = "Időmérős gépszám: " + item?.startKartNumber.toString()
            }
            else if (item?.teamNumber != null) {
                binding.tvKartNumber.text = "Rajtolós gokartszám: " + item?.startKartNumber.toString()
            }

            binding.ibAdd.setOnClickListener {
                listener.onTeamSelected(item?.nameTeam, item?.people, item?.joker, item?.hasJokerRaced, item?.gp2)
            }*/
        }
    }
}
