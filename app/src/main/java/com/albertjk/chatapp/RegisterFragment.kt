package com.albertjk.chatapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(), View.OnClickListener {

    private val TAG = RegisterFragment::class.qualifiedName

    private lateinit var navController: NavController

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

        registerButton.setOnClickListener(this)
        alreadyHaveAnAccountTextView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            registerButton -> {
                val username = usernameTextInputLayout_register.editText?.text.toString()
                val email = emailTextInputLayout_register.editText?.text.toString()
                val password = passwordTextInputLayout_register.editText?.text.toString()

                Log.d(TAG, "username is $username")
                Log.d(TAG, "email is $email")
                Log.d(TAG, "password is $password")

                // TODO: Firebase Authentication to create a user with email and password

            }

            alreadyHaveAnAccountTextView -> navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

}