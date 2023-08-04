package com.albertjk.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.albertjk.chatapp.databinding.ChatToRowBinding

class ChatToItemAdapter(private val messages: List<String>):
    RecyclerView.Adapter<ChatToItemAdapter.ChatToItemViewHolder>() {

    private val TAG = this::class.qualifiedName

    inner class ChatToItemViewHolder(val binding: ChatToRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatToItemViewHolder {
        val binding = ChatToRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatToItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatToItemViewHolder, position: Int) {
        with(messages[position]) {
//                com.squareup.picasso.Picasso.get().load(userList[position].profileImageUrl).into(binding.photoImageViewUser)
            holder.binding.messageTextViewToRow.text = messages[position]
        }
    }
    /**
     * Returns the number of recycler view rows to display.
     */
    override fun getItemCount(): Int = messages.size

    fun getMessages(): List<String> = messages
}