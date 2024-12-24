package com.myjar.jarassignment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.ui.adapter.ItemAdapter
import com.myjar.jarassignment.ui.vm.JarViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<JarViewModel>()
    private lateinit var adapter: ListAdapter<ComputerItem, *>
    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.searchItem)

        setupUi()
        observeFlows()

        editText.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.submitList(viewModel.performSearch(s.toString()))
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun observeFlows() {
        lifecycleScope.launch {
            viewModel.listStringData.collectLatest {
                adapter.submitList(it)
            }
        }

        lifecycleScope.launch {
                viewModel.navigateToItem.filterNotNull().collectLatest {
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra("itemId", it)
                    startActivity(intent)
                }
        }
    }

    private fun setupUi() {
        val recyclerView: RecyclerView = findViewById(R.id.item_list)
        adapter = ItemAdapter { selectedItem ->
            viewModel.navigateToItemDetail(selectedItem.id)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchData()
    }
}