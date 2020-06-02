package com.rajat.objectlistdetailsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajat.objectlistdetailsapp.model.ItemModel
import com.rajat.objectlistdetailsapp.roomdb.AppDataBaseHelper
import kotlinx.android.synthetic.main.fragment_item_list.*
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

internal class MainActivity : AppCompatActivity(), OnListFragmentInteractionListener {

    private var selectedPosition: Int = 0
    lateinit var context: Context
    private var itemList: List<ItemModel>? = null
    private var currentItemList: List<ItemModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_item_list)
        context = this
        performCreate()
    }

    private fun performCreate() {
        Timber.plant(Timber.DebugTree())
//        val deleteCount = AppDataBaseHelper.getInstance(this).itemDao().deleteAll()
//        Timber.d("deleteCount : $deleteCount")
        val dataAvailable = AppDataBaseHelper.getInstance(this).itemDao().getCount() > 0
        Timber.d("dataAvailable : $dataAvailable")
        if (!dataAvailable) {
            progressBar.visibility = View.VISIBLE
            Thread {
                val itemListFromFile = getItemListFromFile()
                AppDataBaseHelper.getInstance(context).itemDao().insertAll(itemListFromFile)
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    setDataOnUi()
                }
            }.start()
        } else {
            setDataOnUi()
        }
    }

    private fun setDataOnUi() {
        recycler.layoutManager = LinearLayoutManager(this)
        resetItemList()
    }

    private fun resetItemList() {
        itemList = AppDataBaseHelper.getInstance(context).itemDao().getAllData()
        setAdapter(itemList as MutableList<ItemModel>)
    }

    private fun getItemListFromFile(): ArrayList<ItemModel> {
        var reader: BufferedReader? = null
        val itemList: ArrayList<ItemModel> = java.util.ArrayList<ItemModel>()
        Timber.d("getListFromFile called")
        try {
            reader = BufferedReader(
                InputStreamReader(
                    context.assets.open("item_list.txt")
                )
            )
            var mLine: String
            var fileData = ""
            //            reader.readLine();
            while (reader.readLine().also { if (it != null) mLine = it else mLine = "" } != null) {
                Timber.d("$mLine")
                try {
//                    fileData = """
//                            $fileData$mLine
//
//                            """.trimIndent()
                    fileData += mLine
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val lines = fileData.split("line_end").toTypedArray()
            var count = 0
            for (l in lines) {
                try {
                    val lineData =
                        l.split("column_separator").toTypedArray()
                    val itemModel = ItemModel(
                        "" + (++count),
                        lineData[0].replace("\n".toRegex(), ""),
                        lineData[1]
                    )
                    itemList.add(itemModel)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return itemList
    }

    private fun setAdapter(itemListForAdapter: List<ItemModel>) {
        currentItemList = itemListForAdapter
        recycler.adapter = MyItemRecyclerViewAdapter(itemListForAdapter, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_favorite -> {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
//                    currentItemList = itemList?.filter { it.isFavorite }
                    setFavoriteItemList()
                    item.icon = null
                } else {
//                    itemList?.let { setAdapter(it) }
                    resetItemList()
                    item.icon = context?.let {
                        AppCompatResources.getDrawable(
                            it,
                            R.drawable.ic_star_24dp
                        )
                    }
                }
            }

            else -> {
            }
        }
        return true
    }

    private fun setFavoriteItemList() {
        currentItemList = AppDataBaseHelper.getInstance(context).itemDao().getFavoriteData()
        currentItemList?.let { setAdapter(it) }
    }

    override fun onResume() {
        recycler.adapter?.notifyDataSetChanged()
        super.onResume()
    }

    override fun onListFragmentInteraction(item: ItemModel?, position: Int) {
        Timber.d("item %s", item.toString())
        selectedPosition = position
        val intent = Intent(this, ItemDetailActivity::class.java)
        intent.putExtra(ItemDetailActivity.ITEM.itemArg, item)
        startActivityForResult(intent, ItemDetailActivity.ITEM.requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        ItemDetailActivity.ITEM.itemArg
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ItemDetailActivity.ITEM.requestCode) {
            if (itemList?.size ?: 0 > currentItemList?.size ?: 0) {
                setFavoriteItemList()
            } else {
                if (data != null) {
                    val itemModel =
                        data.getSerializableExtra(ItemDetailActivity.ITEM.itemArg) as ItemModel
                    myItem(itemModel) {
                        Timber.d("$name $details")
                    }
                    (currentItemList as ArrayList<ItemModel>)[selectedPosition] = itemModel
                    recycler.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private inline fun myItem(name: ItemModel, block: ItemModel.() -> Unit) {
        name.block()
    }
}

interface OnListFragmentInteractionListener {
    fun onListFragmentInteraction(item: ItemModel?, position: Int)
}
