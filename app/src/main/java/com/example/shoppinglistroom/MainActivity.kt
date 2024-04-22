package com.example.shoppinglistroom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglistroom.adapter.ShoppingMemoListAdapter
import com.example.shoppinglistroom.database.ShoppingMemo
import com.example.shoppinglistroom.databinding.ActivityMainBinding
import com.example.shoppinglistroom.viewmodel.ShoppingMemoViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var shoppinMemoViewModel: ShoppingMemoViewModel
    private val adapter = ShoppingMemoListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvShoppingMemos.layoutManager = LinearLayoutManager(this)
        binding.rvShoppingMemos.adapter = adapter

        shoppinMemoViewModel = ShoppingMemoViewModel(application)
        shoppinMemoViewModel.getAllShoppingMemos()?.observe(this) {
            adapter.setShoppingMemos(it)
        }

        adapter.setOnItemClickListener(object : ShoppingMemoListAdapter.OnItemClickListener {
            override fun onItemClick(memo: ShoppingMemo) {
                memo.isSelected = !memo.isSelected
                shoppinMemoViewModel.insertOrUpdate(memo)
            }

        })

        binding.btnAddProduct.setOnClickListener {
            if (binding.etQuantity.text.isBlank()) {
                binding.etQuantity.error = "Feld darf nicht leer sein"
                binding.etQuantity.requestFocus()
                return@setOnClickListener
            }

            if (binding.etProduct.text.isBlank()) {
                binding.etProduct.error = "Feld darf nicht leer sein"
                binding.etProduct.requestFocus()
                return@setOnClickListener
            }

            shoppinMemoViewModel.insertOrUpdate(
                ShoppingMemo(
                    binding.etQuantity.text.toString().toInt(),
                    binding.etProduct.text.toString()
                )
            )

            binding.etProduct.text.clear()
            binding.etQuantity.text.clear()
            binding.etQuantity.requestFocus()
        }

        binding.etProduct.setOnEditorActionListener { _, _, _ ->
            binding.btnAddProduct.performClick()
        }

    }
}