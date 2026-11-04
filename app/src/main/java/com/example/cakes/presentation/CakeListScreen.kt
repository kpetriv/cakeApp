package com.example.cakes.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cakes.R
import com.example.cakes.domain.Cake
import com.example.cakes.ui.theme.CakesTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CakeListScreen(
	modifier: Modifier = Modifier,
	viewModel: CakeScreenViewModel = koinViewModel(),
) {
	val state by viewModel.state.collectAsState()
	val topAppBarState = rememberTopAppBarState()
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
	val sheetState = rememberModalBottomSheetState()

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = "Cake selection") },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier
	) { padding ->
		// TODO pull to refresh and handling error when pulled down refresh fails
		when {
			state.isLoading -> Loading(modifier = Modifier.fillMaxSize())
			state.isError && state.cakes.isEmpty() -> Error(
				onRetry = { viewModel.reload() },
				modifier = Modifier.fillMaxSize()
			)
			state.cakes.isNotEmpty() -> Content(
				padding = padding,
				scrollBehavior = scrollBehavior,
				state = state,
				onCakeClick = {
					viewModel.selectCake(it)
				}
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
fun Loading(modifier: Modifier = Modifier) {
	Box(modifier = modifier, contentAlignment = Alignment.Center) {
		CircularProgressIndicator(modifier = Modifier.size(96.dp))
	}
}

@Composable
fun Error(onRetry: () -> Unit, modifier: Modifier = Modifier) {
	Box(modifier = modifier, contentAlignment = Alignment.Center) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Text(text = stringResource(R.string.something_went_wrong))
			Spacer(modifier = Modifier.width(16.dp))
			Button(onClick = onRetry) {
				Text(text = "Retry")
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
				modifier = Modifier.fillMaxWidth()
			) {
				CakeListItem(cake = cake, modifier = Modifier.clickable { onCakeClick(cake) })
				if (index < state.cakes.lastIndex) {
					HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
				}
			}
		}
	}
}

@Composable
fun CakeListItem(cake: Cake, modifier: Modifier = Modifier) {
	Row(
		modifier = modifier.padding(16.dp)
	) {
		AsyncImage(
			modifier = Modifier
				.size(56.dp)
				.background(MaterialTheme.colorScheme.inverseOnSurface)
				.clip(RoundedCornerShape(8.dp)),
			model = cake.thumbnail,
			contentScale = ContentScale.Crop,
			contentDescription = null
		)
		Spacer(modifier = Modifier.width(16.dp))
		Text(
			text = cake.title,
			color = MaterialTheme.colorScheme.onSurface
		)
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	Text(
		text = "Hello $name!",
		modifier = modifier
	)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	CakesTheme {
		Greeting("Android")
	}
}