package com.apricot.app.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.apricot.app.R
import com.apricot.app.data.database.AppDatabase
import com.apricot.app.data.mvvm.DisplayResultsViewModel
import com.apricot.app.data.mvvm.DisplayResultsViewModelFactory
import com.apricot.app.data.mvvm.RecipeRepository
import com.apricot.app.ui.adapter.RecipeAdapter
import com.apricot.app.data.network.RetrofitInstance
import com.apricot.app.databinding.FragmentDisplayResultsBinding
import kotlinx.coroutines.launch
import kotlin.getValue

class DisplayResultsFragment : Fragment() {
    private val args: DisplayResultsFragmentArgs by navArgs()
    private var _binding: FragmentDisplayResultsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DisplayResultsViewModel by viewModels {
        val api = RetrofitInstance.api
        val dao = AppDatabase.getDatabase(requireContext()).favouriteDao()
        val repository = RecipeRepository(api, dao)
        // Return the Factory with the Repository just created
        DisplayResultsViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDisplayResultsBinding.bind(view)

        // Setup RecyclerView
        val adapter = RecipeAdapter(emptyList()) { recipe ->
            // Handling of the tap on the card
            val action =
                DisplayResultsFragmentDirections.
                actionDisplayResultsFragmentToRecipeDetailsFragment(recipe.id, true)
            findNavController().navigate(action)
        }

        binding.recyclerViewDisplayResults.adapter = adapter

        // Get data
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recipesList.collect { recipesList ->
                    if (!recipesList.isEmpty()) {
                        // Pass the list of recipes to the adapter
                        binding.textViewNoResults.visibility = View.GONE
                        binding.recyclerViewDisplayResults.visibility = View.VISIBLE
                        adapter.recipesList = recipesList
                        adapter.notifyDataSetChanged()
                    } else {
                        // Show no results text
                        binding.textViewNoResults.visibility = View.VISIBLE
                        binding.recyclerViewDisplayResults.visibility = View.GONE
                    }
                }
            }
        }

        // Load Recipes (serach with GET API)
        viewModel.loadRecipesIfNeeded(args)
    }
}