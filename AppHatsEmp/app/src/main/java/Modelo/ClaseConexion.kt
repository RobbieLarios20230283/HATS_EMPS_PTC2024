package Modelo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun CadenaConexion(): Connection? {
            try {
                val url = "jdbc:oracle:thin:@192.168.0.3:1521:xe"
                val usuario = "system"
                val contrasena = "1234567"

                val connection = DriverManager.getConnection(url, usuario, contrasena)
                return connection
            } catch (e: Exception) {
                println("ERROR: $e")
                return null
            }
        }
}