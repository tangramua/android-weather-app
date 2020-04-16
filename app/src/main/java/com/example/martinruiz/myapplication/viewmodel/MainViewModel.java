package com.example.martinruiz.myapplication.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.martinruiz.myapplication.API.API;
import com.example.martinruiz.myapplication.API.APIServices.WeatherServices;
import com.example.martinruiz.myapplication.models.CityWeather;
import com.example.martinruiz.myapplication.models.CityWeatherStorage;
import com.example.martinruiz.myapplication.roomdb.RequestHandler;
import com.example.martinruiz.myapplication.utils.DebugUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.martinruiz.myapplication.utils.ResponseResultConstants.RESULT_CITY_ALREADY_PRESENT;
import static com.example.martinruiz.myapplication.utils.ResponseResultConstants.RESULT_NOT_FOUND;
import static com.example.martinruiz.myapplication.utils.ResponseResultConstants.RESULT_SERVICE_UNAVAILABLE;
import static com.example.martinruiz.myapplication.utils.ResponseResultConstants.RESULT_LOADING_SUCCESS;
import static com.example.martinruiz.myapplication.utils.ResponseResultConstants.RESULT_REFRESH_SUCCESS;

public class MainViewModel extends ViewModel {

    private final String FAHRENHEIT_TEMPERATURE_FORMAT = "imperial";
    private final String CELSIUS_TEMPERATURE_FORMAT = "metric";

    private MutableLiveData<Integer> retrofitRequestResult;
    private MutableLiveData<Boolean> roomRequestStatus;
    private int lastChangedCityWeather;

    private RequestHandler roomRequestHandler;
    private WeatherServices weatherServices;
    private CityWeatherStorage weatherStorage;

    public MainViewModel() {
        roomRequestHandler = new RequestHandler();
        weatherServices = API.getApi().create(WeatherServices.class);
        weatherStorage = CityWeatherStorage.getInstance();

        retrofitRequestResult = new MutableLiveData<>();
        roomRequestStatus = new MutableLiveData<>();
    }

    public void getWeather(String cityName) {
        Call<CityWeather> cityWeather = weatherServices.getWeatherCity(cityName, CELSIUS_TEMPERATURE_FORMAT, API.KEY);

        cityWeather.enqueue(new Callback<CityWeather>() {
            @Override
            public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {
                Log.d(DebugUtils.TAG, "onResponse: Response code: " + response.code());
                if (response.code() == 200) {
                    CityWeather cityWeather = response.body();

                    if (isCityAlreadyPresent(cityWeather)) retrofitRequestResult.postValue(RESULT_CITY_ALREADY_PRESENT);
                    else {
                        weatherStorage.getCityWeatherList().add(cityWeather);
                        retrofitRequestResult.postValue(RESULT_LOADING_SUCCESS);
                    }


                } else {
                    retrofitRequestResult.postValue(RESULT_NOT_FOUND);
                }
            }

            @Override
            public void onFailure(Call<CityWeather> call, Throwable t) {
                retrofitRequestResult.postValue(RESULT_SERVICE_UNAVAILABLE);
                Log.d(DebugUtils.TAG, "onFailure: " + t.toString());
            }
        });
    }

    public void refreshAll() {
        List<CityWeather> weatherList = weatherStorage.getCityWeatherList();

        for (int i = 0; i < weatherList.size(); i++) {
            refreshCityWeather(weatherList.get(i).getCity().getName(), i);
        }

    }

    public void refreshCityWeather(String cityName, int index) {
        Call<CityWeather> newCityWeather = weatherServices.getWeatherCity(cityName, CELSIUS_TEMPERATURE_FORMAT, API.KEY);
        newCityWeather.enqueue(new Callback<CityWeather>() {
            @Override
            public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {
                if (response.code() == 200) {
                    CityWeather cityWeather = response.body();
                    weatherStorage.getCityWeatherList().set(index, cityWeather);

                    lastChangedCityWeather = index;
                    retrofitRequestResult.postValue(RESULT_REFRESH_SUCCESS);
                }
            }

            @Override
            public void onFailure(Call<CityWeather> call, Throwable t) {
                retrofitRequestResult.postValue(RESULT_SERVICE_UNAVAILABLE);
            }
        });
    }

    public List<CityWeather> getCities() {
        return weatherStorage.getCityWeatherList();
    }

    public MutableLiveData<Integer> getRetrofitRequestResult() {
        return retrofitRequestResult;
    }

    public MutableLiveData<Boolean> getRoomRequestStatus() {
        return roomRequestStatus;
    }

    public int getLastChangedCityWeather() {
        return lastChangedCityWeather;
    }



    public void loadLocalData(){
        roomRequestHandler.loadData(isSuccess -> {
            roomRequestStatus.postValue((Boolean) isSuccess);
            Log.d(DebugUtils.TAG, "loadLocalData: " + isSuccess);
        });
    }

    public void saveLocalData(){
        roomRequestHandler.saveData(weatherStorage.getCityWeatherList(), isSuccess -> {
            Log.d(DebugUtils.TAG, "saveLocalData: " + isSuccess);
        });
    }

    private boolean isCityAlreadyPresent(CityWeather cityWeather){
        boolean result = false;

        for (CityWeather cw : weatherStorage.getCityWeatherList()){
            result = cityWeather.getCity().getName().equals(cw.getCity().getName());
        }

        return result;
    }
}
