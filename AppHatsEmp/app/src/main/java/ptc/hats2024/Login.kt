package ptc.hats2024

import Modelo.ClaseConexion
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.SQLException


class
Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnRegistro : Button = findViewById(R.id.btnRegistrarseLogin)

        val txtCorreoOrName : EditText = findViewById(R.id.txtCorreoOrNameLogin)
        val txtContrasena : EditText = findViewById(R.id.txtContrasenaLogin)
        val btnLogin : Button = findViewById(R.id.btnIniciarSesion)


        btnLogin.setOnClickListener {
            val ScreenMain = Intent(this, MainActivity::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val objConnection = ClaseConexion().cadenaConexion()
                    val verification = objConnection?.prepareStatement("SELECT * FROM Trabajador WHERE correo = ?  AND Contrasena = ?")!!


                    verification.setString(1, txtCorreoOrName.text.toString())
                    verification.setString(2, txtContrasena.text.toString())

                    val result = verification.executeQuery()

                    if (result.next()) {
                        startActivity(ScreenMain)
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@Login,
                                "Correo o contraseña incorrectos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: SQLException) {
                    runOnUiThread {
                        Toast.makeText(
                            this@Login,
                            "Error en la conexión a la base de datos: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@Login,
                            "Ocurrió un error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


        btnRegistro.setOnClickListener{
            val pantallaRegistro = Intent(this,Register::class.java)
            startActivity(pantallaRegistro)
        }
    }
}