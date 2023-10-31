package com.purificadora.activity;

import static com.purificadora.activity.HomeActivity.custPrograssbar;
import static com.purificadora.utils.Utiles.isRef;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.purificadora.R;
import com.purificadora.model.Address;
import com.purificadora.model.Area;
import com.purificadora.model.AreaD;
import com.purificadora.model.UpdateAddress;
import com.purificadora.model.User;
import com.purificadora.retrofit.APIClient;
import com.purificadora.retrofit.GetResult;
import com.purificadora.utils.MapaActivity;
import com.purificadora.utils.SessionManager;
import com.purificadora.utils.Utiles;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class AddressActivity extends BaseActivity implements GetResult.MyListener, OnMapReadyCallback {
    @BindView(R.id.ed_username)
    EditText edUsername;
    @BindView(R.id.ed_type)
    EditText edType;
    @BindView(R.id.ed_landmark)
    EditText edLandmark;
    SessionManager sessionManager;
    @BindView(R.id.ed_hoousno)
    EditText edHoousno;
    @BindView(R.id.ed_society)
    EditText edSociety;
    @BindView(R.id.ed_pinno)
    EditText edPinno;
    String areaSelect;
    List<AreaD> areaDS = new ArrayList<>();
    @BindView(R.id.spinner)
    Spinner spinner;
    User user;
    Address address;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Button selectAddressButton;
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker userLocationMarker; // Variable para el marcador
    private Button saveLocationButton; // Declarar el botón
    private TextView txt_save, txtlongidtud,txtlatitud ;
    private double savedLatitude;
    private double savedLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(AddressActivity.this);
        user = sessionManager.getUserDetails();
        address = (Address) getIntent().getSerializableExtra("MyClass");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaSelect = areaDS.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("test", "test");
            }
        });
        getArea();
        if (address != null)
            setcountaint(address);

        mapView = findViewById(R.id.map_view);
        txt_save = findViewById(R.id.txt_save);
        txtlatitud = findViewById(R.id.txtlatitud);
        txtlongidtud = findViewById(R.id.txtlongitud);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        saveLocationButton = findViewById(R.id.saveLocationButton);
        saveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para guardar la ubicación actual

            }
        });


    }

    private void setcountaint(Address address) {
        edUsername.setText("" + address.getName());
        edType.setText("" + address.getName());
        edHoousno.setText("" + address.getHno());
        edSociety.setText("" + address.getSociety());
        edPinno.setText("" + address.getPincode());
        edLandmark.setText("" + address.getLandmark());
        edType.setText("" + address.getType());
    }

    private void getArea() {
        JSONObject jsonObject = new JSONObject();
        JsonParser jsonParser = new JsonParser();
        Call<JsonObject> call = APIClient.getInterface().getArea((JsonObject) jsonParser.parse(jsonObject.toString()));
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "2");
    }

    @OnClick(R.id.txt_save)
    public void onViewClicked() {
        if (validation()) {
            custPrograssbar.prograssCreate(this);

            if (address != null) {
                updateUser(address.getId());
            } else {
                updateUser("0");
            }
        }
    }


    private void updateUser(String aid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("aid", aid);
            jsonObject.put("name", edUsername.getText().toString());
            jsonObject.put("hno", edHoousno.getText().toString());
            jsonObject.put("society", edSociety.getText().toString());
            jsonObject.put("area", areaSelect);
            jsonObject.put("landmark", edLandmark.getText().toString());
            jsonObject.put("pincode", edPinno.getText().toString());
            jsonObject.put("type", edType.getText().toString());
            jsonObject.put("mobile", user.getMobile());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("imei", Utiles.getIMEI(AddressActivity.this));
            // Agregar latitud y longitud a la solicitud
            jsonObject.put("latitud", txtlatitud.getText().toString());
            jsonObject.put("longitud", txtlongidtud.getText().toString());


            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().updateAddress((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                UpdateAddress response = gson.fromJson(result.toString(), UpdateAddress.class);
                Toast.makeText(AddressActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (response.getResult().equals("true")) {
                    sessionManager.setAddress(response.getAddress());
                    isRef = true;
                    finish();
                }
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                Area area = gson.fromJson(result.toString(), Area.class);
                areaDS = area.getData();
                List<String> arrayList = new ArrayList<>();
                for (int i = 0; i < areaDS.size(); i++) {
                    if (areaDS.get(i).getStatus().equalsIgnoreCase("1")) {
                        arrayList.add(areaDS.get(i).getName());
                    }
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
                int spinnerPosition = dataAdapter.getPosition(address.getArea());
                spinner.setSelection(spinnerPosition);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public boolean validation() {
        if (edUsername.getText().toString().isEmpty()) {
            edUsername.setError(getString(R.string.ename));
            return false;
        }
        if (edHoousno.getText().toString().isEmpty()) {
            edHoousno.setError(getString(R.string.ehouse));
            return false;
        }
        if (edSociety.getText().toString().isEmpty()) {
            edSociety.setError(getString(R.string.esociety));
            return false;
        }
        if (edLandmark.getText().toString().isEmpty()) {
            edLandmark.setError(getString(R.string.elandmark));
            return false;
        }
        if (edPinno.getText().toString().isEmpty()) {
            edPinno.setError(getString(R.string.epincode));
            return false;
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng userLocation = new LatLng(latitude, longitude);

                // Crear o actualizar el marcador de la ubicación del usuario
                if (userLocationMarker == null) {
                    userLocationMarker = googleMap.addMarker(new MarkerOptions()
                            .position(userLocation)
                            .title("Tu ubicación"));
                } else {
                    userLocationMarker.setPosition(userLocation);
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            } else {
            //    Toast.makeText(this, "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show();
            }
        });


        // Mover la lógica para obtener la ubicación al botón
        Button saveLocationButton =  findViewById(R.id.saveLocationButton);

        saveLocationButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddressActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mapView.setVisibility(View.VISIBLE);
            txt_save.setEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(AddressActivity.this, (OnSuccessListener<Location>) location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Almacena las coordenadas en las variables globales
                    savedLatitude = latitude;
                    savedLongitude = longitude;
                  //  Toast.makeText(AddressActivity.this,""+ latitude + longitude, Toast.LENGTH_SHORT).show();

                    LatLng userLocation = new LatLng(latitude, longitude);

                    txtlongidtud.setText(""+longitude);
                    txtlatitud.setText(""+latitude);

                    txtlongidtud.setVisibility(View.GONE);
                    txtlatitud.setVisibility(View.GONE);

                    // Actualiza el mapa con la ubicación actual si es necesario
                    if (userLocationMarker == null) {
                        userLocationMarker = googleMap.addMarker(new MarkerOptions()
                                .position(userLocation)
                                .title("Tu ubicación"));
                    } else {
                        userLocationMarker.setPosition(userLocation);
                    }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                } else {
                 //   Toast.makeText(AddressActivity.this, "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de ubicación concedido, configurar el mapa
                onMapReady(mMap);
            } else {
                Toast.makeText(this, "El permiso de ubicación es necesario para seleccionar una dirección.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
