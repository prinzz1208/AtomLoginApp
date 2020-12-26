package com.example.atomloginapp.viewmodel

import android.app.Application

import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.atomloginapp.MainApplication
import com.example.atomloginapp.model.UserPreferences
import com.example.atomloginapp.utils.Constants
import com.example.atomloginapp.utils.hasInternetConnection
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception


class LoginViewModel @ViewModelInject constructor(
        application: Application,
        val up: UserPreferences
): AndroidViewModel(application) {

    val isUserLoggedIn = MutableLiveData<Boolean>()
    val uiState = MutableLiveData<String>()

    // HANDLING OF FIREBASE AUTHENTICATION
    fun googleAuthForFirebase(task: Task<GoogleSignInAccount>, firebaseAuth: FirebaseAuth) {
        val context = getApplication<MainApplication>().applicationContext
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            val credentials = GoogleAuthProvider.getCredential(account?.idToken, null)
            if (hasInternetConnection(context)) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        firebaseAuth.signInWithCredential(credentials).await()
                        withContext(Dispatchers.Main) {
                            uiState.value = Constants.SUCCESS
                            up.storeUser(userName = account!!.displayName.toString(), userEmail = account.email.toString().trim(), userLoggedIn = true)
                            isUserLoggedIn.value = true
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            uiState.value = Constants.ERROR
                            Log.d("TAG", "firebaseAuth.signInWithCredential: Error  ${e.message} ")
                            Toast.makeText(context, "Sign-In Failure", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }else{
                uiState.value = Constants.ERROR
                Toast.makeText(context, "No Internet Connection.", Toast.LENGTH_LONG).show()
            }

        } catch (e: ApiException) {
            uiState.value = Constants.ERROR
            Log.d("TAG", "googleAuthForFirebase: Error  ${e.message} ")
            Toast.makeText(context, "Sign-In Failure,", Toast.LENGTH_LONG).show()
        }

    }

}