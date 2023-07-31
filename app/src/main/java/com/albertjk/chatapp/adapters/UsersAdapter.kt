package com.albertjk.chatapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.albertjk.chatapp.databinding.UserRowBinding
import com.squareup.picasso.Picasso

class UsersAdapter(private val userList: MutableList<User>, private val navController: NavController):
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    private val TAG = this::class.qualifiedName

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
                Picasso.get().load(userList[position].profileImageUrl).into(binding.photoImageViewUser)
                binding.usernameTextView.text = userList[position].username
            }
        }

        holder.itemView.setOnClickListener {
            navController.navigate(R.id.action_newMessageFragment_to_chatLogFragment)
        }
    }

    /**
     * Returns the number of recycler view rows to display.
     */
    override fun getItemCount(): Int = userList.size

}