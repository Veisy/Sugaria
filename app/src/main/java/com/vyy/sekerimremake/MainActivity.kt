package com.vyy.sekerimremake

import android.content.res.Configuration
import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModelMain: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding


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
    }

    private fun setNavigationComponents() {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment?)!!
        val navController = navHostFragment.navController
        val appBarConfiguration =
            AppBarConfiguration.Builder(R.id.catalogMasterFragment, R.id.chartMasterFragment)
                .build()

        // Handle toolbar and bottom navigation menu.
        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.toolbar.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
            }
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                && (destination.id == R.id.catalogDetailsFragment || destination.id == R.id.chartMasterFragment)
            ) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
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
                        viewModelMain.getChart()
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
                .setIsSmartLockEnabled(false)
                .setLogo(R.mipmap.ic_launcher_foreground).setTheme(R.style.AppTheme)
                .build()
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
                    this,
                    getString(R.string.successfully_signed_out),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, getString(R.string.sign_out_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }
}