package ptc.hats2024

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.UUID


class Register : AppCompatActivity() {
    val codig_opcion_Tomar_Foto = 103
    val codig_opcion_Documento = 102
    val CAMERA_REQUEST_CODE = 0
    val STORAGE_REQUEST_CODE = 1

    lateinit var ImgView : ImageView
    lateinit var myPath : String
    lateinit var txtEmail : EditText
    lateinit var txtPass : EditText
    lateinit var txtPdfDocumentName : TextView

    val uuid = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ImgView = findViewById(R.id.ImgUser)
        val ImgDui : ImageView = findViewById(R.id.ImgDui)
        val btnTomarFot : Button = findViewById(R.id.btnTomarFoto)
        val btnTomarFotDui : Button = findViewById(R.id.btnTomarFotoDui)
        val btnIncreasePdfDocument : Button = findViewById(R.id.btnIngresarPdf)
        val btnRegist : Button = findViewById(R.id.btnRegistrarse)


        val txtNumbers : EditText = findViewById(R.id.txtNum)
        val txtCompleteName : EditText = findViewById(R.id.txtName)
        val txtDirection : EditText = findViewById(R.id.txtDirec)
        val txtConfirmPassword : EditText = findViewById(R.id.txtConfirmarCon)


        txtEmail = findViewById(R.id.txtEmail)
        txtPass = findViewById(R.id.txtPassword)
        txtPdfDocumentName = findViewById(R.id.txtPdfName)

        btnTomarFot.setOnClickListener{
            checkCameraPermission()
        }
        btnTomarFotDui.setOnClickListener {
            checkCameraPermission()
        }
        btnIncreasePdfDocument.setOnClickListener {
            checkStoragePermission()
        }
    }
    private fun checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            pedirPermisoCamara()
        }
    }
    private fun checkStoragePermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            pedirPermisoAlmacenamiento()
        }
    }

    private fun pedirPermisoCamara(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)){

        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),CAMERA_REQUEST_CODE)
        }
    }
    private fun pedirPermisoAlmacenamiento(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){

        }else{
         ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),STORAGE_REQUEST_CODE)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            CAMERA_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, codig_opcion_Tomar_Foto)
                }else{
                    Toast.makeText(this, "Permiso de camara denegado", Toast.LENGTH_SHORT).show()
                }
            }
            STORAGE_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "application/pdf"
                    startActivityForResult(intent, codig_opcion_Documento)
                } else{
                    Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
                }
            }
            else ->{

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode){

                codig_opcion_Documento -> {
                    val docUri: Uri? = data?.data
                    docUri?.let { uri ->
                        val storageReference = FirebaseStorage.getInstance().reference.child("documents/${uri.lastPathSegment}")
                        val UploadTask = storageReference.putFile(uri)

                        UploadTask.addOnSuccessListener { taskSnapshot ->
                            storageReference.downloadUrl.addOnSuccessListener { url ->
                                myPath = url.toString()
                                val objUrl = uri.lastPathSegment
                                val txtUrl = objUrl.toString()
                                txtPdfDocumentName.text = txtUrl
                            }
                        }.addOnFailureListener{ exception ->
                            Toast.makeText(this, "Error al subir el documento: ${exception.message}",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}