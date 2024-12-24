package com.myjar.jarassignment.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myjar.jarassignment.createRetrofit
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.repository.JarRepository
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JarViewModel : ViewModel() {

    private val _listStringData = MutableStateFlow<List<ComputerItem>>(emptyList())
    val listStringData: StateFlow<List<ComputerItem>>
        get() = _listStringData

    private val _filterText = MutableStateFlow<List<ComputerItem>>(emptyList())
    val filterText: StateFlow<List<ComputerItem>>
        get() = _filterText



    private val _navigateToItem = MutableStateFlow<String?>(null)
    val navigateToItem: StateFlow<String?>
        get() = _navigateToItem

    private val repository: JarRepository = JarRepositoryImpl(createRetrofit())

    fun fetchData() {
        viewModelScope.launch {
            repository.fetchResults().collect{
                println(it)
                _listStringData.value = it
            }
        }
    }

    fun performSearch(text : String) : List<ComputerItem>{

        val filterText = _listStringData.value.filter {
            it.name.contains(text)
        }

        return  filterText


    }

    fun navigateToItemDetail(id: String) {
        viewModelScope.launch {
            _navigateToItem.emit(id)
        }
    }
}