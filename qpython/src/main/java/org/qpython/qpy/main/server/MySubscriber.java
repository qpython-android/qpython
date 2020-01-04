package org.qpython.qpy.main.server;

import android.widget.Toast;

import org.qpython.qpy.R;
import org.qpython.qpy.main.app.App;

import java.net.ConnectException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 *
 * Created by Hmei on 2017-06-29.
 */

public class MySubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ConnectException | e instanceof HttpException) {
            Toast.makeText(App.getContext(), R.string.lost_connect, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(App.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNext(T o) {

    }
}
