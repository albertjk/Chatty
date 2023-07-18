package com.albertjk.chatapp

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
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.albertjk.chatapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class RegisterFragment : Fragment(), View.OnClickListener {

    private val TAG = this::class.qualifiedName

    private lateinit var navController: NavController

    // The shared instance of the FirebaseAuth object.
    private lateinit var auth: FirebaseAuth

    private var selectedPhotoUri: Uri? = null

    private lateinit var storage: FirebaseStorage

    private lateinit var database: FirebaseDatabase

    private var _binding: FragmentRegisterBinding? = null
    private val binding get () = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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

        // Check if user is signed in (non-null).
        //val currentUser = auth.currentUser
        //if(currentUser != null) {
        //    reload()
        //}

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

        /* If the user did not add a photo or fill out a field, tell them.
        Otherwise, create a new user account. */
        var warningMessage = ""
        if (binding.photoButton.alpha != 0f) {
            warningMessage += "Please add a photo. "
        }
        if (username.isEmpty()) {
            warningMessage += "Please enter a valid username. "
        }
        if (email.isEmpty()) {
            warningMessage += "Please enter a valid email. "
        }
        if (password.isEmpty()) {
            warningMessage += "Please enter a valid password. "
        }

        if (binding.photoButton.alpha != 0f || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            if (warningMessage.length > 10) {
                warningMessage = "Please enter valid input."
            }
            Toast.makeText(this.context, warningMessage, Toast.LENGTH_LONG).show()
            return
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    // Registration successful.
                    if (it.isSuccessful) {
                        Log.d(TAG, "Successfully created user with uid: ${it.result!!.user!!.uid}")
                        Toast.makeText(this.context, "Account created.", Toast.LENGTH_SHORT).show()

                        uploadImageToFirebaseStorage()

                        // Redirect the user to the LatestMessagesFragment.
//                        navController.navigate(R.id.action_registerFragment_to_latestMessagesFragment)

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

//                .addOnFailureListener {
//                    Log.d(TAG, "Failed to create user: ${it.message}")
//                    Toast.makeText(this.context, "Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
//                }
        }
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

                // val user = auth.currentUser




            }
            .addOnFailureListener {
                Log.e(TAG, "Error. $it")
            }
    }
}