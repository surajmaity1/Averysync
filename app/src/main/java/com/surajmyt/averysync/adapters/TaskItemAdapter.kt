package com.surajmyt.averysync.adapters

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        val listEdtBtn = holder.itemView.findViewById<ImageButton>(R.id.lst_edt_btn)
        val edtTaskListNamCardView = holder.itemView.findViewById<CardView>(R.id.edt_tsk_lst_name_card_view)
        val titleViewLinLayout = holder.itemView.findViewById<LinearLayout>(R.id.ttl_view_lin_lt)
        val edtTaskNameEdtTxt = holder.itemView.findViewById<EditText>(R.id.edt_tsk_name_edt_txt)
        val edtViewClsBtn = holder.itemView.findViewById<ImageButton>(R.id.edt_view_cls_btn)
        val edtListNameSavBtn = holder.itemView.findViewById<ImageButton>(R.id.edt_lst_name_sv_btn)
        val listDelBtn = holder.itemView.findViewById<ImageButton>(R.id.lst_del_btn)



        if (holder is MyViewHolder) {
            // Create List
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
                Log.i("checkListName1", listName.toString())
                val listNameString = listName.text.toString()
                Log.i("checkListName2", listNameString)

                if (listNameString.isNotEmpty()){
                    if (context is TaskActivity) {
                        context.createTaskList(listNameString)
                    }
                }
                else {
                    Toast.makeText(context, "List Name Required", Toast.LENGTH_SHORT).show()
                }
            }

            // Update or Edit a list
            listEdtBtn.setOnClickListener {
                edtTaskNameEdtTxt.setText(model.title)
                titleViewLinLayout.visibility = View.GONE
                edtTaskListNamCardView.visibility = View.VISIBLE
            }
            edtViewClsBtn.setOnClickListener {
                titleViewLinLayout.visibility = View.VISIBLE
                edtTaskListNamCardView.visibility = View.GONE
            }
            edtListNameSavBtn.setOnClickListener {
                val lstName = edtTaskNameEdtTxt.text.toString()

                if (context is TaskActivity) {
                    context.updateTaskList(lstName, position, model)
                }else {
                    Toast.makeText(context, "List Name Required", Toast.LENGTH_SHORT).show()
                }
            }
            listDelBtn.setOnClickListener {
                showAlertDialogAsWarning(position, model.title)
            }
        }
    }

    private fun showAlertDialogAsWarning(position: Int, title: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Warning")
        alertDialogBuilder.setIcon(R.drawable.ic_warning)
        alertDialogBuilder.setMessage("Delete $title ?")
        alertDialogBuilder.setPositiveButton("Delete") { dialogInterface, _ ->
            dialogInterface.dismiss()

            if (context is TaskActivity) {
                context.deleteTaskList(position)
            }
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val createAlertDialog = alertDialogBuilder.create()
        createAlertDialog.setCancelable(false)
        createAlertDialog.show()
    }



    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}