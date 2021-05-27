package com.example.notepad

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView

class CustomSpinnerAdapter(context: Context, private val arrayOfString: Array<String>) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val itemHolder: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_note_detail_spinner, parent, false)
            itemHolder = ItemHolder(view)
            view?.tag = itemHolder
        } else {
            view = convertView
            itemHolder = view.tag as ItemHolder
        }

        itemHolder.spinnerItemText.text = arrayOfString[position]

        when (position) {
            0 -> {
                itemHolder.spinnerItemImage.setBackgroundResource(R.drawable.ic_bookmark_blue)
            }
            1 -> {
                itemHolder.spinnerItemImage.setBackgroundResource(R.drawable.ic_bookmark_green)
            }
            2 -> {
                itemHolder.spinnerItemImage.setBackgroundResource(R.drawable.ic_bookmark_orange)
            }
            3 -> {
                itemHolder.spinnerItemImage.setBackgroundResource(R.drawable.ic_bookmark_purple)
            }
            4 -> {
                itemHolder.spinnerItemImage.setBackgroundResource(R.drawable.ic_bookmark_red)
            }
        }

        return view
    }

    override fun getCount(): Int {
        return arrayOfString.size
    }

    override fun getItem(position: Int): Any {
        return arrayOfString[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ItemHolder(view: View?) {
        val spinnerItemText: TextView = view?.findViewById(R.id.item_note_spinner_text) as TextView
        val spinnerItemImage: AppCompatImageView =
            view?.findViewById(R.id.item_note_spinner_image) as AppCompatImageView
    }
}