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
import androidx.navigation.fragment.navArgs
import coil.load
import com.apricot.app.R
import com.apricot.app.data.database.AppDatabase
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.mvvm.RecipeDetailsViewModel
import com.apricot.app.data.mvvm.RecipeDetailsViewModelFactory
import com.apricot.app.data.mvvm.RecipeRepository
import com.apricot.app.data.network.RetrofitInstance
import com.apricot.app.databinding.FragmentRecipeDetailsBinding
import kotlinx.coroutines.launch
import kotlin.getValue

/*
 *  TODO: aggiungere tutte le informazioni utili della ricetta ricevute tramite l'API
 *  - aggiungere views
 *  - cambiare oggetto recipe
 *  - settare le views
 */

class RecipeDetailsFragment : Fragment() {
    private val args: RecipeDetailsFragmentArgs by navArgs()
    private var _binding: FragmentRecipeDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeDetailsViewModel by viewModels {
        val api = RetrofitInstance.api
        val dao = AppDatabase.getDatabase(requireContext()).favouriteDao()
        val repository = RecipeRepository(api, dao)
        // Return the Factory with the Repository just created
        RecipeDetailsViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentRecipeDetailsBinding.bind(view)
        binding.checkBoxFavourite.setOnClickListener { viewModel.toggleFavourite() }
        viewModel.loadRecipe(args.recipeID, args.dataFromNetwork)
        setupObservers()
    }

    private fun setupObservers() {
        // repeatOnLifecycle grants that the data gathering stops when the app is in background, saving resources
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe stateFlow exposed by viewModel
                viewModel.recipeData.collect { recipe ->
                    if (recipe != null) {
                        bindRecipeData(recipe)
                    }
                }
            }
        }
    }

    private fun bindRecipeData(recipe: Recipe) {
        binding.textViewRecipeTitle.text = recipe.title
        binding.imageViewRecipeDetail.load(recipe.imageUrl)
        binding.checkBoxFavourite.isChecked = recipe.isFavourite
        binding.textViewReadyIn.text = "Ready in ${recipe.readyInMinutes} minutes"
        binding.textViewDishTypes.text = "Dish types: ${recipe.dishTypes.joinToString(", ")}"
        binding.textViewRecipeLink.text = "Recipe detail on ${recipe.sourceUrl}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}