package com.albertjk.chatapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.albertjk.chatapp.databinding.FragmentLatestMessagesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LatestMessagesFragment : Fragment() {

    private val TAG = this::class.qualifiedName

    private lateinit var navController: NavController

    // The shared instance of the FirebaseAuth object.
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentLatestMessagesBinding? = null
    private val binding get () = _binding!!

    companion object {
        var signedInUser: User? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        _binding = FragmentLatestMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        auth = Firebase.auth

        checkUserIsLoggedIn()

        getSignedInUser()
    }

    private fun getSignedInUser() {
        val ref = FirebaseDatabase.getInstance().getReference("/users/${auth.uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                signedInUser = snapshot.getValue(User::class.java)
                if (signedInUser == null || signedInUser!!.isNullOrEmpty()) {
                    throw NullPointerException("Recipient user data is missing.")
                }
                Log.d(TAG, "Currently signed in user: ${signedInUser!!.username}")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Getting the currently signed in user is cancelled. Error: $error")
            }
        })
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
        when(item.itemId) {
            R.id.menu_new_message -> {
                navController.navigate(R.id.action_latestMessagesFragment_to_newMessageFragment)
            }
            R.id.menu_log_out -> {

                // Use Firebase Auth to sign the user out.
                auth.signOut()

                // Launch Register fragment.
                navController.navigate(R.id.action_latestMessagesFragment_to_registerFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}