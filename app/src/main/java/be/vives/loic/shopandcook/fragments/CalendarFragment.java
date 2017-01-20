package be.vives.loic.shopandcook.fragments;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.vives.loic.shopandcook.R;

/**
 * Created by LOIC on 14/01/2017.
 */
public class CalendarFragment extends Fragment {
    View view;
    int notificationId;
    NotificationManager notificationManager;
    Notification.Builder builder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        builder = new Notification.Builder(getActivity().getApplicationContext())
                .setSmallIcon(R.drawable.ic_restaurant_menu)
                .setContentTitle("Shop&Cook reminder")
                .setContentText("Basic info");
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());

        return view;
    }
}
