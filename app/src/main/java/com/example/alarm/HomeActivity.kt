package com.example.alarm

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarm.databinding.ActivityHomeBinding
import com.example.alarm.databinding.ActivityMainBinding
import com.example.todoalarm.presentation.screens.viewmodel.TodoViewModel

class HomeActivity : AppCompatActivity(), TodoAdapter.TodoClickListener  {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var database: TodoDatabase
    lateinit var viewModel: TodoViewModel
    lateinit var adapter: TodoAdapter

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val todoToDelete = adapter.getTodoAtPosition(position)
            viewModel.deleteTodo(todoToDelete)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)


        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recycleView)

        initUI()
        database = TodoDatabase.getDatabase(this)
        val repository = TodoRepository(database.getTodoDao())
        viewModel = TodoViewModel(repository)

        viewModel.allTodo.observe(this) { list ->
            list?.let {
                adapter.updateList(list)
            }
        }

    }

    private fun initUI() {
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = TodoAdapter(this, this)
        binding.recycleView.adapter = adapter

        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val todo = result.data?.getSerializableExtra("todo") as? Todo
                    if (todo != null) {
                        viewModel.insertTodo(todo)
                    }
                }
            }

        binding.fab.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            getContent.launch(intent)
        }
    }

    private val updateOrDeleteTodo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val todo = result.data?.getSerializableExtra("todo") as Todo
                val isDelete = result.data?.getBooleanExtra("delete_todo", false) as Boolean
                if (todo != null && !isDelete) {
                    viewModel.updateTodo(todo)
                } else if (todo != null && isDelete) {
                    viewModel.deleteTodo(todo)
                }
            }
        }


    override fun onItemClicked(todo: Todo) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("current_todo", todo)
        updateOrDeleteTodo.launch(intent)
    }
}