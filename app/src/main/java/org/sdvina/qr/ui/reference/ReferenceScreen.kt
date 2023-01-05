package org.sdvina.qr.ui.reference

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
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
import org.sdvina.qr.data.constant.InterceptionConstant
import org.sdvina.qr.data.constant.LogConstant
import org.sdvina.qr.data.constant.ReferenceConstant
import org.sdvina.qr.data.local.AppPreferences
import org.sdvina.qr.ui.theme.AppTheme
import org.sdvina.qr.ui.theme.Blue500
import org.sdvina.qr.ui.util.Interceptor
import java.io.ByteArrayInputStream
import java.io.InputStream


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
    val searchState = remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            ReferenceTopBar(
                urlState = urlState,
                searchState = searchState
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
                    Log.d(LogConstant.TAG_WEB_VIEW, "${LogConstant.MSG_PAGE_LOADING} $url")
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    error?.errorCode?.let {
                        Log.d(LogConstant.TAG_WEB_VIEW, "${LogConstant.MSG_ERROR} $it")
                        when(it) {
                            ERROR_TIMEOUT -> viewModel.showMessage(R.string.error_time_out, arrayOf(it))
                            else -> viewModel.showMessage(R.string.error_other_error, arrayOf(it))
                        }
                    }
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    Interceptor.dismiss(request, InterceptionConstant.ADS)?.let { return it }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if(searchState.value){
                        searchState.value = !searchState.value
                        when(urlState.value.contains(ReferenceConstant.QR_ZH)) {
                            true -> view?.loadUrl(ReferenceConstant.QR_ZH_SEARCH)
                            false -> view?.loadUrl(ReferenceConstant.QR_EN_SEARCH)
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
                webView.settings.javaScriptEnabled = true
                webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
            },
            client = webClient
        )

        if(searchState.value) webClient.navigator.reload()
        if(viewState.messages.isNotEmpty()){
            val message = remember(viewState) { viewState.messages[0] }
            val messageText = stringResource(message.messageId, *message.formatArgs)
            LaunchedEffect(message.id, messageText, snackbarHostState) {
                snackbarHostState.showSnackbar(messageText)
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
    searchState: MutableState<Boolean>
) {
    val context = LocalContext.current
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.app_title),
                modifier = Modifier.clickable {
                    urlState.value = when(urlState.value.contains(ReferenceConstant.QR_EN)) {
                        true -> ReferenceConstant.QR_EN
                        false -> ReferenceConstant.QR_ZH
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
                    searchState.value = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.cd_search)
                )
            }
            IconButton(
                onClick = {
                    urlState.value = when(urlState.value.contains(ReferenceConstant.QR_EN)) {
                        true -> ReferenceConstant.QR_ZH
                        false -> ReferenceConstant.QR_EN
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Translate,
                    contentDescription = stringResource(R.string.cd_translate),
                    tint = when(urlState.value.contains(ReferenceConstant.QR_EN)){
                        true -> Blue500
                        false -> Color.Unspecified
                    }
                )
            }
            IconButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ReferenceConstant.GITHUB_REPO)))
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