package org.sdvina.qr.ui.reference

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LogoDev
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.web.*
import org.sdvina.qr.R
import org.sdvina.qr.data.local.AppPreferences
import org.sdvina.qr.ui.theme.Blue500
import org.sdvina.qr.ui.theme.AppTheme
import org.sdvina.qr.ui.util.ReferenceUrl

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun ReferenceScreen(
    modifier: Modifier = Modifier,
    viewModel: ReferenceViewModel
) {
    val viewState by viewModel.sate.collectAsStateWithLifecycle()
    val urlState = remember { mutableStateOf(AppPreferences.lastViewedUrl!!) }
    val webViewState = rememberWebViewState(url = urlState.value)
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier,
        topBar = {
            ReferenceTopBar(
                urlState = urlState,
                showMessage = { messageId,formatArgs ->
                    viewModel.showMessage(messageId,formatArgs)
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val webClient = remember {
            object : AccompanistWebViewClient() {

                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    super.onPageStarted(view, url, favicon)
                    urlState.value =  url!!
                    AppPreferences.lastViewedUrl = url
                    Log.d("Accompanist WebView", "Page started loading for $url")
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    error?.errorCode?.let {
                        Log.d("Accompanist WebView", "Error $it")
                        when(it) {
                            ERROR_TIMEOUT -> viewModel.showMessage(R.string.error_time_out, arrayOf(it))
                            else -> viewModel.showMessage(R.string.error_other_error, arrayOf(it))
                        }
                    }
                }
            }
        }
        WebView(
            state = webViewState,
            modifier = modifier.padding(innerPadding),
            navigator = rememberWebViewNavigator(),
            onCreated = { webView ->
                webView.settings.javaScriptEnabled = false // need remove ad
            },
            client = webClient
        )

        if(viewState.messages.isNotEmpty()){
            val message = remember(viewState) { viewState.messages[0] }
            val messageText = stringResource(message.messageId, *message.formatArgs)
            LaunchedEffect(message.id, messageText, snackbarHostState) {
                val result = snackbarHostState.showSnackbar(messageText)
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.messageShown(message.id) // TODO fix
                }
                viewModel.messageShown(message.id)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceTopBar(
    modifier: Modifier = Modifier,
    urlState: MutableState<String>,
    showMessage: (Int, Array<Any>) -> Unit
) {
    val context = LocalContext.current
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.app_title),
                modifier = Modifier.clickable {
                    urlState.value = when(urlState.value.contains(ReferenceUrl.QR_ZH)) {
                        true -> ReferenceUrl.QR_ZH
                        false -> ReferenceUrl.QR_EN
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Filled.LogoDev,
                contentDescription = null
            )
        },
        actions = {
            IconButton(
                onClick = {
                    showMessage(R.string.msg_coming_soon, arrayOf())
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.cd_search)
                )
            }
            IconButton(
                onClick = {
                    urlState.value = when(urlState.value.contains(ReferenceUrl.QR_ZH)) {
                        true -> ReferenceUrl.QR_EN
                        false -> ReferenceUrl.QR_ZH
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Translate,
                    contentDescription = stringResource(R.string.cd_translate),
                    tint = when(urlState.value.contains(ReferenceUrl.QR_EN)){
                        true -> Blue500
                        false -> Color.Unspecified
                    }
                )
            }
            IconButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ReferenceUrl.GITHUB_REPO)))
                }
            ){
                Icon(
                    painter = painterResource(R.drawable.github_logo_16dp),
                    contentDescription = stringResource(R.string.cd_github)
                )
            }
        }
    )
}

@Preview
@Composable
fun ReferenceScreenPreview(){
    AppTheme {
        ReferenceScreen(
            viewModel = viewModel(factory = ReferenceViewModel.provideFactory())
        )
    }
}