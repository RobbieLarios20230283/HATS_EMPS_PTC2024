package ptc.hats2024


import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast



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
        val txtContrasenaRegistro = findViewById<EditText>(R.id.txtContraseñaReg)
        val txtConfirmarContrasena = findViewById<EditText>(R.id.txtConfirmarContraseña)
        val txtNombreCompleto = findViewById<EditText>(R.id.txtNombreCompleto)
        val txtDireccion = findViewById<EditText>(R.id.txtDireccion)
        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)

        btnSiguiente.setOnClickListener {
            val correo = txtCorreoRegistro.text.toString()
            val contrasena = txtContrasenaRegistro.text.toString()
            val confirmarContrasena = txtConfirmarContrasena.text.toString()
            val nombreCompleto = txtNombreCompleto.text.toString()
            val direccion = txtDireccion.text.toString()

            if (correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty() || nombreCompleto.isEmpty() || direccion.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (contrasena != confirmarContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val intent = Intent(this, ingreso_de_datos::class.java)
                intent.putExtra("correo", correo)
                intent.putExtra("contrasena", contrasena)
                intent.putExtra("nombreCompleto", nombreCompleto)
                intent.putExtra("direccion", direccion)
                startActivity(intent)
            }
        }
    }
}