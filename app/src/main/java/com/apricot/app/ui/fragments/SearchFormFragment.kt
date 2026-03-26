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

        initializeCheckboxesInput(
            resources.getString(R.string.choose_types),
            dishTypes,
            emptySet(),
            typesSelectedItems,
            typesUserSelections,
            typesInputField
        )

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
        val cuisines = resources.getStringArray(R.array.cuisines_labels) // labels
        val cuisinesPreferencesSet = sharedPreferences.getStringSet(resources.getString(R.string.cuisines_key), emptySet()) ?: emptySet() // saved in preferences
        val cuisinesSelectedItems = BooleanArray(cuisines.size) // indicates which labels checkbox are checked
        val cuisinesUserSelections = mutableListOf<Int>() // indexes of the labels chosen by the user
        val cuisinesInputField = binding.selectCuisines

        initializeCheckboxesInput(
            resources.getString(R.string.choose_cuisines),
            cuisines,
            cuisinesPreferencesSet,
            cuisinesSelectedItems,
            cuisinesUserSelections,
            cuisinesInputField
        )

        // Intolerances management
        val intolerances = resources.getStringArray(R.array.intolerances_labels)
        val intolerancesPreferencesSet = sharedPreferences.getStringSet(resources.getString(R.string.intolerances_key), emptySet()) ?: emptySet()
        val intolerancesSelectedItems = BooleanArray(intolerances.size)
        val intolerancesUserSelections = mutableListOf<Int>()
        val intolerancesInputField = binding.selectIntolerances

        initializeCheckboxesInput(
            resources.getString(R.string.choose_intolerances),
            intolerances,
            intolerancesPreferencesSet,
            intolerancesSelectedItems,
            intolerancesUserSelections,
            intolerancesInputField
        )

        // Max ready time management
        val maxReadyTime = binding.editTextMaxReadyTime
        maxReadyTime.setText(sharedPreferences.getString(resources.getString(R.string.max_ready_time_key), ""))

        // Results limit management
        val resultsLimit = binding.editTextResultsLimit
        resultsLimit.setText(sharedPreferences.getString(resources.getString(R.string.results_limit_key), ""))

        // Button reset
        val buttonReset = binding.buttonReset
        buttonReset.setOnClickListener {
            binding.editTextIngredients.text.clear()
            binding.editTextQuery.text.clear()
            typesInputField.text.clear()
            switchGlutenFree.isChecked = false
            switchVegetarian.isChecked = false
            switchVegan.isChecked = false
            cuisinesInputField.text.clear()
            intolerancesInputField.text.clear()
            maxReadyTime.text.clear()
            resultsLimit.text.clear()
        }

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

    private fun initializeCheckboxesInput(
        title: String,
        labels: Array<String>,
        preferencesSet: Set<String>,
        selectedItems: BooleanArray,
        userSelections: MutableList<Int>,
        inputField: AutoCompleteTextView
    ) {
        // Synchronize indexes state based on saved preferences
        synchronizeState(labels, preferencesSet, selectedItems, userSelections)

        inputField.setText(preferencesSet.joinToString(", "))

        inputField.setOnClickListener { _ ->
            /*
            * Synchronize state arrays with text in inputField
            */
            // Clear current state
            selectedItems.fill(false)
            userSelections.clear()

            // Get text and get each item
            val text = inputField.text.toString()
            if (text.isNotEmpty()) {
                val currentItems = text.split(",").map { it.trim() }
                // Synchronize state with text in inputField
                synchronizeState(labels, currentItems.toSet(), selectedItems, userSelections)
            }

            /*
            * Building the alert dialog with checkboxes
            */
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
    }

    private fun synchronizeState(
        labels: Array<String>,
        itemSet: Set<String>,
        selectedItems: BooleanArray,
        userSelections: MutableList<Int>
    ) {
        labels.forEachIndexed { index, cuisineName ->
            if (itemSet.contains(cuisineName)) {
                selectedItems[index] = true
                userSelections.add(index)
            }
        }
    }

    fun formatForAPIRequest(text: Editable) : String {
        // returns comma separated strings without spaces
        return text
            .toString()
            .split(",", "-", "_", ".", ";", ":")
            .joinToString(",", transform = String::trim)
    }
}
