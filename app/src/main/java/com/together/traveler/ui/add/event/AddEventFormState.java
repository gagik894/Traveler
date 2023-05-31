package com.together.traveler.ui.add.event;

import androidx.annotation.Nullable;

public class AddEventFormState {
    @Nullable
    private final Integer titleError;
    @Nullable
    private final Integer descriptionError;
    @Nullable
    private final Integer locationError;
    @Nullable
    private final Integer startDateError;
    @Nullable
    private final Integer endDateError;
    @Nullable
    private final Integer ticketsError;
    @Nullable
    private Integer imageError;

    private final boolean isDataValid;

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
