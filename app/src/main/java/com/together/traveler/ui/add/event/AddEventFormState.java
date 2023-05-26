package com.together.traveler.ui.add.event;

import androidx.annotation.Nullable;

public class AddEventFormState {
    @Nullable
    private Integer titleError;
    @Nullable
    private Integer descriptionError;
    @Nullable
    private Integer locationError;
    @Nullable
    private Integer startDateError;
    @Nullable
    private Integer endDateError;
    @Nullable
    private Integer ticketsError;
    @Nullable
    private Integer imageError;

    private boolean isDataValid;

    AddEventFormState(@Nullable Integer titleError, @Nullable Integer descriptionError, @Nullable Integer locationError, @Nullable Integer startDateError, @Nullable Integer endDateError, @Nullable Integer ticketsError, @Nullable Integer imageError) {
        this.titleError = titleError;
        this.descriptionError = descriptionError;
        this.locationError = locationError;
        this.startDateError = startDateError;
        this.endDateError = endDateError;
        this.ticketsError = ticketsError;
        this.imageError = imageError;
        this.isDataValid = false;
    }

    AddEventFormState(boolean isDataValid) {
        this.titleError = null;
        this.descriptionError = null;
        this.locationError = null;
        this.startDateError = null;
        this.endDateError = null;
        this.ticketsError = null;
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
    public Integer getStartDateError() {
        return startDateError;
    }

    @Nullable
    public Integer getEndDateError() {
        return endDateError;
    }

    @Nullable
    public Integer getTicketsError() {
        return ticketsError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
