package kr.rinc.ch.model

class ImageList {
  constructor(imageInfo : ArrayList<ImageInfo>, dateCreated : String) {
    this.imageInfo = imageInfo
    this.dateCreated = dateCreated
  }
  lateinit var imageInfo : ArrayList<ImageInfo>
  var dateCreated = ""
}