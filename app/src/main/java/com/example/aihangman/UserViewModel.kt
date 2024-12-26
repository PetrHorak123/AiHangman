package com.example.aihangman

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(context: Context, name: String) {
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val user = User(name = name, deviceId = deviceId)

        viewModelScope.launch(Dispatchers.IO) {
            db.collection("users").document(deviceId).set(user)
        }
    }

    fun getUserByDeviceId(deviceId: String, callback: (User?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val docRef = db.collection("users").document(deviceId)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }.addOnFailureListener {
                callback(null)
            }
        }
    }

    // Function to get the top 10 users by given field
    fun getTopUsers(field: String, callback: (List<User>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("users")
                .orderBy(field)
                .limit(10)
                .get()
                .addOnSuccessListener { result ->
                    val users = result.toObjects(User::class.java)
                    callback(users)
                }
        }
    }
}