package com.together.traveler.ui.add.place;

import androidx.annotation.Nullable;

public class AddPlaceFormState {
    @Nullable
    private final Integer titleError;
    @Nullable
    private final Integer descriptionError;
    @Nullable
    private final Integer locationError;
    @Nullable
    private final Integer openTimesError;
    @Nullable
    private final Integer urlError;
    @Nullable
    private final Integer phoneError;
    @Nullable
    private Integer imageError;

    private final boolean isDataValid;

    AddPlaceFormState(@Nullable Integer titleError, @Nullable Integer descriptionError, @Nullable Integer locationError, @Nullable Integer openTimesError, @Nullable Integer urlError, @Nullable Integer phoneError, @Nullable Integer imageError) {
        this.titleError = titleError;
        this.descriptionError = descriptionError;
        this.locationError = locationError;
        this.openTimesError = openTimesError;
        this.urlError = urlError;
        this.phoneError = phoneError;
        this.imageError = imageError;
        this.isDataValid = false;
    }

    AddPlaceFormState(boolean isDataValid) {
        this.titleError = null;
        this.descriptionError = null;
        this.locationError = null;
        this.openTimesError = null;
        this.urlError = null;
        this.phoneError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getTitleError() {
        return titleError;
    }

    @Nullable
    public Integer getDescriptionError() {
        return descriptionError;
    }

    @Nullable
    public Integer getLocationError() {
        return locationError;
    }

    @Nullable
    public Integer getOpenTimesError() {
        return openTimesError;
    }

    @Nullable
    public Integer getUrlError() {
        return urlError;
    }

    @Nullable
    public Integer getPhoneError() {
        return phoneError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
