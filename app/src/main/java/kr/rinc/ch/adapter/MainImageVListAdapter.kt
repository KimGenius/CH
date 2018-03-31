package kr.rinc.ch.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.recycler_main_v_view.view.*
import kr.rinc.ch.R
import kr.rinc.ch.holder.GridViewHolder
import kr.rinc.ch.model.ImageListWrap

class MainImageVListAdapter(private val context: Context, private val gson: ImageListWrap) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  override fun getItemCount(): Int = gson.imageList.size
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    //todo
    val gridViewHolder = holder as GridViewHolder
    gridViewHolder.itemView.createAt.text = gson.imageList[position].dateCreated
//    GlideUtil.setImage(context, R.drawable.bg_picture, gridViewHolder.itemView.bg)
    val layoutManager1 = GridLayoutManager(context, 1)
    layoutManager1.orientation = GridLayoutManager.HORIZONTAL
    gridViewHolder.itemView.recycler.layoutManager = layoutManager1
    gridViewHolder.itemView.recycler.adapter = MainImageListAdapter(context, gson.imageList[position])

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val viewHolder: RecyclerView.ViewHolder
    val view = LayoutInflater.from(context).inflate(R.layout.recycler_main_v_view, parent, false)
    viewHolder = GridViewHolder(view)
    return viewHolder
  }

}