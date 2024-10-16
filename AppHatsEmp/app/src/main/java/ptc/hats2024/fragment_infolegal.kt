package ptc.hats2024

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_infolegal.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_infolegal : Fragment() {
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

        val root = inflater.inflate(R.layout.fragment_infolegal, container, false)

        val terminosCondiciones: Button = root.findViewById(R.id.button3)
        val terminos_Pagos: Button = root.findViewById(R.id.button4)
        val Politicas: Button = root.findViewById(R.id.button5)
        val regresarrrrr: Button = root.findViewById(R.id.btnRegresar)

        terminosCondiciones.setOnClickListener{
            findNavController().navigate(R.id.terminos_Condiciones)
        }

        terminos_Pagos.setOnClickListener{
            findNavController().navigate(R.id.terminos_Pago)
        }

        Politicas.setOnClickListener{
            findNavController().navigate(R.id.politicas)
        }

        regresarrrrr.setOnClickListener{
            findNavController().navigate(R.id.fragment_configuracion)
        }
        return root

    }
}