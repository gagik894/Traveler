package com.together.traveler.ui.admin

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.together.traveler.R
import com.together.traveler.model.Place
import com.together.traveler.ui.admin.map.MapFragment
import com.together.traveler.ui.theme.TravelerTheme

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adminViewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        setContent {
            TravelerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TabLayout()
                }
            }
        }
    }
}

@Composable
fun TabLayout() {
    val titles = listOf("Places", "Categories", "Reported")
    var selectedTabIndex by remember { mutableStateOf(0) }
    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> Navigation()
            1 -> CategoriesPage()
            2 -> Text("Soon")
        }
    }
}

@Composable
fun Navigation() {
    val mViewModel = viewModel<AdminViewModel>()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "placeCards") {
        composable("placeCards") { PlaceCards(navController, mViewModel) }
        composable("singlePlace") {
            mViewModel.selectedPlaceData.value?.let { placeData ->
                SinglePlace(
                    placeData,
                    mViewModel,
                    navController
                )
            }
        }
    }
}

@Composable
fun PlaceCards(navController: NavController, viewModel: AdminViewModel) {
    val places by viewModel.placesData.observeAsState()
    if (places == null || places?.size == 0) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Nothing to check yet",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxHeight()
            )
        }

    } else {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 5.dp)
        ) {

            places?.forEach { item ->
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    PlaceCard(data = item) {
                        viewModel.setSelectedPlaceData(place = item)
                        navController.navigate("singlePlace")
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlaceCard(
    data: Place,
    onClick: () -> Unit
) {
    TravelerTheme {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable(onClick = onClick),
            shape = MaterialTheme.shapes.medium,
            elevation = 1.dp,
            border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
        ) {
            Row(
                Modifier
                    .wrapContentHeight()
                    .padding(10.dp), Arrangement.SpaceBetween
            )
            {
                data.imgId?.let {
                    GlideImage(
                        model = "https://drive.google.com/uc?export=wiew&id=$it",
                        contentDescription = null,
                        modifier = Modifier
                            .weight(0.5f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    Modifier.weight(2f),
                    Arrangement.SpaceBetween
                ) {
                    data.name?.let {
                        Text(
                            text = it,
                            fontSize = MaterialTheme.typography.h6.fontSize,
                        )
                    }
                    Row(Modifier.wrapContentSize()) {
                        data.userId?.avatar?.let {
                            GlideImage(
                                model = "https://drive.google.com/uc?export=wiew&id=$it",
                                contentDescription = null,
                                modifier = Modifier
                                    .width(24.dp)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        data.userId?.username?.let {
                            Text(
                                text = it,
                                fontSize = MaterialTheme.typography.subtitle2.fontSize,
                                modifier = Modifier.weight(1f)
                            )
                        }

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SinglePlace(data: Place, mViewModel: AdminViewModel, navController: NavController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var name by remember { mutableStateOf(data.name ?: "") }
    var location by remember { mutableStateOf(data.location ?: "") }
    var phone by remember { mutableStateOf(data.phone ?: "") }
    var url by remember { mutableStateOf(data.url ?: "") }
    var description by remember { mutableStateOf(data.description ?: "") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        data.imgId?.let {
            GlideImage(
                model = "https://drive.google.com/uc?export=wiew&id=$it",
                contentDescription = "Place Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f),
                contentScale = ContentScale.Crop
            )
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(10.dp))
            BasicTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.h5,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TintedIcon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Location"
                )
                Spacer(modifier = Modifier.width(5.dp))
                BasicTextField(
                    value = location,
                    onValueChange = { location = it },
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TintedIcon(
                    imageVector = Icons.Rounded.DateRange,
                    contentDescription = "Date"
                )
                Spacer(modifier = Modifier.width(5.dp))
                PlaceTimes(data)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TintedIcon(
                    imageVector = Icons.Rounded.Phone,
                    contentDescription = "Phone"
                )
                BasicTextField(
                    value = phone,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '+' }) {
                            phone = it
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TintedIcon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Link"
                )
                BasicTextField(
                    value = url,
                    onValueChange = { url = it },
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TintedIcon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Category"
                )
                Spacer(modifier = Modifier.width(5.dp))
                data.category?.let {
                    Text(text = it)
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = stringResource(R.string.place_about),
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(5.dp))
            BasicTextField(
                value = description,
                onValueChange = { description = it },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.place_location),
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(10.dp))
            data.latitude?.let {
                FragmentInComposeExample()

                DisposableEffect(Unit) {
                    onDispose {
                        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                        val fragment = fragmentManager.findFragmentById(R.id.placeMap)
                        if (fragment != null) {
                            fragmentManager.beginTransaction().remove(fragment).commit()
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = {
                    mViewModel.deletePlace(data._id)
                    navController.navigateUp()
                }) {
                    Text(text = "Delete", fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    data.name = name
                    data.location = location
                    data.phone = phone.trim()
                    data.url = url.trim()
                    data.description = description
                    mViewModel.verifyPlace(data)
                    navController.navigateUp()
                }) {
                    Text(text = "Verify", fontSize = 15.sp)
                }
            }

        }
    }
}

@Composable
fun PlaceTimes(data: Place) {
    if (data.isAlwaysOpen) {
        Text(text = "Always Open")
    } else {
        Column {
            data.isClosedDays.forEachIndexed { index, isClosed ->
                if (isClosed) {
                    Text(text = stringResource(R.string.place_closed))
                } else {
                    Text(text = "${data.openingTimes[index]} - ${data.closingTimes[index]}")
                }
            }
        }
    }
}

@Composable
fun FragmentInComposeExample() {
    val context = LocalContext.current
    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
    var fragment by remember { mutableStateOf<MapFragment?>(null) }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .padding(top = 10.dp),
        factory = { ctx ->
            FragmentContainerView(ctx).apply {
                id = R.id.placeMap
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { view ->
            // Only create a new MapFragment instance if one does not already exist
            if (fragment == null) {
                fragment = MapFragment()
                fragmentManager.beginTransaction().replace(view.id, fragment!!).commit()
            }
        }
    )
}

@Composable
fun CategoriesPage() {
    CategoryItems()
}


@Composable
fun CategoryItems() {
    val mViewModel = viewModel<AdminViewModel>()
    val eventCategories by mViewModel.eventCategories.observeAsState()
    val placeCategories by mViewModel.placeCategories.observeAsState()

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 5.dp)
    ) {
        item {
            Text(text = "Event Categories", style = MaterialTheme.typography.h5, modifier = Modifier.padding(top = 16.dp))
        }
        eventCategories?.forEach { item ->
            item {
                Spacer(modifier = Modifier.height(5.dp))
                CategoryItem(item) {
                    mViewModel.deleteCategory(item, "event")
                }
            }
        }
        item {
            TextFieldWithButtons(
                onAdd = { text ->
                    mViewModel.addCategory(text, "event")
                },
            )

        }
        item {
            Text(
                text = "Place Categories",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        placeCategories?.forEach { item ->
            item {
                Spacer(modifier = Modifier.height(5.dp))
                CategoryItem(item) {
                    mViewModel.deleteCategory(item, "place")
                }
            }
        }
        item {
            TextFieldWithButtons(
                onAdd = { text ->
                    mViewModel.addCategory(text, "place")
                },
            )
        }
    }
}

@Composable
fun TextFieldWithButtons(onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextField(
                value = text,
                onValueChange = { newText -> text = newText },
                placeholder = { Text("New category") },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 0.dp)
            )
            Button(onClick = { onAdd(text) }, enabled = text.trim().length > 3) {
                Text(text = "Add")
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, onDelete: () -> Unit) {
    Card(modifier = Modifier.height(35.dp)) {
        Row(modifier = Modifier.padding(5.dp), horizontalArrangement = Arrangement.Center) {
            Text(text = name)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                TintedIcon(imageVector = Icons.Default.Delete, contentDescription = "delete")
            }
        }
    }
}

@Composable
fun TintedIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colors.secondary
) {
    TravelerTheme {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}

