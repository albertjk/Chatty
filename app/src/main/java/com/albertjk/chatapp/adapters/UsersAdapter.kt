package com.albertjk.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.albertjk.chatapp.R
import com.albertjk.chatapp.User
import com.albertjk.chatapp.databinding.UserRowBinding
import com.squareup.picasso.Picasso

class UsersAdapter(private val userList: MutableList<User>, private val navController: NavController):
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    private val TAG = this::class.qualifiedName

    inner class UsersViewHolder(val binding: UserRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersViewHolder {
        val binding = UserRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        with(holder) {
            with(userList[position]) {
                val user = userList[position]

                Picasso.get().load(user.profileImageUrl).into(binding.photoImageViewUser)
                binding.usernameTextView.text = user.username

                holder.itemView.setOnClickListener {
                    val bundle = bundleOf("user" to user)
                    navController.navigate(
                        R.id.action_newMessageFragment_to_chatLogFragment,
                        bundle
                    )
                }
            }
        }


    }

    /**
     * Returns the number of recycler view rows to display.
     */
    override fun getItemCount(): Int = userList.size
}