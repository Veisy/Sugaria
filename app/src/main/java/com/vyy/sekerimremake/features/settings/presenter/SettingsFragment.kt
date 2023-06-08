package com.vyy.sekerimremake.features.settings.presenter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vyy.sekerimremake.MainViewModel
import com.vyy.sekerimremake.R
import com.vyy.sekerimremake.databinding.FragmentSettingsBinding
import com.vyy.sekerimremake.features.settings.domain.model.UserModel
import com.vyy.sekerimremake.utils.FirestoreConstants
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SettingsFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModelMain: MainViewModel by activityViewModels()

    private val monitorsAdapter: UsersAdapter by lazy {
        UsersAdapter { position: Int ->
            onMonitorClicked(position)
        }
    }

    private val monitoredsAdapter: UsersAdapter by lazy {
        UsersAdapter { position: Int ->
            onMonitoredClicked(position)
        }
    }

    private val blocklistAdapter: UsersAdapter by lazy {
        UsersAdapter { position: Int ->
            onBlockedClicked(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().apply {
            findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE
            findViewById<Toolbar>(R.id.toolbar).menu.findItem(R.id.action_settings).isVisible = false
        }


        binding.linearLayoutAddMonitor.setOnClickListener(this)
        initRecyclerViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMain.userResponse.collectLatest { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data?.let { user ->
                                handleAccountInfo(user)
                                handleUserList(monitorsAdapter, user.monitors)
                                handleUserList(monitoredsAdapter, user.monitoreds)
                                handleUserList(blocklistAdapter, user.blockList)

                                handleViewVisibility(
                                    binding.viewSeparatorMonitors, user.monitors.isNullOrEmpty()
                                )
                                handleViewVisibility(
                                    binding.constraintLayoutMonitoreds,
                                    user.monitoreds.isNullOrEmpty()
                                )
                                handleViewVisibility(
                                    binding.constraintLayoutBlocklist,
                                    user.blockList.isNullOrEmpty()
                                )
                            }
                        }
                        is Response.Error -> {
                            Log.e("MonitoredsFragment", response.message)
                        }
                        else -> {
                            //TODO
                        }
                    }
                }
            }
        }
    }

    private fun initRecyclerViews() {
        binding.apply {
            initSingleRecyclerView(recyclerViewMonitors, monitorsAdapter)
            initSingleRecyclerView(recyclerViewMonitoreds, monitoredsAdapter)
            initSingleRecyclerView(recyclerViewBlocklist, blocklistAdapter)
        }
    }

    private fun initSingleRecyclerView(recyclerView: RecyclerView, adapter: UsersAdapter) {
        recyclerView.apply {
            this.adapter = adapter
        }

    }

    private fun handleAccountInfo(user: UserModel?) {
        user.let {
            binding.apply {
                textViewDisplayName.text = user?.name
                val userNameWithAtSign = "@${user?.userName}"
                textViewUsernameField.text = userNameWithAtSign
                textViewEmailField.text = user?.email
            }
        }
    }

    private fun handleUserList(
        usersAdapter: UsersAdapter, userList: List<HashMap<String, String>>?
    ) {
        usersAdapter.submitList(userList?.filter { user ->
            user[FirestoreConstants.NAME] != null && user[FirestoreConstants.UID] != null
        }?.map { user ->
            UserModel(
                uid = user[FirestoreConstants.UID],
                name = user[FirestoreConstants.NAME],
                userName = user[FirestoreConstants.USER_NAME]
            )
        })
    }

    private fun handleViewVisibility(view: View, isNullOrEmpty: Boolean) {
        view.visibility = if (isNullOrEmpty) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun onMonitorClicked(position: Int) {
        //TODO("Not yet implemented")
        Log.d("SettingsFragment", "onMonitorClicked: $position")
    }

    private fun onMonitoredClicked(position: Int) {
        //TODO("Not yet implemented")
        Log.d("SettingsFragment", "onMonitoredClicked: $position")
    }

    private fun onBlockedClicked(position: Int) {
        // TODO("Not yet implemented")
        Log.d("SettingsFragment", "onBlockedClicked: $position")
    }

    override fun onClick(view: View?) {
        // TODO("Not yet implemented")
        Toast.makeText(requireContext(), "Add Monitor Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}