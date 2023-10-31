package com.purificadora.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class CustPrograssbar {
    public static ProgressDialog progressDialog;

    public void prograssCreate(Context context) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            } else {
                progressDialog = new ProgressDialog(context);
                if (progressDialog != null && !progressDialog.isShowing()) {

                    progressDialog.setMessage("Cargando información...");
                    progressDialog.show();
                }
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    public void closePrograssBar() {
        if (progressDialog != null) {
            try {
                progressDialog.cancel();
            } catch (Exception e) {
            }
        }
    }
}
