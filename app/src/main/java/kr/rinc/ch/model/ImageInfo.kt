package kr.rinc.ch.model

import android.net.Uri
import java.io.File

class ImageInfo {
  constructor(uri: Uri, file: File, TAG_GPS_LATITUDE: Double, TAG_GPS_LONGITUDE: Double) {
    this.TAG_GPS_LATITUDE = TAG_GPS_LATITUDE
    this.TAG_GPS_LONGITUDE = TAG_GPS_LONGITUDE
    this.uri = uri
    this.file = file
  }

  var name = ""
  var TAG_GPS_LATITUDE = 0.0
  var TAG_GPS_LONGITUDE = 0.0
  lateinit var uri: Uri
  lateinit var file: File
  var path = ""
}