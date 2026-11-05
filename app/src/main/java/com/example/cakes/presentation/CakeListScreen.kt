package com.example.cakes.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.example.cakes.R
import com.example.cakes.domain.Cake
import com.example.cakes.ui.theme.CakesTheme
import org.koin.compose.viewmodel.koinViewModel

private val ThumbnailSize = 56.dp

// ListItem's 16dp start padding + thumbnail + 16dp gap, so dividers line up with the titles.
private val DividerInset = 16.dp + ThumbnailSize + 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CakeListScreen(
	modifier: Modifier = Modifier,
	viewModel: CakeScreenViewModel = koinViewModel(),
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val snackbarHostState = remember { SnackbarHostState() }

	// A failed refresh keeps the current list on screen, so report it with a snackbar instead
	// of the full screen error state.
	val showRefreshError = state.isError && state.cakes.isNotEmpty()
	val refreshFailedMessage = stringResource(R.string.refresh_failed)
	val retryLabel = stringResource(R.string.retry)
	LaunchedEffect(showRefreshError) {
		if (showRefreshError) {
			val result = snackbarHostState.showSnackbar(
				message = refreshFailedMessage,
				actionLabel = retryLabel,
				duration = SnackbarDuration.Short,
			)
			viewModel.refreshErrorShown()
			if (result == SnackbarResult.ActionPerformed) viewModel.reload()
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = stringResource(R.string.cake_selection)) },
				scrollBehavior = scrollBehavior,
			)
		},
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		// One pull to refresh indicator is used for every load, so loading looks the same on
		// first launch, on retry and on refresh.
		PullToRefreshBox(
			isRefreshing = state.isLoading,
			onRefresh = viewModel::reload,
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
		) {
			when {
				state.cakes.isNotEmpty() -> CakeList(
					cakes = state.cakes,
					onCakeClick = viewModel::selectCake,
				)

				state.isLoading -> Unit

				state.isError -> MessageState(
					icon = R.drawable.ic_cloud_off,
					title = stringResource(R.string.something_went_wrong),
					message = stringResource(R.string.load_failed_message),
					actionLabel = stringResource(R.string.retry),
					onAction = viewModel::reload,
				)

				else -> MessageState(
					icon = R.drawable.ic_cake,
					title = stringResource(R.string.no_cakes_title),
					message = stringResource(R.string.no_cakes),
					actionLabel = stringResource(R.string.refresh),
					onAction = viewModel::reload,
				)
			}
		}

		state.selectedCake?.also { cake ->
			ModalBottomSheet(
				sheetState = sheetState,
				onDismissRequest = { viewModel.selectCake(null) },
			) {
				CakeDetails(cake = cake)
			}
		}
	}
}

@Composable
private fun CakeList(
	cakes: List<Cake>,
	onCakeClick: (Cake) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(modifier = modifier.fillMaxSize()) {
		itemsIndexed(
			items = cakes,
			// assuming titles would be unique
			key = { _, cake -> cake.title },
		) { index, cake ->
			// TODO this adds the animate item, but only for new items or change of list,
			// because of the list composable not being in view hierarchy due to interim
			// loading state. so this might be better with a shimmer ghost items and then
			// a new list state or adding appearance effect to individual items
			Column(modifier = Modifier.animateItem()) {
				CakeListItem(cake = cake, onClick = { onCakeClick(cake) })
				if (index < cakes.lastIndex) {
					HorizontalDivider(modifier = Modifier.padding(start = DividerInset))
				}
			}
		}
	}
}

@Composable
private fun CakeListItem(cake: Cake, onClick: () -> Unit, modifier: Modifier = Modifier) {
	ListItem(
		leadingContent = {
			CakeImage(
				url = cake.thumbnail,
				modifier = Modifier
					.size(ThumbnailSize)
					.clip(MaterialTheme.shapes.small),
			)
		},
		headlineContent = { Text(text = cake.title) },
		modifier = modifier.clickable(onClick = onClick),
	)
}

@Composable
private fun CakeDetails(cake: Cake, modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
	) {
		CakeImage(
			url = cake.thumbnail,
			modifier = Modifier
				.fillMaxWidth()
				.aspectRatio(16f / 9f)
				.clip(MaterialTheme.shapes.large),
		)
		Spacer(modifier = Modifier.height(16.dp))
		Text(text = cake.title, style = MaterialTheme.typography.headlineSmall)
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = cake.description,
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

/**
 * Cake photo with a cake icon placeholder, shown while loading and when the image fails to load.
 * Some image URLs in the feed are dead, so the fallback is visible in practice.
 */
@Composable
private fun CakeImage(url: String, modifier: Modifier = Modifier) {
	var showPlaceholder by remember(url) { mutableStateOf(true) }
	val context = LocalContext.current
	val request = remember(url) {
		ImageRequest.Builder(context)
			.data(url)
			.crossfade(true)
			.build()
	}

	Box(
		modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
		contentAlignment = Alignment.Center,
	) {
		if (showPlaceholder) {
			Icon(
				painter = painterResource(R.drawable.ic_cake),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.fillMaxSize(0.4f),
			)
		}
		AsyncImage(
			model = request,
			contentDescription = null,
			contentScale = ContentScale.Crop,
			onState = { showPlaceholder = it !is AsyncImagePainter.State.Success },
			modifier = Modifier.matchParentSize(),
		)
	}
}

/**
 * Full screen message with an icon and a single action, used for the error and empty states.
 */
@Composable
private fun MessageState(
	@DrawableRes icon: Int,
	title: String,
	message: String,
	actionLabel: String,
	onAction: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(32.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		Icon(
			painter = painterResource(icon),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.size(48.dp),
		)
		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = title,
			style = MaterialTheme.typography.titleMedium,
			textAlign = TextAlign.Center,
		)
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = message,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			textAlign = TextAlign.Center,
		)
		Spacer(modifier = Modifier.height(24.dp))
		Button(onClick = onAction) {
			Text(text = actionLabel)
		}
	}
}

private val previewCake = Cake(
	title = "Lemon Cheesecake",
	description = "A cheesecake made of lemon",
	thumbnail = "",
)

@Preview(showBackground = true)
@Composable
private fun CakeListItemPreview() {
	CakesTheme {
		CakeListItem(cake = previewCake, onClick = {})
	}
}

@Preview(showBackground = true)
@Composable
private fun CakeDetailsPreview() {
	CakesTheme {
		CakeDetails(cake = previewCake)
	}
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun ErrorStatePreview() {
	CakesTheme {
		MessageState(
			icon = R.drawable.ic_cloud_off,
			title = stringResource(R.string.something_went_wrong),
			message = stringResource(R.string.load_failed_message),
			actionLabel = stringResource(R.string.retry),
			onAction = {},
		)
	}
}
