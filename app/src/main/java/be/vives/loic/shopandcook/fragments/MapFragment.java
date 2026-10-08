package be.vives.loic.shopandcook.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import be.vives.loic.shopandcook.R;

/**
 * Shows an OpenStreetMap (via osmdroid, no API key required) centered on the user when
 * location permission is granted, with a handful of highlighted shop markers around them.
 * There's no real shop directory yet, so the markers are sample data until the app has a
 * real "nearby shops" data source.
 */
public class MapFragment extends Fragment {

    // Fallback center (Brussels) if location is unavailable or denied.
    private static final GeoPoint DEFAULT_LOCATION = new GeoPoint(50.8503, 4.3517);

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    private final ActivityResultLauncher<String> requestLocationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> centerOnUserLocation());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Configuration.getInstance().load(requireContext().getApplicationContext(),
                requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        if (hasLocationPermission()) {
            centerOnUserLocation();
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancellationTokenSource.cancel();
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void centerOnUserLocation() {
        if (!hasLocationPermission()) {
            showNearbyShops(DEFAULT_LOCATION);
            return;
        }
        // getLastLocation() only returns a location if some other app recently requested one,
        // and is often null on a fresh install/emulator - getCurrentLocation() actively asks
        // the device for a fresh fix instead.
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(location -> {
                    GeoPoint here = location != null
                            ? new GeoPoint(location.getLatitude(), location.getLongitude())
                            : DEFAULT_LOCATION;
                    showNearbyShops(here);
                })
                .addOnFailureListener(e -> showNearbyShops(DEFAULT_LOCATION));
    }

    // Sample shops offset around the given location, highlighted with a title/snippet,
    // until the app has a real "nearby shops" data source.
    private void showNearbyShops(GeoPoint center) {
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(center);

        String[] names = {"Supermarché Central", "Épicerie Bio", "Marché Local", "Boulangerie du Coin"};
        double[][] offsets = {{0.004, 0.003}, {-0.003, 0.004}, {0.002, -0.004}, {-0.004, -0.002}};

        for (int i = 0; i < names.length; i++) {
            GeoPoint position = new GeoPoint(center.getLatitude() + offsets[i][0], center.getLongitude() + offsets[i][1]);
            Marker marker = new Marker(mapView);
            marker.setPosition(position);
            marker.setTitle(names[i]);
            marker.setSnippet("Magasin à proximité");
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(marker);
        }
        mapView.invalidate();
    }
}
