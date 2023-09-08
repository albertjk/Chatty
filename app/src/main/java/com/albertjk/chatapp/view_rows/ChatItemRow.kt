package com.albertjk.chatapp.view_rows

import android.view.View
import com.albertjk.chatapp.R
import com.albertjk.chatapp.databinding.ChatFromRowBinding
import com.albertjk.chatapp.databinding.ChatToRowBinding
import com.albertjk.chatapp.models.User
import com.xwray.groupie.viewbinding.BindableItem

class FromChatItem(private val user: User, private val text: String): BindableItem<ChatFromRowBinding>() {

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewBinding: ChatFromRowBinding, position: Int) {
        viewBinding.apply {
            messageTextViewFromRow.text = text
            com.squareup.picasso.Picasso.get().load(user.profileImageUrl).into(photoImageViewUserFromRow)
        }
    }

    override fun initializeViewBinding(view: View): ChatFromRowBinding {
        return ChatFromRowBinding.bind(view)
    }
}

class ToChatItem(private val user: User, private val text: String): BindableItem<ChatToRowBinding>() {

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewBinding: ChatToRowBinding, position: Int) {
        viewBinding.apply {
            messageTextViewToRow.text = text
            com.squareup.picasso.Picasso.get().load(user.profileImageUrl).into(photoImageViewUserToRow)
        }
    }

    override fun initializeViewBinding(view: View): ChatToRowBinding {
        return ChatToRowBinding.bind(view)
    }
}