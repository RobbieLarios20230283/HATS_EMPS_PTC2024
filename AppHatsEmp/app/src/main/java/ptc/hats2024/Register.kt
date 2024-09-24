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


class Register : AppCompatActivity() {

    private val uuid = UUID.randomUUID().toString()


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
        val txtContrasenaRegistro = findViewById<EditText>(R.id.txtContraseñaReg)
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
            val correo : String = txtCorreoRegistro.text.toString()
            val contrasena : String = txtContrasenaRegistro.text.toString()
            val confirmarContrasena : String = txtConfirmarContrasena.text.toString()
            val nombreCompleto : String = txtNombreCompleto.text.toString()
            val direccion : String = txtDireccion.text.toString()

            if (correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty() || nombreCompleto.isEmpty() || direccion.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (contrasena != confirmarContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                // Insert data into Trabajador table
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            // Establish the connection
                            val objConnection = ClaseConexion().cadenaConexion()
                            if (objConnection != null) {
                                // Prepare the SQL insert statement
                                val statement = objConnection.prepareStatement(
                                    "INSERT INTO Trabajador (uuidTrabajador, nombre, correo, Contrasena, direccion) VALUES (?, ?, ?, ?, ?)"
                                )
                                // Set the values for the prepared statement
                                statement.setString(1, uuid)
                                statement.setString(2, nombreCompleto)
                                statement.setString(3, correo)
                                statement.setString(4, contrasena)
                                statement.setString(5, direccion)

                                // Execute the insert
                                statement.executeUpdate()

                                // Switch to the Main thread for UI updates
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@Register,
                                        "Información guardada correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@Register, ingreso_de_datos::class.java)
                                    startActivity(intent)
                                    limpiarCampos()
                                }
                            } else {
                                // Connection error
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@Register,
                                        "Error de conexión a la base de datos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: SQLException) {
                            // Handle SQL exception
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@Register,
                                    "Error al guardar la información: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            // Handle any other exceptions
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@Register,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                }
            }
        }

