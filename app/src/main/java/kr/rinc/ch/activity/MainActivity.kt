package kr.rinc.ch.activity

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.os.Bundle
import android.widget.Toast
import android.media.ExifInterface
import android.os.Build
import android.util.Log
import android.text.TextUtils
import android.provider.MediaStore.MediaColumns
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import kr.rinc.ch.R
import java.io.IOException
import android.content.Intent
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import kr.rinc.ch.adapter.HeadImageListAdapter
import kr.rinc.ch.adapter.MainImageVListAdapter
import kr.rinc.ch.model.ImageInfo
import kr.rinc.ch.model.ImageList
import kr.rinc.ch.model.ImageListWrap
import java.io.File


class MainActivity : BaseActivity() {
  var imageInfo = ArrayList<ImageInfo>()

  @SuppressLint("MissingPermission")
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val permissionlistener = object : PermissionListener {
      override fun onPermissionGranted() {
        val locationManager = this@MainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationProvider = LocationManager.GPS_PROVIDER;
        val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
          val lng = lastKnownLocation.longitude;
          val lat = lastKnownLocation.latitude;
          getPathOfAllImages(lat, lng)
//          Log.d("test MAIN", getDistance(lat, lat, lng, lng).toString())
//          Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
        }
//        Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
      }

      override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
        Toast.makeText(this@MainActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
      }
    }
    TedPermission.with(this)
        .setPermissionListener(permissionlistener)
        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        .check()
    val layoutManager = GridLayoutManager(this@MainActivity, 1)
    layoutManager.orientation = GridLayoutManager.HORIZONTAL
    headRecycler.layoutManager = layoutManager
//    for (i in response.body()!!.projects) {
//      Log.d("projectList : ", i.toString())
//    }

    cameraBtn.setOnClickListener {
      val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
      startActivityForResult(intent, 1)
    }

    shareHead.setOnClickListener {
      val shareIntent = Intent()
      shareIntent.action = Intent.ACTION_SEND_MULTIPLE
      val imageList = ArrayList<Uri>()
      for (image in imageInfo) {
        imageList.add(image.uri)
      }
      shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageList)
      shareIntent.type = "image/*"
      startActivity(Intent.createChooser(shareIntent, "Share images to.."))
    }

    swipe.setOnRefreshListener {
      val locationManager = this@MainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
      val locationProvider = LocationManager.GPS_PROVIDER;
      val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
      if (lastKnownLocation != null) {
        val lng = lastKnownLocation.longitude;
        val lat = lastKnownLocation.latitude;
        imageInfo = ArrayList<ImageInfo>()
        getPathOfAllImages(lat, lng)
//        Log.d("test MAIN", getDistance(lat, lat, lng, lng).toString())
//        Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
      }
      swipe.isRefreshing = false
    }
//
//    logTokenButton.setOnClickListener {
//      sendNotification()
//    }

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

  fun sendNotification() {
    val mBuilder = NotificationCompat.Builder(this)
    //Create the intent that’ll fire when the user taps the notification//

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.androidauthority.com/"))
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

    mBuilder.setContentIntent(pendingIntent)

    mBuilder.setSmallIcon(R.drawable.ic_c)
    mBuilder.setContentTitle("My notification")
    mBuilder.setContentText("Hello World!")
    mBuilder.setAutoCancel(true)
    val vib = longArrayOf(1000, 1000)
    mBuilder.setVibrate(vib) //노티가 등록될 때 진동 패턴 1초씩 두번.
    mBuilder.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)

    val context = this@MainActivity
    val bigPictureStyle = NotificationCompat.BigPictureStyle(mBuilder) //상단의 빌더를 인자로 받음..
    val bigPictureBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_c) //드래그 후 공간에 표시할 이미지.
    bigPictureStyle.bigPicture(bigPictureBitmap) //상단의 비트맵을 넣어준다.
        .setBigContentTitle("타이틀") //열렸을때의 타이틀
        .setSummaryText("설명") //열렸을때의 Description
    mBuilder.setStyle(bigPictureStyle)
    val buil = mBuilder.build()
    val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    mNotificationManager.notify(1, buil)
  }

  @SuppressLint("MissingPermission")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    try {
      // Find the last picture
//      Log.d("log : ", data.data.toString())
      val locationManager = this@MainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
      val locationProvider = LocationManager.GPS_PROVIDER;
      val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
      if (lastKnownLocation != null) {
        val lng = lastKnownLocation.longitude;
        val lat = lastKnownLocation.latitude;
        imageInfo = ArrayList<ImageInfo>()
        getPathOfAllImages(lat, lng)
//        Log.d("test MAIN", getDistance(lat, lat, lng, lng).toString())
//        Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
      }
    } catch (err: Exception) {
      Log.e("err ", err.message + "")
    }
  }

  @SuppressLint("SetTextI18n")
  private fun getPathOfAllImages(myLat: Double, myLng: Double): ArrayList<String> {
    val result = ArrayList<String>()
    val uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaColumns.DATA, MediaColumns.DISPLAY_NAME)

    val cursor = contentResolver.query(uri, projection, null, null, MediaColumns.DATE_ADDED + " desc")
    val columnIndex = cursor!!.getColumnIndexOrThrow(MediaColumns.DATA)
    val columnDisplayname = cursor.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME)
    var lastIndex: Int
    while (cursor.moveToNext()) {
      val absolutePathOfImage = cursor.getString(columnIndex)
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
              imageInfo.add(ImageInfo(Uri.parse(absolutePathOfImage), File(absolutePathOfImage), resultShow[0], resultShow[1]))

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

    for (string in result) {
//      Log.i("getPathOfAllImages", "|$string|")
    }
    Log.i("getPathOfAllImages", result.size.toString())
    currentImageCount.text = "일치 사진 " + result.size.toString() + "장"
    val testImageList = ImageList(imageInfo, "")
    headRecycler.adapter = HeadImageListAdapter(this@MainActivity, testImageList)


    val layoutManager1 = GridLayoutManager(this@MainActivity, 1)
    layoutManager1.orientation = GridLayoutManager.VERTICAL
    mainRecycler.layoutManager = layoutManager1

    val imageList = ArrayList<ImageList>()
    imageList.add(testImageList)
    imageList.add(testImageList)
    imageList.add(testImageList)

    val testImageListWrap = ImageListWrap(imageList)
    mainRecycler.adapter = MainImageVListAdapter(this@MainActivity, testImageListWrap)
    return result
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

  fun getTagString(tag: String, exif: ExifInterface): String {
    return (tag + " : " + exif.getAttribute(tag) + "\n")
  }
}
