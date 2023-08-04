package com.albertjk.chatapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(val uid: String, var username: String, val profileImageUrl: String, val email: String):
    Parcelable {
    constructor(): this("", "", "", "")

    fun isNotNullOrEmpty(): Boolean {
        return !(this.uid.isNullOrEmpty() || this.username.isNullOrEmpty() || this.profileImageUrl.isNullOrEmpty() || this.email.isNullOrEmpty())
    }
}