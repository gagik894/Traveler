package com.together.traveler.ui.add.event;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.context.AppContext;
import com.together.traveler.model.Event;
import com.together.traveler.web.ApiClient;
import com.together.traveler.web.ApiService;

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

public class AddEventViewModel extends ViewModel {
    private final String TAG = "AddEventViewModel";

    private final MutableLiveData<Event> data;
    private final MutableLiveData<ArrayList<String>> categories;
    private final MutableLiveData<Boolean> isTagValid;
    private final MutableLiveData<Boolean> isValid;
    private final ApiService apiService;


    public AddEventViewModel() {
        data = new MutableLiveData<>();
        categories = new MutableLiveData<>(new ArrayList<>());
        isValid = new MutableLiveData<>(false);
        isTagValid = new MutableLiveData<>(false);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        this.data.setValue(new Event());
        fetchCategories();
    }


    public void dataChanged(String tittle, String location, String description, int count) {
        Event current = data.getValue();
        Objects.requireNonNull(current).setTitle(tittle);
        Objects.requireNonNull(current).setLocation(location);
        Objects.requireNonNull(current).setDescription(description);
        Objects.requireNonNull(current).setTicketsCount(count);
        checkValid(current);
    }

    public void setStartDateAndTime(String dateTimeString) {
        Objects.requireNonNull(data.getValue()).setStartDate(dateTimeString);
        this.data.setValue(data.getValue());
    }

    public void setEndDateAndTime(String dateTimeString) {
        Objects.requireNonNull(data.getValue()).setEndDate(dateTimeString);
        this.data.setValue(data.getValue());
    }

    public void setEventImage(Bitmap image) {
        Event current = data.getValue();
        Objects.requireNonNull(current).setImageBitmap(image);
        this.data.setValue(current);
        checkValid(current);
    }
    public void setEventLocation(double latitude, double longitude) {
        Event current = data.getValue();
        Objects.requireNonNull(current).setLongitude(longitude);
        Objects.requireNonNull(current).setLatitude(latitude);
        this.data.setValue(current);
        checkValid(current);
    }
    public void setEventCategory(String eventCategory) {
        Log.i(TAG, "setEventCategory: " + eventCategory);
        Event current = data.getValue();
        Objects.requireNonNull(current).setCategory(eventCategory);
        this.data.setValue(current);
        checkValid(current);
    }
    public void checkTag(String tag) {
        if (tag.trim().length()>0) {
            isTagValid.setValue(true);
        }else{
            isTagValid.setValue(false);
        }
    }
    public void create() {
        Event event = data.getValue();
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
        RequestBody requestBodyTitle = RequestBody.create(MediaType.parse("text/plain"), event.getTitle());
        RequestBody requestBodyDescription = RequestBody.create(MediaType.parse("text/plain"), event.getDescription());
        RequestBody requestBodyStartDate = RequestBody.create(MediaType.parse("text/plain"), event.getStartDate());
        RequestBody requestBodyEndDate = RequestBody.create(MediaType.parse("text/plain"), event.getEndDate());
        RequestBody requestBodyLocation = RequestBody.create(MediaType.parse("text/plain"), event.getLocation());
        RequestBody requestBodyLatitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(event.getLatitude()));
        RequestBody requestBodyLongitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(event.getLongitude()));
        RequestBody requestBodyCategory = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(event.getCategory()));

        List<String> tags = event.getTags(); // Retrieve the list of tags

        List<MultipartBody.Part> tagParts = new ArrayList<>();

        for (String tag : tags) {
            RequestBody tagBody = RequestBody.create(MediaType.parse("text/plain"), tag);
            MultipartBody.Part tagPart = MultipartBody.Part.createFormData("tags[]", tag);
            tagParts.add(tagPart);
        }

        if (apiService == null) {
            // Handle error, ApiService is null
            return;
        }

        Call<ResponseBody> call = apiService.uploadEventFile(
                body,
                requestBodyTitle,
                requestBodyDescription,
                requestBodyStartDate,
                requestBodyEndDate,
                requestBodyLocation,
                requestBodyLatitude,
                requestBodyLongitude,
                requestBodyCategory,
                tagParts
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

    public LiveData<Event> getData() {
        return data;
    }

    public MutableLiveData<Boolean> isValid() {
        return isValid;
    }

    public MutableLiveData<Boolean> getIsTagValid() {
        return isTagValid;
    }

    public String getStart(){
        return Objects.requireNonNull(this.data.getValue()).getStartDate();
    }

    public String getEnd(){
        return Objects.requireNonNull(this.data.getValue()).getEndDate();
    }

    public void addTag(String tag) {
        ArrayList<String> currentTags = (ArrayList<String>) Objects.requireNonNull(data.getValue()).getTags();
        if (currentTags != null) {
            currentTags.add(tag.trim());
        }
        this.data.getValue().setTags(currentTags);
        Log.i(TAG, "addTag: " +this.data.getValue().getTags());
        this.data.setValue(this.data.getValue());
    }

    public void deleteTag(String tag){
        ArrayList<String> currentTags = (ArrayList<String>) Objects.requireNonNull(data.getValue()).getTags();
        if (currentTags != null) {
            currentTags.remove(tag);
        }
        this.data.getValue().setTags(currentTags);
        Log.i(TAG, "deleteTag: " +this.data.getValue().getTags());
        this.data.setValue(this.data.getValue());
    }

    private void checkValid(Event current) {
        isValid.setValue(!Objects.equals(current.getTitle(), "") && !Objects.equals(current.getLocation(), "")
                && !Objects.equals(current.getStartDate(), "") && !Objects.equals(current.getEndDate(), "")
                && !Objects.equals(current.getDescription(), "") && !Objects.equals(current.getCategory(), "")
                && current.getTicketsCount() > 0
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
        apiService.getCategories("events").enqueue(new Callback<List<String>>() {
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