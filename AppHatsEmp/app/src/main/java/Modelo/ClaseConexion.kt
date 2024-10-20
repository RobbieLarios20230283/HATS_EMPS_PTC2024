package Modelo

import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): java.sql.Connection? {
        try {

            val ip = "jdbc:oracle:thin:@192.168.144.227:1521:xe"
            val usuario = "system"
            val contrasena = "ITR2024"

            val conexion = DriverManager.getConnection(ip, usuario, contrasena)
            return conexion

        }catch (e: Exception){
            println("El error es este: $e")
            return null
        }
    }
}