package com.together.traveler.ui.add.place;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.together.traveler.R;
import com.together.traveler.context.AppContext;
import com.together.traveler.model.Place;
import com.together.traveler.retrofit.ApiClient;
import com.together.traveler.retrofit.ApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlaceViewModel extends ViewModel {
    private final String TAG = "AddPlaceViewModel";
    private final MutableLiveData<AddPlaceFormState> formState; // Form validation state
    private final MutableLiveData<Place> data; // Place data
    private final MutableLiveData<ArrayList<String>> categories; // Available categories
    private final MutableLiveData<Boolean> isValid; // Flag indicating if the form is valid
    private final ApiService apiService; // API service for category fetching

    public AddPlaceViewModel() {
        formState = new MutableLiveData<>();
        data = new MutableLiveData<>();
        categories = new MutableLiveData<>(new ArrayList<>());
        isValid = new MutableLiveData<>(false);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        this.data.setValue(new Place());
        fetchCategories(); // Fetch available categories
    }

    // Update the form state and check if the form is valid
    public void dataChanged(String name, String location, String description, String phone, String url) {
        Place current = data.getValue();
        Objects.requireNonNull(current).setName(name);
        current.setLocation(location);
        current.setDescription(description);
        current.setPhone(phone);
        current.setUrl(url);
        checkValid(current);
    }

    // Update the form state and check if the form is valid
    public void setEventImage(Bitmap image) {
        Place current = data.getValue();
        Objects.requireNonNull(current).setImageBitmap(image);
        this.data.setValue(current);
        checkValid(current);
    }

    // Set the event open times
    public void setEventOpenTimes(String[] openingTimes, String[] closingTimes, boolean[] isClosed, boolean isAlwaysOpen) {
        Place current = data.getValue();
        Objects.requireNonNull(current).setAlwaysOpen(isAlwaysOpen);
        Objects.requireNonNull(current).setOpeningTimes(openingTimes);
        Objects.requireNonNull(current).setClosingTimes(closingTimes);
        Objects.requireNonNull(current).setIsClosedDays(isClosed);
        this.data.setValue(current);
        checkValid(current);
    }

    // Set the event location
    public void setEventLocation(double latitude, double longitude) {
        Place current = data.getValue();
        Objects.requireNonNull(current).setLongitude(longitude);
        Objects.requireNonNull(current).setLatitude(latitude);
        this.data.setValue(current);
        checkValid(current);
    }

    // Set the event category
    public void setEventCategory(String eventCategory) {
        Log.i(TAG, "setEventCategory: " + eventCategory);
        Place current = data.getValue();
        Objects.requireNonNull(current).setCategory(eventCategory);
        this.data.setValue(current);
        checkValid(current);
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

    public MutableLiveData<AddPlaceFormState> getFormState() {
        return formState;
    }

    // Check if the opening times are valid
    public boolean areOpenTimesValid(Place current) {
        if (current.isAlwaysOpen()){
            return true;
        }
        return !Arrays.asList(current.getOpeningTimes()).contains(null)
                && !Arrays.asList(current.getClosingTimes()).contains(null)
                || IntStream.range(0, current.getIsClosedDays().length)
                .allMatch(i -> current.getIsClosedDays()[i] || current.getOpeningTimes()[i] != null)
                && IntStream.range(0, current.getIsClosedDays().length)
                .allMatch(i -> current.getIsClosedDays()[i] || current.getClosingTimes()[i] != null);


    }

    // Check if the URL is valid
    public boolean isUrlValid(Place current) {
        if (current.getUrl().trim().length() == 0){
            return true;
        }
        return current.getUrl().contains(".");
    }

    // Check if the phone number is valid
    public boolean isPhoneValid(Place current) {
        if (current.getPhone().length() == 0){
            return true;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(current.getPhone(), null);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            return false;
        }
    }

    // Check the overall validity of the form
    private void checkValid(Place current) {
        if (current.getName().trim().length() == 0){
            formState.setValue(new AddPlaceFormState(R.string.invalid_name,null,null,null,null,null,null ));
        }else if(current.getDescription().trim().length() <= 10){
            formState.setValue(new AddPlaceFormState(null,R.string.invalid_description,null,null,null,null,null ));
        }else if(Objects.equals(current.getLocation(), "")){
            formState.setValue(new AddPlaceFormState(null,null,R.string.invalid_location,null,null,null,null ));
        }else if(!areOpenTimesValid(current)) {
            formState.setValue(new AddPlaceFormState(null,null,null,R.string.invalid_dates,null,null,null ));
        }else if(!isUrlValid(current)){
            formState.setValue(new AddPlaceFormState(null,null,null,null,R.string.invalid_url,null,null ));
        }else if(!isPhoneValid(current)){
            formState.setValue(new AddPlaceFormState(null,null,null,null,null,R.string.invalid_phone,null ));
        }else if (current.getImageBitmap() == null){
            formState.setValue(new AddPlaceFormState(null,null,null,null,null,null,R.string.invalid_image ));
        }else{
            formState.setValue(new AddPlaceFormState(true));
        }
    }

    // Save the bitmap to a file
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

    // Fetch the available categories from the API
    private void fetchCategories() {
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

    // Create a new place
    public void create() {
        Place event = data.getValue();
        if (event == null) {
            return;
        }

        File file = saveBitmapToFile(event.getImageBitmap());
        if (!file.exists()) {
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
        RequestBody requestBodyPhone = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(event.getPhone()));
        RequestBody requestBodyUrl= RequestBody.create(MediaType.parse("text/plain"), String.valueOf(event.getUrl()));
        RequestBody requestBodyAlwaysOpen= RequestBody.create(MediaType.parse("text/plain"), String.valueOf(event.isAlwaysOpen()));

        String[] openingTimes = event.getOpeningTimes();
        String[] closingTimes = event.getClosingTimes();
        boolean[] isClosedDays = event.getIsClosedDays();
        List<MultipartBody.Part> openingTimeParts = null;
        List<MultipartBody.Part> closingTimeParts = null;
        List<MultipartBody.Part> isClosedDaysParts = null;
        if (!event.isAlwaysOpen()) {
            openingTimeParts = new ArrayList<>();
            for (String openingTime : openingTimes) {
                RequestBody openingTimeBody = RequestBody.create(MediaType.parse("text/plain"), openingTime);
                MultipartBody.Part openingTimePart = MultipartBody.Part.createFormData("openingTimes[]", openingTime);
                openingTimeParts.add(openingTimePart);
            }
            closingTimeParts = new ArrayList<>();
            for (String closingTime : closingTimes) {
                RequestBody closingTimeBody = RequestBody.create(MediaType.parse("text/plain"), closingTime);
                MultipartBody.Part closingTimePart = MultipartBody.Part.createFormData("closingTimes[]", closingTime);
                closingTimeParts.add(closingTimePart);
            }
            isClosedDaysParts = new ArrayList<>();
            for (Boolean isClosed : isClosedDays) {
                MultipartBody.Part isClosedDaysPart = MultipartBody.Part.createFormData("isClosedDays[]", String.valueOf(isClosed));
                isClosedDaysParts.add(isClosedDaysPart);
            }
        }

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
                requestBodyCategory,
                requestBodyPhone,
                requestBodyUrl,
                requestBodyAlwaysOpen,
                openingTimeParts,
                closingTimeParts,
                isClosedDaysParts
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


}