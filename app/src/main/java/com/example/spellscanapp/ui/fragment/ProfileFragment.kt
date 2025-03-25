package com.example.spellscanapp.ui.fragment

import android.os.Bundle
import android.text.method.KeyListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.imageLoader
import coil.request.ImageRequest
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.FragmentProfileBinding
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.util.loadFromUrl
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileFragment : Fragment() {

    private val authService: AuthService by lazy {
        AuthService(requireContext())
    }

    private lateinit var binding: FragmentProfileBinding

    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.enableEditProfileFab.setOnClickListener { toggleEditProfile() }

        binding.resetPasswordButton.setOnClickListener { resetPassword() }

        binding.logoutButton.setOnClickListener { logout() }

        return binding.root
    }

    private fun toggleEditProfile() {
        if (!isEditing) {
            binding.profileUsernameText.keyListener =
                binding.profileUsernameText.tag as KeyListener?
            binding.enableEditProfileFab.setImageResource(R.drawable.check_icon)
        } else {
            saveProfile()
            binding.profileUsernameText.keyListener = null
            binding.enableEditProfileFab.setImageResource(R.drawable.edit_icon)
        }

        isEditing = !isEditing
    }

    private fun resetPassword() {
        val user = authService.getCurrentUser()

        val handleException = CoroutineExceptionHandler { _, throwable ->
            val message = "Could not reset password, try again later."
            Log.e(TAG, message, throwable)
            Snackbar.make(binding.root, message, LENGTH_LONG).show()
        }

        user?.let {
            lifecycleScope.launch(handleException) {
                authService.sendPasswordResetEmail(it.email!!).await()
                Snackbar.make(
                    binding.root,
                    "You will receive a confirmation email shortly.",
                    LENGTH_LONG
                ).show()
            }
        }
    }

    private fun logout() {
        authService.logout()
        findNavController().navigate(R.id.signInFragment)
    }

    private fun saveProfile() {
        val user = authService.getCurrentUser()

        val handleException = CoroutineExceptionHandler { _, throwable ->
            val message = "Error updating profile"
            Log.e(TAG, message, throwable)
            Snackbar.make(binding.root, message, LENGTH_LONG).show()
        }

        user?.let {
            lifecycleScope.launch(handleException) {
                user.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(binding.profileUsernameText.text.toString())
                        .build()
                ).await()

                Snackbar.make(binding.root, "Profile updated.", LENGTH_LONG).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        if (!authService.isAuthorized()) {
            navController.navigate(R.id.signInFragment)
        }

        authService.getCurrentUser()?.let {
            binding.profileUsernameText.text.insert(
                0,
                if (!it.displayName.isNullOrEmpty()) it.displayName else "Liliana Vess"
            )
            binding.profileUsernameText.tag = binding.profileUsernameText.keyListener
            binding.profileUsernameText.keyListener = null

            binding.profileEmailText.text = it.email

            binding.profilePicture.loadFromUrl(it.photoUrl.toString())
        }
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}