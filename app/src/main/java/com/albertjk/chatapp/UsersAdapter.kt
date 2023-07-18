package com.albertjk.chatapp

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.albertjk.chatapp.databinding.FragmentNewMessageBinding
import com.albertjk.chatapp.databinding.UserRowBinding
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter(private val userList: List<String>):
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    private val TAG = this::class.qualifiedName

//    private val usernames: List<String> = listOf("John")

    private var _binding: FragmentNewMessageBinding? = null
    private val binding get () = _binding!!

    inner class UsersViewHolder(val binding: UserRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersAdapter.UsersViewHolder {
        val binding = UserRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersAdapter.UsersViewHolder, position: Int) {
        with(holder) {
            with(userList[position]) {
                binding.usernameTextView.text = userList[position]
            }
        }
    }

    /**
     * Returns the number of recycler view rows to display.
     */
    override fun getItemCount(): Int = userList.size

}