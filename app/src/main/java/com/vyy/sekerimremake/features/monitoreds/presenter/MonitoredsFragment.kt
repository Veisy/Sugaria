package com.vyy.sekerimremake.features.monitoreds.presenter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vyy.sekerimremake.MainViewModel
import com.vyy.sekerimremake.databinding.FragmentMonitoredsBinding
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MonitoredsFragment : Fragment() {

    private var _binding: FragmentMonitoredsBinding? = null
    private val binding get() = _binding!!

    private val viewModelMain: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoredsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMain.userResponse.collectLatest { response ->
                    when (response) {
                        is Response.Success -> {
                            binding.textView.text = response.data?.monitoreds.toString()
                        }
                        is Response.Error -> {
                            Log.d("MonitoredsFragment", response.message)
                        }
                        else -> {
                            //TODO
                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
