package com.example.kiosk_electric_charger_pole_kc_rehearsal.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDialog {
//    public interface ConfirmDialogListener {
//        void onConfirm();
//    }
    Context mContext;

    public ErrorDialog(Context context) {
        mContext = context;
    }

//    public static void showDialog(Context context, String title, String message, ConfirmDialogListener listener) {
    public void showDialog( String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                listener.onConfirm();
                dialog.cancel();
            }
        });



        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void destoryClass(){
        mContext = null;
    }

}
