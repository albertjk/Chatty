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
import com.albertjk.chatapp.R
import com.albertjk.chatapp.models.User
import com.albertjk.chatapp.adapters.UsersAdapter
import com.albertjk.chatapp.databinding.FragmentSelectUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SelectUserFragment : Fragment() {
    private lateinit var navController: NavController

    private lateinit var dbReference: DatabaseReference

    private var _binding: FragmentSelectUserBinding? = null

    private lateinit var usersRecyclerView: RecyclerView

    companion object {
        private val TAG = this::class.qualifiedName
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSelectUserBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Select User"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        initUsersRecyclerView()

        dbReference = Firebase.database.reference

        getUsersFromDatabase(dbReference)
    }

    private fun getUsersFromDatabase(dbReference: DatabaseReference) {
        dbReference.addValueEventListener(object : ValueEventListener {
            // onDataChange is called every time we retrieve all users from the database.
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<User>()

                snapshot.children.forEach {
                    it.children.forEach { child ->
                        Log.d(TAG, child.toString())
                        val user = child.getValue(User::class.java)
                        if (user != null && !user.isNullOrEmpty()) {
                            userList.add(user)
                        }
                    }
                }
                val usersAdapter = activity?.let { UsersAdapter(userList, navController) }
                usersRecyclerView.adapter = usersAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Getting users from database is cancelled. Error: $error")
            }
        })
    }

    private fun initUsersRecyclerView() {
        usersRecyclerView = binding.newMessageRecyclerView.findViewById(R.id.newMessageRecyclerView)
        val usersAdapter = activity?.let { UsersAdapter(mutableListOf(), navController) }
        usersRecyclerView.adapter = usersAdapter
    }
}