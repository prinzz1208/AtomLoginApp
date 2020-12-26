package com.example.atomloginapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.atomloginapp.R
import com.example.atomloginapp.model.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_user_registration.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class UserRegistrationFragment : Fragment(R.layout.fragment_user_registration) {

    @Inject lateinit var up: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CUSTOM ONBACK PRESS HANDLING
        val onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressHandler()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        up.userNameFlow.asLiveData().observe(viewLifecycleOwner, {
            if(!it.isNullOrEmpty()){
                editTextTextPersonName.setText(it)
            }
        })

        setUpListeners()
    }

    // LISTENERS
    private fun setUpListeners() {

        // HANDLE USERNAME INPUT AND NAVIGATION
        doneButton.setOnClickListener{
            val personName = editTextTextPersonName.text.toString()
            if(personName.isNotEmpty()){
                lifecycleScope.launch {
                    up.storeUserName(personName)
                }
                findNavController().navigate(UserRegistrationFragmentDirections.actionUserRegistrationFragmentToHomeFragment())
            }else{
                Toast.makeText(requireContext(), "Please fill your name", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            backPressHandler()
        }

    }

    // CUSTOM ONBACK PRESS HANDLING
    private fun backPressHandler() {
        if (findNavController().previousBackStackEntry?.destination?.id == R.id.splashScreen)
            findNavController().navigate(UserRegistrationFragmentDirections.actionUserRegistrationFragmentToLoginFragment())
        else
            findNavController().popBackStack()
    }


}