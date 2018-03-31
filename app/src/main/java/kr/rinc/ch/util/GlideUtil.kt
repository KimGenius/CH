package kr.rinc.ch.util

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

object GlideUtil {
    fun setImage(ctx: Context, id: Int, image: ImageView) {
        Glide.with(ctx).load(id).into(image)
    }
    fun setImage(ctx: Context, id: String, image: ImageView) {
        Glide.with(ctx).load(id).into(image)
    }
    fun setImage(ctx: Context, id: Uri, image: ImageView) {
        Glide.with(ctx).load(id).into(image)
    }
    fun setImage(ctx: Context, id: File, image: ImageView) {
        Glide.with(ctx).load(id).into(image)
    }
}