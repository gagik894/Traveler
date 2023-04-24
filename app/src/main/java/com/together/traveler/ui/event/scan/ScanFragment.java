package com.together.traveler.ui.event.scan;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.together.traveler.databinding.FragmentScanBinding;

import java.util.List;

public class ScanFragment extends Fragment implements DecoratedBarcodeView.TorchListener, TicketCheckResponseDialog.TicketCheckResponseDialogListener {
    private DecoratedBarcodeView barcodeView;
    private ScanViewModel scanViewModel;
    private ProgressBar progressBar;

    private final ActivityResultLauncher<String[]> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                if (Boolean.TRUE.equals(permissions.get(Manifest.permission.CAMERA))) {
                    // Permission granted
                } else {
                    // Permission denied
                }
            });

    String[] permissions = {Manifest.permission.CAMERA};


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        scanViewModel = new ViewModelProvider(requireActivity()).get(ScanViewModel.class);
        FragmentScanBinding binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        progressBar = binding.scanProgressBar;
        barcodeView = binding.scanBarcodeScanner;
        requestCameraPermissionLauncher.launch(permissions);

        barcodeView.setTorchListener(this);

        if (getArguments() != null) {
            scanViewModel.set_id(getArguments().getString("_id"));
        }

        progressBar.setVisibility(View.GONE);

        scanViewModel.getCheckTicketResponse().observe(getViewLifecycleOwner(), data -> {
            progressBar.setVisibility(View.GONE);
            if (data != null) {
                if (data.getUser() == null) {
                    Toast.makeText(requireContext(), "Wrong Qr code", Toast.LENGTH_SHORT).show();
                    barcodeView.resume();
                } else {
                    TicketCheckResponseDialog dialog = new TicketCheckResponseDialog(requireContext(), data);
                    dialog.setListener(ScanFragment.this);
                    dialog.show();
                }
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("Please scan QR");
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }


    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            scanViewModel.checkTicket(result.getText());
            barcodeView.pauseAndWait();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onTicketCheckResponseDialogDismissed() {
        Log.d("asd", "onTicketCheckResponseDialogDismissed: ");
        barcodeView.resume();
    }
}