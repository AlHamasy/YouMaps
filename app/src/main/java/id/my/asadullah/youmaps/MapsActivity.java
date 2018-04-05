package id.my.asadullah.youmaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.my.asadullah.youmaps.Network.ConfigRetrofit;
import id.my.asadullah.youmaps.ResponseMaps.DirectionMapsV2;
import id.my.asadullah.youmaps.ResponseMaps.ResponseRoute;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.lokasiAwal)
    TextView lokasiAwal;
    @BindView(R.id.lokasiTujuan)
    TextView lokasiTujuan;
    @BindView(R.id.txtJarak)
    TextView txtJarak;
    @BindView(R.id.txtWaktu)
    TextView txtWaktu;

    private GoogleMap mMap;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE2 = 2;

    //todo 13 -- inisialisasi variable untuk get lokasi awal dan tujuan
    Double latAwal,lonAwal, latTujuan, lonTujuan;
    LatLng selectAwal, selectTujuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // todo 1 -- masukkan lokasi anda disini
        LatLng idnjonggol = new LatLng(-6.49105, 107.00631);
        mMap.addMarker(new MarkerOptions().position(idnjonggol).title("Marker in IDN Jonggol")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))); // memberi warna pada panah lokasi

        mMap.moveCamera(CameraUpdateFactory.newLatLng(idnjonggol));

        // todo 2 -- menambah icon baru di lokasi yang diatur
        mMap.addMarker(new MarkerOptions()
                .position(idnjonggol)
                .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_dialog_map)));

        // todo 3 -- auto zoom kamera
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(idnjonggol, 16f));

        // todo 4 -- untuk memberi fitur setting maps zoom in dan out
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // todo 5 -- untuk memberi fitur compass
        mMap.getUiSettings().setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // todo 9 -- penambahan permissions apabila android diatas Marsmello
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 30);
            }
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // todo 6 -- memberi padding
        mMap.setPadding(15, 160, 15, 160);

        // todo 7 -- memberi fitur lokasi sekarang
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // todo 8 -- set ke awal map
        mMap.clear();

    }

    @OnClick({R.id.lokasiAwal, R.id.lokasiTujuan})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.lokasiAwal:
                //todo 10 -- mengambil lokasi awal -- diambil dari dokumentasi google maps api, place autocomplete
                try {
                    AutocompleteFilter.Builder filter = new AutocompleteFilter.Builder();
                    filter.setCountry("id");

                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setFilter(filter.build())
                                    .build(this);

                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;

            case R.id.lokasiTujuan:
                //todo 11 -- mengambil lokasi tujuan -- diambil dari dokumentasi google maps api, place picker
                try {
                    AutocompleteFilter.Builder filter = new AutocompleteFilter.Builder();
                    filter.setCountry("id");

                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setFilter(filter.build())
                                    .build(this);

                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE2);

                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
        }
    }

    //todo 12 -- get respon untuk mencari lokasi
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //todo -- LOKASI AWAL
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                //todo 14A -- get cordinate
                LatLng p1 = place.getLatLng();

                latAwal = place.getLatLng().latitude;
                lonAwal = place.getLatLng().longitude;
                selectAwal = new LatLng(latAwal, lonAwal);

                //todo 27A -- set apabila lokasi dipilih lagi, maka map akan di clear
                if (!lokasiAwal.getText().toString().isEmpty()){
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(selectAwal));
                }

                //todo 15A -- membuat marker berbasis cordinat
                mMap.addMarker(new MarkerOptions()
                                .position(p1)
                                .title(place.getAddress().toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                //todo 16A -- set fokus camera agar marker selalu di bagian tengah
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p1, 14));

                //todo 17A -- pindahin nama lokasi ke textview / edittext
                lokasiAwal.setText(place.getAddress().toString());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("status", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        //todo -- LOKASI TUJUAN
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                //todo 14B -- get cordinate
                LatLng p2 = place.getLatLng();

                latTujuan = place.getLatLng().latitude;
                lonTujuan = place.getLatLng().longitude;
                selectTujuan = new LatLng(latTujuan, lonTujuan);

                //todo 27B -- set apabila lokasi dipilih lagi, maka map akan di clear
                if (!lokasiTujuan.getText().toString().isEmpty()){
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(selectTujuan));
                }

                //todo 15B --membuat marker berbasis cordinat
                mMap.addMarker(new MarkerOptions()
                                .position(p2)
                                .title(place.getAddress().toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                //todo 16B -- set fokus camera agar marker selalu di bagian tengah
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p2, 14));

                //todo 17B -- pindahin nama lokasi te textview / edittext
                lokasiTujuan.setText(place.getAddress().toString());

                //todo 18 -- buat kamera berada di tengah2 antara kordinat awal dan tujuan
                if (latAwal != null && lonAwal != null && latTujuan != null && lonTujuan != null){
                    LatLngBounds.Builder latLongBuilder = new LatLngBounds.Builder();

                    latLongBuilder.include(new LatLng(latAwal, lonAwal));
                    latLongBuilder.include(new LatLng(latTujuan, lonTujuan));

                    //todo -- dapatkan kordinat di tengah tengah
                    LatLngBounds bounds = latLongBuilder.build();

                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    int paddingMap = (int) (width * 0.3); //jarak dari sisi maps 30 %

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, paddingMap);
                    mMap.animateCamera(cu);

                    //todo 19 -- membuat rute jalan -- google maps direction
                    actionRoute();


                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("status", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void actionRoute() {

        //todo 20 -- get semua kordinat
        String origin = latAwal.toString()+","+lonAwal.toString();
        String destination = latTujuan.toString()+","+lonTujuan.toString();
        String mode = "driving";

        //todo 21 -- panggil bikinRute
        ConfigRetrofit.service.bikinRute(origin, destination, mode).enqueue(new Callback<ResponseRoute>() {
            @Override
            public void onResponse(Call<ResponseRoute> call, Response<ResponseRoute> response) {
                Log.d("response route : ", response.message());

                //todo 22 -- bikin kondisional sukses
                if (response.isSuccessful()){

                    //todo 23 -- get points
                    String point = response.body()
                                    .getRoutes()
                                    .get(0)
                                    .getOverviewPolyline()
                                    .getPoints();

                    //todo 24 -- get jarak dengan satuan KM
                    String text = response.body()
                                    .getRoutes()
                                    .get(0)
                                    .getLegs()
                                    .get(0)
                                    .getDistance()
                                    .getText();

                    //todo 25 -- get jarak dengan satuan Meter
                    Double value = Double.valueOf(response.body()
                                    .getRoutes()
                                    .get(0)
                                    .getLegs()
                                    .get(0)
                                    .getDistance()
                                    .getValue());

                    Log.d("text legs", ""+ text + value);

                    txtJarak.setText(text);
                    txtWaktu.setText(value.toString());

                    //todo 26 -- membuat gambar / garis rute
                    new DirectionMapsV2(MapsActivity.this).gambarRoute(mMap,point);

                }
            }

            @Override
            public void onFailure(Call<ResponseRoute> call, Throwable t) {
                Log.d("error",t.getMessage());
            }
        });

    }

    public void mapHybrid(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void mapSatelit(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void mapTerrain(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void mapNormal(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

}