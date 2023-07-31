package com.albertjk.chatapp

import android.os.Bundle
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


class ChatLogFragment : Fragment() {
    private val TAG = this::class.qualifiedName

    private lateinit var navController: NavController

    private var _binding: FragmentChatLogBinding? = null
    private val binding get () = _binding!!

    private lateinit var chatFromItemAdapter: ChatFromItemAdapter
    private lateinit var chatToItemAdapter: ChatToItemAdapter

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

        val messages: List<String> = listOf("Hi", "How are you?", "No", "What?", "Okay")

        setUpMessages()

        initChatLogRecyclerView()
    }

    private fun setUpMessages() {
        val fromMessages = listOf("From Message...")
        val toMessages = listOf("This is the to row text message that is longer.")

        chatFromItemAdapter = ChatFromItemAdapter(fromMessages)
        chatToItemAdapter = ChatToItemAdapter(toMessages)
    }

    private fun initChatLogRecyclerView() {
        chatLogRecyclerView = binding.chatLogRecyclerView.findViewById(R.id.chatLogRecyclerView)
        chatLogRecyclerView.layoutManager = LinearLayoutManager(activity)
        chatLogRecyclerView.adapter = ConcatAdapter(chatFromItemAdapter, chatToItemAdapter)
    }
}