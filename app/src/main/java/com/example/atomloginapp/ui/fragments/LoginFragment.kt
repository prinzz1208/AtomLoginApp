package com.example.atomloginapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.atomloginapp.R
import com.example.atomloginapp.model.UserPreferences
import com.example.atomloginapp.databinding.FragmentLoginBinding
import com.example.atomloginapp.utils.Constants.ERROR
import com.example.atomloginapp.utils.Constants.LOADING
import com.example.atomloginapp.utils.Constants.REQUEST_CODE_GOOGLE_SIGN_IN
import com.example.atomloginapp.utils.Constants.SUCCESS
import com.example.atomloginapp.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject lateinit var up: UserPreferences

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding
    private var backPressed = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CUSTOM ONBACK PRESS HANDLING
        val onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressed.plus(2000) >= System.currentTimeMillis()) {
                    ActivityCompat.finishAffinity(requireActivity())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Press again to exit",
                        Toast.LENGTH_SHORT
                    ).show()
                    backPressed = System.currentTimeMillis()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        init()
    }
    private fun init() {
        setUpListeners()
        setUpObservers()
    }

    // LISTENERS
    private fun setUpListeners() {
        binding.googleSignIn.setOnClickListener {
            viewModel.uiState.value = LOADING
            startGoogleSignIn()
        }

        binding.guestSignIn.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToUserRegistrationFragment())
        }
    }

    // OBSERVERS
    private fun setUpObservers() {

        // HANDLE UI STATE FOR LOADING, ERROR, SUCCESS
        viewModel.uiState.observe(viewLifecycleOwner, {
            if(!it.isNullOrEmpty()) {
                when (it) {
                    LOADING -> binding.signInLoader.visibility = View.VISIBLE
                    SUCCESS, ERROR -> binding.signInLoader.visibility = View.GONE
                }
            }
        })

        // HANDLE USER LOGIN SUCCESSFUL EVENT AND NAVIGATION
        viewModel.isUserLoggedIn.observe(viewLifecycleOwner, {
            if (it) {
                Log.d("TAG", "setUpObservers: LoginFragment: $it ")
                if (findNavController().previousBackStackEntry?.destination?.id == R.id.userRegistrationFragment)
                    viewModel.isUserLoggedIn.value = false
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToUserRegistrationFragment())
            }
        })
    }

    // INITIATE GOOGLE SIGNIN
    private fun startGoogleSignIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .build()
        val signInClient = GoogleSignIn.getClient(requireActivity(), options)
        startActivityForResult(signInClient.signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_GOOGLE_SIGN_IN && data != null) {
            viewModel.uiState.value = LOADING
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            viewModel.googleAuthForFirebase(task,firebaseAuth)
        }
    }

}