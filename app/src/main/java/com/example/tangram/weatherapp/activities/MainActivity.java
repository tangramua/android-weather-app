package com.example.tangram.weatherapp.activities;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tangram.weatherapp.API.API;
import com.example.tangram.weatherapp.API.APIServices.WeatherServices;
import com.example.tangram.weatherapp.R;
import com.example.tangram.weatherapp.adapters.CityWeatherAdapter;
import com.example.tangram.weatherapp.interfaces.onSwipeListener;
import com.example.tangram.weatherapp.models.CityWeather;
import com.example.tangram.weatherapp.utils.ItemTouchHelperCallback;
import com.example.tangram.weatherapp.utils.ResponseResultConstants;
import com.example.tangram.weatherapp.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @BindView(R.id.recyclerViewWeatherCards)
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fabAddCity)
    FloatingActionButton fabAddCity;
    private WeatherServices weatherServices;
    private MaterialTapTargetPrompt mFabPrompt;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        init();
        observ();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter.getItemCount() == 0) {
            viewModel.loadLocalData();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        viewModel.saveLocalData();
    }

    private void observ() {
        viewModel.getRetrofitRequestResult().observe(this, result -> {
            switch (result) {
                case ResponseResultConstants.RESULT_LOADING_SUCCESS:
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    break;

                case ResponseResultConstants.RESULT_REFRESH_SUCCESS:
                    adapter.notifyItemChanged(viewModel.getLastChangedCityWeather());
                    break;

                case ResponseResultConstants.RESULT_CITY_ALREADY_PRESENT:
                    Toast.makeText(this, "Selected city is already present!", Toast.LENGTH_SHORT).show();
                    break;

                case ResponseResultConstants.RESULT_NOT_FOUND:
                    Toast.makeText(this, "City not found!", Toast.LENGTH_SHORT).show();
                    break;

                case ResponseResultConstants.RESULT_SERVICE_UNAVAILABLE:
                    Toast.makeText(this, "Service unavailable!", Toast.LENGTH_SHORT).show();
                    break;

            }
        });

        viewModel.getRoomRequestStatus().observe(this, isSuccess -> {
            if (isSuccess && adapter.getItemCount() != 0) {
                adapter.notifyDataSetChanged();
                //TODO loadProgress.setEnabled(View.GONE);
            } else showFabPrompt();
        });
    }

    private void init() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);


        weatherServices = API.getApi().create(WeatherServices.class);

        layoutManager = new LinearLayoutManager(this);
        adapter = new CityWeatherAdapter(viewModel.getCities(), R.layout.weather_card, this, (cityWeather, position, clickView) -> {
            Intent intent = new Intent(MainActivity.this, WeatherDetails.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    MainActivity.this, clickView,
                    "weatherCardTransition");

            intent.putExtra("city", cityWeather);
            startActivity(intent, options.toBundle());
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // Scroll Down
                    if (fabAddCity.isShown()) {
                        fabAddCity.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!fabAddCity.isShown()) {
                        fabAddCity.show();
                    }
                }
            }
        });


        fabAddCity.setOnClickListener(view -> {
            showAlertAddCity("Add city", "Type the city you want to add");
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback((onSwipeListener) adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


    }


    public void showFabPrompt() {
        if (mFabPrompt != null) {
            return;
        }
        mFabPrompt = new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.fabAddCity))
                .setFocalPadding(R.dimen.dp40)
                .setPrimaryText("Add your City")
                .setSecondaryText("Tap the add button and add your favorites cities to get weather updates")
                .setBackButtonDismissEnabled(true)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_DISMISSING) {
                        mFabPrompt = null;
                        //Do something such as storing a value so that this prompt is never shown again
                    }
                })
                .create();
        mFabPrompt.show();
    }


    private void refreshData() {
        viewModel.refreshAll();
        swipeRefreshLayout.setRefreshing(false);
    }


    public void showAlertAddCity(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_city, null);
        builder.setView(view);
        final TextView editTextAddCityName = view.findViewById(R.id.editTextAddCityName);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String cityToAdd = editTextAddCityName.getText().toString();
                addCity(cityToAdd);
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_LONG).show();
            }
        });
        builder.create().show();
    }


    public void addCity(String cityName) {
        viewModel.getWeather(cityName);
    }

    private List<CityWeather> getCities() {
        return viewModel.getCities();
    }
}
