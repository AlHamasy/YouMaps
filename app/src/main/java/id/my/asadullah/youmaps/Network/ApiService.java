package id.my.asadullah.youmaps.Network;

import id.my.asadullah.youmaps.ResponseMaps.ResponseRoute;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by asadullah on 1/20/18.
 */

public interface ApiService {

    @GET("directions/json")
    Call<ResponseRoute> bikinRute (@Query("origin") String awal,
                                    @Query("destination") String tujuan,
                                    @Query("mode") String mode);

}