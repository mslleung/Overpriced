package com.igrocery.overpriced.presentation.shared

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.igrocery.overpriced.presentation.R

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors()
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = colors
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
            contentDescription = stringResource(R.string.back_button_content_description),
        )
    }
}

@Composable
fun CloseButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_close_24),
            contentDescription = stringResource(R.string.close_button_content_description),
        )
    }
}

@Composable
fun DeleteButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_delete_24),
            contentDescription = stringResource(R.string.delete_button_content_description),
        )
    }
}

@Composable
fun SaveButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(100),
        colors = ButtonDefaults.textButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
            disabledContentColor = MaterialTheme.colorScheme.onSecondary
        ),
        modifier = modifier,
        enabled = enabled
    ) {
        Text(
            text = stringResource(id = R.string.save_button_text)
        )
    }
}
