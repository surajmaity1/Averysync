package com.surajmyt.averysync.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmyt.averysync.R
import com.surajmyt.averysync.models.Board
import de.hdodenhof.circleimageview.CircleImageView

open class BoardAdapter(
    private val context: Context,
    private val list: ArrayList<Board>
): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.new_board_item, parent, false)
        )
    }

    override fun getItemCount(): Int  = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val model = list[position]

        if (holder is MyViewHolder) {
            val newBoardImg = holder.itemView.findViewById<CircleImageView>(R.id.new_brd_itm_cir_img)

            Glide
                .with(context)
                .load(model.img)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(newBoardImg)

            val developedBy = holder.itemView.resources.getString(R.string.developed_by) +" "+ model.createdBy

            holder.itemView.findViewById<TextView>(R.id.new_brd_nam_tv).text = model.name
            holder.itemView.findViewById<TextView>(R.id.brd_md_by).text = developedBy

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnClickListener{
        fun onClick(position: Int, model: Board)
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}