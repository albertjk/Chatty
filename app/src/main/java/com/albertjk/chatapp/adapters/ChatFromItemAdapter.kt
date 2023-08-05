package com.albertjk.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.albertjk.chatapp.databinding.ChatFromRowBinding

class ChatFromItemAdapter(private val messages: List<String>, private val fromUserProfileImageUrl: String):
    RecyclerView.Adapter<ChatFromItemAdapter.ChatFromItemViewHolder>() {

    private val TAG = this::class.qualifiedName

    inner class ChatFromItemViewHolder(val binding: ChatFromRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatFromItemViewHolder {
        val binding = ChatFromRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatFromItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatFromItemViewHolder, position: Int) {
        with(messages[position]) {
            com.squareup.picasso.Picasso.get().load(fromUserProfileImageUrl).into(holder.binding.photoImageViewUserFromRow)
            holder.binding.messageTextViewFromRow.text = messages[position]
        }
    }
        /**
     * Returns the number of recycler view rows to display.
     */
    override fun getItemCount(): Int = messages.size

    fun getMessages(): List<String> = messages
}