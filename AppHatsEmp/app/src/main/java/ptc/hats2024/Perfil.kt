package ptc.hats2024

import DataList.PerfilTrabajador
import Modelo.ClaseConexion
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import ptc.hats2024.Login.VaraibleGlobal.CorreoGlobal

class Perfil : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_perfil, container, false)

        fun PerfilUser(): List<PerfilTrabajador> {
            val Correo = CorreoGlobal
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()

            val resultSet = statement?.executeQuery("SELECT * FROM Trabajador WHERE correo = '$Correo'")
            val listaTrabajador = mutableListOf<PerfilTrabajador>()

            if (resultSet != null) {
                while (resultSet.next()) {
                    val nombre = resultSet.getString("nombre")
                    val correo = resultSet.getString("correo")
                    val direccion = resultSet.getString("direccion")
                    val telefono = resultSet.getString("telefono")
                    val servicio = resultSet.getString("servicio")
                    val nombrePerfil = resultSet.getString("nombrePerfil")
                    val imagenPerfil = resultSet.getString("imagenPerfil")

                    val perfilTrabajador = PerfilTrabajador(nombre, correo, direccion, telefono, servicio, nombrePerfil, imagenPerfil)
                    listaTrabajador.add(perfilTrabajador)

                }
            }
            return listaTrabajador
        }
        val perfilTrabajadorLista = PerfilUser()
        if (perfilTrabajadorLista.isNotEmpty()){
            val perfilTrabajador = perfilTrabajadorLista[0]

            val nombre = root.findViewById<EditText>(R.id.editTextNombre)
            nombre.setText(perfilTrabajador.nombre)

            val correo = root.findViewById<EditText>(R.id.editTextEmail)
            correo.setText(perfilTrabajador.correo)

            val direccion = root.findViewById<EditText>(R.id.editTextDireccion)
            direccion.setText(perfilTrabajador.direccion)

            val telefono = root.findViewById<EditText>(R.id.editTextTelefono)
            telefono.setText(perfilTrabajador.telefono)

            val servicios = root.findViewById<EditText>(R.id.editTextServicios)
            servicios.setText(perfilTrabajador.servicio)

            val nombrePerfil = root.findViewById<EditText>(R.id.editTextPerfil)
            nombrePerfil.setText(perfilTrabajador.nombrePerfil)

            val imagenPerfil = root.findViewById<TextView>(R.id.imageView17)
            imagenPerfil.text = perfilTrabajador.imagenPerfil

        }
        return root
    }

}