package com.example.notepad

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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

        holder.cardView.tag = position
        holder.cardView.setOnClickListener(itemClickListener)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_note_title)
        val content: TextView = view.findViewById(R.id.item_note_content)
        val date: TextView = view.findViewById(R.id.item_note_date)
        val cardView: CardView = view.findViewById(R.id.item_note_cardview)
    }
}