package com.example.storyin.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyin.R
import com.example.storyin.data.preferences.UserPreference
import com.example.storyin.di.Injection
import com.example.storyin.ui.adapter.StoryAdapter
import com.example.storyin.ui.viewmodel.StoryViewModel
import com.example.storyin.ui.viewmodel.factory.StoryViewModelFactory
import kotlinx.coroutines.launch

class StoryFragment : Fragment(R.layout.fragment_story) {
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var userPreference: UserPreference
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeView(view)
        setupViewModel()
        setupObservers()
        setupListeners()
    }

    private fun initializeView(view: View) {
        userPreference = UserPreference.getInstance(requireContext())
        recyclerView = view.findViewById(R.id.story_recycler_view)
        progressBar = view.findViewById(R.id.progressBar)

        storyAdapter = StoryAdapter()
        recyclerView.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupViewModel() {
        val storyRepository = Injection.provideStoryRepository(requireContext())
        storyViewModel = ViewModelProvider(
            this,
            StoryViewModelFactory(storyRepository)
        )[StoryViewModel::class.java]

        // Fetch stories immediately on view creation
        storyViewModel.fetchStories()
    }

    private fun setupObservers() {
        storyViewModel.stories.observe(viewLifecycleOwner) { stories ->
            storyAdapter.submitList(stories)
        }

        storyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        storyViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showToast(it)
            }
        }
    }

    private fun setupListeners() {
        view?.findViewById<View>(R.id.action_logout)?.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        storyViewModel.fetchStories()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.logout_confirmation_message))
            .setPositiveButton(getString(R.string.logout_confirmation_yes)) { dialog, _ ->
                lifecycleScope.launch {
                    userPreference.clearToken()
                    showToast(getString(R.string.logout_successful))
                    requireActivity().finish()
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.logout_confirmation_no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}