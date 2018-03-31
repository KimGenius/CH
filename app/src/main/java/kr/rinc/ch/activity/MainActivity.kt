package kr.rinc.ch.activity

import android.Manifest
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
import android.support.v7.widget.GridLayoutManager
import kr.rinc.ch.adapter.HeadImageListAdapter
import kr.rinc.ch.adapter.MainImageVListAdapter
import kr.rinc.ch.model.ImageInfo
import kr.rinc.ch.model.ImageList
import kr.rinc.ch.model.ImageListWrap


class MainActivity : BaseActivity() {

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val permissionlistener = object : PermissionListener {
      override fun onPermissionGranted() {
//        getPathOfAllImages()
        Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
      }

      override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
        Toast.makeText(this@MainActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
      }
    }
    TedPermission.with(this)
        .setPermissionListener(permissionlistener)
        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        .check()
    val layoutManager = GridLayoutManager(this@MainActivity, 1)
    layoutManager.orientation = GridLayoutManager.HORIZONTAL
    headRecycler.layoutManager = layoutManager
//    for (i in response.body()!!.projects) {
//      Log.d("projectList : ", i.toString())
//    }
    val imageInfo = ArrayList<ImageInfo>()
    imageInfo.add(ImageInfo("a", 0.1, 0.2))
    imageInfo.add(ImageInfo("b", 0.1, 0.2))
    imageInfo.add(ImageInfo("c", 0.1, 0.2))

    val testImageList = ImageList(imageInfo, "now")
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

//    iv.setOnClickListener {
//      val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//      startActivityForResult(intent, 1)
//    }
//
//    logTokenButton.setOnClickListener {
//      sendNotification()
//    }

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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    Log.d("log : ", data.data.toString())
    Log.d("log : ", requestCode.toString())
//    GlideUtil.setImage(this@MainActivity, data.data, iv)
    val imageUris = ArrayList<Uri>()
    imageUris.add(data.data)
    imageUris.add(data.data)
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUris)
    shareIntent.type = "image/jpeg"
    startActivity(Intent.createChooser(shareIntent, "hi"))
  }

  private fun getPathOfAllImages(): ArrayList<String> {
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
          Log.d("path : ", absolutePathOfImage)
          val exif = ExifInterface(absolutePathOfImage)
          showExif(exif)
        } catch (e: IOException) {
          e.printStackTrace()
          Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show()
        }
        result.add(absolutePathOfImage)
      }
    }

    for (string in result) {
//      Log.i("getPathOfAllImages", "|$string|")
    }
    Log.i("getPathOfAllImages", result.size.toString())
    return result
  }

  fun showExif(exif: ExifInterface) {

    var myAttribute = "[Exif information] \n"

//    myAttribute += getTagString(ExifInterface.TAG_DATETIME, exif)
//    myAttribute += getTagString(ExifInterface.TAG_FLASH, exif)
    myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif)
//    myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif)
    myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif)
//    myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif)
//    myAttribute += getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif)
//    myAttribute += getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif)
//    myAttribute += getTagString(ExifInterface.TAG_MAKE, exif)
//    myAttribute += getTagString(ExifInterface.TAG_MODEL, exif)
//    myAttribute += getTagString(ExifInterface.TAG_ORIENTATION, exif)
//    myAttribute += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif)

    Log.d("attribute : ", myAttribute)
//    mView.setText(myAttribute)
  }

  fun getTagString(tag: String, exif: ExifInterface): String {
    return (tag + " : " + exif.getAttribute(tag) + "\n")
  }
}
