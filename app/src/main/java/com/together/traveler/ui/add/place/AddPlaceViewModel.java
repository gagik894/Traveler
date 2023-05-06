package com.together.traveler.ui.add.place;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.context.AppContext;
import com.together.traveler.model.Place;
import com.together.traveler.requests.ApiClient;
import com.together.traveler.requests.ApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlaceViewModel extends ViewModel {
    private final String TAG = "AddPlaceViewModel";

    private final MutableLiveData<Place> data;
    private final MutableLiveData<ArrayList<String>> categories;
    private final MutableLiveData<Boolean> isValid;
    private final ApiService apiService;

    public AddPlaceViewModel() {
        data = new MutableLiveData<>();
        categories = new MutableLiveData<>(new ArrayList<>());
        isValid = new MutableLiveData<>(false);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        this.data.setValue(new Place());
        fetchCategories();
    }


    public void dataChanged(String name, String location, String description) {
        Place current = data.getValue();
        Objects.requireNonNull(current).setName(name);
        Objects.requireNonNull(current).setLocation(location);
        Objects.requireNonNull(current).setDescription(description);
        checkValid(current);
    }


    public void setEventImage(Bitmap image) {
        Place current = data.getValue();
        Objects.requireNonNull(current).setImageBitmap(image);
        this.data.setValue(current);
        checkValid(current);
    }

    public void setEventLocation(double latitude, double longitude) {
        Place current = data.getValue();
        Objects.requireNonNull(current).setLongitude(longitude);
        Objects.requireNonNull(current).setLatitude(latitude);
        this.data.setValue(current);
        checkValid(current);
    }

    public void setEventCategory(String eventCategory) {
        Log.i(TAG, "setEventCategory: " + eventCategory);
        Place current = data.getValue();
        Objects.requireNonNull(current).setCategory(eventCategory);
        this.data.setValue(current);
        checkValid(current);
    }

    public void create() {
        Place event = data.getValue();
        if (event == null) {
            // Handle error, event is null
            return;
        }

        File file = saveBitmapToFile(event.getImageBitmap());
        if (!file.exists()) {
            // Handle error, file does not exist
            return;
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        RequestBody requestBodyName = RequestBody.create(MediaType.parse("text/plain"), event.getName());
        RequestBody requestBodyDescription = RequestBody.create(MediaType.parse("text/plain"), event.getDescription());
        RequestBody requestBodyLocation = RequestBody.create(MediaType.parse("text/plain"), event.getLocation());
        RequestBody requestBodyLatitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(event.getLatitude()));
        RequestBody requestBodyLongitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(event.getLongitude()));
        RequestBody requestBodyCategory = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(event.getCategory()));

        if (apiService == null) {
            // Handle error, ApiService is null
            return;
        }

        Call<ResponseBody> call = apiService.uploadPlaceFile(
                body,
                requestBodyName,
                requestBodyDescription,
                requestBodyLocation,
                requestBodyLatitude,
                requestBodyLongitude,
                requestBodyCategory
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.i(TAG, "onResponse: " + response + response.body());
                if (response.isSuccessful()) {
                    // Handle success response
                } else {
                    // Handle error response
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public MutableLiveData<ArrayList<String>> getCategories() {
        return categories;
    }

    public LiveData<Place> getData() {
        return data;
    }

    public MutableLiveData<Boolean> isValid() {
        return isValid;
    }

    private void checkValid(Place current) {
        isValid.setValue(!Objects.equals(current.getName(), "") && !Objects.equals(current.getLocation(), "")
                && !Objects.equals(current.getDescription(), "") && !Objects.equals(current.getCategory(), "")
                && current.getImageBitmap() != null);
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        Context context = AppContext.getContext();
        File file = new File(context.getCacheDir(), "image.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void fetchCategories(){
        apiService.getCategories("places").enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> categoriesResponse = response.body();
                    categories.setValue((ArrayList<String>) categoriesResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                // Handle the error
            }
        });
    }
}
