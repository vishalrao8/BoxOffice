package com.united_creation.visha.boxoffice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.united_creation.visha.boxoffice.activity.SplashActivity.movies;

public class DateUtils {

    public static String getFormattedDate(int position) {

        String rawReleaseString = movies.get(position).getReleaseDate();

        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date rawReleaseDate = new Date();

        try {
            rawReleaseDate = dateParser.parse(rawReleaseString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return dateFormatter.format(rawReleaseDate);

    }

}
