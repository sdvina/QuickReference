package org.sdvina.qr.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.sdvina.qr.ui.reference.ReferenceScreen
import org.sdvina.qr.ui.theme.AppTheme

@Composable
fun QRApp() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            ReferenceScreen()
        }
    }
}
