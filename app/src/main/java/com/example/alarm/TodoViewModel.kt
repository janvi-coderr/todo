package com.example.todoalarm.presentation.screens.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarm.Todo
import com.example.alarm.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel( private val repository: TodoRepository): ViewModel() {

    val allTodo : LiveData<List<Todo>>

    init {
        try {
            allTodo = repository.allTodos
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun insertTodo(todo: Todo) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(todo)
    }

    fun updateTodo(todo: Todo) = viewModelScope.launch(Dispatchers.IO){
        repository.update(todo)
    }

    fun deleteTodo(todo: Todo) = viewModelScope.launch(Dispatchers.IO){
        repository.delete(todo)
    }
}