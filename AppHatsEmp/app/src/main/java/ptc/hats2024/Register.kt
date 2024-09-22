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

        val btnRegresar = findViewById<Button>(R.id.btnRegresar)

        btnSiguiente.setOnClickListener {
            val correo : String = txtCorreoRegistro.text.toString()
            val contrasena : String = txtContrasenaRegistro.text.toString()
            val confirmarContrasena = txtConfirmarContrasena.text.toString()
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
                val sendInformation = Intent(this, ingreso_de_datos::class.java)
                sendInformation.putExtra("correo", correo)
                sendInformation.putExtra("contrasena", contrasena)
                sendInformation.putExtra("nombreCompleto", nombreCompleto)
                sendInformation.putExtra("direccion", direccion)
                startActivity(sendInformation)
            }
        }


        btnRegresar.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}