package com.albertjk.chatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
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


}