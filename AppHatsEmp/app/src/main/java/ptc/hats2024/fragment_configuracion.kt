package ptc.hats2024

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_configuracion.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_configuracion : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_configuracion, container, false)

        val editarDatosPer = root.findViewById<Button>(R.id.button6)
        val notificaciones = root.findViewById<Button>(R.id.button10)
        val infoLegal = root.findViewById<Button>(R.id.button11)
        val asistencia = root.findViewById<Button>(R.id.button12)

        editarDatosPer.setOnClickListener{
            val editarDatos = Intent(requireContext(),perfil::class.java)
            startActivity(editarDatos)
        }

        notificaciones.setOnClickListener{
            val notifi = Intent(requireContext(), fragment_notificaciones::class.java)
            startActivity(notifi)
        }

        infoLegal.setOnClickListener{
            val legal = Intent(requireContext(), fragment_infolegal::class.java)
            startActivity(legal)
        }

        asistencia.setOnClickListener{
            val asis = Intent(requireContext(), fragment_asistencia::class.java)
            startActivity(asis)
        }

        return root
    }
}