package com.albertjk.chatapp

import android.os.Bundle
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
import com.google.firebase.ktx.Firebase

class LatestMessagesFragment : Fragment() {

    private val TAG = this::class.qualifiedName

    private lateinit var navController: NavController

    // The shared instance of the FirebaseAuth object.
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentLatestMessagesBinding? = null
    private val binding get () = _binding!!

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

        // Initialise Firebase Auth.
        auth = Firebase.auth

        checkUserIsLoggedIn()
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