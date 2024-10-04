package hu.bme.aut.android.enduranceoagb.public

import android.os.SystemClock
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class SntpClient {
    private val ntpHost = "time.google.com"
    private val ntpPort = 123
    private val timeout = 3000

    fun getNtpTime(): Long {
        val buffer = ByteArray(48)
        buffer[0] = 0x1B.toByte()

        val address = InetAddress.getByName(ntpHost)
        val packet = DatagramPacket(buffer, buffer.size, address, ntpPort)

        DatagramSocket().use { socket ->
            socket.soTimeout = timeout
            socket.send(packet)
            socket.receive(packet)

            val transmitTime = (buffer[43].toLong() and 0xFF) shl 24 or
                    (buffer[42].toLong() and 0xFF) shl 16 or
                    (buffer[41].toLong() and 0xFF) shl 8 or
                    (buffer[40].toLong() and 0xFF)

            return (transmitTime * 1000) + (SystemClock.elapsedRealtime() % 1000)
        }
    }
}
