package com.example.atomloginapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat.finishAffinity
import com.example.atomloginapp.R


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var backPressed = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CUSTOM ONBACK PRESS HANDLING
        val onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressed.plus(2000) >= System.currentTimeMillis()) {
                    finishAffinity(requireActivity())
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
}