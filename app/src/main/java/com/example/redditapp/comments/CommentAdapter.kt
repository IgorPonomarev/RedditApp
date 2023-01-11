package com.example.redditapp.comments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redditapp.R

class CommentAdapter(
    private val context: Context,
    private val dataset: List<Comment>,
    private val onCommentClickListener: OnCommentClickListener
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(
        private val view: View,
        val onCommentClickListener: OnCommentClickListener
    ) : RecyclerView.ViewHolder(view), OnClickListener {
        val commentTextView: TextView = view.findViewById(R.id.comment)
        val commentAuthorTextView: TextView = view.findViewById(R.id.comment_author)
        val commentUpdatedTextView: TextView = view.findViewById(R.id.comment_updated)
        val commentProgressBar: ProgressBar = view.findViewById(R.id.comment_progress_bar)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onCommentClickListener.onCommentClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_layout, parent, false)

        return CommentViewHolder(adapterLayout, onCommentClickListener)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = dataset[position]
        holder.commentTextView.text = item.comment
        holder.commentAuthorTextView.text = item.author
        holder.commentUpdatedTextView.text = item.updatedFormatted
        holder.commentProgressBar.visibility = View.GONE
    }

    override fun getItemCount() = dataset.size

    interface OnCommentClickListener {
        fun onCommentClick(position: Int)
    }

}