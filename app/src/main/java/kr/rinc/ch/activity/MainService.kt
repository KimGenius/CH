package kr.rinc.ch.activity

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kr.rinc.ch.R
import kr.rinc.ch.adapter.HeadImageListAdapter
import kr.rinc.ch.model.ImageInfo
import kr.rinc.ch.model.ImageList
import kr.rinc.ch.util.IntentUtil
import java.io.File
import java.io.IOException

public class MainService : Service() {
  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()
  }

  override fun onStart(intent: Intent?, startId: Int) {
    super.onStart(intent, startId)
    push()
  }

  var absolutePathOfImage = ""
  @SuppressLint("MissingPermission")
  fun sendNotification() {
    val mBuilder = NotificationCompat.Builder(this)
    //Create the intent that’ll fire when the user taps the notification//

    val intent = Intent(applicationContext, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

    mBuilder.setContentIntent(pendingIntent)

    mBuilder.setSmallIcon(R.drawable.ic_c)
    mBuilder.setContentTitle("Compare History")
    mBuilder.setContentText("")
    mBuilder.setAutoCancel(true)
    val vib = longArrayOf(1000, 1000)
    mBuilder.setVibrate(vib) //노티가 등록될 때 진동 패턴 1초씩 두번.
    mBuilder.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
    val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val locationProvider = LocationManager.GPS_PROVIDER;
    val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
    if (lastKnownLocation != null) {
      val myLng = lastKnownLocation.longitude;
      val myLat = lastKnownLocation.latitude;
      val result = ArrayList<String>()
      val uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
      val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)

      val cursor = contentResolver.query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc")
      val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
      val columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
      var lastIndex: Int
      while (cursor.moveToNext()) {
        absolutePathOfImage = "/storage/emulated/0/DCIM/Camera/20180331_222533_HDR.jpg"
        val nameOfFile = cursor.getString(columnDisplayname)
        lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile)
        lastIndex = if (lastIndex >= 0) lastIndex else nameOfFile.length - 1

        if (!TextUtils.isEmpty(absolutePathOfImage)) {
          try {
            val exif = ExifInterface(absolutePathOfImage)
            val resultShow = showExif(exif)
            if (resultShow[0] != 365.0) {
              val toResult = getDistance(resultShow[0], myLat, resultShow[1], myLng)
//            Log.d("dis", toResult.toString())
              if (toResult <= 100) {
                result.add(absolutePathOfImage)
//              Log.d("path where0: ", resultShow[0].toString())
//              Log.d("path where1: ", resultShow[1].toString())
//              Log.d("path : ", absolutePathOfImage)
//                imageInfo.add(ImageInfo(Uri.parse(absolutePathOfImage), File(absolutePathOfImage), resultShow[0], resultShow[1]))

              } else {
//              Log.d("멀", "다")
              }
            }
          } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show()
          }
        }
      }
//        Log.d("test MAIN", getDistance(lat, lat, lng, lng).toString())
//        Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
    }
    val context = applicationContext
    val bigPictureStyle = NotificationCompat.BigPictureStyle(mBuilder) //상단의 빌더를 인자로 받음..
    val bigPictureBitmap = BitmapFactory.decodeFile(absolutePathOfImage) //드래그 후 공간에 표시할 이미지.
    bigPictureStyle.bigPicture(bigPictureBitmap) //상단의 비트맵을 넣어준다.
        .setBigContentTitle("당신의 추억을 확인하세요") //열렸을때의 타이틀
        .setSummaryText("이곳에 당신의 추억이 있습니다") //열렸을때의 Description
    mBuilder.setStyle(bigPictureStyle)
    val buil = mBuilder.build()
    val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    mNotificationManager.notify(1, buil)
  }

  fun push() {
    Handler().postDelayed({
      sendNotification()
      push()
    }, 60000)
  }

  fun showExif(exif: ExifInterface): DoubleArray {
    try {
      val lat = getTagString(ExifInterface.TAG_GPS_LATITUDE, exif).split(":")[1].trim()
//      Log.d("lat", lat)
      val lon = getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif).split(":")[1].trim()
//      Log.d("lon", lon)
      val splLat = lat.split(',')
      val splLon = lon.split(',')
      val resultLat = convertDMSToDD(splLat[0], splLat[1], splLat[2])
      val resultLng = convertDMSToDD(splLon[0], splLon[1], splLon[2])
      return doubleArrayOf(resultLat, resultLng)
    } catch (e: Exception) {
//      Log.e("err", e.message + "")
      return doubleArrayOf(365.0, 365.0)
    }
//    mView.setText(myAttribute)
  }

  private val radi = 6378100.0
  private fun getDistance(latitude1: Double, latitude2: Double, longitude1: Double, longitude2: Double): Double {
    val dLat = deg2rad(latitude2 - latitude1)
    val dLon = deg2rad(longitude2 - longitude1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(latitude1)) * Math.cos(deg2rad(latitude2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    val d = radi * c
    return Math.abs(d)
  }

  private fun deg2rad(deg: Double): Double {
    return deg * (Math.PI / 180)
  }

  private fun convertDMSToDD(str1: String, str2: String, str3: String): Double {
    val degreeString = str1.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val minuteString = str2.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val secondString = str3.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val degree = java.lang.Double.parseDouble(degreeString[0]) / java.lang.Double.parseDouble(degreeString[1])
    val minute = java.lang.Double.parseDouble(minuteString[0]) / java.lang.Double.parseDouble(minuteString[1]) / 60.0
    val second = java.lang.Double.parseDouble(secondString[0]) / java.lang.Double.parseDouble(secondString[1]) / 3600.0
    return degree + minute + second
  }

  fun getTagString(tag: String, exif: ExifInterface): String {
    return (tag + " : " + exif.getAttribute(tag) + "\n")
  }


  override fun onDestroy() {
    super.onDestroy()
  }
}