package com.example.notepad.controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.model.Note
import com.example.notepad.R
import java.util.*

class RecyclerViewNoteAdapter(
    private var listOfNotes: MutableList<Note>,
    private val itemClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerViewNoteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfNotes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = listOfNotes[position].title
        holder.content.text = listOfNotes[position].content

        if (listOfNotes[position].date.isEmpty()) {
            holder.date.text = Calendar.getInstance().time.toString()
        } else {
            holder.date.text = listOfNotes[position].date
        }

        if (listOfNotes[position].isFavorite == 1) {
            holder.isFavorite.visibility = View.VISIBLE
        } else {
            holder.isFavorite.visibility = View.INVISIBLE
        }

        holder.cardView.tag = position
        holder.cardView.setOnClickListener(itemClickListener)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_note_title)
        val content: TextView = view.findViewById(R.id.item_note_content)
        val date: TextView = view.findViewById(R.id.item_note_date)
        val cardView: CardView = view.findViewById(R.id.item_note_cardview)
        val isFavorite: AppCompatImageView = view.findViewById(R.id.item_note_is_favorite)
    }
}