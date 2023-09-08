package com.albertjk.chatapp

import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import com.albertjk.chatapp.databinding.LatestMessageRowBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.viewbinding.BindableItem

class LatestMessageRow(private val chatMessage: ChatMessage) : BindableItem<LatestMessageRowBinding>() {

    var chatPartner: User? = null

    companion object {
        private val TAG = this::class.qualifiedName
    }

    override fun getLayout() = R.layout.latest_message_row

    override fun bind(viewBinding: LatestMessageRowBinding, position: Int) {

        // Find out the ID of the user who sent the message.
        // Handle if the message was sent by the currently logged in user to themselves.
        val chatPartnerId: String = if (chatMessage.fromUserId == Firebase.auth.uid) {
            chatMessage.toUserId
        }
        // Or someone else.
        else {
            chatMessage.fromUserId
        }

        // Get the chat partner user from the database.
        val ref = Firebase.database.getReference("users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartner = snapshot.getValue(User::class.java)
                if (chatPartner == null || chatPartner!!.isNullOrEmpty()) {
                    throw NullPointerException("Signed in user's data is missing.")
                }
                Log.d(TAG, "Chat partner: $chatPartner")

                // Display the chat partner user's data.
                viewBinding.apply {
                    usernameTextView.text = chatPartner!!.username
                    com.squareup.picasso.Picasso.get().load(chatPartner!!.profileImageUrl).into(photoImageViewLatestMessageRow)
                    latestMessageTextView.text = chatMessage.text
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    TAG,
                    "Getting the chat partner user info from the database is cancelled. Error: $error"
                )
            }
        })
    }

    override fun initializeViewBinding(view: View) = LatestMessageRowBinding.bind(view)
}