package com.apricot.app.ui.fragments

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.apricot.app.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Make vetetarian_only and vegan_only preferences mutually exclusive
        val vegetarianOnly = findPreference<SwitchPreferenceCompat>(resources.getString(R.string.vegetarian_only_key))
        val veganOnly = findPreference<SwitchPreferenceCompat>(resources.getString(R.string.vegan_only_key))

        vegetarianOnly?.setOnPreferenceChangeListener { _, newValue ->
            val isChecked = newValue as Boolean
            if (isChecked) {
                veganOnly?.isChecked = false // if vegetarian_only is checked, vegan_only is unchecked
            }
            true
        }

        veganOnly?.setOnPreferenceChangeListener { _, newValue ->
            val isChecked = newValue as Boolean
            if (isChecked) {
                vegetarianOnly?.isChecked = false // if vegan_only is checked, vegetarian_only is unchecked
            }
            true
        }

        // Make max_ready_time and results_limit input type number
        val maxReadyTime = findPreference<EditTextPreference>(resources.getString(R.string.max_ready_time))
        val resultsLimit = findPreference<EditTextPreference>(resources.getString(R.string.results_limit))

        maxReadyTime?.setOnBindEditTextListener { editText ->
            // Set the keyboard only numeric
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        resultsLimit?.setOnBindEditTextListener { editText ->
            // Set the keyboard only numeric
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }
}