package com.example.alarm

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(private val context: Context, val listener: TodoClickListener):
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    private val todoList: MutableList<Todo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(context).inflate(R.layout.todo_alarm_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        val item = todoList[position]
        val imageUri = Uri.parse(item.image)
        holder.note.text = item.note
        holder.title.text = item.title
        holder.imageView?.setImageURI(imageUri)
        holder.todo_layout.setOnClickListener {
            listener.onItemClicked(todoList[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
    fun getTodoAtPosition(position: Int): Todo {
        return todoList[position]
    }

    fun updateList(newList: List<Todo>){
        todoList.clear()
        todoList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class TodoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val todo_layout = itemView.findViewById<ConstraintLayout>(R.id.cLayout)
        val note = itemView.findViewById<TextView>(R.id.tvNote)
        val title = itemView.findViewById<TextView>(R.id.tvTitle)
        val imageView = itemView.findViewById<ImageView>(R.id.ivImg)
    }

    interface TodoClickListener {
        fun onItemClicked(todo: Todo)
    }
}