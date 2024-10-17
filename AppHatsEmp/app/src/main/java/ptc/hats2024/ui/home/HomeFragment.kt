package ptc.hats2024.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ptc.hats2024.R
import ptc.hats2024.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn1s: Button = view.findViewById(R.id.button111)
        val btn2s: Button = view.findViewById(R.id.button222)
        val btn3s: Button = view.findViewById(R.id.button777)

        btn1s.setOnClickListener {


            findNavController().navigate(R.id.fragment_infolegal)
        }

        btn2s.setOnClickListener {


            findNavController().navigate(R.id.fragment_asistencia)
        }
        btn3s.setOnClickListener {

            findNavController().navigate(R.id.fragment_configuracion)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}