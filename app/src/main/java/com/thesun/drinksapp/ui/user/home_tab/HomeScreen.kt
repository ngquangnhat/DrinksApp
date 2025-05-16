package com.thesun.drinksapp.ui.user.home_tab

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Category
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.Filter
import com.thesun.drinksapp.ui.theme.BgMainColor
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.utils.Constant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val drinks by viewModel.allDrinks.collectAsState()
    val keyword by viewModel.searchKeyword.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ColorPrimaryDark)
        }
    } else {
        val context = LocalContext.current
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
        ) {
            item {
                SearchBar(keyword) {
                    viewModel.onSearchKeywordChange(it)
                }
            }

            item {
                DrinkBanner(
                    drinks.filter { it.isFeatured },
                    onClickDrink = {
                        navController.navigate("drinkDetail/${it.id}")

                    }
                )

            }

            item {
                CategoryTabScreen(
                    navController,
                    categories,
                    drinks,
                    listOf(
                        Filter(Filter.TYPE_FILTER_ALL, context.getString(R.string.filter_all)),
                        Filter(Filter.TYPE_FILTER_RATE, context.getString(R.string.filter_rate)),
                        Filter(Filter.TYPE_FILTER_PRICE, context.getString(R.string.filter_price)),
                        Filter(
                            Filter.TYPE_FILTER_PROMOTION,
                            context.getString(R.string.filter_promotion)
                        ),
                    ),
                    onFilterSelected = { categoryId, newFilter ->
                        viewModel.updateFilter(categoryId, newFilter)
                    },
                    selectedFiltersFlow = viewModel.selectedFilters,
                )

            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(keyword: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = keyword,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { Text("Hôm nay bạn muốn uống gì?", fontSize = 14.sp) },
        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = ColorPrimaryDark,
            unfocusedBorderColor = ColorAccent
        )
    )
}

@Composable
fun DrinkBanner(
    drinkList: List<Drink>,
    onClickDrink: (Drink) -> Unit
) {
    if (drinkList.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { drinkList.size }
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage < drinkList.size - 1) {
            coroutineScope.launch {
                delay(3000)
                pagerState.animateScrollToPage(
                    pagerState.currentPage + 1,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
            }
        } else {
            coroutineScope.launch {
                delay(3000)
                pagerState.animateScrollToPage(
                    0,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
            .height(200.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            val drink = drinkList[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClickDrink(drink) }
            ) {
                AsyncImage(
                    model = drink.banner,
                    contentDescription = drink.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(drinkList.size) { index ->
                val isSelected = pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .padding(1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) ColorPrimaryDark else ColorAccent
                            )
                    )
                }
            }
        }

    }
}


@Composable
fun CategoryTabScreen(
    navController: NavController,
    listCategory: List<Category>,
    allDrinks: List<Drink>,
    filters: List<Filter>,
    onFilterSelected: (Long, Filter) -> Unit,
    selectedFiltersFlow: StateFlow<Map<Long, Filter>>,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        pageCount = { listCategory.size }
    )
    val selectedFilters by selectedFiltersFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        if (selectedTabIndex != pagerState.currentPage) {
            selectedTabIndex = pagerState.currentPage
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), verticalArrangement = Arrangement.Top
    ) {
        val coroutineScope = rememberCoroutineScope()
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            contentColor = ColorPrimaryDark,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                if (tabPositions.isNotEmpty() && selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.Indicator(
                        color = ColorPrimaryDark,
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                    )
                }
            }
        ) {
            listCategory.forEachIndexed { index, category ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = {
                        selectedTabIndex = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    selectedContentColor = ColorPrimaryDark,
                    unselectedContentColor = ColorAccent,
                    modifier = Modifier
                        .width(130.dp)
                        .background(Color.White),
                    text = {
                        category.name?.let {
                            Text(
                                text = it.uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { pageIndex ->
            val categoryId = listCategory[pageIndex].id
            val drinks = allDrinks.filter { it.categoryId == categoryId }
            val selectedFilter = selectedFilters[categoryId] ?: Filter(id = Filter.TYPE_FILTER_ALL)

            val drinksFiltered = remember(selectedFilter, drinks) {
                when (selectedFilter.id) {
                    Filter.TYPE_FILTER_ALL -> drinks
                    Filter.TYPE_FILTER_PRICE -> drinks.sortedBy { it.realPrice }
                    Filter.TYPE_FILTER_PROMOTION -> drinks.filter { it.sale > 0 }
                    Filter.TYPE_FILTER_RATE -> drinks.sortedByDescending { it.rate }
                    else -> drinks
                }
            }

            DrinkTabPage(
                navController = navController,
                filters = filters.map {
                    it.copy(isSelected = it.id == selectedFilter.id)
                },
                onFilterSelected = { newFilter ->
                    onFilterSelected(categoryId, newFilter)
                },
                drinks = drinksFiltered
            )
        }
    }

}

@Composable
fun DrinkTabPage(
    navController: NavController,
    filters: List<Filter>,
    onFilterSelected: (Filter) -> Unit,
    drinks: List<Drink>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val coroutineScope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(filters) { index, filter ->
                FilterItem(
                    filter = filter,
                    onClick = {
                        onFilterSelected(filter)
                        coroutineScope.launch {
                            listState.animateScrollToItem(index)
                        }
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(640.dp)
        ) {
            items(drinks) { drink ->
                DrinkItem(drink) {
                    navController.navigate("drinkDetail/${drink.id}")
                }
            }
        }
    }
}

@Composable
fun FilterItem(filter: Filter, onClick: () -> Unit) {
    val backgroundColor = if (filter.isSelected) ColorPrimaryDark else Color.LightGray
    val textColor = if (filter.isSelected) Color.White else Color.Black

    val icon = when (filter.id) {
        Filter.TYPE_FILTER_ALL -> R.drawable.ic_filter_all
        Filter.TYPE_FILTER_RATE -> R.drawable.ic_filter_rate
        Filter.TYPE_FILTER_PRICE -> R.drawable.ic_filter_price
        Filter.TYPE_FILTER_PROMOTION -> R.drawable.ic_filter_sell
        else -> R.drawable.ic_filter_all
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = filter.name ?: "",
            fontSize = 14.sp,
            color = textColor
        )
    }
}


@Composable
fun DrinkItem(drink: Drink, onClickDrink: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clickable {
                onClickDrink()
            },
    ) {
        Row (
            modifier = Modifier.align(Alignment.CenterStart),
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = rememberAsyncImagePainter(drink.image),
                    contentDescription = drink.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .offset(y = (-10).dp)
                        .background(color = Color(0xFFFFF8E1), shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = drink.rate.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .padding(top = 10.dp),
            ) {
                Text(
                    text = drink.name ?: "",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = drink.description ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .offset(y=(-10).dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${drink.realPrice}" + Constant.CURRENCY,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            if (drink.sale > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${drink.price}" + Constant.CURRENCY,
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.LineThrough,
                        color = Color.Gray
                    )
                )
            }
        }
    }

    HorizontalDivider(
        color = BgMainColor,
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
    )

}








