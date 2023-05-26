package com.kanj.apps.preferhdmi

import android.os.Bundle
import android.util.Log
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.leanback.preference.LeanbackSettingsFragmentCompat
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen

class MainFragment : LeanbackSettingsFragmentCompat() {

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat?, pref: Preference?): Boolean {
        Log.v("Kanj", "onPreferenceStartFragment")
        pref ?: return false

        val args = pref.extras
        val fragment = childFragmentManager.fragmentFactory.instantiate(
            requireActivity().classLoader, pref.fragment
        )
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)
        if (fragment is PreferenceFragmentCompat || fragment is PreferenceDialogFragmentCompat) {
            Log.v("Kanj", "Start preference frag")
            startPreferenceFragment(fragment)
        } else {
            Log.v("Kanj", "Start immersive frag")
            startImmersiveFragment(fragment)
        }
        return true
    }

    override fun onPreferenceStartScreen(caller: PreferenceFragmentCompat?, pref: PreferenceScreen?): Boolean {
        Log.v("Kanj", "onPreferenceStartScreen")
        pref ?: return false

        val fragment = DemoFragment()
        val args = Bundle(1)
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.key)
        fragment.arguments = args
        startPreferenceFragment(fragment)

        return true
    }

    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(DemoFragment())
    }

    class DemoFragment : LeanbackPreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
    }
}
