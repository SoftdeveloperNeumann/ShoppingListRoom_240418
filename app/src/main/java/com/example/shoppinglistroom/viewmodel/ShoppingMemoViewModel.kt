package com.example.shoppinglistroom.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinglistroom.database.ShoppingMemo
import com.example.shoppinglistroom.repository.ShoppingMemoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ShoppingMemoViewModel: ViewModel() {
    private var repository:ShoppingMemoRepository? = null
    private var allShoppingMemos: LiveData<List<ShoppingMemo>>? = null

   operator fun invoke(app:Application){
       repository = ShoppingMemoRepository(app)
       allShoppingMemos = repository?.getAllShoppingMemos()
   }

    fun getAllShoppingMemos():LiveData<List<ShoppingMemo>>?{
        return allShoppingMemos
    }

    fun insertOrUpdate(memo: ShoppingMemo){
        CoroutineScope(Dispatchers.IO).launch {
            repository?.insertOrUpdate(memo)
        }
    }

    fun delete(memo: ShoppingMemo){
        CoroutineScope(Dispatchers.IO).launch {
            repository?.delete(memo)
        }
    }


}