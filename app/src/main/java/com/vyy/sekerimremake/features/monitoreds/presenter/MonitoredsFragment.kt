package com.vyy.sekerimremake.features.monitoreds.presenter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyy.sekerimremake.MainViewModel
import com.vyy.sekerimremake.R
import com.vyy.sekerimremake.databinding.FragmentMonitoredsBinding
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
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
class MonitoredsFragment : Fragment(), ChartAdapter.OnDayClickListener {

    private var _binding: FragmentMonitoredsBinding? = null
    private val binding get() = _binding!!

    private val viewModelMain: MainViewModel by activityViewModels()
    private val viewModelMonitoreds: MonitoredsViewModel by viewModels()



    private val listAdapter: MonitoredsAdapter by lazy {
        MonitoredsAdapter { position: Int ->
            onMonitoredClicked(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoredsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMonitoredsRecyclerView()
        initChartRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelMain.userResponse.collectLatest { response ->
                        when (response) {
                            is Response.Success -> {
                                val monitoredList = response.data?.monitoreds
                                    ?.filter { monitored ->
                                        monitored[NAME] != null && monitored[UID] != null
                                    }
                                    ?.map { monitored ->
                                        MonitoredModel(
                                            uid = monitored[UID],
                                            name = monitored[NAME],
                                            userId = monitored[USER_ID]
                                        )
                                    }
                                monitoredList.let {
                                    listAdapter.submitList(monitoredList)
                                }
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
                    viewModelMain.chartResponse.collectLatest { response ->
                        when (response) {
                            is Response.Success -> {
                                val chartAdapter = ChartAdapter(
                                    requireContext(), response.data, this@MonitoredsFragment
                                )

                                with(binding.recyclerViewMonitoredsChart) {
                                    this.adapter = chartAdapter
                                    if (chartAdapter.itemCount > 1) {
                                        this.scrollToPosition(chartAdapter.itemCount - 1)
                                    }
                                }
                            }
                            is Response.Error -> {
                                Log.d("MonitoredsFragment", response.message)
                            }
                            else -> {
                                //TODO
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
                adapter = listAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
    }

    private fun initChartRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val layoutAnimationController = AnimationUtils.loadLayoutAnimation(
            requireContext(), R.anim.recyclerview_layout_animation
        )
        binding.recyclerViewMonitoredsChart.apply {
            layoutManager = linearLayoutManager
            layoutAnimation = layoutAnimationController
        }
    }

    private fun onMonitoredClicked(position: Int) {
        if (listAdapter.selectedPosition == position) {
            return
        }
        binding.progressBarMonitoredsChart.visibility = View.VISIBLE
        listAdapter.selectMonitored(position)
        listAdapter.currentList[position].uid?.let {
            viewModelMonitoreds.getChart(it)
        }
    }

    override fun onRowClick(day: ChartDayModel?, view: View?) {
        Toast.makeText(requireContext(), "You can not change monitored values.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
