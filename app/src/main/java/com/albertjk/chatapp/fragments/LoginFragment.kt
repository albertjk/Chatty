package com.albertjk.chatapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.albertjk.chatapp.R
import com.albertjk.chatapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment(), View.OnClickListener {
    private lateinit var navController: NavController

    // The shared instance of the FirebaseAuth object.
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentLoginBinding? = null

    companion object {
        private val TAG = this::class.qualifiedName
    }
    private val binding get () = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        // Initialise Firebase Auth
        auth = Firebase.auth

        binding.loginButton.setOnClickListener(this)
        binding.backToRegistrationTextView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.loginButton -> {
                login()
            }

            binding.backToRegistrationTextView -> navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    /**
     * Get the user's input and log them in.
     */
    private fun login() {
        val email = binding.emailTextInputLayoutLogin.editText?.text.toString().trim()
        val password = binding.passwordTextInputLayoutLogin.editText?.text.toString().trim()

        Log.d(TAG, "Attempted login")
        Log.d(TAG, "email is $email")
        Log.d(TAG, "password is $password")

        /* If the user did not fill out a field, tell them.
        Otherwise, try to log them in. */
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this.context, "Please enter your login credentials.", Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    // Log in successful.
                    if (it.isSuccessful) {
                        Log.d(TAG, "signInWithEmailAndPassword: success")
                        Log.d(TAG, "Successfully logged in user with uid: ${it.result!!.user!!.uid}")

                        // Redirect user to the Latest Messages fragment.
                        navController.navigate(R.id.action_loginFragment_to_latestMessagesFragment)

                        //val user = auth.currentUser

                    }
                    /* If it fails, display a message to the user.
                    Input validation errors are displayed here by Firebase Auth. */
                    else {
                        Log.w(TAG, "signInWithEmailAndPassword: failure", it.exception)
                        Log.d(TAG, "Exception message: " + it.exception!!.message)
                        Toast.makeText(this.context, it.exception!!.message, Toast.LENGTH_LONG).show()
                    }
                }
//                .addOnFailureListener {
//
//                }
        }
    }

}