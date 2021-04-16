package com.albertjk.chatapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class RegisterFragment : Fragment() {

    private val TAG = RegisterFragment::class.qualifiedName

    // private lateinit var navController: navController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // navController = Navigation.findNavController(view)

        registerButton.setOnClickListener {

            val username = usernameTextInputLayout_register.editText?.text.toString()
            val email = emailTextInputLayout_register.editText?.text.toString()
            val password = passwordTextInputLayout_register.editText?.text.toString()

            Log.d(TAG, "username is $username")
            Log.d(TAG, "email is $email")
            Log.d(TAG, "password is $password")
        }
    }

}