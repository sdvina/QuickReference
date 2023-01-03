package org.sdvina.qr.ui.reference

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.sdvina.qr.R
import org.sdvina.qr.data.local.AppPreferences
import org.sdvina.qr.ui.theme.Blue500
import org.sdvina.qr.ui.theme.AppTheme
import org.sdvina.qr.ui.util.ReferenceUrl

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceScreen(
    modifier: Modifier = Modifier
) {
    val referenceUrlState = remember { mutableStateOf(AppPreferences.lastViewedUrl!!) }
    val webViewState = rememberWebViewState(url = referenceUrlState.value)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        topBar = {
            ReferenceTopBar(
                referenceUrlState = referenceUrlState,
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        WebView(
            state = webViewState,
            modifier = modifier.padding(innerPadding),
            navigator = rememberWebViewNavigator(),
            onCreated = { webView ->
                webView.settings.javaScriptEnabled = false // need remove ad
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceTopBar(
    modifier: Modifier = Modifier,
    referenceUrlState: MutableState<String>,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val message = stringResource(R.string.msg_coming_soon)
    val context = LocalContext.current
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.app_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            ) },
        navigationIcon = {
            Icon(
                imageVector = Icons.Filled.LogoDev,
                contentDescription = null
            )
        },
        actions = {
            IconButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.cd_search)
                )
            }
            IconButton(
                onClick = {
                    referenceUrlState.value = when(referenceUrlState.value == ReferenceUrl.QR_ZH) {
                        true -> ReferenceUrl.QR_EN
                        false -> ReferenceUrl.QR_ZH
                    }
                    AppPreferences.lastViewedUrl = referenceUrlState.value
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Translate,
                    contentDescription = stringResource(R.string.cd_translate),
                    tint = when(referenceUrlState.value == ReferenceUrl.QR_EN){
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
        ReferenceScreen()
    }
}