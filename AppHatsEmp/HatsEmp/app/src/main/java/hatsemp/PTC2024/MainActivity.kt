package hatsemp.PTC2024

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val txtEmail : EditText = findViewById(R.id.txtEmail)
        val txtPasswor : EditText = findViewById(R.id.txtContraseña)
        val btnRegistro : Button = findViewById(R.id.btnRegistro)
        val btnLogin : Button = findViewById(R.id.btnLog_in)

        btnRegistro.setOnClickListener{
            val pantallaRegistro = Intent(this,Registro::class.java)
            startActivity(pantallaRegistro)
        }
    }
}