package es.ucm.fdi.workitout.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.DialogSettingsChangePasswordBinding
import es.ucm.fdi.workitout.utils.createAlertDialog
import es.ucm.fdi.workitout.utils.string
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel
import es.ucm.fdi.workitout.viewModel.SettingsPreferencesViewModel

class PreferencesFragment : PreferenceFragmentCompat()  {
    private val sharedViewModel: MainSharedViewModel by activityViewModels()
    private val viewModel: SettingsPreferencesViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        //Botón de cambiar contraseña
        val btnChangePassword: Preference = preferenceManager.findPreference(getString(R.string.change_password_var))!!
        btnChangePassword.setOnPreferenceClickListener {
            activity?.let { activity ->
                val binding = DialogSettingsChangePasswordBinding.inflate(activity.layoutInflater)
                val alertDialog = context?.createAlertDialog(R.string.settings_user_change_password,
                    ok = R.string.confirm to {},
                    cancel = R.string.cancel to {}
                )?.setView(binding.root)?.create()

                //Observa cambios en el campo de contraseña actual
                binding.etCurrentPassword.doAfterTextChanged { binding.tilCurrentPassword.error = "" }
                //Observa cambios en el campo de contraseña nueva
                binding.etNewPassword.doAfterTextChanged { binding.tilNewPassword.error = "" }
                //Observa cambios en el campo de contraseña de validación
                binding.etValidationPassword.doAfterTextChanged { binding.tilValidationPassword.error = "" }
                //Sobreescribimos el Listener del botón de aceptar para que no cierre el Dialog
                alertDialog?.setOnShowListener {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        viewModel.validateAndUpdatePassword(binding.etCurrentPassword.string,
                            binding.tilCurrentPassword, binding.etNewPassword.string, binding.tilNewPassword,
                            binding.etValidationPassword.string, binding.tilValidationPassword, sharedViewModel)
                    }
                }
                alertDialog?.show()
            }

            return@setOnPreferenceClickListener true
        }

        //Botón de dar de baja el usuario
        /*val btnDeleteAccount: Preference = preferenceManager.findPreference(getString(R.string.delete_account_var))!!
        btnDeleteAccount.setOnPreferenceClickListener {
            activity?.let { activity ->
                val binding = DialogSettingsDeleteAccountBinding.inflate(activity.layoutInflater)
                val alertDialog =
                    context?.createAlertDialog(R.string.settings_user_unsubscribe_title,
                        R.string.settings_user_unsubscribe,
                        ok = R.string.confirm to {},
                        cancel = R.string.cancel to {}
                    )?.setView(binding.root)?.create()

                //Observa cambios en el campo de contraseña actual
                binding.etCurrentPassword.doAfterTextChanged { binding.tilCurrentPassword.error = "" }
                //Sobreescribimos el Listener del botón de aceptar para que no cierre el Dialog
                alertDialog?.setOnShowListener {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val currentPassword = binding.etCurrentPassword.string
                        sharedViewModel.validateCheckPasswordAndDeleteAccount(currentPassword, binding.tilCurrentPassword)
                    }
                }
                alertDialog?.show()
            }

            return@setOnPreferenceClickListener true
        }*/
    }
}