package com.albertjk.chatapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.albertjk.chatapp.databinding.FragmentNewMessageBinding
import com.google.firebase.auth.FirebaseAuth


class NewMessageFragment : Fragment() {

    private val TAG = this::class.qualifiedName

    private lateinit var navController: NavController

    // The shared instance of the FirebaseAuth object.
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentNewMessageBinding? = null
    private val binding get () = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Select User"

        val usernameList: List<String> = listOf("John", "Jen", "Jack")

        initUsersRecyclerView(usernameList)



    }

    private fun initUsersRecyclerView(usernameList: List<String>) {
        val usersRecyclerView: RecyclerView = binding.newMessageRecyclerView.findViewById(R.id.newMessageRecyclerView)
        val usersAdapter = activity?.let { UsersAdapter(usernameList) }
        usersRecyclerView.layoutManager = LinearLayoutManager(activity)
        usersRecyclerView.adapter = usersAdapter
    }
}