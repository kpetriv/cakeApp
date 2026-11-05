package com.example.cakes.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.cakes.R
import com.example.cakes.domain.Cake
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CakeListScreen(
	modifier: Modifier = Modifier,
	viewModel: CakeScreenViewModel = koinViewModel(),
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	val topAppBarState = rememberTopAppBarState()
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
	val sheetState = rememberModalBottomSheetState()
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
		modifier = modifier
	) { padding ->
		// TODO this could use a bit more thought and UX improvements to share loading, refresh
		when {
			state.isLoading && state.cakes.isEmpty() -> Loading(modifier = Modifier.fillMaxSize())
			state.isError && state.cakes.isEmpty() -> MessageWithAction(
				message = stringResource(R.string.something_went_wrong),
				actionLabel = stringResource(R.string.retry),
				onAction = { viewModel.reload() },
				modifier = Modifier.fillMaxSize()
			)

			state.cakes.isEmpty() -> MessageWithAction(
				message = stringResource(R.string.no_cakes),
				actionLabel = stringResource(R.string.refresh),
				onAction = { viewModel.reload() },
				modifier = Modifier.fillMaxSize()
			)

			else -> Content(
				padding = padding,
				scrollBehavior = scrollBehavior,
				state = state,
				onCakeClick = {
					viewModel.selectCake(it)
				},
				onRefresh = { viewModel.reload() },
			)
		}

		state.selectedCake?.also {
			ModalBottomSheet(
				sheetState = sheetState,
				onDismissRequest = {
					viewModel.selectCake(null)
				}
			) {
				CakeDetails(cake = it, modifier = Modifier.padding(16.dp))
			}
		}
	}
}

// TODO make this look a lot nicer :D
@Composable
fun CakeDetails(cake: Cake, modifier: Modifier = Modifier) {
	Text(
		text = cake.description,
		modifier = modifier
	)
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
	Box(modifier = modifier, contentAlignment = Alignment.Center) {
		CircularProgressIndicator(modifier = Modifier.size(48.dp))
	}
}

/**
 * Full screen message with a single action, used for the error and empty states.
 */
@Composable
private fun MessageWithAction(
	message: String,
	actionLabel: String,
	onAction: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(modifier = modifier, contentAlignment = Alignment.Center) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Text(text = message)
			Spacer(modifier = Modifier.height(16.dp))
			Button(onClick = onAction) {
				Text(text = actionLabel)
			}
		}
	}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Content(
	padding: PaddingValues,
	scrollBehavior: TopAppBarScrollBehavior,
	state: CakeListUiState,
	onCakeClick: (Cake) -> Unit,
	onRefresh: () -> Unit,
) {
	PullToRefreshBox(
		isRefreshing = state.isLoading,
		onRefresh = onRefresh,
		modifier = Modifier.fillMaxSize()
	) {
		LazyColumn(
			modifier = Modifier
				.padding(padding)
				.nestedScroll(scrollBehavior.nestedScrollConnection),
			contentPadding = PaddingValues(bottom = 16.dp)
		) {
			itemsIndexed(
				items = state.cakes,
				// assuming titles would be unique
				key = { _, item -> item.title },
			) { index, cake ->
				Column(
					// TODO this adds the animate item, but only for new items or change of list,
					// because of the list composable not being in view hierarchy due to interim
					// loading state. so this might be better with a shimmer ghost items and then
					// a new list state or adding appearance effect to individual items
					modifier = Modifier
						.fillMaxWidth()
						.animateItem()
				) {
					CakeListItem(cake = cake, modifier = Modifier.clickable { onCakeClick(cake) })
					if (index < state.cakes.lastIndex) {
						HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
					}
				}
			}
		}
	}
}

@Composable
fun CakeListItem(cake: Cake, modifier: Modifier = Modifier) {
	ListItem(
		leadingContent = {
			AsyncImage(
				modifier = Modifier
					.size(56.dp)
					.clip(RoundedCornerShape(8.dp)),
				model = cake.thumbnail,
				contentScale = ContentScale.Crop,
				contentDescription = null
			)
		},
		headlineContent = {
			Text(text = cake.title)
		},
		modifier = modifier,
	)
}
