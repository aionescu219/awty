package edu.washington.aiones.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val messageEdit = findViewById<EditText>(R.id.enterMessage)

        val numberEdit = findViewById<EditText>(R.id.enterNumber)

        val minutesEdit = findViewById<EditText>(R.id.enterMinutes)

        val startButton = findViewById<Button>(R.id.startButton)

        val timer: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intentToSend = Intent(this, Receiver::class.java)

        startButton.isEnabled = false

        var ready = mutableMapOf(R.id.enterMessage to false, R.id.enterNumber to false, R.id.enterMinutes to false)
        var edits = arrayOf(messageEdit, numberEdit, minutesEdit)

        for (edit in edits) {
            edit.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        ready[edit.id] = true
                        startButton.isEnabled = !ready.values.contains(false)
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        startButton.setOnClickListener {

            if (startButton.text.toString() == "start") {
                val message = messageEdit.text.toString()
                val number = numberEdit.text.toString()
                val minutes = minutesEdit.text.toString().toLong()
                val interval = minutes * 60000

                intentToSend.putExtra("MESSAGE", message)
                intentToSend.putExtra("NUMBER", number)

                val pending = PendingIntent.getBroadcast(this,0,intentToSend,0)
                timer.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pending)
                startButton.text = "stop"

            } else {
                val pending = PendingIntent.getBroadcast(this, 0,intentToSend,PendingIntent.FLAG_CANCEL_CURRENT)
                timer.cancel(pending)
                startButton.text = "start"
            }


        }

    }

    inner class Receiver : BroadcastReceiver () {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent!!.getStringExtra("MESSAGE")
            val numberText = intent!!.getStringExtra("NUMBER")
            val phoneNumber = "(${numberText.substring(0, 3)}) ${numberText.substring(3, 6)}-${numberText.substring(6)}"
            val toastMessage = "$phoneNumber: $message"
            Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
        }
    }
}
