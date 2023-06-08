package com.vyy.sekerimremake

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.vyy.sekerimremake.databinding.ActivityMainBinding
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModelMain: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigationComponents()
        setAuthStateListener()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMain.userResponse.collectLatest { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data?.let { user ->
                                handleMonitorInvitation(user.waiting_monitors)
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

    private fun setNavigationComponents() {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment?)!!
        val navController = navHostFragment.navController
        val appBarConfiguration =
            AppBarConfiguration.Builder(R.id.catalogMasterFragment, R.id.chartMasterFragment, R.id.aiFragment)
                .build()

        // Handle toolbar and bottom navigation menu.
        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            binding.apply {
                if (destination.id != R.id.settingsFragment) {
                    toolbar.menu.findItem(R.id.action_settings)?.isVisible = true
                }

                when (destination.id) {
                    R.id.catalogMasterFragment, R.id.chartMasterFragment -> {
                        bottomNavigation.visibility = View.VISIBLE
                    }
                }

                toolbar.visibility = when (destination.id) {
                    R.id.chartDetailsFragment, R.id.catalogDetailsFragment ->
                        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) View.GONE else View.VISIBLE
                    else -> View.VISIBLE
                }
            }
        }
        setupWithNavController(binding.bottomNavigation, navController)
        setupWithNavController(binding.toolbar, navController, appBarConfiguration)

        binding.toolbar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.action_sign_out -> {
                        signOut()
                        return true
                    }
                    R.id.action_settings -> {
                        navController.navigate(R.id.action_global_settingsFragment)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun setAuthStateListener() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMain.getAuthState().collectLatest { isCurrentUserNull ->
                    if (isCurrentUserNull) {
                        signInUi()
                    } else {
                        viewModelMain.getUser()
                    }
                }
            }
        }
    }

    private fun signInUi() {
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent =
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .setIsSmartLockEnabled(false).setLogo(R.mipmap.ic_launcher_foreground)
                .setTheme(R.style.AppTheme).build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val signInResponseText: String
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            signInResponseText = getString(R.string.successfully_signed_in)
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            signInResponseText = getString(R.string.sign_in_failed)
            signInUi()
        }
        Toast.makeText(this, signInResponseText, Toast.LENGTH_SHORT).show()
    }

    fun signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this, getString(R.string.successfully_signed_out), Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, getString(R.string.sign_out_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleMonitorInvitation(invitationList: List<HashMap<String, String>>?) {
        // TODO
//        invitationList?.let { invitations ->
//            if (invitations.isNotEmpty()) {
//                AlertDialog.Builder(this).apply {
//                    setTitle("Monitor Invitation")
//                    setMessage(" <b><i>${invitations[0][FirestoreConstants.USER_NAME]}</i></b> wants wants you to monitor his/her glucose levels. \nDo you accept?")
//                    setPositiveButton("ACCEPT") { _, _ ->
//                        Log.d("SettingsFragment", "Accept")
//                    }
//                    setNegativeButton("DECLINE") { _, _ ->
//                        Log.d("SettingsFragment", "Block User")
//                    }
//                    setNeutralButton("BLOCK USER") { _, _ ->
//                        Log.d("SettingsFragment", "Decline")
//                    }
//
//                    create()
//                    show()
//                }
//            }
//        }
    }
}