package ptc.hats2024

import Modelo.ClaseConexion
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
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
    private lateinit var txtFechaNacimiento: EditText
    private lateinit var txtNumeroTelefono: EditText

    private val codigo_opcion_pdf = 104
    private val uuid = UUID.randomUUID().toString()
    private lateinit var uuidTrabajador : String
    private lateinit var pdfUri: Uri

    // Variables para almacenar las URLs de Firebase
    private lateinit var perfilUrl: String
    private lateinit var duiUrl: String
    private lateinit var pdfUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso_de_datos)

        // Obtener datos del Intent
        correo = intent.extras?.getString("correo").orEmpty()
        contraseña = intent.extras?.getString("contrasena").orEmpty()
        nombre = intent.extras?.getString("nombreCompleto").orEmpty()
        direccion = intent.extras?.getString("direccion").orEmpty()

        // Configuración de UI
        ImgPerfil = findViewById(R.id.imgPerfil)
        ImgDui = findViewById(R.id.imgDui)
        txtNombrePerfil = findViewById(R.id.txtNombrePerfil)
        txtAreaTrabajo = findViewById(R.id.txtAreaTrabajo)
        txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento)
        txtNumeroTelefono = findViewById(R.id.txtNumeroTelefono)

        val btnTomarFoto: Button = findViewById(R.id.btnTomarFoto)
        val btnRegistrarse: Button = findViewById(R.id.btnRegistrarse)
        val btnPdf: Button = findViewById(R.id.btnPDF)
        val btnTomarFotoDui: Button = findViewById(R.id.btnTomarFotoDui)

        uuidTrabajador = intent.getStringExtra("uuidTrabajador") ?: ""

        if (uuidTrabajador.isEmpty()) {
            Toast.makeText(this, "UUID del usuario no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        btnTomarFoto.setOnClickListener {
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
            val areaTrabajo : String = txtAreaTrabajo.text.toString()
            val edad : String = txtFechaNacimiento.text.toString()
            val numeroTelefono : String = txtNumeroTelefono.text.toString()
            val nombrePerfil : String = txtNombrePerfil.text.toString()

            if (areaTrabajo.isNotEmpty() && edad.isNotEmpty() && numeroTelefono.isNotEmpty() && nombrePerfil.isNotEmpty()) {
                GuardarInformacion(duiUrl, perfilUrl)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun GuardarInformacion(imgDuiUri: String?, imgPerfilUri: String?) {
        if(imgDuiUri.isNullOrEmpty() || imgPerfilUri.isNullOrEmpty()) {
            runOnUiThread {
                Toast.makeText(this, "Error: No se ha proporcionado todas las imágenes o PDF", Toast.LENGTH_SHORT).show()
            }
            return}

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConnection = ClaseConexion().cadenaConexion()
                if (objConnection != null) {
                    val statement = objConnection.prepareStatement(
                        """
                       UPDATE Trabajador 
                       SET 
                       telefono = ?, 
                       servicios = ?, 
                       nombrePerfil = ?, 
                       fechadeNacimiento = ?, 
                       duiTrabajadorUrl = ?, 
                       fotoPerfilUrl = ? 
                       WHERE uuidTrabajador = ?
                       """)!!

                    statement.setString(1, txtNumeroTelefono.text.toString())
                    statement.setString(2, txtAreaTrabajo.text.toString())
                    statement.setString(3, txtNombrePerfil.text.toString())
                    statement.setString(4, txtFechaNacimiento.text.toString())
                    statement.setString(5, duiUrl)
                    statement.setString(6, perfilUrl)
                    statement.setString(7, uuidTrabajador)

                    statement.executeUpdate()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ingreso_de_datos, "Información guardada correctamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ingreso_de_datos, Login::class.java)
                        startActivity(intent)
                        limpiarCampos()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ingreso_de_datos, "Error de conexión a la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SQLException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ingreso_de_datos, "Error al guardar la información: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ingreso_de_datos, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun limpiarCampos() {

        txtFechaNacimiento.text.clear()
        txtAreaTrabajo.text.clear()
        txtNombrePerfil.text.clear()
        txtNumeroTelefono.text.clear()
        ImgDui.setImageResource(0)
        ImgPerfil.setImageResource(0)
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
            // Mostrar mensaje explicativo si es necesario
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA), if (isDui) CAMERA_REQUEST_CODE_DUI else CAMERA_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
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
                            perfilUrl = url
                            ImgPerfil.setImageBitmap(it)
                        }
                    }
                }

                CAMERA_REQUEST_CODE_DUI -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        subirImagenDuiFirebase(it) { url ->
                            duiUrl = url
                            ImgDui.setImageBitmap(it)
                        }
                    }
                }

                codigo_opcion_pdf -> {
                    pdfUri = data?.data ?: return
                    subirPDFfirabase(pdfUri) { url ->
                        pdfUrl = url
                    }
                }
            }
        }
    }

    private fun subirImagenFirebase(bitmap: Bitmap, onComplete: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("Images/${uuid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                onComplete(downloadUri)
            }
        }
    }

    private fun subirImagenDuiFirebase(bitmap: Bitmap, onComplete: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("ImagesDui/${uuid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                onComplete(downloadUri)
            }
        }
    }

    private fun subirPDFfirabase(uri: Uri, onComplete: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("PDF/${uuid}.pdf")
        val uploadTask = storageRef.putFile(uri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                onComplete(downloadUri)
                Toast.makeText(this, "PDF subido correctamente", Toast.LENGTH_SHORT).show() // Mensaje de éxito
            } else {
                Toast.makeText(this, "Error al subir el PDF", Toast.LENGTH_SHORT).show() // Mensaje de error
            }
        }
    }

}
