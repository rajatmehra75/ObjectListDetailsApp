package com.rajat.objectlistdetailsapp


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rajat.objectlistdetailsapp.model.ItemModel
import com.rajat.objectlistdetailsapp.roomdb.AppDataBaseHelper
import kotlinx.android.synthetic.main.fragment_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [ItemModel] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyItemRecyclerViewAdapter(
    private val mValues: List<ItemModel>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private lateinit var mContext: Context

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ItemModel
            mListener?.onListFragmentInteraction(item, mValues.indexOf(item))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        mContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.name
        if (item.isFavorite) {
            holder.mFavoriteView.setImageResource(R.drawable.ic_star_24dp)
        } else {
            holder.mFavoriteView.setImageResource(R.drawable.ic_star_border_24dp)
        }

        holder.mFavoriteView.setOnClickListener {
            item.isFavorite = !item.isFavorite
            AppDataBaseHelper.getInstance(mContext).itemDao().updateItem(item)
            notifyDataSetChanged()
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_name
        val mFavoriteView: ImageView = mView.favorite
    }
}
