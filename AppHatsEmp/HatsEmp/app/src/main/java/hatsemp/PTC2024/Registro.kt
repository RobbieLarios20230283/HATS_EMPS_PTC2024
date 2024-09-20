package hatsemp.PTC2024

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.UUID

class Registro : AppCompatActivity() {
    val codig_opcion_Tomar_Foto = 103
    val codig_opcion_Documento = 102
    val CAMERA_REQUEST_CODE = 0
    val STORAGE_REQUEST_CODE = 1

    lateinit var ImgView : ImageView
    lateinit var myPath : String
    lateinit var txtEmail : EditText
    lateinit var txtPass : EditText

    val uuid = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ImgView = findViewById(R.id.ImgUser)
        val ImgDui : ImageView = findViewById(R.id.ImgDui)
        val btnTomarFot : Button = findViewById(R.id.btnTomarFoto)
        val btnTomarFotDui : Button = findViewById(R.id.btnTomarFotoDui)
        val btnsubirDoc : Button = findViewById(R.id.btnIngresarPdf)

        txtEmail = findViewById(R.id.txtEmail)
        txtPass = findViewById(R.id.txtPassword)
    }
}