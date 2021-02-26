@file:Suppress("MagicNumber")

package im.bnw.android.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import im.bnw.android.R
import im.bnw.android.di.core.ViewModelFactory
import javax.inject.Inject

class AuthComposeFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    val vmClass = AuthViewModel::class.java
    val viewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(vmClass)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    AuthScreen()
                }
            }
        }
    }

    @Preview(widthDp = 360, heightDp = 480)
    @Composable
    fun AuthScreen() {
        val state = viewModel.stateLiveData().observeAsState().value ?: return
        val scrollState = rememberScrollState()
        Column() {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { viewModel.backPressed() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "",
                        )
                    }
                },
                title = { Text(text = getString(R.string.log_in)) },
                elevation = 8.dp
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Name(state)
                Password(state)
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { viewModel.backPressed() },
                        content = {
                            Text(text = getString(R.string.cancel))
                        }
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(
                        onClick = { viewModel.signInClicked() },
                        content = {
                            Text(text = getString(R.string.sign_in))
                        },
                        modifier = Modifier
                            .padding(end = 16.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun Name(state: AuthState) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp
                ),
            value = state.userName,
            singleLine = true,
            label = { Text(getString(R.string.user_name)) },
            onValueChange = { viewModel.loginChanged(it) }
        )
    }

    @Composable
    private fun Password(state: AuthState) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp
                ),
            value = state.password,
            singleLine = true,
            label = { Text(getString(R.string.password)) },
            onValueChange = { viewModel.passwordChanged(it) }
        )
    }

    companion object {
        fun newInstance() = AuthComposeFragment()
    }
}
