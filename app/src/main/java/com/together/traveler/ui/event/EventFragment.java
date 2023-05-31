package com.together.traveler.ui.event;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.together.traveler.R;
import com.together.traveler.databinding.FragmentEventBinding;
import com.together.traveler.model.Event;
import com.together.traveler.ui.event.ticket.TicketDialog;
import com.together.traveler.ui.main.home.HomeViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

import pub.devrel.easypermissions.EasyPermissions;

public class EventFragment extends Fragment implements TicketDialog.OnImageLoadedListener {
    private final String TAG = "EventFragment";

    private FragmentEventBinding binding;
    private EventViewModel eventViewModel;
    private TicketDialog dialog;

    private NestedScrollView scrollView;
    private final ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.READ_CALENDAR, false)) && Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.WRITE_CALENDAR, false))) {
                    alertSaveInCalendar();
                }
            });


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollView = binding.eventNsv;
        final View childView = view.findViewById(R.id.eventMap);
        final ImageView image = binding.eventIvImage;
        final TextView name = binding.ticketTvName;
        final TextView location = binding.eventTvLocation;
        final TextView startDate = binding.eventTvDate;
        final TextView endDate = binding.eventTvTime;
        final TextView description = binding.eventTvDescription;
        final TextView moreButton = binding.eventTvMore;
        final TextView category = binding.eventTvCategory;
        final TextView tags = binding.eventTvTags;
        final Button bottomButton = binding.eventBtnBottom;
        final ImageButton backButton = binding.eventIBtnBack;
        final ImageButton saveButton = binding.eventIBtnSave;
        final ChipGroup chipGroup = binding.eventChgTags;
        final FragmentContainerView userCard = binding.eventUser;
        final FragmentContainerView mapCard = binding.eventMap;
        int maxLines = description.getMaxLines();

        childView.setClickable(false);
        if (getArguments() != null) {
            Log.d("asd", "onCreateView: " + getArguments());
            eventViewModel.setData(getArguments().getParcelable("cardData"));
            eventViewModel.setUserId(getArguments().getString("userId"));
        }

        eventViewModel.getData().observe(getViewLifecycleOwner(), data -> {
            String imageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", data.getImgId());
            Log.i("asd", "onCreateView: " + imageUrl);
            Glide.with(requireContext()).load(imageUrl).into(image);
            name.setText(data.getTitle());
            location.setText(data.getLocation());
            startDate.setText(String.format("From %s", data.getStartDate()));
            endDate.setText(String.format("To %s", data.getEndDate()));
            category.setText(data.getCategory());
            description.setText(data.getDescription());

            if (data.isEnrolled()) {
                bottomButton.setText(R.string.event_button_ticket);
                bottomButton.setBackgroundColor(requireContext().getResources().getColor(R.color.secondary_color));
            } else if (data.isUserOwned()) {
                bottomButton.setText(R.string.event_button_check_tickets);
                bottomButton.setBackgroundColor(requireContext().getResources().getColor(R.color.check_btn_color));
            } else {
                bottomButton.setText(R.string.event_button_enroll);
                bottomButton.setBackgroundColor(requireContext().getResources().getColor(R.color.primary_color));
            }

            if (data.isSaved()) {
                saveButton.setImageResource(R.drawable.favorite);
            } else {
                saveButton.setImageResource(R.drawable.favorite_border);
            }

            if (data.getTags().size() > 0){
                tags.setVisibility(View.VISIBLE);
                chipGroup.removeAllViews();
                for (int i = 0; i < data.getTags().size(); i++) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(data.getTags().get(i));
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    chipGroup.addView(chip);
                }
            }else{
                tags.setVisibility(View.GONE);
            }

            ViewTreeObserver vto = description.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int lineCount = description.getLineCount();
                    if (description.getMaxLines() == lineCount) {
                        moreButton.setVisibility(View.VISIBLE);
                    } else {
                        moreButton.setVisibility(View.GONE);
                    }
                    ViewTreeObserver vto = description.getViewTreeObserver();
                    vto.removeOnGlobalLayoutListener(this);
                }
            });
        });

        saveButton.setOnClickListener(v -> eventViewModel.save());
        bottomButton.setOnClickListener(v -> {
            if (v instanceof Button) {
                Button button = (Button) v;
                String buttonText = button.getText().toString();
                if (buttonText.equals(getString(R.string.event_button_ticket))) {
                    TicketDialog dialog = new TicketDialog(requireContext(), eventViewModel.getData().getValue(), eventViewModel.getUserId());
                    dialog.show();
                } else if (buttonText.equals(getString(R.string.event_button_check_tickets))) {
                    if (eventViewModel.getData().getValue() == null)
                        return;
                    NavDirections action = EventFragmentDirections.actionEventFragmentToScanFragment(eventViewModel.getData().getValue().get_id());
                    NavHostFragment.findNavController(this).navigate(action);
                } else {
                    eventViewModel.enroll();
                    requestCalendarAndSetEvent();
                }
            }
        });
        backButton.setOnClickListener(v->backPress());
        moreButton.setOnClickListener(v -> {
            if (description.getMaxLines() == maxLines) {
                description.setMaxLines(Integer.MAX_VALUE);
                moreButton.setText(R.string.event_read_less);
            } else {
                description.setMaxLines(maxLines);
                moreButton.setText(R.string.event_read_more);
            }
        });
        userCard.setOnClickListener(v -> {
            NavDirections action = EventFragmentDirections.actionEventFragmentToUserFragment(eventViewModel.getData().getValue().getUser().get_id());
            NavHostFragment.findNavController(this).navigate(action);
        });

    }

    private void requestCalendarAndSetEvent() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.WRITE_CALENDAR) && EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_CALENDAR)) {
            // Permission already granted
            alertSaveInCalendar();
        } else {
            // Request permission
            requestMultiplePermissionsLauncher.launch(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR});
        }
    }

    private void alertSaveInCalendar() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.event_alert_title)
                .setMessage(R.string.event_alert_message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    saveEventInCalendar();
                    callDialogWithListener();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void callDialogWithListener() {
        dialog = new TicketDialog(requireContext(), eventViewModel.getData().getValue(), eventViewModel.getUserId());
        dialog.setOnImageLoadedListener(this);
        dialog.show();
    }

    private void saveEventInCalendar() {
        ContentValues calendarEvent = new ContentValues();
        Event event = eventViewModel.getData().getValue();
        SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy, HH:mm", Locale.ENGLISH);
        Date start, end;
        if (event != null) {
            try {
                String[] projection = new String[]{CalendarContract.Calendars._ID};
                Cursor cursor = requireContext().getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    long calendarId = cursor.getLong(0);
                    calendarEvent.put(CalendarContract.Events.CALENDAR_ID, calendarId);
                    cursor.close();
                }
                start = format.parse(event.getStartDate());
                end = format.parse(event.getEndDate());
                String coordinates = String.format(Locale.US, "%.6f,%.6f", event.getLatitude(), event.getLongitude());

                String location = event.getLocation() + "  " + coordinates;

                calendarEvent.put(CalendarContract.Events.TITLE, event.getTitle());
                calendarEvent.put(CalendarContract.Events.EVENT_LOCATION, location);
                calendarEvent.put(CalendarContract.Events.DTSTART, start != null ? start.getTime() : 0);
                calendarEvent.put(CalendarContract.Events.DTEND, end != null ? end.getTime() : 0);
                calendarEvent.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
                calendarEvent.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

                Uri uri = requireContext().getContentResolver().insert(CalendarContract.Events.CONTENT_URI, calendarEvent);
                long eventId = Long.parseLong(uri.getLastPathSegment());
                Toast.makeText(requireContext(), "Event added to calendar", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "saveEventInCalendar: " + eventId);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(TAG, "onCreateView: ", e);
            }
        }
    }

    private CompletableFuture<File> createPdfFromEvent() {
        CompletableFuture<File> future = new CompletableFuture<>();

        View rootView = dialog.getWindow().getDecorView().getRootView();
        rootView.findViewById(R.id.ticketIBtnBack).setVisibility(View.GONE);
        rootView.post(() -> {
            PdfDocument document = new PdfDocument();
            int pageWidth = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.9);
            int pageHeight = (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.95);
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

            PdfDocument.Page page = document.startPage(pageInfo);

            int measureWidth = View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY);
            int measureHeight = View.MeasureSpec.makeMeasureSpec(pageHeight, View.MeasureSpec.EXACTLY);
            rootView.measure(measureWidth, measureHeight);
            rootView.layout(0, 0, pageWidth, pageHeight);

            rootView.draw(page.getCanvas());

            document.finishPage(page);

            File pdfFile = new File(requireContext().getFilesDir(), "Ticket for " + eventViewModel.getData().getValue().getTitle() + ".pdf");

            try {
                FileOutputStream outputStream = new FileOutputStream(pdfFile);
                document.writeTo(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            } finally {
                document.close();
            }

            future.complete(pdfFile);
        });

        return future;
    }


    private void saveTicketInDownloads() {
        createPdfFromEvent().thenAccept(pdfFile ->{
            Log.d(TAG, "saveTicketInDownloads: " + pdfFile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 and above
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, "Ticket for " + eventViewModel.getData().getValue().getTitle() + ".pdf");
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.IS_PENDING, 1);

                ContentResolver resolver = requireContext().getContentResolver();
                Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                Uri item = resolver.insert(collection, values);

                try {
                    @SuppressLint("Recycle") ParcelFileDescriptor pfd = resolver.openFileDescriptor(item, "w");
                    FileInputStream fis = new FileInputStream(pdfFile);
                    FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fis.close();
                    fos.close();

                    values.clear();
                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                    resolver.update(item, values, null, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "saveTicketInDownloads: ", e);
                }
            } else {
                // Android 9 and below
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File outputFile = new File(downloadsDir, "Ticket for " + eventViewModel.getData().getValue().getTitle() + ".pdf");

                try {
                    FileInputStream fis = new FileInputStream(pdfFile);
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fis.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } );
    }

    @Override
    public void onResume() {
        BottomNavigationView bottomNavigationView =requireActivity().findViewById(R.id.nvMain);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.homeFragment);
        menuItem.setChecked(true);
        super.onResume();
    }

    @Override
    public void onImageLoaded() {
        saveTicketInDownloads();
    }

    public void scrollUp(){
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_UP));
    }
    public void scrollDown(){
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
    public void backPress(){
        NavHostFragment.findNavController(this).navigateUp();
    }
}
