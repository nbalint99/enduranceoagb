package hu.bme.aut.android.enduranceoagb.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.BoxTime
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.DetailsstintfragmentListBinding
import hu.bme.aut.android.enduranceoagb.databinding.DetailsstintfragmentwatchListBinding
import kotlin.math.ceil
import kotlin.math.roundToInt


class DetailsStintFragmentAdapter(private val listener: DetailsStintFragmentItemClickListener) :
    RecyclerView.Adapter<DetailsStintFragmentAdapter.DetailsStintFragmentViewHolder>() {


    private val items = mutableListOf<Stint>()

    private val itemsTeams = mutableListOf<Teams>()

    private val itemsDrivers = mutableListOf<Drivers>()

    private val numberOfStint = mutableListOf<Int>()

    private val itemsBox = mutableListOf<BoxTime>()

    private val itemsTime = mutableListOf<Double>()

    private var positionDefault = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailsStintFragmentViewHolder(
        DetailsstintfragmentwatchListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    @SuppressLint("SetTextI18n", "ResourceAsColor", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: DetailsStintFragmentViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val detailsStintItem = items[position]

        if (detailsStintItem.shortTeamName == null || detailsStintItem.shortTeamName == "null") {
            holder.binding.tvTeamName.text = detailsStintItem.teamName
        }
        else {
            holder.binding.tvTeamName.text = detailsStintItem.shortTeamName
        }

        holder.binding.tvTeamNumber.text = detailsStintItem.teamNumber.toString() + ". csapat"

        holder.binding.tvStintDriver.text = "Versenyző neve:"
        holder.binding.tvStintPlusWeight.text = "Plusz súly:"


        if (detailsStintItem.numberStint != 1) {
            holder.binding.tvPrevDriver.text = detailsStintItem.prevDriverName
            holder.binding.tvPrevKart.text = "Gokart: " + detailsStintItem.prevKartNumber
            holder.binding.tvPrevWeight.text = detailsStintItem.prevPlusWeight.toString() + " kg"
            holder.binding.tvComing.text = "Érkezik:"
            holder.binding.tvPrevDriver.setTextColor(Color.BLUE)
            holder.binding.tvPrevKart.setTextColor(Color.BLUE)
            holder.binding.tvPrevWeight.setTextColor(Color.BLUE)
            holder.binding.tvComing.setTextColor(Color.BLUE)

            holder.binding.tvTeamName.setOnClickListener {
                for (i in itemsTeams) {
                    if (i.teamNumber == detailsStintItem.teamNumber) {
                        listener.onTeamListener(detailsStintItem.teamName, detailsStintItem.teamNumber.toString(), i.gp2)
                        break
                    }
                }
            }
        }

        if (detailsStintItem.hasStintDone) {
            holder.itemView.setBackgroundResource(R.color.pink)
            holder.binding.tvInfoText.text = "Megjegyzés: "
            holder.binding.tvStintDriverName.text = detailsStintItem.driverName
            holder.binding.tvStintPlusWeightValue.text = detailsStintItem.plusWeight.toString() + " kg"
            holder.binding.tvKartNumberStint.text = "Gokart: ${detailsStintItem.kartNumber}"
            if (detailsStintItem.info == "") {
                holder.binding.tvInfo.text = "-"
            }
            else {
                holder.binding.tvInfo.text = detailsStintItem.info
                holder.binding.tvInfo.setTextColor(Color.RED)
            }
            for (i in itemsTeams) {
                if (detailsStintItem.teamNumber == i.teamNumber) {
                    val prevAvgWeight = detailsStintItem.prevAvgWeight
                    for (element in itemsDrivers) {
                        if (element.nameDriver == detailsStintItem.driverName) {
                            val driverWeight = element.weight!!
                            val plusWeight = detailsStintItem.plusWeight
                            val newWeight = driverWeight + plusWeight!!
                            val totalWeight = prevAvgWeight?.plus(newWeight)
                            val avgWeight = totalWeight?.div(detailsStintItem.numberStint.toDouble())
                            if (avgWeight != null) {
                                holder.binding.tvAvgWeight.text =
                                    "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Átlag súly: - "
                            }
                        }
                    }
                    /*if (detailsStintItem.prevAvgWeight.toString() != "null" || detailsStintItem.prevAvgWeight != null) {
                        val prevAvgWeight = detailsStintItem.prevAvgWeight
                        for (element in itemsDrivers) {
                            if (element.nameDriver == detailsStintItem.driverName) {
                                val driverWeight = element.weight!!
                                val plusWeight = detailsStintItem.plusWeight
                                val newWeight = driverWeight + plusWeight!!
                                val totalWeight = prevAvgWeight?.plus(newWeight)
                                val avgWeight = totalWeight?.div(i.stintsDone.toString().toDouble())
                                println(detailsStintItem.teamNumber)
                                println("hasStintDone")
                                /*if ((numberOfStint[0] == detailsStintItem.numberStint) && !detailsStintItem.hasStintDone) {
                                    val requiredWeight = 90.0 * detailsStintItem.numberStint
                                    val needWeight = requiredWeight - i.avgWeight!!
                                    if (needWeight < 0) {
                                        holder.binding.tvAvgWeight.text = "Már megvan a minimum súly!"
                                    }
                                    else {
                                        holder.binding.tvAvgWeight.text = "Szükséges súly: ${((needWeight * 100.0).roundToInt() / 100.0)} kg"
                                        holder.binding.tvAvgWeight.setTextColor(Color.RED)
                                    }
                                    break
                                }
                                else {*/
                                if (avgWeight != null) {
                                    println(detailsStintItem.teamNumber)
                                    println("hasStintDone else")
                                    holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                                }
                                else {
                                    holder.binding.tvAvgWeight.text = "Átlag súly: - "
                                }
                                break
                            }
                        }
                    }
                    else {
                        println(detailsStintItem.teamNumber)
                        println("hasStintDone, else")
                        val avgWeight = i.avgWeight?.div(i.stintsDone.toString().toDouble())
                        /*if ((numberOfStint[0] == detailsStintItem.numberStint) && !detailsStintItem.hasStintDone) {
                            val requiredWeight = 90.0 * detailsStintItem.numberStint
                            val needWeight = requiredWeight - i.avgWeight!!
                            if (needWeight < 0) {
                                holder.binding.tvAvgWeight.text = "Már megvan a minimum súly!"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Szükséges súly: ${((needWeight * 100.0).roundToInt() / 100.0)} kg"
                                holder.binding.tvAvgWeight.setTextColor(Color.RED)
                            }
                            break
                        }
                        else {*/
                        if (avgWeight != null) {
                            println(detailsStintItem.teamNumber)
                            println("hasStintDone, else else")
                            holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                        }
                        else {
                            holder.binding.tvAvgWeight.text = "Átlag súly: - "
                        }
                        break
                    }*/


                    //}
                }
            }
        }
        else if (!detailsStintItem.hasStintDone) {
            holder.binding.tvInfoText.text = "Előző megjegyzése: "
            holder.binding.tvStintDriverName.text = ""
            holder.binding.tvStintPlusWeightValue.text = ""
            holder.binding.tvKartNumberStint.text = "Gokart: ${detailsStintItem.expectedKartNumber}"
            if (detailsStintItem.previousInfo == "" || detailsStintItem.previousInfo == null) {
                holder.binding.tvInfo.text = "-"
            }
            else {
                holder.binding.tvInfo.text = detailsStintItem.previousInfo
                holder.binding.tvInfo.setTextColor(Color.RED)
            }
            for (i in itemsTeams) {
                if (detailsStintItem.teamNumber == i.teamNumber) {
                    //println(detailsStintItem.teamNumber)
                    val prevAvgWeight = detailsStintItem.prevAvgWeight
                            if ((numberOfStint[0] == detailsStintItem.numberStint) && !detailsStintItem.hasStintDone) {
                                val requiredWeight = 90.0 * detailsStintItem.numberStint
                                val needWeight = requiredWeight - prevAvgWeight!!
                                //println(detailsStintItem.teamNumber)
                                //println("hasNOTStintDone")
                                if (needWeight < 0) {
                                    holder.binding.tvAvgWeight.text = "Már megvan a minimum súly!"
                                }
                                else {
                                    holder.binding.tvAvgWeight.text = "Szükséges súly: ${((needWeight * 100.0).roundToInt() / 100.0)} kg"
                                    holder.binding.tvAvgWeight.setTextColor(Color.RED)
                                }
                                break
                            }
                            else {
                                //val driverWeight = element.weight!!
                                //val plusWeight = detailsStintItem.plusWeight
                                //val newWeight = driverWeight + plusWeight!!
                                //val totalWeight = prevAvgWeight?.plus(newWeight)
                                val avgWeight = prevAvgWeight?.div(i.stintsDone.toString().toDouble())
                                //println(detailsStintItem.teamNumber)
                                //println("hasNOTStintDone else")
                                if (avgWeight != null) {
                                    holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                                }
                                else {
                                    holder.binding.tvAvgWeight.text = "Átlag súly: - "
                                }
                                break
                            }
                    //    }
                    //}
                    /*if (detailsStintItem.prevAvgWeight.toString() != "null" || detailsStintItem.prevAvgWeight != null) {
                        val avgWeight = detailsStintItem.prevAvgWeight?.div(i.stintsDone.toString().toDouble())
                        if ((numberOfStint[0] == detailsStintItem.numberStint) && !detailsStintItem.hasStintDone) {
                            val requiredWeight = 90.0 * detailsStintItem.numberStint
                            val needWeight = requiredWeight - i.avgWeight!!
                            println(detailsStintItem.teamNumber)
                            println("hasNOTStintDone")
                            if (needWeight < 0) {
                                holder.binding.tvAvgWeight.text = "Már megvan a minimum súly!"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Szükséges súly: ${((needWeight * 100.0).roundToInt() / 100.0)} kg"
                                holder.binding.tvAvgWeight.setTextColor(Color.RED)
                            }
                            break
                        }
                        else {
                            println(detailsStintItem.teamNumber)
                            println("hasNOTStintDone else")
                            if (avgWeight != null) {
                                holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Átlag súly: - "
                            }
                            break
                        }
                    }
                    else {
                        val avgWeight = i.avgWeight?.div(i.stintsDone.toString().toDouble())
                        if ((numberOfStint[0] == detailsStintItem.numberStint) && !detailsStintItem.hasStintDone) {
                            val requiredWeight = 90.0 * detailsStintItem.numberStint
                            val needWeight = requiredWeight - i.avgWeight!!
                            println(detailsStintItem.teamNumber)
                            println("hasNOTStintDone else, not else")
                            if (needWeight < 0) {
                                holder.binding.tvAvgWeight.text = "Már megvan a minimum súly!"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Szükséges súly: ${((needWeight * 100.0).roundToInt() / 100.0)} kg"
                                holder.binding.tvAvgWeight.setTextColor(Color.RED)
                            }
                            break
                        }
                        else {
                            println(detailsStintItem.teamNumber)
                            println("hasNOTStintDone else, else")
                            if (avgWeight != null) {
                                holder.binding.tvAvgWeight.text = "Átlag súly: ${((avgWeight * 100.0).roundToInt() / 100.0)} kg"
                            }
                            else {
                                holder.binding.tvAvgWeight.text = "Átlag súly: - "
                            }
                            break
                        }
                    }*/

                }
            }
        }


        holder.binding.ibDone.setOnClickListener {
            listener.onNewStintListener(position, detailsStintItem.teamNumber, detailsStintItem.teamName, detailsStintItem.hasStintDone, detailsStintItem.driverName, detailsStintItem.plusWeight, detailsStintItem.shortTeamName, detailsStintItem.driverWeight, detailsStintItem.prevAvgWeight)
        }

        /*val item = itemsBox[position]
        val time = itemsTime[0]

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
        }*/


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
        /*if (!item.hasDone) {
            if (detailsStintItem.hasStintDone) {
                holder.itemView.setBackgroundResource(R.color.pink)
            }
            if (!detailsStintItem.hasStintDone) {
                holder.itemView.setBackgroundResource(R.color.blueLight)
                holder.itemView.background.alpha = 30
            }
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
            if (!detailsStintItem.hasStintDone) {
                holder.itemView.setBackgroundResource(R.color.blueLight)
                holder.itemView.background.alpha = 30
            }
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
                    listener.onNewBoxListener(position, item.teamNumber, item.hasDone, element.shortTeamName.toString())
                    break
                }
            }
        }

        holder.bind(item)*/
    }

    override fun getItemCount(): Int = items.size

    interface DetailsStintFragmentItemClickListener {
        fun onNewStintListener(position: Int, teamNumber: Int, teamName: String, stintDone: Boolean, driverName: String?, plusWeight: Double?, shortTeamName: String?, driverWeight: Double?, prevTotalWeight: Double?)
        fun onTeamListener(teamName: String?, number: String?, gp2: Boolean?)
        fun dataChanged(position: Int, initTime: Double)
        fun dataChangedBool(position: Int)
        fun onNewBoxListener(position: Int, teamNumber: Int, stintDone: Boolean, nameTeam: String)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(detailsStintItems: List<Stint>) {
        items.clear()
        items.addAll(detailsStintItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2(detailsStintItems: MutableList<Stint>) {
        items.clear()
        items.addAll(detailsStintItems)
        notifyDataSetChanged()
    }

    fun addItemStints(item: Stint) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun teams(detailsTeamItems: MutableList<Teams>) {
        itemsTeams.clear()
        itemsTeams.addAll(detailsTeamItems)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun drivers(detailsDriverItems: MutableList<Drivers>) {
        itemsDrivers.clear()
        itemsDrivers.addAll(detailsDriverItems)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun stints(stint: MutableList<Int>) {
        numberOfStint.clear()
        numberOfStint.addAll(stint)
    }

    fun addItem(item: BoxTime) {
        itemsBox.add(item)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: BoxTime) {
        itemsBox.remove(item)
        notifyItemRemoved(positionDefault)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update2Box(itemsWatch: MutableList<BoxTime>) {
        itemsBox.clear()
        itemsBox.addAll(itemsWatch)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update3time(time: MutableList<Double>) {
        itemsTime.clear()
        itemsTime.addAll(time)
        notifyDataSetChanged()
    }

    inner class DetailsStintFragmentViewHolder(val binding: DetailsstintfragmentwatchListBinding) : RecyclerView.ViewHolder(binding.root) {
        //var item: BoxTime? = null

        //var countDownTimer : CountDownTimer? = null

        //fun bind(newItem: BoxTime) {
        //    item = newItem
        //}
    }
}