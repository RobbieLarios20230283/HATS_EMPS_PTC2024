package ptc.hats2024

import DataList.PerfilTrabajador
import Modelo.ClaseConexion
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptc.hats2024.Login.VaraibleGlobal.CorreoGlobal
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class Perfil : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_perfil, container, false)

        val btnRegresar : Button = root.findViewById(R.id.btnRegresar)

        btnRegresar.setOnClickListener {
            val pantallaAnterior = Intent(requireContext(),fragment_configuracion::class.java)
            startActivity(pantallaAnterior)
        }

        // Corrutina para obtener los datos del perfil del usuario
        CoroutineScope(Dispatchers.Main).launch {
            val perfilTrabajadorLista = withContext(Dispatchers.IO) {
                PerfilUser()
            }

            if (perfilTrabajadorLista.isNotEmpty()) {
                val perfilTrabajador = perfilTrabajadorLista[0]

                val nombre = root.findViewById<TextView>(R.id.lblNombre)
                nombre.text = perfilTrabajador.nombre

                val correoUser = root.findViewById<TextView>(R.id.lblEmail)
                correoUser.text = perfilTrabajador.correo

                val direccion = root.findViewById<TextView>(R.id.lblDireccion)
                direccion.text = perfilTrabajador.direccion

                val telefono = root.findViewById<TextView>(R.id.lblTelefono)
                telefono.text = perfilTrabajador.telefono

                val servicios = root.findViewById<TextView>(R.id.lblServicios)
                servicios.text = perfilTrabajador.servicio

                val nombrePerfil = root.findViewById<TextView>(R.id.lblnombrePerfil)
                nombrePerfil.text = perfilTrabajador.nombrePerfil

                val fechaNacimiento = root.findViewById<TextView>(R.id.lblFechaNacimiento)
                fechaNacimiento.text = perfilTrabajador.fechadeNacimiento

                val imagenPerfil = root.findViewById<ImageView>(R.id.imgPerfilUser)

                Glide.with(root.context)
                    .load(perfilTrabajador.fotoPerfilUrl)
                    .placeholder(R.drawable.perfil_icon)
                    .error(R.drawable.perfil_icon)
                    .into(imagenPerfil)
            } else {
                Toast.makeText(context, "No se encontraron datos del trabajador", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    // Función suspendida para obtener los datos del perfil del usuario
    private suspend fun PerfilUser(): List<PerfilTrabajador> {
        val listaTrabajador = mutableListOf<PerfilTrabajador>()
        val objConexion: Connection? = ClaseConexion().cadenaConexion()

        // Verifica que la conexión no sea nula
        if (objConexion == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al conectar a la base de datos", Toast.LENGTH_SHORT).show()
            }
            return listaTrabajador
        }

        try {
            val query = "SELECT * FROM Trabajador WHERE correo = ?"
            val preparedStatement: PreparedStatement? = objConexion.prepareStatement(query)
            preparedStatement?.setString(1, CorreoGlobal) // Usamos la variable global de correo

            val resultSet: ResultSet? = preparedStatement?.executeQuery()

            if (resultSet != null && resultSet.next()) {
                do {
                    val nombre = resultSet.getString("nombre")
                    val correo = resultSet.getString("correo")
                    val direccion = resultSet.getString("direccion")
                    val telefono = resultSet.getString("telefono")
                    val servicio = resultSet.getString("servicios")
                    val nombrePerfil = resultSet.getString("nombrePerfil")
                    val fechaNacimiento = resultSet.getString("fechadeNacimiento")
                    val imagenPerfil = resultSet.getString("fotoPerfilUrl")

                    val perfilTrabajador = PerfilTrabajador(nombre, correo, direccion, telefono, servicio, nombrePerfil, fechaNacimiento, imagenPerfil)
                    listaTrabajador.add(perfilTrabajador)
                } while (resultSet.next())
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No se encontraron datos para el correo $CorreoGlobal", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: SQLException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error SQL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al obtener los datos del perfil: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } finally {
            try {
                objConexion?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return listaTrabajador
    }
}
