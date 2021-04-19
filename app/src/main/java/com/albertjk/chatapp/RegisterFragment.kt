package com.albertjk.chatapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(), View.OnClickListener {

    private val TAG = this::class.qualifiedName

    private lateinit var navController: NavController

    // The shared instance of the FirebaseAuth object.
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        // Initialise Firebase Auth
        auth = Firebase.auth

        // Check if user is signed in (non-null).
        //val currentUser = auth.currentUser
        //if(currentUser != null) {
        //    reload()
        //}

        registerButton.setOnClickListener(this)
        alreadyHaveAnAccountTextView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            registerButton -> {
                register()
            }

            alreadyHaveAnAccountTextView -> navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    /**
     * Get the user's input and create a new account for them.
     */
    private fun register() {
        val username = usernameTextInputLayout_register.editText?.text.toString().trim()
        val email = emailTextInputLayout_register.editText?.text.toString().trim()
        val password = passwordTextInputLayout_register.editText?.text.toString().trim()

        Log.d(TAG, "Attempted registration.")
        Log.d(TAG, "username is $username")
        Log.d(TAG, "email is $email")
        Log.d(TAG, "password is $password")

        /* If the user did not fill out a field, tell them.
        Otherwise, create a new user account with the email and the password. */
        if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this.context, "Please enter valid input.", Toast.LENGTH_LONG).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    // Registration successful.
                    if (it.isSuccessful) {
                        Log.d(TAG, "Successfully created user with uid: ${it.result!!.user.uid}")
                        Toast.makeText(this.context, "Account created.", Toast.LENGTH_SHORT).show()

                        // TODO:
                        // The user is automatically logged in, so redirect them the Messages fragment.

                        // val user = auth.currentUser
                    }
                    /* If it fails, display a message to the user.
                    Input validation errors are displayed here by Firebase Auth.
                    If the e-mail is already in use, a message is displayed here by Firebase Auth. */
                    else {
                        Log.w(TAG, "createUserWithEmail: failure", it.exception)
                        Log.d(TAG, "Exception message: " + it.exception!!.message)
                        Toast.makeText(this.context, it.exception!!.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}