package com.example.martinruiz.myapplication.roomdb;

import android.util.Log;

import com.example.martinruiz.myapplication.App;
import com.example.martinruiz.myapplication.models.CityWeather;
import com.example.martinruiz.myapplication.models.CityWeatherStorage;
import com.example.martinruiz.myapplication.utils.Callback;
import com.example.martinruiz.myapplication.utils.DebugUtils;

import java.util.List;

public class RequestHandler {

    private AppDatabase db;
    private CityWeatherDao cityWeatherDao;
    private CityWeatherStorage cityWeatherStorage;

    public RequestHandler() {
        db = App.getInstance().getDatabase();
        cityWeatherDao = db.cityWeatherDao();

        cityWeatherStorage = CityWeatherStorage.getInstance();
    }

    public void saveData(List<CityWeather> cityWeatherList, Callback callback) {
        RequestExecutor executor = new RequestExecutor(() -> {
            try {
                cityWeatherDao.clear();
                cityWeatherDao.insert(cityWeatherList);
                return true;
            } catch (Exception e) {
                return false;
            }


        }, callback);

        new Thread(executor).start();
    }

    public void loadData(Callback callback) {
        RequestExecutor executor = new RequestExecutor(() -> {

            try {
                List<CityWeather> cityWeatherList =  cityWeatherStorage.getCityWeatherList();
                cityWeatherList.clear();
                cityWeatherList.addAll(cityWeatherDao.getAll());

                return true;

            }catch (Exception e){
                return false;
            }

        }, callback);

        new Thread(executor).start();
    }


}

class RequestExecutor implements Runnable {

    boolean isSuccess;
    Request request;
    Callback callback;

    public RequestExecutor(Request request, Callback callback) {
        this.request = request;
        this.callback = callback;
    }

    @Override
    public void run() {
        isSuccess = request.execute();
        if (isSuccess) Log.d(DebugUtils.TAG, "run: request Successful");
        else Log.d(DebugUtils.TAG, "run: request Failed");

        callback.callback(isSuccess);
    }
}
