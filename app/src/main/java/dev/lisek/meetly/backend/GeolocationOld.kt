//package dev.lisek.meetly.backend
//
//import android.Manifest
//import android.content.pm.PackageManager
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.platform.LocalContext
//import androidx.core.content.ContextCompat
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.maps.model.LatLng
//import dev.lisek.meetly.ui.main.getCurrentLocation
//
//class GeolocationOld() {
//    @Composable
//    fun GeoPermission() {
//        val context = LocalContext.current
//        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
//
//        var permission by remember { mutableStateOf(false) }
//        var location by remember { mutableStateOf(LatLng(.0, .0)) }
//
//        val locationPermissionLauncher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            permission = isGranted
//            if (isGranted) {
//                getCurrentLocation(fusedLocationClient) { loc ->
//                    location = loc ?: LatLng(0.0, 0.0)
//                }
//            }
//        }
//
//        // Check permission status
//        LaunchedEffect(Unit) {
//            permission = ContextCompat.checkSelfPermission(
//                context, Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//
//            if (permission) {
//                getCurrentLocation(fusedLocationClient) { loc ->
//                    location = loc ?: LatLng(0.0, 0.0)
//                }
//            } else {
//                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//            }
//        }
//    }
//}
