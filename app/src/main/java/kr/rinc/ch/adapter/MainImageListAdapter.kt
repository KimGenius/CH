package kr.rinc.ch.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.recycler_main_view.view.*
import kr.rinc.ch.R
import kr.rinc.ch.holder.GridViewHolder
import kr.rinc.ch.model.ImageList
import kr.rinc.ch.util.GlideUtil

class MainImageListAdapter(private val context: Context, private val gson: ImageList) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  override fun getItemCount(): Int = gson.imageInfo.size
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    //todo
    val gridViewHolder = holder as GridViewHolder
    Log.d("position", position.toString())
    GlideUtil.setImage(context, gson.imageInfo[position].file, gridViewHolder.itemView.bg)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val viewHolder: RecyclerView.ViewHolder
    val view = LayoutInflater.from(context).inflate(R.layout.recycler_main_view, parent, false)
    viewHolder = GridViewHolder(view)
    return viewHolder
  }

}