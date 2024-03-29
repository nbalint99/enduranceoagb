package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.BoxTime
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.data.Watch
import hu.bme.aut.android.enduranceoagb.databinding.BoxtimeListBinding
import hu.bme.aut.android.enduranceoagb.databinding.WatchListBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class BoxTimeAdapter(private val listener: BoxTimeItemClickListener) :
    RecyclerView.Adapter<BoxTimeAdapter.BoxTimeViewHolder>() {

    private val items = mutableListOf<BoxTime>()

    private val itemsTeams = mutableListOf<Teams>()

    private val itemsTime = mutableListOf<Double>()

    private var positionDefault = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxTimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.boxtime_list, parent, false)
        return BoxTimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoxTimeViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = items[position]
        val time = itemsTime[0]

        for (element in itemsTeams) {
            if (element.teamNumber == item.teamNumber) {
                holder.binding.tvNumberOfTeamBox.text = item.teamNumber.toString() + ". csapat - " + element.nameTeam
                break
            }
        }

        val minIni = (item.initialTime / 60000 % 60).toInt()
        val secIni = (item.initialTime / 1000 % 60).toInt()
        holder.binding.tvNeedTime.text =
            "Elvárt csereidő: " + String.format("%01d:%02d", minIni, secIni)

        var initPenalty = ((item.initialTime - time) / 1000).toInt()

        when {
            initPenalty == 0 -> {
                holder.binding.tvPrevPenalty.text = "-"
                holder.binding.tvPrevPenalty.setTextColor(Color.BLACK)
            }
            initPenalty < 0 -> {
                holder.binding.tvPrevPenalty.setTextColor(Color.BLACK)
                holder.binding.tvPrevPenalty.text = initPenalty.toString() + " mp"
            }
            initPenalty > 0 -> {
                holder.binding.tvPrevPenalty.setTextColor(Color.RED)
                holder.binding.tvPrevPenalty.text = "+" + initPenalty.toString() + " mp"
            }
        }


        /*if (item.hasDone) {
            holder.binding.timeTV.text = "MEHET!"
            holder.binding.timeTV.setTextColor(Color.GREEN)
            holder.binding.startButton.visibility = View.GONE
        }
        else {
            holder.binding.timeTV.text = String.format("%02d:%02d.%01d", minIni, secIni, msIni)
            holder.binding.startButton.visibility = View.VISIBLE
        }

        holder.binding.startButton.setBackgroundColor(Color.GREEN)

        item.time = item.initialTime

        holder.binding.startButton.setOnClickListener {
            if (holder.binding.timeTV.text != "MEHET!") {
                if (!counting) {
                    counting = true
                    holder.binding.startButton.text = "Stop"
                    holder.binding.startButton.setTextColor(Color.WHITE)
                    holder.binding.startButton.setBackgroundColor(Color.RED)
                    holder.binding.timeTV.setTextColor(Color.BLACK)
                    countDownTimer = object : CountDownTimer(item.time.toLong(), 100) {
                        @SuppressLint("SetTextI18n")
                        override fun onTick(millisUntilFinished: Long) {
                            // Used for formatting digit to be in 2 digits only
                            val f: NumberFormat = DecimalFormat("00")
                            val f2: NumberFormat = DecimalFormat("0")
                            val ms = millisUntilFinished /*/ 3600000 % 24*/ / 100 % 10
                            val min = millisUntilFinished / 60000 % 60
                            val sec = millisUntilFinished / 1000 % 60
                            (
                                    f.format(min)
                                        .toString() + ":" + f.format(sec) + "." + f2.format(
                                        ms
                                    )
                                    ).also { holder.binding.timeTV.text = it }
                            item.time = millisUntilFinished.toDouble()
                            if (millisUntilFinished < 5000) {
                                holder.binding.timeTV.setTextColor(Color.RED)
                                holder.binding.timeTV.text = (f.format(min)
                                    .toString() + ":" + f.format(sec) + "." + f2.format(
                                    ms)) + " / "  + ((millisUntilFinished / 1000) + 1).toString() + " mp"
                            }
                        }

                        // When the task is over it will print 00:00:00 there
                        override fun onFinish() {
                            counting = false
                            holder.binding.startButton.text = "Start"
                            holder.binding.startButton.setTextColor(Color.BLACK)
                            holder.binding.startButton.setBackgroundColor(Color.GREEN)
                            holder.binding.timeTV.text = "MEHET!"
                            holder.binding.timeTV.setTextColor(Color.GREEN)
                            item.time = 40000.0

                            holder.binding.startButton.visibility = View.GONE

                            listener.dataChangedBool(position)
                        }
                    }.start()
                } else {
                    countDownTimer.cancel()
                    counting = false
                    holder.binding.startButton.text = "Start"
                    holder.binding.startButton.setTextColor(Color.BLACK)
                    holder.binding.startButton.setBackgroundColor(Color.GREEN)
                }
            }
        }

        holder.binding.timeTV.setOnClickListener {
            if (holder.binding.timeTV.text != "MEHET!") {
                if (!counting) {
                    counting = true
                    holder.binding.startButton.text = "Stop"
                    holder.binding.startButton.setTextColor(Color.WHITE)
                    holder.binding.startButton.setBackgroundColor(Color.RED)
                    holder.binding.timeTV.setTextColor(Color.BLACK)
                    countDownTimer = object : CountDownTimer(item.time.toLong(), 100) {
                        @SuppressLint("SetTextI18n")
                        override fun onTick(millisUntilFinished: Long) {
                            // Used for formatting digit to be in 2 digits only
                            val f: NumberFormat = DecimalFormat("00")
                            val f2: NumberFormat = DecimalFormat("0")
                            val ms = millisUntilFinished /*/ 3600000 % 24*/ / 100 % 10
                            val min = millisUntilFinished / 60000 % 60
                            val sec = millisUntilFinished / 1000 % 60
                            (
                                    f.format(min)
                                        .toString() + ":" + f.format(sec) + "." + f2.format(
                                        ms
                                    )
                                    ).also { holder.binding.timeTV.text = it }
                            item.time = millisUntilFinished.toDouble()
                            if (millisUntilFinished < 5000) {
                                holder.binding.timeTV.setTextColor(Color.RED)
                                holder.binding.timeTV.text = (f.format(min)
                                    .toString() + ":" + f.format(sec) + "." + f2.format(
                                    ms)) + " / "  + ((millisUntilFinished / 1000) + 1).toString() + " mp"
                            }
                        }

                        // When the task is over it will print 00:00:00 there
                        override fun onFinish() {
                            counting = false
                            holder.binding.startButton.text = "Start"
                            holder.binding.startButton.setTextColor(Color.BLACK)
                            holder.binding.startButton.setBackgroundColor(Color.GREEN)
                            holder.binding.timeTV.text = "MEHET!"
                            holder.binding.timeTV.setTextColor(Color.GREEN)
                            item.time = 40000.0

                            holder.binding.startButton.visibility = View.GONE

                            listener.dataChangedBool(position)
                        }
                    }.start()
                } else {
                    countDownTimer.cancel()
                    counting = false
                    holder.binding.startButton.text = "Start"
                    holder.binding.startButton.setTextColor(Color.BLACK)
                    holder.binding.startButton.setBackgroundColor(Color.GREEN)
                }
            }
        }

        holder.binding.resetButton.setOnClickListener {
            if (!counting) {
                holder.binding.timeTV.text = "00:40.0"
                holder.binding.timeTV.setTextColor(Color.BLACK)
                item.time = 40000.0
                item.initialTime = 40000.0
                holder.binding.tvInitialTime.text = "Boxban töltött idő: 00:40.0"

                holder.binding.startButton.visibility = View.VISIBLE

                listener.dataChangedBoolFalse(position)
            }
        }


         */
        if (!item.hasDone) {
            holder.binding.plus5Button.setOnClickListener {
                item.initialTime += 5000.0
                initPenalty += 5
                when {
                    initPenalty == 0 -> {
                        holder.binding.tvPrevPenalty.text = "-"
                        holder.binding.tvPrevPenalty.setTextColor(Color.BLACK)
                    }
                    initPenalty < 0 -> {
                        holder.binding.tvPrevPenalty.setTextColor(Color.BLACK)
                        holder.binding.tvPrevPenalty.text = initPenalty.toString() + " mp"
                    }
                    initPenalty > 0 -> {
                        holder.binding.tvPrevPenalty.setTextColor(Color.RED)
                        holder.binding.tvPrevPenalty.text = "+" + initPenalty.toString() + " mp"
                    }
                }
                val minIni = (item.initialTime / 60000 % 60).toInt()
                val secIni = (item.initialTime / 1000 % 60).toInt()
                holder.binding.tvNeedTime.text =
                    "Elvárt csereidő: " + String.format("%01d:%02d", minIni, secIni)

                listener.dataChanged(position, item.initialTime)
            }



            holder.binding.plus1Button.setOnClickListener {
                item.initialTime += 1000.0
                initPenalty += 1
                when {
                    initPenalty == 0 -> {
                        holder.binding.tvPrevPenalty.text = "-"
                        holder.binding.tvPrevPenalty.setTextColor(Color.BLACK)
                    }
                    initPenalty < 0 -> {
                        holder.binding.tvPrevPenalty.setTextColor(Color.BLACK)
                        holder.binding.tvPrevPenalty.text = initPenalty.toString() + " mp"
                    }
                    initPenalty > 0 -> {
                        holder.binding.tvPrevPenalty.setTextColor(Color.RED)
                        holder.binding.tvPrevPenalty.text = "+" + initPenalty.toString() + " mp"
                    }
                }
                val minIni = (item.initialTime / 60000 % 60).toInt()
                val secIni = (item.initialTime / 1000 % 60).toInt()
                holder.binding.tvNeedTime.text =
                    "Elvárt csereidő: " + String.format("%01d:%02d", minIni, secIni)

                listener.dataChanged(position, item.initialTime)
            }

            holder.binding.minus5Button.setOnClickListener {
                item.initialTime -= 5000.0
                initPenalty -= 5
                when {
                    initPenalty == 0 -> {
                        holder.binding.tvPrevPenalty.text = "-"
                        holder.binding.tvPrevPenalty.setTextColor(Color.BLACK)
                    }
                    initPenalty < 0 -> {
                        holder.binding.tvPrevPenalty.setTextColor(Color.BLACK)
                        holder.binding.tvPrevPenalty.text = initPenalty.toString() + " mp"
                    }
                    initPenalty > 0 -> {
                        holder.binding.tvPrevPenalty.setTextColor(Color.RED)
                        holder.binding.tvPrevPenalty.text = "+" + initPenalty.toString() + " mp"
                    }
                }
                val minIni = (item.initialTime / 60000 % 60).toInt()
                val secIni = (item.initialTime / 1000 % 60).toInt()
                holder.binding.tvNeedTime.text =
                    "Elvárt csereidő: " + String.format("%01d:%02d", minIni, secIni)

                listener.dataChanged(position, item.initialTime)
            }

            holder.binding.minus1Button.setOnClickListener {
                item.initialTime -= 1000.0
                initPenalty -= 1
                when {
                    initPenalty == 0 -> {
                        holder.binding.tvPrevPenalty.text = "-"
                        holder.binding.tvPrevPenalty.setTextColor(Color.BLACK)
                    }
                    initPenalty < 0 -> {
                        holder.binding.tvPrevPenalty.setTextColor(Color.BLACK)
                        holder.binding.tvPrevPenalty.text = initPenalty.toString() + " mp"
                    }
                    initPenalty > 0 -> {
                        holder.binding.tvPrevPenalty.setTextColor(Color.RED)
                        holder.binding.tvPrevPenalty.text = "+" + initPenalty.toString() + " mp"
                    }
                }
                val minIni = (item.initialTime / 60000 % 60).toInt()
                val secIni = (item.initialTime / 1000 % 60).toInt()
                holder.binding.tvNeedTime.text =
                    "Elvárt csereidő: " + String.format("%01d:%02d", minIni, secIni)

                listener.dataChanged(position, item.initialTime)
            }
        }

        if (!item.hasDone) {
            holder.binding.tvActualTime.text = ""
        }
        if (item.hasDone) {
            holder.binding.tvActualTime.setTextColor(Color.BLACK)
            val minIniActual = (item.actualTime!! / 60000 % 60).toInt()
            val secIniActual = (item.actualTime!! / 1000 % 60).toInt()
            holder.binding.tvActualTime.text =
                String.format("%01d:%02d", minIniActual, secIniActual)
            if (item.penaltyTime.toString().toDouble() != 0.0) {
                val penaltyTimeInt = item.penaltyTime.toString().toDouble().toInt()
                holder.binding.tvNextPenalty.text = "+" + (penaltyTimeInt / 1000) + " mp"
                holder.binding.tvNextPenalty.setTextColor(Color.RED)
            }
            else {
                holder.binding.tvNextPenalty.text = "-"
                holder.binding.tvNextPenalty.setTextColor(Color.BLACK)
            }
            holder.binding.tvActualTime.textSize = 45.0F

        }

        holder.binding.btnActualTimeText.setOnClickListener {
            for (element in itemsTeams) {
                if (element.teamNumber == item.teamNumber) {
                    listener.onNewBoxListener(position, item.teamNumber, item.hasDone, element.nameTeam)
                    break
                }
            }
        }

        holder.bind(item)

    }

    override fun getItemCount(): Int = items.size

    interface BoxTimeItemClickListener {
        fun dataChanged(position: Int, initTime: Double)
        fun dataChangedBool(position: Int)
        fun onNewBoxListener(position: Int, teamNumber: Int, stintDone: Boolean, nameTeam: String)
        //fun dataChangedBoolFalse(position: Int)
    }

    fun addItem(item: BoxTime) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: BoxTime) {
        items.remove(item)
        notifyItemRemoved(positionDefault)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(itemsWatch: MutableList<BoxTime>) {
        items.clear()
        items.addAll(itemsWatch)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2Teams(itemsWatch: MutableList<Teams>) {
        itemsTeams.clear()
        itemsTeams.addAll(itemsWatch)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update3time(time: MutableList<Double>) {
        itemsTime.clear()
        itemsTime.addAll(time)
        notifyDataSetChanged()
    }

    inner class BoxTimeViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = BoxtimeListBinding.bind(itemView)
        var item: BoxTime? = null

        //var countDownTimer : CountDownTimer? = null

        fun bind(newItem: BoxTime) {
            item = newItem
        }
    }
}