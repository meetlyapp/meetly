package dev.lisek.meetly.ui.homescreen

import android.content.Context
import android.location.Geocoder
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dev.lisek.meetly.backend.geo.Geolocation.getAddressFromLatLng
import dev.lisek.meetly.backend.geo.Geolocation.getPermission
import java.io.IOException
import java.util.Locale

/**
 * Find place predictions based on user's input.
 * 
 * @param [placesClient] Google Places API client.
 * @param [query] User's input.
 * @param [callback] Callback to return the list of predictions.
 */
fun getPlacePredictions(
    placesClient: PlacesClient,
    query: String,
    callback: (List<AutocompletePrediction>) -> Unit
) {
    val request = FindAutocompletePredictionsRequest.builder()
        .setQuery(query)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            callback(response.autocompletePredictions)
        }
        .addOnFailureListener { exception ->
            Log.e("PlacesAPI", "Error getting autocomplete predictions", exception)
            callback(emptyList())
        }
}

/**
 * Fetch place coordinates based on place ID.
 * 
 * @param [placesClient] Google Places API client.
 * @param [placeId] Place ID.
 * @param [callback] Callback to return the place coordinates.
 */
fun getPlaceDetails(
    placesClient: PlacesClient,
    placeId: String,
    callback: (LatLng) -> Unit
) {
    val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG)).build()

    placesClient.fetchPlace(request)
        .addOnSuccessListener { response ->
            val latLng = response.place.latLng
            if (latLng != null) {
                callback(latLng)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("PlacesAPI", "Error fetching place details", exception)
        }
}

/**
 * Text field displaying place suggestions.
 * 
 * @param [onPlaceSelected] Callback to pass the selected place coordinates.
 */
@Composable
fun PlacesAutoCompleteTextField(onPlaceSelected: (LatLng) -> Unit) {
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    var query by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clip(RoundedCornerShape(28.dp))
    ) {
        TextField(query, { newValue ->
            query = newValue
            getPlacePredictions(placesClient, newValue) { result ->
                predictions = result
            }
        },
            Modifier.fillMaxWidth(),
            label = { Text("Search Location") },
            trailingIcon = { Icon(Icons.Default.Search, null) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )

        LazyColumn {
            items(predictions) { prediction ->
                Text(
                    text = prediction.getFullText(null).toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            getPlaceDetails(placesClient, prediction.placeId) { latLng ->
                                onPlaceSelected(latLng)
                                query = prediction.getFullText(null).toString()
                                predictions = emptyList() // Hide suggestions
                            }
                        }
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(16.dp),
                )
            }
        }
    }
}

/**
 * Map modal.
 * 
 * @param [onConfirm] Callback to pass the selected location.
 */
@Composable
fun Map(onConfirm: (LatLng, String) -> Unit) {
    val context = LocalContext.current

    var permission by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf(LatLng(.0, .0)) }
    var address by remember { mutableStateOf("Unknown Location") }
    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 14f)
    }
    val markerState = rememberMarkerState(position = cameraPositionState.position.target)
    var uiSettings = MapUiSettings(
        compassEnabled = false,
        myLocationButtonEnabled = false,
        zoomControlsEnabled = false
    )
    var properties = MapProperties(true, false, permission)

    fun updateLocation(loc: LatLng?) {
        location = loc ?: LatLng(.0, .0)
        address = getAddressFromLatLng(context, location)
        markerState.position = location
        cameraPositionState.move(CameraUpdateFactory.newLatLng(location))
        properties = MapProperties(true, false, permission)
    }

    permission = getPermission { updateLocation(it) }

    Box(Modifier
        .fillMaxHeight(.8f)
        .clip(RoundedCornerShape(24.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        GoogleMap(
            properties = properties,
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            onMapClick = { updateLocation(it) },
            onPOIClick = { updateLocation(it.latLng) },
            onMyLocationClick = { updateLocation(LatLng(it.latitude, it.longitude)) },
        ) {
            Marker(
                state = markerState
            )
        }

        Column(Modifier.fillMaxSize(), Arrangement.SpaceBetween) {
            PlacesAutoCompleteTextField {
                updateLocation(it)
            }

            Row(
                Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(16.dp)
                    .fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Column(Modifier, Arrangement.Bottom) {
                    Text(address.replace(", ", "\n"), Modifier.fillMaxWidth(.5f))
                }
                Button({
                    onConfirm(location, address)
                }) {
                    Text("Confirm")
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
