package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ServerValue
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.data.Watch
import hu.bme.aut.android.enduranceoagb.databinding.WatchListBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class WatchAdapter2(private val listener: Watch2ItemClickListener) :
    RecyclerView.Adapter<WatchAdapter2.Watch2ViewHolder>() {

    private val items = mutableListOf<Watch>()

    private val itemsTeams = mutableListOf<Teams>()

    private val group = mutableListOf<Int>()

    private var positionDefault = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Watch2ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.watch_list, parent, false)
        return Watch2ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Watch2ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = items[position]

        for (element in itemsTeams) {
            if (element.teamNumber == item.teamNumber) {
                if (element.shortTeamName != null) {
                    holder.binding.tvNumberOfTeamWatch.text = item.teamNumber.toString() + ". csapat - " + element.shortTeamName
                    break
                }
                else {
                    holder.binding.tvNumberOfTeamWatch.text =
                        item.teamNumber.toString() + ". csapat - " + element.nameTeam
                    break
                }
            }
        }

        lateinit var countDownTimer: CountDownTimer

        var counting = false

        val msIni = (item.initialTime / 100 % 10).toInt()
        val minIni = (item.initialTime / 60000 % 60).toInt()
        val secIni = (item.initialTime / 1000 % 60).toInt()
        holder.binding.tvInitialTime.text =
            "Boxban töltött idő: " + String.format("%02d:%02d.%01d", minIni, secIni, msIni)

        if (item.hasDone) {
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
                val unixTime = System.currentTimeMillis()
                if (!counting) {
                    listener.startTimer(position)
                    counting = true
                    listener.serverTime(position, item.time, unixTime, counting)
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
                            item.time = item.initialTime

                            holder.binding.startButton.visibility = View.GONE

                            listener.dataChangedBool(position)
                        }
                    }.start()
                } else {
                    countDownTimer.cancel()
                    counting = false
                    listener.serverTime(position, item.time, unixTime, counting)
                    holder.binding.startButton.text = "Start"
                    holder.binding.startButton.setTextColor(Color.BLACK)
                    holder.binding.startButton.setBackgroundColor(Color.GREEN)
                }
            }
        }

        holder.binding.timeTV.setOnClickListener {
            if (holder.binding.timeTV.text != "MEHET!") {
                val unixTime = System.currentTimeMillis()
                if (!counting) {
                    listener.startTimer(position)
                    counting = true
                    listener.serverTime(position, item.time, unixTime, counting)
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
                            item.time = item.initialTime

                            holder.binding.startButton.visibility = View.GONE

                            listener.dataChangedBool(position)
                        }
                    }.start()
                } else {
                    countDownTimer.cancel()
                    counting = false
                    listener.serverTime(position, item.time, unixTime, counting)
                    holder.binding.startButton.text = "Start"
                    holder.binding.startButton.setTextColor(Color.BLACK)
                    holder.binding.startButton.setBackgroundColor(Color.GREEN)
                }
            }
        }

        holder.binding.resetButton.setOnClickListener {
            if (!counting) {
                item.time = item.initialTime

                val msIni = (item.initialTime / 100 % 10).toInt()
                val minIni = (item.initialTime / 60000 % 60).toInt()
                val secIni = (item.initialTime / 1000 % 60).toInt()
                holder.binding.tvInitialTime.text =
                    "Boxban töltött idő: " + String.format("%02d:%02d.%01d", minIni, secIni, msIni)

                holder.binding.timeTV.text = String.format("%02d:%02d.%01d", minIni, secIni, msIni)
                holder.binding.timeTV.setTextColor(Color.BLACK)

                holder.binding.startButton.visibility = View.VISIBLE

                listener.dataChangedBoolFalse(position)
            }
        }

        holder.binding.plus5Button.setOnClickListener {
            if (holder.binding.timeTV.text != "MEHET!") {
                if (counting) {
                    countDownTimer.cancel()
                    item.time += 5000.0
                    val ms = (item.time / 100 % 10).toInt()
                    val min = (item.time / 60000 % 60).toInt()
                    val sec = (item.time / 1000 % 60).toInt()
                    holder.binding.timeTV.setTextColor(Color.BLACK)
                    holder.binding.timeTV.text = String.format("%02d:%02d.%01d", min, sec, ms)
                    countDownTimer = object : CountDownTimer(item.time.toLong(), 100) {
                        @SuppressLint("SetTextI18n")
                        override fun onTick(millisUntilFinished: Long) {
                            // Used for formatting digit to be in 2 digits only
                            val f: NumberFormat = DecimalFormat("00")
                            val f2: NumberFormat = DecimalFormat("0")
                            val msTick = millisUntilFinished /*/ 3600000 % 24*/ / 100 % 10
                            val minTick = millisUntilFinished / 60000 % 60
                            val secTick = millisUntilFinished / 1000 % 60
                            (
                                    f.format(minTick)
                                        .toString() + ":" + f.format(secTick) + "." + f2.format(
                                        msTick
                                    )
                                    ).also { holder.binding.timeTV.text = it }
                            item.time = millisUntilFinished.toDouble()
                            if (millisUntilFinished < 5000) {
                                holder.binding.timeTV.setTextColor(Color.RED)
                                holder.binding.timeTV.text = (f.format(minTick)
                                    .toString() + ":" + f.format(secTick) + "." + f2.format(
                                    msTick)) + " / "  + ((millisUntilFinished / 1000) + 1).toString() + " mp"
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
                            item.time = item.initialTime

                            holder.binding.startButton.visibility = View.GONE

                            listener.dataChangedBool(position)
                        }
                    }.start()
                } else {
                    item.time += 5000.0
                    val ms = (item.time / 100 % 10).toInt()
                    val min = (item.time / 60000 % 60).toInt()
                    val sec = (item.time / 1000 % 60).toInt()
                    holder.binding.timeTV.text = String.format("%02d:%02d.%01d", min, sec, ms)
                }
                item.initialTime += 5000.0
                val msIni = (item.initialTime / 100 % 10).toInt()
                val minIni = (item.initialTime / 60000 % 60).toInt()
                val secIni = (item.initialTime / 1000 % 60).toInt()
                holder.binding.tvInitialTime.text =
                    "Boxban töltött idő: " + String.format("%02d:%02d.%01d", minIni, secIni, msIni)

                listener.dataChanged(position, item.initialTime)
            }
        }

        holder.binding.plus1Button.setOnClickListener {
            if (holder.binding.timeTV.text != "MEHET!") {
                if (counting) {
                    countDownTimer.cancel()
                    item.time += 1000.0

                    val ms = (item.time / 100 % 10).toInt()
                    val min = (item.time / 60000 % 60).toInt()
                    val sec = (item.time / 1000 % 60).toInt()
                    if (item.time > 5000.0) {
                        holder.binding.timeTV.setTextColor(Color.BLACK)
                    }
                    holder.binding.timeTV.text = String.format("%02d:%02d.%01d", min, sec, ms)
                    countDownTimer = object : CountDownTimer(item.time.toLong(), 100) {
                        @SuppressLint("SetTextI18n")
                        override fun onTick(millisUntilFinished: Long) {
                            // Used for formatting digit to be in 2 digits only
                            val f: NumberFormat = DecimalFormat("00")
                            val f2: NumberFormat = DecimalFormat("0")
                            val msTick = millisUntilFinished /*/ 3600000 % 24*/ / 100 % 10
                            val minTick = millisUntilFinished / 60000 % 60
                            val secTick = millisUntilFinished / 1000 % 60
                            (
                                    f.format(minTick)
                                        .toString() + ":" + f.format(secTick) + "." + f2.format(
                                        msTick
                                    )
                                    ).also { holder.binding.timeTV.text = it }
                            item.time = millisUntilFinished.toDouble()
                            if (millisUntilFinished < 5000) {
                                holder.binding.timeTV.setTextColor(Color.RED)
                                holder.binding.timeTV.text = (f.format(minTick)
                                    .toString() + ":" + f.format(secTick) + "." + f2.format(
                                    msTick)) + " / "  + ((millisUntilFinished / 1000) + 1).toString() + " mp"
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
                            item.time = item.initialTime

                            holder.binding.startButton.visibility = View.GONE

                            listener.dataChangedBool(position)
                        }
                    }.start()
                } else {
                    item.time += 1000.0
                    val ms = (item.time / 100 % 10).toInt()
                    val min = (item.time / 60000 % 60).toInt()
                    val sec = (item.time / 1000 % 60).toInt()
                    holder.binding.timeTV.text = String.format("%02d:%02d.%01d", min, sec, ms)
                }
                item.initialTime += 1000.0
                val msIni = (item.initialTime / 100 % 10).toInt()
                val minIni = (item.initialTime / 60000 % 60).toInt()
                val secIni = (item.initialTime / 1000 % 60).toInt()
                holder.binding.tvInitialTime.text =
                    "Boxban töltött idő: " + String.format("%02d:%02d.%01d", minIni, secIni, msIni)

                listener.dataChanged(position, item.initialTime)
            }
        }

        holder.binding.minus5Button.setOnClickListener {
            if (holder.binding.timeTV.text != "MEHET!") {
                if (item.time > 5000.0) {
                    if (counting) {
                        countDownTimer.cancel()
                        item.time -= 5000.0
                        val ms = (item.time / 100 % 10).toInt()
                        val min = (item.time / 60000 % 60).toInt()
                        val sec = (item.time / 1000 % 60).toInt()
                        holder.binding.timeTV.text = String.format("%02d:%02d.%01d", min, sec, ms)
                        countDownTimer = object : CountDownTimer(item.time.toLong(), 100) {
                            @SuppressLint("SetTextI18n")
                            override fun onTick(millisUntilFinished: Long) {
                                // Used for formatting digit to be in 2 digits only
                                val f: NumberFormat = DecimalFormat("00")
                                val f2: NumberFormat = DecimalFormat("0")
                                val msTick = millisUntilFinished /*/ 3600000 % 24*/ / 100 % 10
                                val minTick = millisUntilFinished / 60000 % 60
                                val secTick = millisUntilFinished / 1000 % 60
                                (
                                        f.format(minTick)
                                            .toString() + ":" + f.format(secTick) + "." + f2.format(
                                            msTick
                                        )
                                        ).also { holder.binding.timeTV.text = it }
                                item.time = millisUntilFinished.toDouble()
                                if (millisUntilFinished < 5000) {
                                    holder.binding.timeTV.setTextColor(Color.RED)
                                    holder.binding.timeTV.text = (f.format(minTick)
                                        .toString() + ":" + f.format(secTick) + "." + f2.format(
                                        msTick
                                    )) + " / " + ((millisUntilFinished / 1000) + 1).toString() + " mp"
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
                                item.time = item.initialTime

                                holder.binding.startButton.visibility = View.GONE

                                listener.dataChangedBool(position)
                            }
                        }.start()
                    } else {
                        item.time -= 5000.0
                        val ms = (item.time / 100 % 10).toInt()
                        val min = (item.time / 60000 % 60).toInt()
                        val sec = (item.time / 1000 % 60).toInt()
                        holder.binding.timeTV.text = String.format("%02d:%02d.%01d", min, sec, ms)
                    }
                    item.initialTime -= 5000.0
                    val msIni = (item.initialTime / 100 % 10).toInt()
                    val minIni = (item.initialTime / 60000 % 60).toInt()
                    val secIni = (item.initialTime / 1000 % 60).toInt()
                    holder.binding.tvInitialTime.text = "Boxban töltött idő: " + String.format(
                        "%02d:%02d.%01d",
                        minIni,
                        secIni,
                        msIni
                    )

                    listener.dataChanged(position, item.initialTime)
                }
            }
        }

        holder.binding.minus1Button.setOnClickListener {
            if (holder.binding.timeTV.text != "MEHET!") {
                if (item.time > 1000.0) {
                    if (counting) {
                        countDownTimer.cancel()
                        item.time -= 1000.0
                        val ms = (item.time / 100 % 10).toInt()
                        val min = (item.time / 60000 % 60).toInt()
                        val sec = (item.time / 1000 % 60).toInt()
                        holder.binding.timeTV.text = String.format("%02d:%02d.%01d", min, sec, ms)
                        countDownTimer = object : CountDownTimer(item.time.toLong(), 100) {
                            @SuppressLint("SetTextI18n")
                            override fun onTick(millisUntilFinished: Long) {
                                // Used for formatting digit to be in 2 digits only
                                val f: NumberFormat = DecimalFormat("00")
                                val f2: NumberFormat = DecimalFormat("0")
                                val msTick = millisUntilFinished /*/ 3600000 % 24*/ / 100 % 10
                                val minTick = millisUntilFinished / 60000 % 60
                                val secTick = millisUntilFinished / 1000 % 60
                                (
                                        f.format(minTick)
                                            .toString() + ":" + f.format(secTick) + "." + f2.format(
                                            msTick
                                        )
                                        ).also { holder.binding.timeTV.text = it }
                                item.time = millisUntilFinished.toDouble()
                                if (millisUntilFinished < 5000) {
                                    holder.binding.timeTV.setTextColor(Color.RED)
                                    holder.binding.timeTV.text = (f.format(minTick)
                                        .toString() + ":" + f.format(secTick) + "." + f2.format(
                                        msTick
                                    )) + " / " + ((millisUntilFinished / 1000) + 1).toString() + " mp"
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
                                item.time = item.initialTime

                                holder.binding.startButton.visibility = View.GONE

                                listener.dataChangedBool(position)
                            }
                        }.start()
                    } else {
                        item.time -= 1000.0
                        val ms = (item.time / 100 % 10).toInt()
                        val min = (item.time / 60000 % 60).toInt()
                        val sec = (item.time / 1000 % 60).toInt()
                        holder.binding.timeTV.text = String.format("%02d:%02d.%01d", min, sec, ms)
                    }
                    item.initialTime -= 1000.0
                    val msIni = (item.initialTime / 100 % 10).toInt()
                    val minIni = (item.initialTime / 60000 % 60).toInt()
                    val secIni = (item.initialTime / 1000 % 60).toInt()
                    holder.binding.tvInitialTime.text = "Boxban töltött idő: " + String.format(
                        "%02d:%02d.%01d",
                        minIni,
                        secIni,
                        msIni
                    )

                    listener.dataChanged(position, item.initialTime)
                }
            }
        }

        holder.bind(item)

    }

    override fun getItemCount(): Int = items.size

    interface Watch2ItemClickListener {
        fun dataChanged(position: Int, initTime: Double)
        fun dataChangedBool(position: Int)
        fun dataChangedBoolFalse(position: Int)
        fun startTimer(position: Int)
        fun serverTime(position: Int, time: Double, unixTime: Long, counting: Boolean)
    }

    fun addItem(item: Watch) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: Watch) {
        items.remove(item)
        notifyItemRemoved(positionDefault)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(itemsWatch: MutableList<Watch>) {
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
    fun update2Group(groupNumber: MutableList<Int>) {
        group.clear()
        group.addAll(groupNumber)
        notifyDataSetChanged()
    }

    inner class Watch2ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = WatchListBinding.bind(itemView)
        var item: Watch? = null

        //var countDownTimer : CountDownTimer? = null

        fun bind(newItem: Watch) {
            item = newItem
            if (group.isNotEmpty()) {
                if (item!!.teamNumber == group[0]) {
                    itemView.setBackgroundResource(R.color.pink)
                }
            }
        }
    }
}