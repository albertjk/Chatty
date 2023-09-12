package com.albertjk.chatapp.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.albertjk.chatapp.databinding.FragmentRegisterBinding
import com.albertjk.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

const val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$"

class RegisterFragment : Fragment(), View.OnClickListener {
    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    private var selectedPhotoUri: Uri? = null

    private lateinit var storage: FirebaseStorage

    private lateinit var database: FirebaseDatabase

    private var _binding: FragmentRegisterBinding? = null

    companion object {
        private val TAG = this::class.qualifiedName
    }

    private val binding get () = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        // Initialise Firebase Auth.
        auth = Firebase.auth

        // Initialise Firebase Storage.
        storage = Firebase.storage

        // Initialise the Firebase Realtime Database.
        database = Firebase.database

        binding.photoButton.setOnClickListener(this)
        binding.registerButton.setOnClickListener(this)
        binding.alreadyHaveAnAccountTextView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.photoButton -> {
                Log.d(TAG, "Show photo selector")

                // Start photo selector intent.
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
            binding.registerButton -> {
                register()
            }
            binding.alreadyHaveAnAccountTextView -> navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    /**
     * This method is called when the photo selector intent is finished.
     * The activity's result is processed here.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")

            // Get the selected photo.
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedPhotoUri)

            // Show the photo on the circular image view.
            binding.photoImageViewRegister.setImageBitmap(bitmap)

            // Make the circular photo button transparent. The user can tap it to choose another image.
            binding.photoButton.alpha = 0f
        }
    }

    /**
     * Gets the user's input and creates a new account for them.
     */
    private fun register() {
        val username = binding.usernameTextInputLayoutRegister.editText?.text.toString().trim()
        val email = binding.emailTextInputLayoutRegister.editText?.text.toString().trim()
        val password = binding.passwordTextInputLayoutRegister.editText?.text.toString().trim()

        Log.d(TAG, "Attempted registration.")
        Log.d(TAG, "username is $username")
        Log.d(TAG, "email is $email")
        Log.d(TAG, "password is $password")

        val photoAlpha = binding.photoButton.alpha

        val (validInput, errorMessage) = validateInput(photoAlpha, username, email, password)

        if (!validInput) {
            Toast.makeText(this.context,errorMessage, Toast.LENGTH_LONG).show()
        } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {

                        // Registration successful.
                        if (it.isSuccessful) {
                            Log.d(TAG, "Successfully created user with uid: ${it.result!!.user!!.uid}")
                            Toast.makeText(this.context, "Account created.", Toast.LENGTH_SHORT).show()

                            uploadImageToFirebaseStorage()
                        }
                        /* If it fails, display a message to the user.
                        Input validation errors are displayed here by Firebase Auth.
                        If the e-mail is already in use, a message is displayed here by Firebase Auth. */
                        else {
                            Log.w(TAG, "createUserWithEmail: failure", it.exception)
                            Log.d(TAG, it.exception!!.message.toString())
                            Toast.makeText(this.context, it.exception!!.message, Toast.LENGTH_LONG).show()
                            return@addOnCompleteListener
                        }
                    }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to create user: ${it.message}")
                    Toast.makeText(this.context, "Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    fun validateInput(photoAlpha: Float, username: String, email: String, password: String): Pair<Boolean, String> {
        val warningMessage = mutableListOf<String>()
        if (photoAlpha != 0f) {
            warningMessage.add("Please add a photo.")
        }
        if (username.isEmpty()) {
            warningMessage.add("Please enter a valid username.")
        }
        if (email.isEmpty()) {
            warningMessage.add("Please enter a valid email.")
        }
        if (password.isEmpty()) {
            warningMessage.add("Please enter a valid password.")
        }

        return if (warningMessage.size > 0) {
            if (warningMessage.size == 1) {
                Pair(false, warningMessage[0])
            } else {
                Pair(false, "Please enter valid input.")
            }
        } else {
            val validPassword = isValidPassword(password)
            if (!validPassword) {
                Pair(false, "The password doesn't meet the requirements.")
            } else {
                Pair(true, "")
            }
        }
    }

    /**
     * Validates the password to see if it is strong enough.
     */
    private fun isValidPassword(password: String): Boolean {
        val regex = Regex(PASSWORD_REGEX)
        return regex.matches(password)
    }

    /**
     * Gets the selected photo's Uri and uploads it to Firebase storage.
     */
    private fun uploadImageToFirebaseStorage() {

        if (selectedPhotoUri == null) {
            return
        }

        // Images are saved in the 'images' directory in Firebase Storage.
        val fileName = UUID.randomUUID().toString()
        val imagesRef = storage.reference.child("/images/$fileName")

        imagesRef.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                // Access the image file download URL.
                imagesRef.downloadUrl.addOnSuccessListener { url ->
                    Log.d(TAG, "File download URL: $url")

                    saveUserToFirebaseRealtimeDatabase(url.toString())
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Error. $it")
            }
    }

    /**
     * Creates a new User using the provided data and saves it in the Firebase Realtime Database.
     */
    private fun saveUserToFirebaseRealtimeDatabase(profileImageUrl: String) {
        val ref = database.getReference("/users/${auth.uid}")

        Log.d(TAG, "username: ${binding.usernameTextInputLayoutRegister.editText!!.text}")
        Log.d(TAG, "email: ${binding.emailTextInputLayoutRegister.editText!!.text}")

        // Create a User object and save it in the database.
        val user = User(auth.uid!!, binding.usernameTextInputLayoutRegister.editText!!.text.toString(), profileImageUrl, binding.emailTextInputLayoutRegister.editText!!.text.toString())

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "User saved to Realtime Database.")

                // The user is automatically logged in, so redirect them to the Latest Messages fragment.
                navController.navigate(R.id.action_registerFragment_to_latestMessagesFragment)
            }
            .addOnFailureListener {
                Log.e(TAG, "Error. $it")
            }
    }
}