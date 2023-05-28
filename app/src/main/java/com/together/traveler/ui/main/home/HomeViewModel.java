package com.together.traveler.ui.main.home;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Event;
import com.together.traveler.model.EventsResponse;
import com.together.traveler.web.ApiClient;
import com.together.traveler.web.ApiService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel implements LocationProvider.OnLocationChangedListener{
    private final String TAG = "HomeViewModel";

    private final ArrayList<Event> allEvents;
    private final ArrayList<Event> categoryFilteredEvents;
    private final MutableLiveData<ArrayList<Event>> filteredEvents;
    private final MutableLiveData<ArrayList<String>> categories;
    private final MutableLiveData<List<Integer>> selectedCategories;
    private final MutableLiveData<String> locationName;
    private final MutableLiveData<Boolean> categoriesVisibility;
    private final MutableLiveData<Boolean> loading;
    private String userId;
    private CharSequence constraint;
    private final ApiService apiService;
    private final LocationProvider locationProvider;

    public HomeViewModel() {
        constraint = "";
        allEvents = new ArrayList<>();
        categoryFilteredEvents = new ArrayList<>();
        filteredEvents = new MutableLiveData<>(new ArrayList<>());
        categories = new MutableLiveData<>(new ArrayList<>());
        selectedCategories = new MutableLiveData<>(new ArrayList<>());
        categoriesVisibility = new MutableLiveData<>(false);
        loading = new MutableLiveData<>(false);
        locationName = new MutableLiveData<>();
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        locationProvider = new LocationProvider(this);
        fetchCategories();
    }

    public void getLocationByGPS() {
        locationProvider.getLastKnownLocation();
    }

    public void changeCategoriesVisibility() {
        this.categoriesVisibility.setValue(Boolean.FALSE.equals(this.categoriesVisibility.getValue()));
    }

    public void setLocationName(String locationName){
        this.locationName.postValue(locationName);
        fetchEvents(locationName);
    }

    public LiveData<ArrayList<Event>> getAllEvents() {
        return filteredEvents;
    }

    public MutableLiveData<ArrayList<String>> getCategories() {
        return categories;
    }

    public MutableLiveData<Boolean> getCategoriesVisibility() {
        return categoriesVisibility;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<List<Integer>> getSelectedCategories() {
        return selectedCategories;
    }

    public LiveData<String> getLocationName() {
        return locationName;
    }

    public void fetch(){
        fetchEvents(locationName.getValue());
    }

    private void fetchEvents(String location) {
        loading.postValue(true);
        apiService.getEvents(null, location).enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventsResponse> call, @NonNull Response<EventsResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    EventsResponse eventsResponse = response.body();
                    List<Event> events = eventsResponse != null ? eventsResponse.getData() : null;

                    AsyncTask.execute(() -> {
                        HomeViewModel.this.filteredEvents.postValue((ArrayList<Event>) events);
                        HomeViewModel.this.allEvents.clear();
                        HomeViewModel.this.allEvents.addAll(events);
                        userId = eventsResponse != null ? eventsResponse.getUserId() : null;
                        HomeViewModel.this.filterEventsByCategory();
                        loading.postValue(false);
                    });
                } else {
                    Log.e(TAG, "fetchEvents request failed with code: " + response.code() + response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchEvents request failed with error: " + t.getMessage());
            }
        });
    }


    private void fetchCategories(){
        apiService.getCategories("events").enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> categoriesResponse = response.body();
                    Log.i(TAG, "onResponse: " + categoriesResponse);
                    AsyncTask.execute(() -> categories.postValue((ArrayList<String>) categoriesResponse));

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                // Handle the error
            }
        });
    }

    public String getUserId() {
        return userId;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }


    public void addOrRemoveSelectedCategories(String checkedChip) {
        Integer selectedId = Objects.requireNonNull(categories.getValue()).indexOf(checkedChip);
        List<Integer> selected = this.selectedCategories.getValue();
        if (selected != null) {
            if (selected.contains(selectedId)){
                selected.remove(selectedId);
            }else{
                selected.add(selectedId);
            }
        }
        this.selectedCategories.setValue(selected);
        filterEventsByCategory();
    }

    public void filterEventsByCategory() {
        ArrayList<Event> allEvents = this.allEvents;
        if (allEvents != null) {
            List<Integer> selectedCategories = this.selectedCategories.getValue();
            if (selectedCategories == null || selectedCategories.size() == 0) {
                HomeViewModel.this.categoryFilteredEvents.clear();
                HomeViewModel.this.categoryFilteredEvents.addAll(allEvents);
                HomeViewModel.this.filterBySearchAndTags(this.constraint);
                return;
            }

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                ArrayList<Event> filteredEvents = new ArrayList<>();

                for (Event event : allEvents) {
                    for (int i = 0; i < selectedCategories.size(); i++) {
                        String category = Objects.requireNonNull(this.categories.getValue()).get(selectedCategories.get(i));
                        if (Objects.equals(event.getCategory(), category)) {
                            filteredEvents.add(event);
                            break; // Skip to the next event
                        }
                    }
                }

//                HomeViewModel.this.filteredEvents.postValue(filteredEvents);

                HomeViewModel.this.categoryFilteredEvents.clear();
                HomeViewModel.this.categoryFilteredEvents.addAll(filteredEvents);
                HomeViewModel.this.filterBySearchAndTags(this.constraint);

            });

            executorService.shutdown();
        }
    }

    public void filterBySearchAndTags(CharSequence constraint) {
        loading.postValue(true);
        this.constraint = constraint;
        ArrayList<Event> allEvents = this.categoryFilteredEvents;
        if (allEvents != null) {
            if (constraint == null || constraint.length() == 0) {
                HomeViewModel.this.loading.postValue(false);
                HomeViewModel.this.filteredEvents.postValue(allEvents);
                return;
            }

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                ArrayList<Event> filteredEvents = new ArrayList<>();

                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Event event : allEvents) {
                    boolean eventAdded = false;
                    if (event.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredEvents.add(event);
                        eventAdded = true;
                    } else {
                        for (String tag : event.getTags()) {
                            if (tag.contains(filterPattern)) {
                                filteredEvents.add(event);
                                eventAdded = true;
                                break;
                            }
                        }
                    }
                    if (!eventAdded && event.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredEvents.add(event);
                    }
                }
                Log.i(TAG, "filterBySearchAndTags: false ");
                HomeViewModel.this.loading.postValue(false);
                HomeViewModel.this.filteredEvents.postValue(filteredEvents);

            });
            executorService.shutdown();
        }
    }

    public void getLocationByIP() {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                URL url = null;
                try {
                    url = new URL("https://ipapi.co/city/");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                BufferedReader reader;
                String cityName;
                try {
                    reader = new BufferedReader(new InputStreamReader(url != null ? url.openStream() : null));
                    cityName = reader.readLine();
                    setLocationName(cityName);
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            executorService.shutdown();
    }

    @Override
    public void onLocationChanged(String cityName) {
        this.setLocationName(cityName);
    }
}
