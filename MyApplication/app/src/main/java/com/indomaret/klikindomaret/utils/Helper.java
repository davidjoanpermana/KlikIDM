package com.indomaret.klikindomaret.utils;

import android.content.pm.PackageManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by USER on 8/18/2017.
 */

public class Helper {
    public static void moveMapCamera(GoogleMap gmap, LatLng position, int zoom) {
        if (position != null) {
            gmap.moveCamera(CameraUpdateFactory.newLatLng(position));
            gmap.animateCamera(CameraUpdateFactory.zoomIn());
            gmap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 5000, null);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position)      // Sets the center of the map to Mountain View
                    .zoom(zoom)              // Sets the zoom
                    .build();                // Creates a CameraPosition from the builder
            gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }


    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                              String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

//    public static void requestPermission(FragmentManager fragmentManager, Activity activity, int requestId,
//                                         String permission, boolean finishActivity) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
//            // Display a dialog with rationale.
//            Helper.RationaleDialog.newInstance(requestId, finishActivity)
//                    .show(fragmentManager, "dialog");
//        } else {
//            // Location permission has not been granted yet, request it.
//            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestId);
//
//        }
//    }
//
//
//    public static class RationaleDialog extends DialogFragment {
//
//        private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";
//
//        private static final String ARGUMENT_FINISH_ACTIVITY = "finish";
//
//        private boolean mFinishActivity = false;
//
//        /**
//         * Creates a new instance of a dialog displaying the rationale for the use of the location
//         * permission.
//         * <p/>
//         * The permission is requested after clicking 'ok'.
//         *
//         * @param requestCode    Id of the request that is used to request the permission. It is
//         *                       returned to the
//         *                       {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
//         * @param finishActivity Whether the calling Activity should be finished if the dialog is
//         *                       cancelled.
//         */
//        public static RationaleDialog newInstance(int requestCode, boolean finishActivity) {
//            Bundle arguments = new Bundle();
//            arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
//            arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);
//            RationaleDialog dialog = new RationaleDialog();
//            dialog.setArguments(arguments);
//            return dialog;
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            Bundle arguments = getArguments();
//            final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);
//            mFinishActivity = arguments.getBoolean(ARGUMENT_FINISH_ACTIVITY);
//
//            return new AlertDialog.Builder(getActivity())
//                    .setMessage(R.string.permission_rationale_location)
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // After click on Ok, request the permission.
//                            ActivityCompat.requestPermissions(getActivity(),
//                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                    requestCode);
//                            // Do not finish the Activity while requesting permission.
//                            mFinishActivity = false;
//                        }
//                    })
//                    .setNegativeButton(android.R.string.cancel, null)
//                    .create();
//        }
//
//        @Override
//        public void onDismiss(DialogInterface dialog) {
//            super.onDismiss(dialog);
//            if (mFinishActivity) {
//                try {
//                    Toast.makeText(getActivity(),
//                            R.string.permission_required_toast,
//                            Toast.LENGTH_SHORT)
//                            .show();
//                    getActivity().finish();
//                } catch (Exception e) {
////                    Crashlytics.logException(e);
//                }
//            }
//        }
//    }
}
