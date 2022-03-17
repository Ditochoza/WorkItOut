package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentLoginBinding
import es.ucm.fdi.workitout.viewModel.StartSharedViewModel

class LoginFragment : Fragment() {
    private val startSharedViewModel: StartSharedViewModel by activityViewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.sModel = startSharedViewModel
        binding.loginFrag = this
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    fun navigateToRegister(){
        view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}