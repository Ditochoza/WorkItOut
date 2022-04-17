package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment() {
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)

        //Configuramos el Toolbar
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        childFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout_settings_fragment, PreferencesFragment())
            .commit()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}