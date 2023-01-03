package org.sdvina.qr.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import org.sdvina.qr.R

@Composable
fun ShareButton(onClick: () -> Unit) {

    IconButton(onClick) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = stringResource(R.string.cd_share)
        )
    }
}

@Composable
fun SearchButton(onClick: () -> Unit) {

    IconButton(onClick) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.cd_search)
        )
    }
}

@Composable
fun MoreActionsButton(content:  @Composable (ColumnScope.() -> Unit)) {

    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.cd_more_actions)
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        content = content
    )
}