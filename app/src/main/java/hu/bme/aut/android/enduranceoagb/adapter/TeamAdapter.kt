package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.TeamsListBinding

class TeamAdapter(private val listener: TeamItemClickListener) :
    RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    private val items = mutableListOf<Teams>()

    private var positionDefault = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.teams_list, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val teamItem = items[position]

        holder.bind(teamItem)

    }

    override fun getItemCount(): Int = items.size

    interface TeamItemClickListener {
        fun onTeamCreated(nameTeam: String, people: Int, gp2: Boolean)
        fun onTeamSelected(nameTeam: String?, teamNumber: String?, people: Int?, startKartNumber: Int?, gp2: Boolean?)
        fun onItemClick(nameTeam: String?, teamNumber: String?, gp2: Boolean?)
        fun onItemLongClick(team: Teams?): Boolean
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
    fun update2(teamItems: MutableList<Teams>) {
        items.clear()
        items.addAll(teamItems)
        notifyDataSetChanged()
    }

    inner class TeamViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = TeamsListBinding.bind(itemView)
        var item: Teams? = null

        init {
            binding.root.setOnClickListener { listener.onItemClick(item?.nameTeam, item?.teamNumber.toString(), item?.gp2) }
            binding.root.setOnLongClickListener { listener.onItemLongClick(item) }
        }

        fun bind(newItem: Teams) {
            item = newItem

            if (item?.gp2 == true) {
                if (item?.shortTeamName == "null") {
                    binding.tvName.text = item?.nameTeam + " (GP2)"
                }
                else if (item?.shortTeamName == null) {
                    binding.tvName.text = item?.nameTeam + " (GP2)"
                }
                else {
                    binding.tvName.text = item?.nameTeam + " (GP2) - " + item?.shortTeamName
                }
            }
            else {
                if (item?.shortTeamName == "null") {
                    binding.tvName.text = item?.nameTeam
                }
                else if (item?.shortTeamName == null) {
                    binding.tvName.text = item?.nameTeam
                }
                else {
                    binding.tvName.text = item?.nameTeam + " - " + item?.shortTeamName
                }
            }

            if (item?.hasDriversDone != item?.people) {
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
                listener.onTeamSelected(item?.nameTeam, item?.teamNumber.toString(), item?.people.toString().toIntOrNull(), item?.startKartNumber.toString().toIntOrNull(), item?.gp2)
            }
        }
    }
}
