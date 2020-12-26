package com.example.atomloginapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.atomloginapp.R
import com.example.atomloginapp.model.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_splash_screen.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SplashScreen : Fragment(R.layout.fragment_splash_screen) {

    @Inject
    lateinit var up: UserPreferences
    private val SPLASH_SCREEN_TIMEOUT: Long = 1000

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        atom.animate().alpha(1f).duration = SPLASH_SCREEN_TIMEOUT
        up.userIsLoggedInFlow.asLiveData().observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                delay(SPLASH_SCREEN_TIMEOUT)
                if(it){
                    up.userNameFlow.asLiveData().observe(viewLifecycleOwner, {
                        if(it.isNullOrEmpty()){
                            findNavController().navigate(SplashScreenDirections.actionSplashScreenToUserRegistrationFragment())
                        }else{
                            findNavController().navigate(SplashScreenDirections.actionSplashScreenToHomeFragment())
                        }
                    })

                }else{
                    findNavController().navigate(SplashScreenDirections.actionSplashScreenToLoginFragment())
                }
            }
        })
    }
}