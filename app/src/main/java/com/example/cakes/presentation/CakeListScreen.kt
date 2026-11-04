package com.example.cakes.presentation

import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = "Cake selection") },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier
	) { padding ->
		when (state.isLoading) {
			true -> Loading(modifier = Modifier.fillMaxSize())
			false -> Content(padding, scrollBehavior, state)
		}
	}
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
	Box(modifier = modifier, contentAlignment = Alignment.Center) {
		CircularProgressIndicator(modifier = Modifier.size(96.dp))
	}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Content(
	padding: PaddingValues,
	scrollBehavior: TopAppBarScrollBehavior,
	state: CakeListUiState
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
				CakeListItem(cake = cake)
				if (index < state.cakes.lastIndex) {
					HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
				}
			}
		}
	}
}

@Composable
fun CakeListItem(cake: Cake) {
	Row(
		modifier = Modifier
			.padding(16.dp)
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