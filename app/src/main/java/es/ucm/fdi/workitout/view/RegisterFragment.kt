package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.workitout.databinding.FragmentRegisterBinding
import es.ucm.fdi.workitout.viewModel.StartSharedViewModel

class RegisterFragment : Fragment() {
    private val startSharedViewModel: StartSharedViewModel by activityViewModels()

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.sModel = startSharedViewModel
        binding.registerFrag = this
        binding.lifecycleOwner = viewLifecycleOwner

        this.configTypeInputLayout();

        return binding.root
    }

    fun goBack(){
        activity?.onBackPressed()
    }

    fun signUp (){

        binding.sModel?.let { model ->
            model.register(
                binding.etName.getText().toString(),
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString(),
                binding.etPasswordValidate.getText().toString());

            val ok: Boolean = this.checkFields(model);
            val user = model.user.value;

            if(ok){
                db.collection("users").document(user.email).set(
                    hashMapOf("Name" to user.name,
                        "password" to user.tempPassword)
                )
            }

        }
    }

    fun checkFields(model: StartSharedViewModel): Boolean{

        var error: Boolean = false;
        val user = model.user.value;

        if (user.name.isEmpty()) {
            error = true;
            binding.tilName.error = "Debes introducir tu nombre";
        }

        if (user.email.isEmpty()) {
            error = true;
            binding.tilEmail.error = "Debes introducir tu correo electronico";
        }else if(!Patterns.EMAIL_ADDRESS.matcher(user.email).matches()){
            binding.tilEmail.error = "Debes introducir una dirrección de correo válida";
        }


        if (user.tempPassword.isEmpty()) {
            error = true;
            binding.tilPassword.error = "Debes introducir tu contraseña";
        }

        if (user.tempPasswordValidate.isEmpty()) {
            error = true;
            binding.tilPasswordValidate.error  = "Debes volver a introducir tu contraseña";
        }

        if(!user.tempPassword.isEmpty() && !user.tempPasswordValidate.isEmpty() &&
            user.tempPassword != user.tempPasswordValidate){
            error = true;
            binding.tilPasswordValidate.error = "Las contaseñas deben coincidir";
        }

        return !error;
    }

    fun configTypeInputLayout(){
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) { binding.tilName.error = "" }
        })

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) { binding.tilEmail.error = "" }
        })
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) { binding.tilPassword.error = "" }
        })

        binding.etPasswordValidate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) { binding.tilPasswordValidate.error = "" }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}