package ptc.hats2024

import Modelo.ClaseConexion
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.sql.SQLException
import java.util.UUID

class ingreso_de_datos : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 0
    private val CAMERA_REQUEST_CODE_DUI = 1
    private var isDuiCapture = false

    private lateinit var correo: String
    private lateinit var contraseña: String
    private lateinit var nombre: String
    private lateinit var direccion: String

    private lateinit var miPath: String
    private lateinit var ImgPerfil: ImageView
    private lateinit var ImgDui: ImageView
    private lateinit var txtNombrePerfil: EditText
    private lateinit var txtAreaTrabajo: EditText
    private lateinit var txtEdad: EditText
    private lateinit var txtNumeroTelefono: EditText

    val codigo_opcion_pdf = 104
    private val uuid = UUID.randomUUID().toString()
    private lateinit var pdfUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ingreso_de_datos)

        correo = intent.getStringExtra("correo") ?: "Correo no proporcionado"
        contraseña = intent.getStringExtra("contrasena") ?: "Contraseña no proporcionada"
        nombre = intent.getStringExtra("nombreCompleto") ?: "Nombre no proporcionado"
        direccion = intent.getStringExtra("direccion") ?: "Dirección no proporcionada"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ImgPerfil = findViewById(R.id.imgPerfil)
        ImgDui = findViewById(R.id.imgDui)
        txtNombrePerfil = findViewById(R.id.txtNombrePerfil)
        txtAreaTrabajo = findViewById(R.id.txtAreaTrabajo)
        txtEdad = findViewById(R.id.txtEdad)
        txtNumeroTelefono = findViewById(R.id.txtNumeroTelefono)

        val bntTomarFoto: Button = findViewById(R.id.btnTomarFoto)
        val btnRegistrarse: Button = findViewById(R.id.btnRegistrarse)
        val btnPdf: Button = findViewById(R.id.btnPDF)
        val btnTomarFotoDui: Button = findViewById(R.id.btnTomarFotoDui)

        bntTomarFoto.setOnClickListener {
            isDuiCapture = false
            checkCameraPermission(false)
        }
        btnTomarFotoDui.setOnClickListener {
            isDuiCapture = true
            checkCameraPermission(true)
        }

        btnPdf.setOnClickListener {
            SeleccionarPDF()
        }
        btnRegistrarse.setOnClickListener {
            val areaTrabajo = txtAreaTrabajo.text.toString().trim()
            val edad = txtEdad.text.toString().trim()
            val numeroTelefono = txtNumeroTelefono.text.toString().trim()
            val nombrePerfil = txtNombrePerfil.text.toString().trim()

            if (areaTrabajo.isNotEmpty() && edad.isNotEmpty() && numeroTelefono.isNotEmpty() && nombrePerfil.isNotEmpty()) {
                GuardarInformacion(pdfUri.toString(), miPath, miPath)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun GuardarInformacion(pdfUrl: String, imgDui: String, imgPerfil: String) {
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val objConnection = ClaseConexion().cadenaConexion()
                val statement = objConnection?.prepareStatement("INSERT INTO Trabajador (uuidTrabajador, nombreTrabajador, Telefono, Correo, Antecedentes, Servicios, DuiTrabajador, Direccion, FotoPerfil, NombrePerfil, ContraseNa, FechadeNacimiento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")!!
                statement.setString(1, uuid)
                statement.setString(2, nombre)
                statement.setString(3, txtNumeroTelefono.text.toString())
                statement.setString(4, correo)
                statement.setString(5, pdfUrl)
                statement.setString(6, txtAreaTrabajo.text.toString())
                statement.setString(7, imgDui)
                statement.setString(8, direccion)
                statement.setString(9, imgPerfil)
                statement.setString(10, txtNombrePerfil.text.toString())
                statement.setString(11, contraseña)
                statement.setString(12, txtEdad.text.toString())
                statement.executeUpdate()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ingreso_de_datos, "Información guardada correctamente", Toast.LENGTH_SHORT).show()
                    txtEdad.text.clear()
                    txtAreaTrabajo.text.clear()
                    txtNombrePerfil.text.clear()
                    txtNumeroTelefono.text.clear()
                    ImgDui.tag = null
                    ImgPerfil.tag = null
                    pdfUri = Uri.EMPTY
                }
            }
        } catch (e: SQLException) {
                Toast.makeText(this, "Error al guardar la información: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }





    private fun SeleccionarPDF() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(Intent.createChooser(intent, "Seleccionar un PDF"), codigo_opcion_pdf)
    }

    private fun checkCameraPermission(isDui: Boolean) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            pedirPermisoCamara(isDui)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (isDui) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE_DUI)
            } else {
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
        }
    }

    private fun pedirPermisoCamara(isDui: Boolean) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            // Aquí puedes mostrar un mensaje explicando por qué necesitas el permiso
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA), if (isDui) CAMERA_REQUEST_CODE_DUI else CAMERA_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            when (requestCode) {
                CAMERA_REQUEST_CODE -> startActivityForResult(intent, CAMERA_REQUEST_CODE)
                CAMERA_REQUEST_CODE_DUI -> startActivityForResult(intent, CAMERA_REQUEST_CODE_DUI)
            }
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

                when (requestCode) {
                    CAMERA_REQUEST_CODE -> {
                        val imageBitmap = data?.extras?.get("data") as? Bitmap
                        imageBitmap?.let {
                            subirImagenFirebase(it) { url ->
                                miPath = url
                                ImgPerfil.setImageBitmap(it)
                            }
                        }
                    }
                    CAMERA_REQUEST_CODE_DUI -> {
                        val imageBitmap = data?.extras?.get("data") as? Bitmap
                        imageBitmap?.let {
                            subirImagenDuiFirebase(it) { urlDui ->
                                miPath = urlDui
                                ImgDui.setImageBitmap(it)
                            }
                        }
                    }
                    codigo_opcion_pdf -> {
                        pdfUri = data?.data!!
                        subirPDFfirabase(pdfUri)
                    }
                }
        }
    }

    private fun subirImagenFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("Images/${uuid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)


        uploadTask.addOnFailureListener {
            Toast.makeText(this@ingreso_de_datos, "Error al subir la imagen", Toast.LENGTH_SHORT).show()

        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
    }
    private fun subirImagenDuiFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val imageDuiRef = storageRef.child("ImagesDui/${uuid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadDuiTask = imageDuiRef.putBytes(data)


        uploadDuiTask.addOnFailureListener {
            Toast.makeText(this@ingreso_de_datos, "Error al subir la imagen", Toast.LENGTH_SHORT).show()

        }.addOnSuccessListener { taskSnapshot ->
            imageDuiRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
    }

    private fun subirPDFfirabase(pdfUri: Uri) {
        val storageRef = Firebase.storage.reference
        val pdfRef = storageRef.child("PDF/${UUID.randomUUID()}.pdf")

        val txtPdfUrl : TextView = findViewById(R.id.txtUrlPdf)

        pdfRef.putFile(pdfUri)
            .addOnSuccessListener {
                pdfRef.downloadUrl.addOnSuccessListener { uri ->
                    Toast.makeText(
                        this,
                        "PDF subido correctamente: ${uri.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    txtPdfUrl.text = uri.toString()

                    println("URL del PDF: $uri")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al subir el PDF", Toast.LENGTH_SHORT).show()
                println("Error al subir el PDF: $it")
            }
    }

}