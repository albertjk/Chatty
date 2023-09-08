package com.albertjk.chatapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.albertjk.chatapp.models.ChatMessage
import com.albertjk.chatapp.view_rows.FromChatItem
import com.albertjk.chatapp.R
import com.albertjk.chatapp.view_rows.ToChatItem
import com.albertjk.chatapp.models.User
import com.albertjk.chatapp.fragments.LatestMessagesFragment.Companion.signedInUser
import com.albertjk.chatapp.databinding.FragmentChatLogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class ChatLogFragment : Fragment() {
    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private var _binding: FragmentChatLogBinding? = null
    private val binding get () = _binding!!

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var chatLogRecyclerView: RecyclerView

    private var fromUserProfileImageUrl: String = ""
    private lateinit var toUserProfileImageUrl: String

    private var recipientUser: User? = null

    companion object {
        private val TAG = this::class.qualifiedName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatLogBinding.inflate(inflater, container, false)

        recipientUser = arguments?.getParcelable("user")

        if (recipientUser == null || recipientUser!!.isNullOrEmpty()) {
            throw NullPointerException("Recipient user data is missing.")
        }

        toUserProfileImageUrl = recipientUser!!.profileImageUrl
        Log.d(TAG, "toUserProfileImageUrl: $toUserProfileImageUrl")

        (activity as AppCompatActivity?)!!.supportActionBar?.title = recipientUser!!.username

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toUserId = recipientUser!!.uid

        navController = Navigation.findNavController(view)

        auth = FirebaseAuth.getInstance()

        database = Firebase.database

        getFromUser()

        // The signed in user's UID.
        val fromUserId = auth.uid
            ?: throw NullPointerException("fromUserId cannot be null")

        listenForMessages(fromUserId, recipientUser!!)

        binding.sendButton.setOnClickListener {
            Log.d(TAG, "Send message...")
            sendMessage(fromUserId, toUserId)
        }

        initChatLogRecyclerView()
    }

    private fun getFromUser() {
        val ref = database.getReference("/users/${auth.uid}").child("profileImageUrl")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fromUserProfileImageUrl = snapshot.getValue(String::class.java).toString()

                Log.d(TAG, "fromUserProfileImageUrl: $fromUserProfileImageUrl")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Getting user profile image from database is cancelled. Error: $error")
            }
        })
    }

    private fun listenForMessages(fromUserId: String, toUser: User) {
        val ref = database.getReference("/user-messages/$fromUserId/${toUser.uid}")

        /* Notifies of every piece of data that belongs to the 'messages' node.
        This allows for listening for new messages as they are coming in real time. */
        ref.addChildEventListener(object : ChildEventListener {

            // Adds a new message to the current node of 'messages'.
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                    ?: throw NullPointerException("Chat message cannot be null.")

                Log.d(TAG, "Chat message: ${chatMessage.text}")

                val signedInUsersId = signedInUser?.uid
                    ?: throw NullPointerException("fromUserId cannot be null")

                // Decide which adapter to use based on the signed in user's ID.
                // Get all existing messages and add the new one to it.
                if (signedInUsersId == chatMessage.fromUserId) {
                    val signedInUser = LatestMessagesFragment.signedInUser
                    adapter.add(FromChatItem(signedInUser!!, chatMessage.text))
                } else {
                    adapter.add(ToChatItem(toUser, chatMessage.text))
                }

                // Scroll to the bottom of the recycler view.
                chatLogRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Adding a new message to the database is cancelled. Error: $error")
            }
        })
    }

    private fun sendMessage(fromUserId: String, toUserId: String) {
        val message = binding.enterMessageEditText.text.toString()

        // push generates a new node to start saving data.
        val ref = database.getReference("/user-messages/$fromUserId/$toUserId").push()

        val messageId = ref.key.toString()

        val currentTimeInSeconds: Long = System.currentTimeMillis() / 1000

        val chatMessage =
            ChatMessage(messageId, message, fromUserId, toUserId, currentTimeInSeconds)

        ref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved chat message: ${ref.key}")
                binding.enterMessageEditText.text.clear()

                // After sending the message, scroll to the bottom of the screen.
                chatLogRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }

        // Send the message to the fromUser's chat history as well so they also have a copy of the message.
        database.getReference("/user-messages/$toUserId/$fromUserId").push().setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved chat message copy: ${ref.key}")
            }

        // Allows to keep track of the latest message between the currently logged in user and another user.
        // Not using push() here as the aim is to overwrite the latest message saved in the database.
        database.getReference("/latest-messages/$fromUserId/$toUserId").setValue(chatMessage)

        // Store the counterpart message as the latest message received by the recipient user.
        database.getReference("/latest-messages/$toUserId/$fromUserId").setValue(chatMessage)
    }

    private fun initChatLogRecyclerView() {
        chatLogRecyclerView = binding.chatLogRecyclerView.findViewById(R.id.chatLogRecyclerView)
        chatLogRecyclerView.adapter = adapter
    }
}