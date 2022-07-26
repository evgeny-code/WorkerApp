package com.linksrussia.tsup.workerapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.linksrussia.tsup.workerapp.BuildConfig;
import com.linksrussia.tsup.workerapp.db.entities.Measure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ShareUtil {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    public static void sendData(Activity activity, List<Measure> measures) {
        StringBuffer csvBuffer = new StringBuffer();

        csvBuffer.append("ID;");
        csvBuffer.append("Дата и время;");
        csvBuffer.append("CG;");
        csvBuffer.append("GJ;");
        csvBuffer.append('\n');

        for (Measure measure : measures) {
            csvBuffer.append(measure.id);
            csvBuffer.append(';');

            csvBuffer.append(dateFormat.format(new Date(measure.timeMills)));
            csvBuffer.append(';');

            csvBuffer.append(measure.cg);
            csvBuffer.append(';');

            csvBuffer.append(measure.gj);
            csvBuffer.append(';');

            csvBuffer.append('\n');
        }


        File outputDir = activity.getCacheDir(); // context being the Activity pointer
        try {
            File outputFile = File.createTempFile("measure-data-", ".csv", outputDir);
            writeToFile(outputFile, csvBuffer.toString(), activity);

            Uri uri = FileProvider.getUriForFile(
                    activity,
                    BuildConfig.APPLICATION_ID,
                    outputFile);


            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION & Intent.FLAG_ACTIVITY_NEW_TASK);
            share.setType("text/csv");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(share, "Отправить как файл"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeToFile(File outputFile, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
