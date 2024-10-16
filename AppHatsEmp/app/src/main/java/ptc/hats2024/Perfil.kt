package ptc.hats2024

import DataList.PerfilTrabajador
import Modelo.ClaseConexion
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
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

        // Función para obtener los datos del perfil del usuario
        fun PerfilUser(): List<PerfilTrabajador> {
            val listaTrabajador = mutableListOf<PerfilTrabajador>()
            val objConexion: Connection? = ClaseConexion().CadenaConexion()

            // Verifica que la conexión no sea nula
            if (objConexion == null) {
                Toast.makeText(context, "Error al conectar a la base de datos", Toast.LENGTH_SHORT).show()
                return listaTrabajador
            }

            try {
                // Preparamos la consulta usando una sentencia preparada para evitar inyecciones SQL
                val query = "SELECT * FROM Trabajador WHERE correo = ?"
                val preparedStatement: PreparedStatement? = objConexion.prepareStatement(query)

                preparedStatement?.setString(1, CorreoGlobal) // Usamos la variable global de correo

                // Ejecutamos la consulta
                val resultSet: ResultSet? = preparedStatement?.executeQuery()

                // Verificamos si hay resultados
                if (resultSet != null && resultSet.next()) { // Verificamos si hay al menos un resultado
                    do {
                        val nombre = resultSet.getString("nombre")
                        val correo = resultSet.getString("correo")
                        val direccion = resultSet.getString("direccion")
                        val telefono = resultSet.getString("telefono")
                        val servicio = resultSet.getString("servicios")
                        val nombrePerfil = resultSet.getString("nombrePerfil")
                        val imagenPerfil = resultSet.getString("fotoPerfilUrl") // Corregimos el nombre de la columna

                        val perfilTrabajador = PerfilTrabajador(nombre, correo, direccion, telefono, servicio, nombrePerfil, imagenPerfil)
                        listaTrabajador.add(perfilTrabajador)
                    } while (resultSet.next()) // Si hay más de un resultado, lo agregamos a la lista
                } else {
                    Toast.makeText(context, "No se encontraron datos para el correo $CorreoGlobal", Toast.LENGTH_SHORT).show()
                }

            } catch (e: SQLException) {
                e.printStackTrace()
                Toast.makeText(context, "Error SQL: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error al obtener los datos del perfil: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                try {
                    objConexion?.close() // Cerramos la conexión al final
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }

            return listaTrabajador
        }

        // Cargamos los datos del perfil
        val perfilTrabajadorLista = PerfilUser()
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

            val imagenPerfil = root.findViewById<ImageView>(R.id.imgPerfilUser)

            Glide.with(root.context)
                .load(perfilTrabajador.fotoPerfilUrl)
                .placeholder(R.drawable.perfil_icon)
                .error(R.drawable.perfil_icon)
                .into(imagenPerfil)
        } else {
            Toast.makeText(context, "No se encontraron datos del trabajador", Toast.LENGTH_SHORT).show()
        }

        return root
    }
}
