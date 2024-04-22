package com.example.shoppinglistroom

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglistroom.adapter.ShoppingMemoListAdapter
import com.example.shoppinglistroom.database.ShoppingMemo
import com.example.shoppinglistroom.databinding.ActivityMainBinding
import com.example.shoppinglistroom.viewmodel.ShoppingMemoViewModel
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var shoppinMemoViewModel: ShoppingMemoViewModel
    private lateinit var itemTouchHelper: ItemTouchHelper
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

        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val currentMemo = shoppinMemoViewModel.getAllShoppingMemos()?.value?.get(viewHolder.adapterPosition)

                when(direction){
                    ItemTouchHelper.RIGHT ->{ // Eintrag löschen
                        shoppinMemoViewModel.delete(currentMemo!!)
                        Snackbar.make(
                            this@MainActivity,
                            binding.root,
                            "Löschen rückgängig machen",
                            Snackbar.LENGTH_LONG)
                            .setAction(
                                "UNDO"
                            ) { shoppinMemoViewModel.insertOrUpdate(currentMemo) }.show()

                    }
                   ItemTouchHelper.LEFT ->{ // Eintrag editieren

                   }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(Color.LTGRAY)
                    .addSwipeLeftActionIcon(R.drawable.ic_edit_black_24dp)
                    .addSwipeRightActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate()

//                val itemView = viewHolder.itemView
//                val background = ColorDrawable(Color.LTGRAY)
//                val iconDelete = AppCompatResources.getDrawable(this@MainActivity,R.drawable.ic_delete_black_24dp)
//                val iconEdit = AppCompatResources.getDrawable(this@MainActivity,R.drawable.ic_edit_black_24dp)
//                val backgroundCornerOffset=20
//                val iconMargin = (itemView.height - iconEdit!!.intrinsicHeight)/2
//                val iconTop = itemView.top + (itemView.height - iconEdit.intrinsicHeight)/2
//                val iconBottom = iconTop + iconDelete!!.intrinsicHeight
//
//                if(dX > 0){ // Swipe to right
//                    val iconLeft = itemView.left + iconMargin+iconDelete.intrinsicHeight
//                    val iconRight = itemView.left +iconMargin
//                    iconDelete.setBounds(iconLeft,iconTop,iconRight,iconBottom)
//                    background.setBounds(itemView.left,itemView.top,
//                        itemView.left + dX.toInt()+backgroundCornerOffset,itemView.bottom)
//
//                }else if (dX < 0) { // Swipe to left
//                    val iconLeft =
//                        itemView.right - iconMargin - iconEdit.intrinsicWidth
//                    val iconRight = itemView.right - iconMargin
//                    iconEdit.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//                    background.setBounds(
//                        itemView.right + dX.toInt() - backgroundCornerOffset,
//                        itemView.top, itemView.right, itemView.bottom
//                    )
//
//                }else{ // unSwiped
//                    background.setBounds(0,0,0,0)
//                }
//                background.draw(c)
//                iconDelete.draw(c)
//                iconEdit.draw(c)


            }
        })

        itemTouchHelper.attachToRecyclerView(binding.rvShoppingMemos)

    }
}