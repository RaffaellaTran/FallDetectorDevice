package com.app.raffaellatran.falldetectorlibrary.data.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.raffaellatran.falldetectorlibrary.FallDetectorLibrary
import com.app.raffaellatran.falldetectorlibrary.R
import com.app.raffaellatran.falldetectorlibrary.data.model.FallDetectorModel
import io.reactivex.Observable
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.math.sqrt

class FallDetectorService : Service(), SensorEventListener {

    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var mGyroscope: Sensor
    private lateinit var startDateTime: LocalDateTime
    private var activityClass: Class<Activity>? = null
    private val fallThreshold = 2.5f
    private val gravityThreshold = 1f
    private val rotationThreshold = 12f
    private val shakeThreshold = 6f
    private val slopTimeMS = 10L
    private val CHANNEL_ID = "show notification"

    @Inject
    lateinit var fallDetectorRepository: FallDetectorRepository

    override fun onCreate() {
        super.onCreate()
        (applicationContext as FallDetectorLibrary).appComponent.inject(this)
        FallNotification(this).setToForeground()
    }

    override fun onBind(intent: Intent?): IBinder? =
        FallDetectorServiceBinder(fallDetectorRepository.getAll())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        activityClass = intent?.getSerializableExtra(EXTRA_ACTIVITY_CLASS) as Class<Activity>?
        startDateTime = LocalDateTime.now()

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mSensorManager.registerListener(
            this, mAccelerometer,
            SensorManager.SENSOR_DELAY_UI, Handler()
        )
        mSensorManager.registerListener(
            this, mGyroscope,
            SensorManager.SENSOR_DELAY_UI, Handler()
        )
        return START_STICKY
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {

        val x: Float = event.values[0]
        val y: Float = event.values[1]
        val z: Float = event.values[2]

        val gX: Float = x / SensorManager.GRAVITY_EARTH
        val gY: Float = y / SensorManager.GRAVITY_EARTH
        val gZ: Float = z / SensorManager.GRAVITY_EARTH

        // gForce will be close to 1 when there is no movement.
        val gForce =
            sqrt(gX.toDouble() * gX.toDouble() + gY.toDouble() * gY.toDouble() + gZ.toDouble() * gZ.toDouble())
                .toFloat()
        val startDateTimeInMS = startDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

            fallFreeDetected(gZ, startDateTimeInMS)
            shakeDetected(gForce, startDateTimeInMS)

        } else {

            if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
                rotationDetected(event)
            }
        }
    }

    // function to detect shake. For future work related shake, implement here
    private fun shakeDetected(gForce: Float, startDateTimeInMS: Long) {

        if (gForce > shakeThreshold && startDateTimeInMS + slopTimeMS > startDateTimeInMS) {
            Timber.d("SHAKE")
        }
    }

    // function to detect free fall.
    private fun fallFreeDetected(gZ: Float, startDateTimeInMS: Long) {
        if (gZ < gravityThreshold) {
            //assign value for having the correct time
            startDateTime = LocalDateTime.now()
        }

        if (gZ > fallThreshold && startDateTimeInMS + slopTimeMS > startDateTimeInMS) {
            Timber.d("FALL")
            val endFallInMS =
                LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
            val startFallInMS =
                startDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
            val duration = endFallInMS - startFallInMS

            if (duration > slopTimeMS && endFallInMS > startFallInMS) {
                val fall = FallDetectorModel(
                    fallDate = startDateTime,
                    fallDuration = duration
                )
                saveDateDuration(fall)
                FallNotification(this).sendNotification()
            }
        }
    }

    // function to detect rotation. For future work related rotation, implement here
    private fun rotationDetected(event: SensorEvent) {
        val axisX = event.values[0]
        val axisY = event.values[1]
        val axisZ = event.values[2]

        // Calculate the angular speed of the sample
        val omegaMagnitude = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

        if (omegaMagnitude > rotationThreshold) {
            Timber.d("ROTATION")
        }
    }

    private fun saveDateDuration(fallDetector: FallDetectorModel) {
        fallDetectorRepository.addFall(fallDetector)
    }

    companion object {
        const val EXTRA_ACTIVITY_CLASS = "EXTRA_ACTIVITY_CLASS"
    }

    inner class FallDetectorServiceBinder(val fallList: Observable<List<FallDetectorModel>>) :
        Binder()

    inner class FallNotification(val context: Context) {
        fun setToForeground() {
            createNotificationChannel()
            val notification = Notification.Builder(context, CHANNEL_ID)
                .build()
            startForeground(101, notification)
        }

        fun sendNotification() {
            val notificationTitle = getString(R.string.notification_title)
            val notificationDescription = getString(R.string.notification_description)

            val resultIntent = Intent(context, activityClass)
            resultIntent.putExtra("openNotification", true)

            val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(resultIntent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            val notification = Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setGroup(Notification.GROUP_ALERT_ALL.toString())
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)

            FallNotification(context).createNotificationChannel()

            with(NotificationManagerCompat.from(context)) {
                notify(0, notification.build())
            }
        }

        private fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.channel_name)
                val descriptionText = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
                mChannel.description = descriptionText
                val notificationManager =
                    getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
            }
        }
    }
}
