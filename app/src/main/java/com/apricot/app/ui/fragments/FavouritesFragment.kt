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
import com.apricot.app.R
import com.apricot.app.data.database.AppDatabase
import com.apricot.app.data.mvvm.FavouriteRecipesViewModel
import com.apricot.app.data.mvvm.FavouriteRecipesViewModelFactory
import com.apricot.app.data.mvvm.RecipeRepository
import com.apricot.app.data.network.RetrofitInstance
import com.apricot.app.databinding.FragmentFavouritesBinding
import com.apricot.app.ui.adapter.RecipeAdapter
import kotlinx.coroutines.launch
import kotlin.getValue

class FavouritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavouriteRecipesViewModel by viewModels {
        val api = RetrofitInstance.api
        val dao = AppDatabase.getDatabase(requireContext()).favouriteDao()
        val repository = RecipeRepository(api, dao)
        // Return the Factory with the Repository just created
        FavouriteRecipesViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFavouritesBinding.bind(view)

        // Setup RecyclerView
        val adapter = RecipeAdapter(emptyList()) { recipe ->
            // Handling of the tap on the card
            val action =
                FavouritesFragmentDirections.
                actionFavouritesFragmentToRecipeDetailsFragment(recipe.id, false)
            findNavController().navigate(action)
        }

        binding.recyclerViewFavourites.adapter = adapter

        // Get data
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favouriteRecipes.collect { favouritesList ->
                    if (!favouritesList.isEmpty()) {
                        // Pass the list of recipes to the adapter
                        binding.textViewNoResults.visibility = View.GONE
                        binding.recyclerViewFavourites.visibility = View.VISIBLE
                        adapter.recipesList = favouritesList
                        adapter.notifyDataSetChanged()
                    } else {
                        // Show no results text
                        binding.textViewNoResults.visibility = View.VISIBLE
                        binding.recyclerViewFavourites.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}