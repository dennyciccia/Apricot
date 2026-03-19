package com.apricot.app.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.apricot.app.R
import com.apricot.app.databinding.FragmentSearchFormBinding

class SearchFormFragment : Fragment() {

    private var _binding: FragmentSearchFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchFormBinding.bind(view)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Types management
        val dishTypes = resources.getStringArray(R.array.dish_types)
        val typesSelectedItems = BooleanArray(dishTypes.size)
        val typesUserSelections = mutableListOf<Int>()
        val typesInputField = binding.selectDishType

        typesInputField.setOnClickListener {_ ->
            onClickListenerForAutocompleteTextView(
                resources.getString(R.string.choose_types),
                dishTypes,
                typesSelectedItems,
                typesUserSelections,
                typesInputField
            )
        }

        // Gluten free switch management
        val switchGlutenFree = binding.switchGlutenFree
        switchGlutenFree.isChecked = sharedPreferences.getBoolean(resources.getString(R.string.gluten_free_only_key), false)

        // Vegetarian switch management
        val switchVegetarian = binding.switchVegetarian
        switchVegetarian.isChecked = sharedPreferences.getBoolean(resources.getString(R.string.vegetarian_only_key), false)
        switchVegetarian.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switchVegan.isChecked = false
            }
        }

        // Vegan switch management
        val switchVegan = binding.switchVegan
        switchVegan.isChecked = sharedPreferences.getBoolean(resources.getString(R.string.vegan_only_key), false)
        switchVegan.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switchVegetarian.isChecked = false
            }
        }

        // Cuisines management
        val cuisines = resources.getStringArray(R.array.cuisines_labels)
        val cuisinesSet = sharedPreferences.getStringSet(resources.getString(R.string.cuisines_key), emptySet()) ?: emptySet()
        val cuisinesSelectedItems = BooleanArray(cuisines.size)
        val cuisinesUserSelections = mutableListOf<Int>()

        // Synchronize indexes state based on saved preferences
        cuisines.forEachIndexed { index, cuisineName ->
            if (cuisinesSet.contains(cuisineName)) {
                cuisinesSelectedItems[index] = true
                cuisinesUserSelections.add(index)
            }
        }

        val cuisinesInputField = binding.selectCuisines
        cuisinesInputField.setText(cuisinesSet.joinToString(", "))

        cuisinesInputField.setOnClickListener {_ ->
            onClickListenerForAutocompleteTextView(
                resources.getString(R.string.choose_cuisines),
                cuisines,
                cuisinesSelectedItems,
                cuisinesUserSelections,
                cuisinesInputField
            )
        }

        // Intolerances management
        val intolerances = resources.getStringArray(R.array.intolerances_labels)
        val intolerancesSet = sharedPreferences.getStringSet(resources.getString(R.string.intolerances_key), emptySet()) ?: emptySet()
        val intolerancesSelectedItems = BooleanArray(intolerances.size)
        val intolerancesUserSelections = mutableListOf<Int>()

        // Synchronize indexes state based on saved preferences
        intolerances.forEachIndexed { index, intolerancesName ->
            if (intolerancesSet.contains(intolerancesName)) {
                intolerancesSelectedItems[index] = true
                intolerancesUserSelections.add(index)
            }
        }

        val intolerancesInputField = binding.selectIntolerances
        intolerancesInputField.setText(intolerancesSet.joinToString(", "))

        intolerancesInputField.setOnClickListener {_ ->
            onClickListenerForAutocompleteTextView(
                resources.getString(R.string.choose_intolerances),
                intolerances,
                intolerancesSelectedItems,
                intolerancesUserSelections,
                intolerancesInputField
            )
        }

        // Max ready time management
        val maxReadyTime = binding.editTextMaxReadyTime
        maxReadyTime.setText(sharedPreferences.getString(resources.getString(R.string.max_ready_time_key), ""))

        // Results limit management
        val resultsLimit = binding.editTextResultsLimit
        resultsLimit.setText(sharedPreferences.getString(resources.getString(R.string.results_limit_key), ""))

        // Button search
        val buttonSearch = binding.buttonSearch
        buttonSearch.setOnClickListener {
            // Moving to next fragment
            val action = SearchFormFragmentDirections.actionSearchFormFragmentToDisplayResultsFragment(
                formatForAPIRequest(binding.editTextIngredients.text),
                binding.editTextQuery.text.toString(),
                formatForAPIRequest(typesInputField.text),
                formatForAPIRequest(cuisinesInputField.text),
                formatForAPIRequest(intolerancesInputField.text),
                maxReadyTime.text.toString(),
                switchGlutenFree.isChecked,
                switchVegetarian.isChecked,
                switchVegan.isChecked,
                resultsLimit.text.toString()
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onClickListenerForAutocompleteTextView(
        title: String,
        labels: Array<String>,
        selectedItems: BooleanArray,
        userSelections: MutableList<Int>,
        inputField: AutoCompleteTextView
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)

        // Set the checkboxes
        builder.setMultiChoiceItems(labels, selectedItems) { _, index, isChecked ->
            if (isChecked) {
                userSelections.add(index)
            } else {
                userSelections.remove(index)
            }
        }

        // Confirm button
        builder.setPositiveButton(R.string.OK_dialog) { _, _ ->
            val stringBuilder = StringBuilder()
            for (i in userSelections.indices) {
                stringBuilder.append(labels[userSelections[i]])
                if (i != userSelections.size - 1) stringBuilder.append(", ")
            }
            inputField.setText(stringBuilder.toString())
        }

        builder.setNegativeButton(R.string.deny_dialog, null)
        builder.show()
    }

    fun formatForAPIRequest(text: Editable) : String {
        // returns comma separated strings without spaces
        return text
            .toString()
            .split(",", "-", "_", ".", ";", ":", " ")
            .joinToString(",", transform = String::trim)
    }
}