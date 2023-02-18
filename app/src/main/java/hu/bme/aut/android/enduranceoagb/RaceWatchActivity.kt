/*package hu.bme.aut.android.enduranceoagb

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import hu.bme.aut.android.enduranceoagb.databinding.ActivityRaceBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityRacewatchBinding
import java.text.DecimalFormat
import java.text.NumberFormat


class RaceWatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRacewatchBinding

    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
    }


    private var raceId: String? = null

    private val CHANNEL_ID = "channel_id_001"
    private val notificationId = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRacewatchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        raceId = intent.getStringExtra(TeamActivity.EXTRA_RACE_NAME)

        binding.tvRaceTime.setText("00:00:50")

        var time = 51000

        lateinit var countDownTimer: CountDownTimer

        var counting = false

        createNotificationChannel()

        binding.startRace.setOnClickListener {
            if (!counting) {
                counting = true
                binding.startRace.setText("STOP")
                binding.startRace.setBackgroundColor(Color.RED)
                binding.startRace.setTextColor(Color.WHITE)
                sendNotification(time.toLong())
                countDownTimer = object : CountDownTimer(time.toLong(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Used for formatting digit to be in 2 digits only
                        val f: NumberFormat = DecimalFormat("00")
                        val hour = millisUntilFinished / 3600000 % 24
                        val min = millisUntilFinished / 60000 % 60
                        val sec = millisUntilFinished / 1000 % 60
                        binding.tvRaceTime.text =
                            f.format(hour).toString() + ":" + f.format(min) + ":" + f.format(
                                sec
                            )
                        time = millisUntilFinished.toInt()
                    }

                    // When the task is over it will print 00:00:00 there
                    override fun onFinish() {
                        binding.tvRaceTime.text = "VÉGE"
                        counting = false
                        time = 51000
                        binding.startRace.text = "START"
                        binding.startRace.setBackgroundColor(Color.GREEN)
                        binding.startRace.setTextColor(Color.BLACK)
                    }
                }.start()
            }
            else {
                countDownTimer.cancel()
                counting = false
                binding.startRace.text = "START"
                binding.startRace.setBackgroundColor(Color.GREEN)
                binding.startRace.setTextColor(Color.BLACK)

            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Endurance OAGB"
            val descriptonText = "A következő cserénél tankolni kell!"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptonText
            }
            val notificationManager: NotificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(time: Long) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(hu.bme.aut.android.enduranceoagb.R.mipmap.endu_logo_round)
            .setContentTitle("Endurance OAGB")
            .setAutoCancel(true)
            .setSound(null)
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, notificationBuilder.build())
        }

        object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Used for formatting digit to be in 2 digits only
                val f: NumberFormat = DecimalFormat("00")
                val hour = millisUntilFinished / 3600000 % 24
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                val contextText =
                    f.format(hour).toString() + ":" + f.format(min) + ":" + f.format(
                        sec
                    )
                println(contextText)
                //notificationBuilder.setContentText("Hátralévő idő: $contextText")
                //with(NotificationManagerCompat.from(this@RaceWatchActivity)) {
                //    notify(notificationId, notificationBuilder.build())
                //}
            }

            // When the task is over it will print 00:00:00 there
            override fun onFinish() {

            }
        }.start()
    }
}*/