package com.vyy.sekerimremake.features.chart.presenter.master

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
import com.vyy.sekerimremake.R
import com.vyy.sekerimremake.databinding.FragmentChartBinding
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.settings.domain.model.UserModel
import com.vyy.sekerimremake.features.settings.presenter.UsersAdapter
import com.vyy.sekerimremake.utils.FirestoreConstants
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

    private val usersAdapter: UsersAdapter by lazy {
        UsersAdapter { position: Int ->
            onUserClicked(position)
        }
    }

    private val chartAdapter: ChartAdapter by lazy {
        ChartAdapter { chartDayModel, chartDayView ->
            onRowClick(chartDayModel, chartDayView)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        scrollPosition =
            savedInstanceState?.getInt("scrollPosition") ?: (arguments?.getInt("scrollPosition")
                ?: 0)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUsersRecyclerView()
        initChartRecyclerView()
        setFloatingActionButton()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelMain.userResponse.collectLatest { response ->
                        when (response) {
                            is Response.Success -> {
                                response.data?.let { data ->
                                    handleSelectedUser(data)
                                }
                            }
                            is Response.Error -> {
                                Log.e("MonitoredsFragment", response.message)
                            }
                            else -> {
                                // TODO
                            }
                        }
                    }
                }
                launch {
                    viewModelMain.chartResponse.collectLatest { response ->
                        when (response) {
                            is Response.Success -> {
                                binding.recyclerViewChart.apply {
                                    scheduleLayoutAnimation()
                                    chartAdapter.submitList(response.data)
                                    if (scrollPosition != 0) {
                                        scrollToPosition(scrollPosition)
                                    }
                                }
                            }
                            is Response.Error -> {
                                chartAdapter.submitList(null)
                                Log.d("ChartMasterFragment", response.message)
                            }
                            else -> {
                                chartAdapter.submitList(null)
                                //TODO
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initUsersRecyclerView() {
        binding.recyclerViewUsers?.apply {
            adapter = usersAdapter
            setHasFixedSize(true)
        }
    }

    //TODO: Revisit
//TODO: Loading Dialog
    private fun initChartRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.recyclerViewChart.apply {
            adapter = chartAdapter
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
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

    private fun handleSelectedUser(mUser: UserModel) {
        val userList = mutableListOf<UserModel>().apply {
            add(UserModel(uid = mUser.uid, name = getString(R.string.my_values)))
        }

        if (mUser.monitoreds.isNullOrEmpty()) {
            binding.recyclerViewUsers?.visibility = View.GONE
            usersAdapter.selectedPosition = -1
        } else {
            binding.recyclerViewUsers?.visibility = View.VISIBLE
            val monitoredList = mUser.monitoreds!!.filter { monitored ->
                monitored[FirestoreConstants.NAME] != null && monitored[FirestoreConstants.UID] != null
            }.map { monitored ->
                UserModel(
                    uid = monitored[FirestoreConstants.UID],
                    name = monitored[FirestoreConstants.NAME],
                    userName = monitored[FirestoreConstants.USER_NAME]
                )
            }
            userList.addAll(monitoredList)
            if (usersAdapter.selectedPosition < 0 || usersAdapter.selectedPosition >= userList.size) {
                usersAdapter.selectedPosition = 0
            }
        }

        if (usersAdapter.selectedPosition <= 0) {
            viewModelMain.getChart(mUser.uid!!)
            binding.constraintLayoutFloatActionGroup?.visibility = View.VISIBLE
        } else {
            userList[usersAdapter.selectedPosition].uid?.let {
                viewModelMain.getChart(it)
            }
            binding.constraintLayoutFloatActionGroup?.visibility = View.GONE
        }

        usersAdapter.submitList(userList)
    }

    private fun onUserClicked(position: Int) {
        val userResponse = viewModelMain.userResponse.value as? Response.Success ?: return
        val currentUser = usersAdapter.currentList.getOrNull(position) ?: return

        val uid = if (currentUser.uid.isNullOrEmpty() || userResponse.data?.uid == currentUser.uid) {
            usersAdapter.selectUser(0)
            binding.constraintLayoutFloatActionGroup?.visibility = View.VISIBLE
            userResponse.data?.uid
        } else {
            usersAdapter.selectUser(position)
            binding.constraintLayoutFloatActionGroup?.visibility = View.GONE
            currentUser.uid
        }

        uid?.let { viewModelMain.getChart(it) }
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