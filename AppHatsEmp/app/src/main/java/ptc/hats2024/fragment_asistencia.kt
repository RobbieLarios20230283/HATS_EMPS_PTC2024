package ptc.hats2024

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_asistencia.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_asistencia : Fragment() {
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
        val root = inflater.inflate(R.layout.fragment_asistencia, container, false)

        val preguntas: EditText = root.findViewById(R.id.textView15)
        val metodoPago: EditText = root.findViewById(R.id.textView18)
        val masInfo: EditText = root.findViewById(R.id.textView19)

        preguntas.setOnClickListener {
            findNavController().navigate(R.id.pregunta_frecuentes)
        }

        metodoPago.setOnClickListener{
            findNavController().navigate(R.id.terminos_Pago)
        }

        masInfo.setOnClickListener{
            findNavController().navigate(R.id.mas_informacion)
        }

        return root
    }

}