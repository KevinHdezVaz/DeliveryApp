package com.purificadora.utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
 import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.purificadora.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView plusCodeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        plusCodeTextView = findViewById(R.id.plusCodeTextView);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Activa la capa de "plus codes" en el mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Añade un marcador en una ubicación (por ejemplo, San Francisco)
        LatLng location = new LatLng(37.7749, -122.4194);
        Marker marker = mMap.addMarker(new MarkerOptions().position(location).title("Ubicación Actual"));

        // Muestra el "plus code" cuando se hace clic en el marcador
        mMap.setOnMarkerClickListener(marker1 -> {
            if (marker1.equals(marker)) {
                obtainPlusCode(location);
            }
            return false;
        });

        // Mueve la cámara a la ubicación
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f));
    }

    private void obtainPlusCode(LatLng location) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Verificar si los campos de administración y localidad no son nulos
                if (address.getAdminArea() != null && address.getLocality() != null) {
                    String plusCode = address.getAdminArea() + address.getLocality();
                    plusCodeTextView.setText("Plus Code: " + plusCode);
                } else {
                    plusCodeTextView.setText("No se pudo obtener el Plus Code");
                }
            } else {
                plusCodeTextView.setText("No se pudo obtener la dirección");
            }
        } catch (IOException e) {
            e.printStackTrace();
            plusCodeTextView.setText("Error al obtener el Plus Code");
        }
    }







}
