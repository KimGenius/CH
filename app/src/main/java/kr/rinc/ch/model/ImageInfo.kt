package kr.rinc.ch.model

class ImageInfo {
  constructor(name: String, TAG_GPS_LATITUDE: Double, TAG_GPS_LONGITUDE: Double) {
    this.name = name
    this.TAG_GPS_LATITUDE = TAG_GPS_LATITUDE
    this.TAG_GPS_LONGITUDE = TAG_GPS_LONGITUDE
  }

  var name = ""
  var TAG_GPS_LATITUDE = 0.0
  var TAG_GPS_LONGITUDE = 0.0
}