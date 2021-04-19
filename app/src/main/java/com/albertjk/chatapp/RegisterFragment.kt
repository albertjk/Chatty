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
                val username = usernameTextInputLayout_register.editText?.text.toString().trim()
                val email = emailTextInputLayout_register.editText?.text.toString().trim()
                val password = passwordTextInputLayout_register.editText?.text.toString().trim()

                Log.d(TAG, "Attempted registration.")

                Log.d(TAG, "username is $username")
                Log.d(TAG, "email is $email")
                Log.d(TAG, "password is $password")

                // TODO: PERFORM INPUT VALIDATION.



                // Create a new user account with the email and the password.
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        // If it fails, display a message to the user.
                        if (it.isSuccessful) {
                            Log.d(TAG, "Successfully created user with uid: ${it.result!!.user.uid}")
                            Toast.makeText(this.context, "Account created.", Toast.LENGTH_SHORT).show()

                            // val user = auth.currentUser
                        }
                        else {


                            Log.w(TAG, "createUserWithEmail: failure", it.exception)
                            Log.d(TAG, "Exception message:" + it.exception!!.message)
                            Toast.makeText(this.context, "Authentication failed.", Toast.LENGTH_SHORT).show()


                        }
                    }



            }

            alreadyHaveAnAccountTextView -> navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

}