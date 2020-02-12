package jp.techacademy.madoka.iwasaki.sensortest

import android.os.Bundle
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var textView: TextView? = null
    private var textInfo: TextView? = null
    private var suddenMoveTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get an instance of the SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        textInfo = findViewById(R.id.text_info)

        // Get an instance of the TextView
        textView = findViewById(R.id.text_view)

    }

    override fun onResume() { // フォアグラウンドになる時に呼ばれる
        super.onResume()
        // Listenerの登録
        val accel = sensorManager!!.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )

        sensorManager!!.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
    }

    // 解除するコードも入れる!
    override fun onPause() {// バックグラウンド移行時に呼ばれる
        super.onPause()
        // Listenerを解除
        sensorManager!!.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val sensorX: Float
        val sensorY: Float
        val sensorZ: Float

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            sensorX = event.values[0]
            sensorY = event.values[1]
            sensorZ = event.values[2]

            val strTmp = ("加速度センサー\n"
                    + " X: " + sensorX + "\n"
                    + " Y: " + sensorY + "\n"
                    + " Z: " + sensorZ)
            textView!!.text = strTmp

            if(getNowTime() > suddenMoveTime + 2000 ){ // 前回記録時から1500ミリ秒以上経過していたら
                // 急停止/急加速時の処理
                if(sensorZ >= 30) {
                    suddenMoveTime = getNowTime() // 記録時間を更新
                    Log.d("debug", "Time: " + suddenMoveTime
                            + "\tsensorZ: " + sensorZ)

                } else if (sensorZ <= -30){
                    suddenMoveTime = getNowTime() // 記録時間を更新
                    Log.d("debug", "Time: " + suddenMoveTime
                            + "\tsensorZ: " + sensorZ)
                }
            }
            showInfo(event)
        }
    }

    // （お好みで）加速度センサーの各種情報を表示
    private fun showInfo(event: SensorEvent) {
        // センサー名
        val info = StringBuffer("Name: ")
        info.append(event.sensor.name)
        info.append("\n")

        // ベンダー名
        info.append("Vendor: ")
        info.append(event.sensor.vendor)
        info.append("\n")

        // 型番
        info.append("Type: ")
        info.append(event.sensor.type)
        info.append("\n")

        // 最小遅れ
        var data = event.sensor.minDelay
        info.append("Mindelay: ")
        info.append(data.toString())
        info.append(" usec\n")

        // 最大遅れ
        data = event.sensor.maxDelay
        info.append("Maxdelay: ")
        info.append(data.toString())
        info.append(" usec\n")

        // レポートモード
        data = event.sensor.reportingMode
        var stinfo = "unknown"
        if (data == 0) {
            stinfo = "REPORTING_MODE_CONTINUOUS"
        } else if (data == 1) {
            stinfo = "REPORTING_MODE_ON_CHANGE"
        } else if (data == 2) {
            stinfo = "REPORTING_MODE_ONE_SHOT"
        }
        info.append("ReportingMode: ")
        info.append(stinfo)
        info.append("\n")

        // 最大レンジ
        info.append("MaxRange: ")
        var fData = event.sensor.maximumRange
        info.append(fData.toString())
        info.append("\n")

        // 分解能
        info.append("Resolution: ")
        fData = event.sensor.resolution
        info.append(fData.toString())
        info.append(" m/s^2\n")

        // 消費電流
        info.append("Power: ")
        fData = event.sensor.power
        info.append(fData.toString())
        info.append(" mA\n")

        textInfo!!.text = info
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    // 1970 年 1 月 1 日 00:00:00から現在までの経過時間をミリ秒で表した数値を返す
    private fun getNowTime(): Long{
        val date = Date()
        return date.time
    }

}