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
<<<<<<< HEAD
=======
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
>>>>>>> ccf6e455b6bacdb55cb81e24a14d0dcaf2651108


fun encriptacionSHA256(contrasena: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(contrasena.toByteArray(StandardCharsets.UTF_8))
    val hexString = StringBuilder()
    for (byte in hash) {
        val hex = String.format("%02x", byte)
        hexString.append(hex)
    }
    return hexString.toString()
}

class Login : AppCompatActivity() {

    companion object VaraibleGlobal{
        lateinit var CorreoGlobal: String
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

<<<<<<< HEAD
        val txtCorreoOrName : EditText = findViewById(R.id.txtCorreoOrNameLogin)
        val txtContrasena : EditText = findViewById(R.id.txtContrasenaLogin)
        val btnLogin : Button = findViewById(R.id.btnIniciarSesion)
        val btnRegistro : Button = findViewById(R.id.btnRegistrarseLogin)
=======
        val btnRegistro: Button = findViewById(R.id.btnRegistrarseLogin)
        val txtCorreo: EditText = findViewById(R.id.txtCorreoOrNameLogin)
        val txtContrasena: EditText = findViewById(R.id.txtContrasenaLogin)
        val btnLogin: Button = findViewById(R.id.btnIniciarSesion)

        btnLogin.setOnClickListener {

            CorreoGlobal = txtCorreo.text.toString()

            val ScreenMain = Intent(this, MainActivity::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val objConnection = ClaseConexion().cadenaConexion()
>>>>>>> ccf6e455b6bacdb55cb81e24a14d0dcaf2651108

        btnLogin.setOnClickListener {
            val ScreenMain = Intent(this, MainActivity::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val objConnection = ClaseConexion().CadenaConexion()
                    val verification = objConnection?.prepareStatement("SELECT * FROM Trabajador WHERE (correo = ? OR nombrePerfil = ?) AND Contrasena = ?")!!

                    // Asignar los valores a los parámetros
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

                    val verification = objConnection?.prepareStatement(
                        "SELECT * FROM Trabajador WHERE correo = ? AND Contrasena = ?"
                    )!!


                    val contrasenaEncriptada = encriptacionSHA256(txtContrasena.text.toString())

                    // Pasar los valores al query
                    verification.setString(1, txtCorreo.text.toString())
                    verification.setString(2, contrasenaEncriptada)

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

        btnRegistro.setOnClickListener {
            val pantallaRegistro = Intent(this, Register::class.java)
            startActivity(pantallaRegistro)
        }
    }
}
