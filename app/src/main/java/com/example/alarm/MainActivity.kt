package com.example.alarm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alarm.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var todo: Todo
    private lateinit var oldTodo: Todo
    var isUpdate = false
    private val PICK_IMAGE_REQUEST = 5
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)

        createNotificationChennel()
        try {
            oldTodo = intent.getSerializableExtra("current_todo") as Todo
            binding.etTitle.setText(oldTodo.title)
            binding.etNote.setText(oldTodo.note)
//            binding.shapeImg.setImageURI(oldTodo.image)
            isUpdate = true
        }catch (e: Exception){
            e.printStackTrace()
        }
        binding.buttonAlarm.setOnClickListener { scheduleNotification() }

        binding.imgBackArrow.setOnClickListener {
            onBackPressed()
        }
        binding.imgPhoto.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            selectedImageUri = data?.data!!
            binding.shapeImg.setImageURI(selectedImageUri)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification() {
        val title = binding.etTitle.text.toString()
        val intent = Intent(applicationContext, Notification::class.java)
        val message = binding.etNote.text.toString()

        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        if(title.isNotEmpty() && message.isNotEmpty()){
            if (!::selectedImageUri.isInitialized) {
                Toast.makeText(this@MainActivity, "Please select an image", Toast.LENGTH_LONG).show()
                return
            }
            val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val time = getTime()
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )

            if (isUpdate) {
                todo = Todo(
                    oldTodo.id,
                    title,
                    message,
                    formatter.format(Date()),
                    selectedImageUri.toString()
                )
            } else {
                todo = Todo(
                    null,
                    title,
                    message,
                    formatter.format(Date()),
                    selectedImageUri.toString()
                )
            }

            var intent = Intent()
            intent.putExtra("todo", todo)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }else {
            Toast.makeText(this@MainActivity, "please enter some data", Toast.LENGTH_LONG).show()
            return
        }
    }

    private fun getTime(): Long {
        val min = binding.timePicker.minute
        val hr = binding.timePicker.hour
        Log.d("getTime", "Hour: $hr, Minute: $min")

        val calendar = Calendar.getInstance()

        try {
            calendar.set(Calendar.HOUR_OF_DAY, hr)
            calendar.set(Calendar.MINUTE, min)
        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.e("getTime", "ArrayIndexOutOfBoundsException: ${e.message}")
        }

        return calendar.timeInMillis
    }

    @SuppressLint("NewApi")
    private fun createNotificationChennel() {
        val name = "Notify Channel"
        val desc = "A Description of channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        NotificationManager.createNotificationChannel(channel)
    }
}