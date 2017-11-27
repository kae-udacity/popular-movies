package com.example.android.popularmovies.util;

import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Provides helper methods for creating a {@link Date} object using the passed-in
 * pattern.
 */

public final class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    private DateUtils() {

    }

    public static Date getDate(Context context, String pattern, String unformattedDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat(pattern, Locale.getDefault()).parse(unformattedDate);
        } catch (ParseException e) {
            Log.e(TAG, context.getString(R.string.error_parsing_date), e);
        }

        return date;
    }
}
