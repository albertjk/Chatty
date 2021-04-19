package com.albertjk.chatapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*

class LoginFragment : Fragment(), View.OnClickListener {

    private val TAG = LoginFragment::class.qualifiedName

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        loginButton.setOnClickListener(this)
        backToRegistrationTextView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            loginButton -> {

                val email = emailTextInputLayout_login.editText?.text.toString()
                val password = passwordTextInputLayout_login.editText?.text.toString()

                Log.d(TAG, "Attempted login")
            }

            backToRegistrationTextView -> navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

}