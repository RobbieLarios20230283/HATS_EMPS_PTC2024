package Modelo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {
<<<<<<< HEAD
    suspend fun CadenaConexion(): Connection? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "jdbc:oracle:thin:@192.168.0.6:1521:xe"
                val usuario = "system"
                val contrasena = "1234567"
                DriverManager.getConnection(url, usuario, contrasena)
            } catch (e: Exception) {
                println("Error en la cadena de conexión: $e")
                null
            }
=======

    fun cadenaConexion(): java.sql.Connection? {
        try {

            val ip = "jdbc:oracle:thin:@192.168.0.3:1521:xe"
            val usuario = "system"
            val contrasena = "1234567"

            val conexion = DriverManager.getConnection(ip, usuario, contrasena)
            return conexion

        }catch (e: Exception){
            println("El error es este: $e")
            return null
>>>>>>> ccf6e455b6bacdb55cb81e24a14d0dcaf2651108
        }
    }
}