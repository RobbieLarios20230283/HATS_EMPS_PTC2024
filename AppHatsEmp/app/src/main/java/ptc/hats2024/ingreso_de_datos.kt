package ptc.hats2024

import DataList.DataListServicio
import Modelo.ClaseConexion
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*

class ingreso_de_datos : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 0
    private val CAMERA_REQUEST_CODE_DUI = 1
    private var isDuiCapture = false

    private var selectedService: DataListServicio? = null


    private lateinit var miPath: String
    private lateinit var ImgPerfil: ImageView
    private lateinit var ImgDui: ImageView
    private lateinit var txtNombrePerfil: EditText
    private lateinit var txtAreaTrabajo: AutoCompleteTextView
    private lateinit var txtServicio: EditText
    private lateinit var txtFechaNacimiento: EditText
    private lateinit var txtNumeroTelefono: EditText

    private val codigo_opcion_pdf = 104
    private val uuid = UUID.randomUUID().toString()
    private lateinit var uuidTrabajador: String
    private lateinit var pdfUri: Uri

    // Variables para almacenar las URLs de Firebase
    private var perfilUrl: String = ""
    private var duiUrl: String = ""





    private lateinit var pdfUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso_de_datos)

        txtAreaTrabajo = findViewById(R.id.txtAreaTrabajo)

        // Lista del catálogo
        val catalogoItems = listOf(
            "Carpintería",
            "Pintura",
            "Electricidad",
            "Mecánica",
            "Fontanería",
            "Limpieza",
            "Cerrajería",
            "Planchado"
        )


        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, catalogoItems)
        txtAreaTrabajo.setAdapter(adapter)



        // Configuración de UI
        ImgPerfil = findViewById(R.id.imgPerfil)
        ImgDui = findViewById(R.id.imgDui)



        txtNombrePerfil = findViewById(R.id.txtNombrePerfilLog)
        txtServicio = findViewById(R.id.txtServicioLog)
        txtFechaNacimiento = findViewById(R.id.txtFechaNacimientoReg)
        txtNumeroTelefono = findViewById(R.id.txtNumeroTelefonoReg)

        val btnTomarFoto: Button = findViewById(R.id.btnTomarFoto)
        val btnRegistrarse: Button = findViewById(R.id.btnRegistrarse)
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


        btnRegistrarse.setOnClickListener {
            val areaTrabajo = txtAreaTrabajo.text.toString().trim()
            val FechaNacimiento = txtFechaNacimiento.text.toString().trim()
            val numeroTelefono = txtNumeroTelefono.text.toString().trim()
            val nombrePerfil = txtNombrePerfil.text.toString().trim()
            val servicios = txtServicio.text.toString().trim()

            try {

                if (areaTrabajo == "Selecciona un servicio") {
                    Toast.makeText(this, "Por favor, selecciona un servicio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Verificar que las URLs no estén vacías
                if (perfilUrl.isEmpty() || duiUrl.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Por favor, tome una foto para el perfil y del DUI.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Verificar que todos los campos estén llenos
                if (FechaNacimiento.isNotEmpty() && numeroTelefono.isNotEmpty() && nombrePerfil.isNotEmpty() && servicios.isNotEmpty()) {
                    // Verificar si la fecha de nacimiento es válida antes de guardar
                    if (isValidDate(FechaNacimiento)) {
                        GuardarInformacion(
                            numeroTelefono,
                            servicios,
                            nombrePerfil,
                            FechaNacimiento,
                            duiUrl,
                            perfilUrl
                        )
                    } else {
                        Toast.makeText(
                            this,
                            "Fecha de nacimiento inválida. Por favor, use el formato YYYY-MM-DD.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                // Manejar cualquier excepción que pueda ocurrir
                Toast.makeText(this, "Ha ocurrido un error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }




        txtNumeroTelefono.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) {
                    return
                }

                var input = s.toString().replace("-", "") // Quitar el guion si ya existe
                if (input.length > 8) {
                    input = input.substring(0, 8) // Limitar a 8 dígitos
                }

                isUpdating = true

                val formattedPhone = when (input.length) {
                    in 5..8 -> "${input.substring(0, 4)}-${input.substring(4)}" // Formato 7989-1230
                    else -> input // No aplicar formato aún
                }

                txtNumeroTelefono.setText(formattedPhone)
                txtNumeroTelefono.setSelection(formattedPhone.length) // Mover el cursor al final del texto

                isUpdating = false
            }
        })

        // Añadir TextWatcher para formato automático de fecha con guiones
        txtFechaNacimiento.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val dateFormat = "yyyy-MM-dd"

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) {
                    return
                }

                val input = s.toString().replace("-", "") // Elimina los guiones si ya existen
                if (input.length > 8) {
                    return // No permitir más de 8 dígitos (YYYYMMDD)
                }

                isUpdating = true

                val formattedDate = when (input.length) {
                    in 5..6 -> "${input.substring(0, 4)}-${input.substring(4)}" // YYYY-MM
                    in 7..8 -> "${input.substring(0, 4)}-${input.substring(4, 6)}-${
                        input.substring(
                            6
                        )
                    }" // YYYY-MM-DD
                    else -> input // Caso en el que aún no es necesario agregar guiones
                }

                // Establecer el texto formateado
                txtFechaNacimiento.setText(formattedDate)
                txtFechaNacimiento.setSelection(formattedDate.length) // Mover el cursor al final del texto

                isUpdating = false
            }
        })


        txtFechaNacimiento.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtered = s.toString()
                    .filter { it.isDigit() || it == '-' }
                if (s.toString() != filtered) {
                    txtFechaNacimiento.setText(filtered)
                    txtFechaNacimiento.setSelection(filtered.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }



        // Verificación de la fecha válida
        private fun isValidDate(date: String): Boolean {
            return try {
                // Preprocesar la fecha para que tenga el formato "yyyy-MM-dd"
                val parts = date.split("-")
                if (parts.size != 3) return false

                val year = parts[0].toIntOrNull() ?: return false
                val month = parts[1].toIntOrNull() ?: return false
                val day = parts[2].toIntOrNull() ?: return false

                // Crear un nuevo string con el formato "yyyy-MM-dd" agregando ceros a la izquierda si es necesario
                val formattedDate = String.format("%04d-%02d-%02d", year, month, day)

                // Validar la fecha usando SimpleDateFormat
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.isLenient = false
                val parsedDate = sdf.parse(formattedDate) ?: return false

                // Extraer el año, mes y día para validaciones adicionales
                val calendar = Calendar.getInstance().apply { time = parsedDate }
                val parsedYear = calendar.get(Calendar.YEAR)
                val parsedMonth = calendar.get(Calendar.MONTH) + 1 // Enero es 0
                val parsedDay = calendar.get(Calendar.DAY_OF_MONTH)

                // Validar el rango del año, mes y día
                parsedYear in 1961..2006 && parsedMonth == month && parsedDay == day
            } catch (e: Exception) {
                false
            }

        }
    // Obtener el número máximo de días del mes
    private fun getMaxDayOfMonth(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31 // Meses con 31 días
            4, 6, 9, 11 -> 30 // Meses con 30 días
            2 -> if (isLeapYear(year)) 29 else 28 // Febrero
            else -> 0 // Mes no válido
        }
    }

    // Verificar si el año es bisiesto
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun GuardarCatalogo(catalogo: String) {
        val objConnection = ClaseConexion().cadenaConexion()
        if (objConnection != null) {
            val statement = objConnection.prepareStatement(
                "Insert into tbservicios (uuidCatalogo) Values(?)"
            )
            statement.setString(1, catalogo)
            try {
                statement.executeUpdate()
                println("Catalogo $catalogo insertado con éxito.")
            } catch (e: SQLException) {
                e.printStackTrace()  //
            } finally {
                statement.close()
                objConnection.close()
            }
        }
        else {
            println("Error: No se pudo establecer la conexión a la base de datos.")
        }
    }
        private fun GuardarInformacion(numeroTelefono: String, servicios: String, nombrePerfil: String, FechaNacimiento: String,  imgDuiUri: String, imgPerfilUri: String) {

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

                    statement.setString(1, numeroTelefono)
                    statement.setString(2, servicios)
                    statement.setString(3, nombrePerfil)
                    statement.setString(4, FechaNacimiento)
                    statement.setString(5, imgDuiUri)
                    statement.setString(6, imgPerfilUri)
                    statement.setString(7, uuidTrabajador)

                    val resultado = statement.executeUpdate()

                    if (resultado > 0) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ingreso_de_datos, "Información guardada correctamente", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                            val intent = Intent(this@ingreso_de_datos, Login::class.java)
                            startActivity(intent)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ingreso_de_datos, "Error: No se pudo guardar la información", Toast.LENGTH_SHORT).show()
                        }
                    }
                    objConnection.close()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ingreso_de_datos, "Error en la conexión a la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SQLException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ingreso_de_datos, "Error al guardar información: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun limpiarCampos() {
        txtNombrePerfil.text.clear()
        txtServicio.text.clear()
        txtNumeroTelefono.text.clear()
        txtFechaNacimiento.text.clear()
    }

    private fun GuardarCatalogId(){

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
                CAMERA_REQUEST_CODE, CAMERA_REQUEST_CODE_DUI -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    if (imageBitmap != null) {
                        if (requestCode == CAMERA_REQUEST_CODE) {
                            subirImagenFirebase(imageBitmap) { url ->
                                perfilUrl = url
                                ImgPerfil.setImageBitmap(imageBitmap)
                            }
                        } else {
                            subirImagenDuiFirebase(imageBitmap) { url ->
                                duiUrl = url
                                ImgDui.setImageBitmap(imageBitmap)
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error al capturar la imagen", Toast.LENGTH_SHORT).show()
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

                // Mostrar Toast al completar la subida de la imagen
                Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
            } else {
                // Manejar el error si ocurre
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
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

                // Mostrar Toast al completar la subida de la imagen del DUI
                Toast.makeText(this, "Imagen de DUI subida correctamente", Toast.LENGTH_SHORT).show()
            } else {
                // Manejar el error si ocurre
                Toast.makeText(this, "Error al subir la imagen del DUI", Toast.LENGTH_SHORT).show()
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
    override fun onBackPressed() {

            if (txtNombrePerfil.text.isEmpty() || txtAreaTrabajo.text.isEmpty() ||
                txtFechaNacimiento.text.isEmpty() || txtNumeroTelefono.text.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos para salir", Toast.LENGTH_SHORT).show()
            } else {

                super.onBackPressed()
            }
    }
}

