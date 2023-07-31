package com.surajmyt.averysync.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.surajmyt.averysync.R
import com.surajmyt.averysync.activities.TaskActivity
import com.surajmyt.averysync.models.Task

open class TaskItemAdapter
    (private val context: Context, private val list: ArrayList<Task>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(
            R.layout.task_item, parent, false
        )
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.8).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins((12.toDp().toPx()), 0, (30.toDp().toPx()), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        val createTaskTextView = holder.itemView.findViewById<TextView>(R.id.crt_tsk_lst_txt_view)
        val taskItemLinLayout = holder.itemView.findViewById<LinearLayout>(R.id.tsk_itm_lin_lt)
        val taskTitleTextView = holder.itemView.findViewById<TextView>(R.id.tsk_lst_ttl_txt_view)
        val createTaskListCardView = holder.itemView.findViewById<CardView>(R.id.crt_tsk_lst_name_card_view)
        val listNameClsBtn = holder.itemView.findViewById<ImageButton>(R.id.lst_name_cls_btn)
        val listNameSavBtn = holder.itemView.findViewById<ImageButton>(R.id.lst_name_sv_btn)
        val listName = holder.itemView.findViewById<EditText>(R.id.tsk_lst_name_edt_txt)

        if (holder is MyViewHolder) {
            if (position == list.size - 1) {
                createTaskTextView.visibility = View.VISIBLE
                taskItemLinLayout.visibility = View.GONE
            }
            else {
                createTaskTextView.visibility = View.GONE
                taskItemLinLayout.visibility = View.VISIBLE
            }

            taskTitleTextView.text = model.title
            createTaskTextView.setOnClickListener {
                createTaskTextView.visibility = View.GONE
                createTaskListCardView.visibility = View.VISIBLE
            }
            listNameClsBtn.setOnClickListener {
                createTaskTextView.visibility = View.VISIBLE
                createTaskListCardView.visibility = View.GONE
            }
            listNameSavBtn.setOnClickListener {
                val listNameString = listName.toString()

                if (listNameString.isNotEmpty()){

                    if (context is TaskActivity) {
                        context.createTaskList(listNameString)
                    }
                    else {
                        Toast.makeText(context, "List Name Required", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}