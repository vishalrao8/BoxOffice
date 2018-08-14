package com.example.visha.boxoffice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.visha.boxoffice.activity.SplashScreen.movies;

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
