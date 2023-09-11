package com.albertjk.chatapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import com.albertjk.chatapp.R
import com.albertjk.chatapp.databinding.FragmentLatestMessagesBinding
import com.albertjk.chatapp.models.ChatMessage
import com.albertjk.chatapp.models.User
import com.albertjk.chatapp.view_rows.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder


class LatestMessagesFragment : Fragment() {

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private var _binding: FragmentLatestMessagesBinding? = null
    private val adapter = GroupAdapter<GroupieViewHolder>()

    private val latestMessagesMap = HashMap<String, ChatMessage>()

    companion object {
        var signedInUser: User? = null
        private val TAG = this::class.qualifiedName
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        _binding = FragmentLatestMessagesBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Latest Messages"
        hideKeyboard(activity as AppCompatActivity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        auth = Firebase.auth

        database = Firebase.database

        binding.latestMessagesRecyclerView.adapter = adapter
        binding.latestMessagesRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        setChatPartnerClickListener()

        checkUserIsLoggedIn()

        getSignedInUser()

        listenForLatestMessages()
    }

    private fun hideKeyboard(activity: Activity) {
        val inputManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) {
            inputManager.hideSoftInputFromWindow(
                activity.currentFocus!!
                    .windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    /**
     * Sets a click listener for the recycler view rows of the latest messages.
     */
    private fun setChatPartnerClickListener() {
        adapter.setOnItemClickListener { item, _ ->
            val row = item as LatestMessageRow


            Log.d(TAG, "Chat partner clicked, opening ChatLogFragment...")
            val bundle = bundleOf("user" to row.chatPartner)
            navController.navigate(
                R.id.action_latestMessagesFragment_to_chatLogFragment,
                bundle
            )
        }
    }

    /**
     * Checks if the user is logged in to the app using Firebase Auth.
     */
    private fun checkUserIsLoggedIn() {

        // If the user is not logged in, redirect them to the Register fragment.
        if (auth.uid == null) {
            navController.navigate(R.id.action_latestMessagesFragment_to_registerFragment)
        }
    }

    private fun getSignedInUser() {
        val ref = database.getReference("/users/${auth.uid}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                signedInUser = snapshot.getValue(User::class.java)
                if (signedInUser == null || signedInUser!!.isNullOrEmpty()) {
                    throw NullPointerException("Signed in user's data is missing.")
                }
                Log.d(TAG, "Currently signed in user: ${signedInUser!!.username}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Getting the currently signed in user is cancelled. Error: $error")
            }
        })
    }

    private fun listenForLatestMessages() {
        val fromUserId = auth.uid

        // Already checked in getSignedInUser() that signedInUser is not null.
        val ref = database.getReference("/latest-messages/$fromUserId")

        ref.addChildEventListener(object : ChildEventListener {

            // Handles when a new latest message is added to the database.
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                    ?: throw NullPointerException("Chat message cannot be null.")

                val recipientUsersId = snapshot.key

                if (!recipientUsersId.isNullOrEmpty()) {
                    // Store the message in latestMessagesMap.
                    // key is the recipient user's ID
                    latestMessagesMap[recipientUsersId] = chatMessage

                    refreshRecyclerView()
                }
            }

            // Handles when a latest message by a user has changed.
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                    ?: throw NullPointerException("Chat message cannot be null.")

                val recipientUsersId = snapshot.key

                if (!recipientUsersId.isNullOrEmpty()) {
                    // Store the message in latestMessagesMap.
                    // key is the recipient user's ID
                    latestMessagesMap[recipientUsersId] = chatMessage

                    refreshRecyclerView()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    TAG,
                    "Getting the latest messages from the database is cancelled. Error: $error"
                )
            }
        })
    }

    private fun refreshRecyclerView() {
        // Clear all old messages and add new messages to the adapter.
        adapter.clear()
        latestMessagesMap.values.forEach { chatMessage ->
            adapter.add(LatestMessageRow(chatMessage))
        }
    }

    /**
     * Creates the top navigation menu.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Runs when a menu item is clicked.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_message -> {
                navController.navigate(R.id.action_latestMessagesFragment_to_newMessageFragment)
            }

            R.id.menu_log_out -> {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setMessage("Are you sure you want to log out?")

                alertDialog.setPositiveButton("Yes"
                ) { dialog, _ ->
                    // Use Firebase Auth to sign the user out.
                    auth.signOut()

                    // Launch Register fragment.
                    navController.navigate(R.id.action_latestMessagesFragment_to_registerFragment)

                    dialog.dismiss()
                }

                alertDialog.setNegativeButton("No"
                ) { dialog, _ ->
                    dialog.dismiss()
                }

                alertDialog.create().show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}