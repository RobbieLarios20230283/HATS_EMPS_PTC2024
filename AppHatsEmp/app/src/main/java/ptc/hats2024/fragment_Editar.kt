package ptc.hats2024

import Modelo.ClaseConexion
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptc.hats2024.Login.VaraibleGlobal.CorreoGlobal

class fragment_Editar : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment__editar, container, false)

        // Referencias a los elementos de la vista
        val EditName: EditText = view.findViewById(R.id.etName)
        val EditEmail: EditText = view.findViewById(R.id.etEmail)
        val EditPhone: EditText = view.findViewById(R.id.etPhone)
        val EditAddress: EditText = view.findViewById(R.id.etAddress)
        val EditNameUser: EditText = view.findViewById(R.id.etUsername)
        val btnSave: Button = view.findViewById(R.id.btnSave)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnCancel.setOnClickListener {
            findNavController().navigate(R.id.perfil)
        }

        // Configuración del botón guardar
        btnSave.setOnClickListener {
            val CorreoG = CorreoGlobal
            val Nombre = EditName.text.toString()
            val Email = EditEmail.text.toString()
            val NumeroTel = EditPhone.text.toString()
            val Address = EditAddress.text.toString()
            val Username = EditNameUser.text.toString()

            if (Nombre.isEmpty() || Nombre.length > 50) {
                Toast.makeText(requireContext(), "El nombre no puede estar vacío y debe tener máximo 50 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                Toast.makeText(requireContext(), "Por favor ingrese un correo válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (NumeroTel.isEmpty() || !NumeroTel.matches(Regex("\\d{8}"))) {
                Toast.makeText(requireContext(), "El número debe tener 8 dígitos y solo contener números.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Address.isEmpty() || Address.length > 100) {
                Toast.makeText(requireContext(), "La dirección no puede estar vacía y debe tener máximo 100 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Username.isEmpty() || Username.length > 100) {
                Toast.makeText(requireContext(), "El nombre de perfil no puede estar vacío y debe tener máximo 100 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Iniciar la actualización si pasa las validaciones
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val objConexion = ClaseConexion().cadenaConexion()
                    val ActualizarEP = objConexion?.prepareStatement(
                        "UPDATE Trabajador SET nombre = ?, correo = ?, telefono = ?, direccion = ?, nombrePerfil = ? WHERE correo = ?"
                    )

                    if (ActualizarEP != null) {
                        ActualizarEP.setString(1, Nombre)
                        ActualizarEP.setString(2, Email)
                        ActualizarEP.setString(3, NumeroTel)
                        ActualizarEP.setString(4, Address)
                        ActualizarEP.setString(5, Username)
                        ActualizarEP.setString(6, CorreoG) // El correo que actúa como identificador

                        val filasAfectadas = ActualizarEP.executeUpdate()

                        withContext(Dispatchers.Main) {
                            if (filasAfectadas > 0) {
                                Toast.makeText(requireContext(), "Actualizado exitosamente.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "No se encontró el registro para actualizar.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        // Liberamos el recurso del PreparedStatement
                        ActualizarEP.close()
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Error de conexión.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }
}
