package com.linksrussia.tsup.workerapp.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.linksrussia.tsup.workerapp.App;
import com.linksrussia.tsup.workerapp.R;
import com.linksrussia.tsup.workerapp.activities.MeasurementActivity;
import com.linksrussia.tsup.workerapp.activities.SelectDeviceActivity;
import com.linksrussia.tsup.workerapp.activities.SeriesActivity;
import com.linksrussia.tsup.workerapp.db.dao.SeriesDao;
import com.linksrussia.tsup.workerapp.db.entities.Series;

public class DialogUtil {

    public Dialog onCreateNoDeviceForkDialog(MeasurementActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Нет подключения к прибору с которого можно получать данные.")
                .setPositiveButton("Выбрать прибор", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(activity, SelectDeviceActivity.class));
                    }
                })
                .setNegativeButton("Пока не надо", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public Dialog onCreateAddSeriesDialog(SeriesActivity activity) {
        final SeriesDao seriesDao = App.getAppDatabase().seriesDao();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogContent = inflater.inflate(R.layout.dialog_series_add, null);
        builder.setView(dialogContent)
                // Add action buttons
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText seriesNameEditText = dialogContent.findViewById(R.id.seriesNameEditText);
                        String text = seriesNameEditText.getText().toString();
                        if (!text.trim().isEmpty()) {
                            seriesDao.insertAll(new Series(text));
                            activity.renderData();
                        }
                        seriesNameEditText.setText("");
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText seriesNameEditText = dialogContent.findViewById(R.id.seriesNameEditText);
                        seriesNameEditText.setText("");
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}
