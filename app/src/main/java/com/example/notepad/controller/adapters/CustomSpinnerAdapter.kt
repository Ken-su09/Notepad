package com.example.notepad.controller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.R
import com.example.notepad.model.App
import com.example.notepad.model.Category

class CustomSpinnerAdapter(
    val context: Context,
    private val arrayOfCategory: MutableList<Category>
) :
    BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val itemHolder: ItemHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.custom_spinner, parent, false)
            itemHolder = ItemHolder(view)
            view?.tag = itemHolder
        } else {
            view = convertView
            itemHolder = view.tag as ItemHolder
        }

        val category = arrayOfCategory[position]
        itemHolder.spinnerItemText.text = category.title

        if (category.title == "NOUVEAU" || category.title == "NEW") {
            itemHolder.spinnerItemText.setTextColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            itemHolder.spinnerItemImage.setImageResource(category.color.toInt())
        }

        return view
    }

    override fun getCount(): Int {
        return arrayOfCategory.size
    }

    override fun getItem(position: Int): Any {
        return arrayOfCategory[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ItemHolder(view: View?) {
        val spinnerItemText: TextView =
            view?.findViewById(R.id.custom_spinner_category_text) as TextView

        val spinnerItemImage: AppCompatImageView =
            view?.findViewById(R.id.custom_spinner_category_image) as AppCompatImageView
    }
}