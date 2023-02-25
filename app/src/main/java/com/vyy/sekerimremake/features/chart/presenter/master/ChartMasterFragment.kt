package com.vyy.sekerimremake.features.chart.presenter.master

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyy.sekerimremake.R
import com.vyy.sekerimremake.databinding.FragmentChartBinding
import com.vyy.sekerimremake.features.chart.utils.ChartConstants.DAY_ID
import com.vyy.sekerimremake.features.chart.utils.ChartConstants.INITIAL_BUTTON_FLAG
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChartMasterFragment : Fragment(), ChartAdapter.OnDayClickListener {

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChartViewModel by viewModels()
    private var scrollPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        //TODO: scrollPosition seems not working.
        scrollPosition = savedInstanceState?.getInt("scrollPosition")
            ?: (arguments?.getInt("scrollPosition") ?: 0)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        binding.floatingActionButtonAddRow.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("button_flag", true)
            findNavController().navigate(
                R.id.action_chartMasterFragment_to_chartDetailsFragment,
                bundle
            )
        }
    }

    //TODO: Revisit
    //TODO: Loading Dialog
    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())

        val layoutAnimationController = AnimationUtils.loadLayoutAnimation(
            requireContext(),
            R.anim.recyclerview_layout_animation
        )

        binding.recyclerViewChart.apply {
            layoutManager = linearLayoutManager
            layoutAnimation = layoutAnimationController
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                val data = ChartDbHelper(requireContext()).everyone
//                val chartAdapter = ChartAdapter(
//                    requireContext(),
//                    data,
//                    this@ChartMasterFragment
//                )
//
//                with(binding.recyclerViewChart) {
//                    this.adapter = chartAdapter
//                    if (chartAdapter.itemCount > 1) {
//                        this.scrollToPosition(chartAdapter.itemCount - 1)
//                    }
//                }

                viewModel.chartResponse.collect { response ->
                    when (response) {
                        is Response.Success -> {
                            val chartAdapter = ChartAdapter(
                                requireContext(),
                                response.data,
                                this@ChartMasterFragment
                            )

                            with(binding.recyclerViewChart) {
                                this.adapter = chartAdapter
                                if (chartAdapter.itemCount > 1) {
                                    this.scrollToPosition(chartAdapter.itemCount - 1)
                                }
                            }
                        }
                        is Response.Error -> {
                            Log.d("ChartMasterFragment", response.message)
                        }
                        else -> {
                            //TODO
                        }
                    }
                }
            }
        }
    }


    //TODO: Send values instead of ID.
    override fun onRowClick(dayId: String, view: View?) {
        val bundle = Bundle()
        bundle.putBoolean(INITIAL_BUTTON_FLAG, false)
        bundle.putString(DAY_ID, dayId)

        if (view != null && view.transitionName != null) {
            val extras = FragmentNavigatorExtras(Pair(view, view.transitionName))

            setEnterSharedElementCallback(object : androidx.core.app.SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>,
                    sharedElements: MutableMap<String, View>
                ) {
                    super.onMapSharedElements(names, sharedElements)
                    sharedElements[view.transitionName] = view
                }
            })

            findNavController().navigate(
                R.id.action_chartMasterFragment_to_chartDetailsFragment,
                bundle,
                null,
                extras
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

//    private fun fillChartWithRandomData(
//        monthStart: Int,
//        monthEnd: Int,
//        dayStart: Int,
//        dayEnd: Int
//    ) {
//        val helper = ChartDbHelper(requireContext())
//        var model: ChartDayModel?
//        for (i in monthStart until monthEnd) {
//            for (j in dayStart until dayEnd) {
//                model = ChartDayModel(
//                    -1,
//                    j,
//                    i,
//                    2021,
//                    if (Random().nextInt(10) > 1) Random().nextInt(75) + 65 else 0,
//                    if (Random().nextInt(10) > 1) Random().nextInt(95) + 95 else 0,
//                    if (Random().nextInt(10) > 1) Random().nextInt(75) + 65 else 0,
//                    if (Random().nextInt(10) > 1) Random().nextInt(95) + 95 else 0,
//                    if (Random().nextInt(10) > 1) Random().nextInt(75) + 65 else 0,
//                    if (Random().nextInt(10) > 1) Random().nextInt(95) + 95 else 0,
//                    if (Random().nextInt(10) > 5) Random().nextInt(75) + 85 else 0
//                )
//                helper.addOne(model)
//            }
//        }
//    }
}