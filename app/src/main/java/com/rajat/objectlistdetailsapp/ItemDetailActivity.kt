package com.rajat.objectlistdetailsapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.rajat.objectlistdetailsapp.model.ItemModel
import com.rajat.objectlistdetailsapp.roomdb.AppDataBaseHelper
import kotlinx.android.synthetic.main.activity_item_detail.*

class ItemDetailActivity : AppCompatActivity() {
    object ITEM {
        const val itemArg = "item"
        const val requestCode = 1
    }

    private lateinit var itemData: ItemModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        itemData = intent.getSerializableExtra(ITEM.itemArg) as ItemModel
        if (itemData.name == null) {
            finish()
        }
        setDataOnUI(itemData)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        val findItem = menu!!.findItem(R.id.menu_favorite) as MenuItem
        setFavoriteMenuItemDrawable(findItem)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorite -> {
                itemData.isFavorite = !itemData.isFavorite
                AppDataBaseHelper.getInstance(this).itemDao().updateItem(itemData)
                setFavoriteMenuItemDrawable(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setDataOnUI(item: ItemModel) {
        title = item.name
        tv_detail.text = item.details
    }

    private fun setFavoriteMenuItemDrawable(findItem: MenuItem) {
        if (itemData.isFavorite) {
            findItem.icon = getDrawable(R.drawable.ic_star_24dp)
        } else {
            findItem.icon = getDrawable(R.drawable.ic_star_border_24dp)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(ITEM.itemArg, itemData)
        setResult(ITEM.requestCode, intent)
        super.onBackPressed()
    }
}
