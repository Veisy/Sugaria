package com.vyy.sekerimremake.features.monitoreds.presenter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyy.sekerimremake.MainViewModel
import com.vyy.sekerimremake.databinding.FragmentMonitoredsBinding
import com.vyy.sekerimremake.features.chart.presenter.master.ChartAdapter
import com.vyy.sekerimremake.features.settings.domain.model.MonitoredModel
import com.vyy.sekerimremake.utils.FirestoreConstants.NAME
import com.vyy.sekerimremake.utils.FirestoreConstants.UID
import com.vyy.sekerimremake.utils.FirestoreConstants.USER_ID
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MonitoredsFragment : Fragment() {

    private var _binding: FragmentMonitoredsBinding? = null
    private val binding get() = _binding!!

    private val viewModelMain: MainViewModel by activityViewModels()
    private val viewModelMonitoreds: MonitoredsViewModel by viewModels()

    private val monitoredsAdapter: MonitoredsAdapter by lazy {
        MonitoredsAdapter { position: Int ->
            onMonitoredClicked(position)
        }
    }

    private val chartAdapter: ChartAdapter by lazy {
        ChartAdapter { _, _ ->
            onRowClick()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoredsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMonitoredsRecyclerView()
        initChartRecyclerView()
        viewModelMonitoreds.resetChartResponse()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelMain.userResponse.collectLatest { response ->
                        when (response) {
                            is Response.Success -> {
                                val monitoredList = response.data?.monitoreds?.filter { monitored ->
                                    monitored[NAME] != null && monitored[UID] != null
                                }?.map { monitored ->
                                    MonitoredModel(
                                        uid = monitored[UID],
                                        name = monitored[NAME],
                                        userId = monitored[USER_ID]
                                    )
                                }
                                monitoredsAdapter.submitList(monitoredList)
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
                launch {
                    viewModelMonitoreds.chartResponse.collectLatest { response ->
                        when (response) {
                            is Response.Success -> {
                                if (response.data.isNotEmpty()) {
                                    binding.recyclerViewMonitoredsChart.apply {
                                        chartAdapter.submitList(response.data)
                                        scheduleLayoutAnimation()
                                    }
                                }
                            }
                            is Response.Error -> {
                                chartAdapter.submitList(null)
                                Log.d("MonitoredsFragment", response.message)
                            }
                            else -> {
                                chartAdapter.submitList(null)
                            }
                        }
                        binding.progressBarMonitoredsChart.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun initMonitoredsRecyclerView() {
        binding.apply {
            recyclerViewMonitoreds.apply {
                adapter = monitoredsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
    }

    private fun initChartRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.apply {
            recyclerViewMonitoredsChart.apply {
                adapter = chartAdapter
                layoutManager = linearLayoutManager
                setHasFixedSize(true)
            }
        }
    }

    private fun onMonitoredClicked(position: Int) {
        if (monitoredsAdapter.selectedPosition == position) {
            return
        }
        binding.progressBarMonitoredsChart.visibility = View.VISIBLE
        monitoredsAdapter.selectMonitored(position)
        monitoredsAdapter.currentList[position].uid?.let {
            viewModelMonitoreds.getChart(it)
        }
    }

    private fun onRowClick() {
        Toast.makeText(requireContext(), "You can not change monitored values.", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
