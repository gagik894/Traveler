package com.together.traveler.ui.add.event;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.R;
import com.together.traveler.context.AppContext;
import com.together.traveler.model.Event;
import com.together.traveler.retrofit.ApiClient;
import com.together.traveler.retrofit.ApiService;

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

    // MutableLiveData to hold the form state, event data, categories, and tag validity
    private final MutableLiveData<AddEventFormState> formState;
    private final MutableLiveData<Event> data;
    private final MutableLiveData<ArrayList<String>> categories;
    private final MutableLiveData<Boolean> isTagValid;
    private final MutableLiveData<Boolean> isValid;
    private final ApiService apiService;

    public AddEventViewModel() {
        // Initialize the MutableLiveData objects
        formState = new MutableLiveData<>();
        data = new MutableLiveData<>();
        categories = new MutableLiveData<>(new ArrayList<>());
        isValid = new MutableLiveData<>(false);
        isTagValid = new MutableLiveData<>(false);

        // Create an instance of the ApiService using the ApiClient
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Set initial values for the event data and fetch categories
        this.data.setValue(new Event());
        fetchCategories();
    }

    // Method to handle changes in the input fields and update the form state
    public void dataChanged(String title, String location, String description, int count) {
        Event current = data.getValue();
        Objects.requireNonNull(current).setTitle(title);
        Objects.requireNonNull(current).setLocation(location);
        Objects.requireNonNull(current).setDescription(description);
        Objects.requireNonNull(current).setTicketsCount(count);
        checkValid(current);
    }

    // Method to set the start date and time of the event
    public void setStartDateAndTime(String dateTimeString) {
        Objects.requireNonNull(data.getValue()).setStartDate(dateTimeString);
        this.data.setValue(data.getValue());
    }

    // Method to set the end date and time of the event
    public void setEndDateAndTime(String dateTimeString) {
        Objects.requireNonNull(data.getValue()).setEndDate(dateTimeString);
        this.data.setValue(data.getValue());
    }

    // Method to set the event image
    public void setEventImage(Bitmap image) {
        Event current = data.getValue();
        Objects.requireNonNull(current).setImageBitmap(image);
        this.data.setValue(current);
        checkValid(current);
    }

    // Method to set the event location
    public void setEventLocation(double latitude, double longitude) {
        Event current = data.getValue();
        Objects.requireNonNull(current).setLongitude(longitude);
        Objects.requireNonNull(current).setLatitude(latitude);
        this.data.setValue(current);
        checkValid(current);
    }

    // Method to set the event category
    public void setEventCategory(String eventCategory) {
        Log.i(TAG, "setEventCategory: " + eventCategory);
        Event current = data.getValue();
        Objects.requireNonNull(current).setCategory(eventCategory);
        this.data.setValue(current);
        checkValid(current);
    }

    // Method to check the validity of the tag
    public void checkTag(String tag) {
        if (tag.trim().length() > 0) {
            isTagValid.setValue(true);
        } else {
            isTagValid.setValue(false);
        }
    }

    // Method to create the event
    public void create() {
        Event event = data.getValue();
        if (event == null) {
            // Handle error, event is null
            return;
        }

        // Save the event image to a file
        File file = saveBitmapToFile(event.getImageBitmap());
        if (!file.exists()) {
            // Handle error, file does not exist
            return;
        }

        // Create the multipart request body parts for the event data
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

        // Create multipart request body parts for each tag
        for (String tag : tags) {
            RequestBody tagBody = RequestBody.create(MediaType.parse("text/plain"), tag);
            MultipartBody.Part tagPart = MultipartBody.Part.createFormData("tags[]", tag);
            tagParts.add(tagPart);
        }

        if (apiService == null) {
            // Handle error, ApiService is null
            return;
        }

        // Make the API call to upload the event file and data
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

    // Getter methods for the MutableLiveData objects
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

    public MutableLiveData<AddEventFormState> getFormState() {
        return formState;
    }

    public String getStart(){
        return Objects.requireNonNull(this.data.getValue()).getStartDate();
    }

    public String getEnd(){
        return Objects.requireNonNull(this.data.getValue()).getEndDate();
    }

    // Method to add a new tag
    public void addTag(String tag) {
        ArrayList<String> currentTags = (ArrayList<String>) Objects.requireNonNull(data.getValue()).getTags();
        if (currentTags != null) {
            currentTags.add(tag.trim());
        }
        this.data.getValue().setTags(currentTags);
        Log.i(TAG, "addTag: " +this.data.getValue().getTags());
        this.data.setValue(this.data.getValue());
    }

    // Method to delete a tag
    public void deleteTag(String tag){
        ArrayList<String> currentTags = (ArrayList<String>) Objects.requireNonNull(data.getValue()).getTags();
        if (currentTags != null) {
            currentTags.remove(tag);
        }
        this.data.getValue().setTags(currentTags);
        Log.i(TAG, "deleteTag: " +this.data.getValue().getTags());
        this.data.setValue(this.data.getValue());
    }

    // Method to check the validity of the event data
    private void checkValid(Event current) {
        if (current.getTitle().trim().length() <= 5){
            formState.setValue(new AddEventFormState(R.string.invalid_title,null,null,null,null,null,null ));
        }else if(current.getDescription().trim().length() <= 10){
            formState.setValue(new AddEventFormState(null,R.string.invalid_description,null,null,null,null,null ));
        }else if(Objects.equals(current.getLocation(), "")){
            formState.setValue(new AddEventFormState(null,null,R.string.invalid_location,null,null,null,null ));
        }else if(Objects.equals(current.getStartDate(), "")) {
            formState.setValue(new AddEventFormState(null,null,null,R.string.invalid_start_date,null,null,null ));
        }else if(Objects.equals(current.getEndDate(), "")){
            formState.setValue(new AddEventFormState(null,null,null,null,R.string.invalid_end_date,null,null ));
        }else if(current.getTicketsCount() == 0){
            formState.setValue(new AddEventFormState(null,null,null,null,null,R.string.invalid_tickets,null ));
        }else if (current.getImageBitmap() == null){
            formState.setValue(new AddEventFormState(null,null,null,null,null,null,R.string.invalid_image ));
        }else{
            formState.setValue(new AddEventFormState(true));
        }
    }

    // Method to save a bitmap to a file
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

    // Method to fetch categories from the API
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