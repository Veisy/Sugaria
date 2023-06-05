package com.vyy.sekerimremake.features.chart.presenter.master

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyy.sekerimremake.MainViewModel
import com.vyy.sekerimremake.databinding.FragmentChartBinding
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ChartMasterFragment : Fragment() {
    //TODO: BaseFragment for binding.
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    private val viewModelMain: MainViewModel by activityViewModels()

    private var scrollPosition: Int = 0

    private val listAdapter: ChartAdapter by lazy {
        ChartAdapter { chartDayModel, chartDayView ->
            onRowClick(chartDayModel, chartDayView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        //TODO: scrollPosition seems not working.
        scrollPosition =
            savedInstanceState?.getInt("scrollPosition") ?: (arguments?.getInt("scrollPosition")
                ?: 0)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setFloatingActionButton()
        viewModelMain.resetChartResponse()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMain.chartResponse.collectLatest { response ->
                    when (response) {
                        is Response.Success -> {
                            if (response.data.isNotEmpty()) {
                                binding.recyclerViewChart.apply {
                                    scheduleLayoutAnimation()
                                    listAdapter.submitList(response.data)
                                    if (scrollPosition != 0) {
                                        scrollToPosition(scrollPosition)
                                    }
                                }
                            }
                        }
                        is Response.Error -> {
                            listAdapter.submitList(null)
                            Log.d("ChartMasterFragment", response.message)
                        }
                        else -> {
                            listAdapter.submitList(null)
                            //TODO
                        }
                    }
                }

            }
        }
    }

    //TODO: Revisit
    //TODO: Loading Dialog
    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.apply {
            recyclerViewChart.apply {
                adapter = listAdapter
                layoutManager = linearLayoutManager
                setHasFixedSize(true)
            }
        }
    }

    private fun setFloatingActionButton() {
        binding.floatingActionButtonAddRow.setOnClickListener {
            val chartResponse = viewModelMain.chartResponse.value
            if (chartResponse is Response.Success) {
                val calendar = Calendar.getInstance()
                val action =
                    ChartMasterFragmentDirections.actionChartMasterFragmentToChartDetailsFragment(
                        day = calendar[Calendar.DAY_OF_MONTH].toString(),
                        month = calendar[Calendar.MONTH].toString(),
                        year = calendar[Calendar.YEAR].toString()
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun onRowClick(day: ChartDayModel, view: View?) {
        if (view != null && view.transitionName != null) {
            val extras = FragmentNavigatorExtras(Pair(view, view.transitionName))

            setEnterSharedElementCallback(object : androidx.core.app.SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>, sharedElements: MutableMap<String, View>
                ) {
                    super.onMapSharedElements(names, sharedElements)
                    sharedElements[view.transitionName] = view
                }
            })
            val action =
                ChartMasterFragmentDirections.actionChartMasterFragmentToChartDetailsFragment(
                    day = day.dayOfMonth.toString(),
                    month = day.month.toString(),
                    year = day.year.toString()
                )
            findNavController().navigate(
                action, extras
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("scrollPosition", scrollPosition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}