package ptc.hats2024

import Modelo.ClaseConexion
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.SQLException
import java.util.UUID
import java.util.regex.Pattern
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

// Función para encriptar la contraseña con SHA-256
fun encriptarSHA256(contrasena: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(contrasena.toByteArray(StandardCharsets.UTF_8))
    val hexString = StringBuilder()
    for (byte in hash) {
        val hex = String.format("%02x", byte)
        hexString.append(hex)
    }
    return hexString.toString()
}

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtCorreoRegistro = findViewById<EditText>(R.id.txtCorreoRegistro)
        val txtContrasenaRegistro = findViewById<EditText>(R.id.txtContraseñaRegistro)
        val txtConfirmarContrasena = findViewById<EditText>(R.id.txtConfirmarContraseña)
        val txtNombreCompleto = findViewById<EditText>(R.id.txtNombreCompleto)
        val txtDireccion = findViewById<EditText>(R.id.txtDireccion)
        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)
        val btnRegresar = findViewById<Button>(R.id.btnRegresar)

        fun limpiarCampos() {
            txtCorreoRegistro.text.clear()
            txtContrasenaRegistro.text.clear()
            txtNombreCompleto.text.clear()
            txtDireccion.text.clear()
            txtConfirmarContrasena.text.clear()
        }

        btnRegresar.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        btnSiguiente.setOnClickListener {
            val correo: String = txtCorreoRegistro.text.toString()
            val contrasena: String = txtContrasenaRegistro.text.toString()
            val confirmarContrasena: String = txtConfirmarContrasena.text.toString()
            val nombreCompleto: String = txtNombreCompleto.text.toString()
            val direccion: String = txtDireccion.text.toString()

            // Validar que los campos no estén vacíos
            if (correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty() || nombreCompleto.isEmpty() || direccion.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Expresión regular personalizada para validar el correo
            val emailPattern = Pattern.compile("^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$")
            if (!emailPattern.matcher(correo).matches()) {
                Toast.makeText(this, "Por favor, ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar que la contraseña tenga al menos 6 caracteres
            if (contrasena.length < 8) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar que las contraseñas coincidan
            if (contrasena != confirmarContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Inserción de datos en la tabla Trabajador
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val objConnection = ClaseConexion().cadenaConexion()
                    val uuid = UUID.randomUUID().toString()

                    if (objConnection != null) {
                        val statement = objConnection.prepareStatement(
                            "INSERT INTO Trabajador (uuidTrabajador, nombre, correo, Contrasena, direccion) VALUES (?, ?, ?, ?, ?)"
                        )
                        val contrasenaEncriptada = encriptarSHA256(contrasena)
                        statement.setString(1, uuid)
                        statement.setString(2, nombreCompleto)
                        statement.setString(3, correo)
                        statement.setString(4, contrasenaEncriptada)
                        statement.setString(5, direccion)
                        statement.executeUpdate()

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@Register,
                                "Información guardada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@Register, ingreso_de_datos::class.java)
                            intent.putExtra("uuidTrabajador", uuid)
                            startActivity(intent)
                            limpiarCampos()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@Register,
                                "Error de conexión a la base de datos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: SQLException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Register,
                            "Error al guardar la información: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Register, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
