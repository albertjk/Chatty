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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.albertjk.chatapp.adapters.ChatFromItemAdapter
import com.albertjk.chatapp.adapters.ChatToItemAdapter
import com.albertjk.chatapp.databinding.FragmentChatLogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChatLogFragment : Fragment() {
    private val TAG = this::class.qualifiedName

    private lateinit var navController: NavController

    private var _binding: FragmentChatLogBinding? = null
    private val binding get () = _binding!!

    private lateinit var chatFromItemAdapter: ChatFromItemAdapter
    private lateinit var chatToItemAdapter: ChatToItemAdapter

    // The Concat Adapter shows the two adapter's contents in sequence on the screen.
    private lateinit var concatAdapter: ConcatAdapter

    private lateinit var chatLogRecyclerView: RecyclerView

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

        val recipientUser = arguments?.getParcelable<User>("user")

        if (recipientUser?.username == null) {
            throw NullPointerException("Username missing.")
        }

        (activity as AppCompatActivity?)!!.supportActionBar?.title = recipientUser.username

        listenForMessages()

        val recipientUserId = recipientUser.uid

        binding.sendButton.setOnClickListener {
            Log.d(TAG, "Send message...")
            sendMessage(recipientUserId)
        }

        initChatLogRecyclerView()
    }

    private fun sendMessage(toUserId: String) {
        // push generates a new node to start saving data in messages.
        val ref = Firebase.database.getReference("/messages").push()

        val message = binding.enterMessageEditText.text.toString()

        val messageId = ref.key.toString()

        // The signed in user's UID.
        val fromUserId = FirebaseAuth.getInstance().uid
            ?: throw NullPointerException("fromUserId cannot be null")

        val currentTimeInSeconds: Long = System.currentTimeMillis() / 1000

        val chatMessage = ChatMessage(messageId, message, fromUserId, toUserId, currentTimeInSeconds)

        ref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved chat message: ${ref.key}")
            }
    }

    private fun listenForMessages() {
        val ref = Firebase.database.getReference("/messages")

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
                    val existingMessages: MutableList<String> = chatFromItemAdapter.getMessages().toMutableList()
                    existingMessages.add(chatMessage.text)
                    chatFromItemAdapter = ChatFromItemAdapter(existingMessages)
                } else {
                    val existingMessages: MutableList<String> = chatToItemAdapter.getMessages().toMutableList()
                    existingMessages.add(chatMessage.text)
                    chatToItemAdapter = ChatToItemAdapter(existingMessages)
                }

                setConcatAdapter()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initChatLogRecyclerView() {
        chatLogRecyclerView = binding.chatLogRecyclerView.findViewById(R.id.chatLogRecyclerView)
        chatLogRecyclerView.layoutManager = LinearLayoutManager(activity)
        chatFromItemAdapter = ChatFromItemAdapter(mutableListOf())
        chatToItemAdapter = ChatToItemAdapter(mutableListOf())
        setConcatAdapter()
    }

    private fun setConcatAdapter() {
        concatAdapter = ConcatAdapter(chatFromItemAdapter, chatToItemAdapter)
        chatLogRecyclerView.adapter = concatAdapter
    }
}