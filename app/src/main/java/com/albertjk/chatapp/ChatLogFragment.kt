package com.albertjk.chatapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private val TAG = this::class.qualifiedName

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private var _binding: FragmentChatLogBinding? = null
    private val binding get () = _binding!!

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var chatLogRecyclerView: RecyclerView

    private var fromUserProfileImageUrl: String = ""
    private lateinit var toUserProfileImageUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        auth = FirebaseAuth.getInstance()

        database = Firebase.database

        val recipientUser = arguments?.getParcelable<User>("user")

        if (recipientUser == null || recipientUser.isNullOrEmpty()) {
            throw NullPointerException("Recipient user data is missing.")
        }

        toUserProfileImageUrl = recipientUser.profileImageUrl
        Log.d(TAG, "toUserProfileImageUrl: $toUserProfileImageUrl")
        getFromUser()

        (activity as AppCompatActivity?)!!.supportActionBar?.title = recipientUser.username

        val toUserId = recipientUser.uid

        // The signed in user's UID.
        val fromUserId = auth.uid
            ?: throw NullPointerException("fromUserId cannot be null")

        listenForMessages(fromUserId, recipientUser)

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

                // The signed in user's UID.
                val signedInUsersId = FirebaseAuth.getInstance().uid
                    ?: throw NullPointerException("fromUserId cannot be null")

                // Decide which adapter to use based on the signed in user's ID.
                // Get all existing messages and add the new one to it.
                if (signedInUsersId == chatMessage.fromUserId) {
                    val signedInUser = LatestMessagesFragment.signedInUser
                    adapter.add(FromChatItem(signedInUser!!, chatMessage.text))
                } else {
                    adapter.add(ToChatItem(toUser, chatMessage.text))
                }
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

        val chatMessage = ChatMessage(messageId, message, fromUserId, toUserId, currentTimeInSeconds)

        ref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved chat message: ${ref.key}")
                binding.enterMessageEditText.text.clear()
                chatLogRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }

        // Send the message to the fromUser's chat history as well so they also have a copy of the message.
        database.getReference("/user-messages/$toUserId/$fromUserId").push().setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved chat message copy: ${ref.key}")
            }
    }

    private fun initChatLogRecyclerView() {
        chatLogRecyclerView = binding.chatLogRecyclerView.findViewById(R.id.chatLogRecyclerView)
        chatLogRecyclerView.layoutManager = LinearLayoutManager(activity)
        chatLogRecyclerView.adapter = adapter
    }
}