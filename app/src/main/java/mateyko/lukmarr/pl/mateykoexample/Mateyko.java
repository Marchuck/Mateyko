package mateyko.lukmarr.pl.mateykoexample;
/**
 * <p>THIS MATERIAL IS PROVIDED AS IS, WITH ABSOLUTELY NO WARRANTY EXPRESSED
 * OR IMPLIED.  ANY USE IS AT YOUR OWN RISK.
 * <p/>
 * Permission is hereby granted to use or copy this program
 * for any purpose,  provided the above notices are retained on all copies.
 * Permission to modify the code and to distribute modified code is granted,
 * provided the above notices are retained, and a notice that the code was
 * modified is included with the above copyright notice.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.mime.TypedInput;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 *         <p/>
 *         Lightweight & simple image loader
 * @since 2015-10-04
 */
public class Mateyko {

    private static String TAG = Mateyko.class.getSimpleName();
    private static final Mateyko instance = new Mateyko();
    private static final int FADE_DURATION = 200;

    private Activity activity;
    private String endpoint;
    private String query;

    private Mateyko() {
    }

    public static Mateyko with(@NonNull Activity _context) {
        instance.activity = _context;
        return instance;
    }

    public Mateyko load(@NonNull String url) {
        String[] pieces = url.split("/");
        query = pieces[pieces.length - 1];
        endpoint = url.replace("/" + query, "");
        Log.d(TAG, "endpoint: " + endpoint);
        Log.d(TAG, "query: " + query);
        return instance;
    }

    public void into(@NonNull final ImageView imageView) {
        Log.d(TAG, "into ");
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint).build();
        ImagesAPI api = adapter.create(ImagesAPI.class);

        api.getImage(query).map(new Func1<Response, Bitmap>() {
            @Override
            public Bitmap call(Response response) {

                Log.d(TAG, "trying to decode image...");
                Log.d(TAG, "url = " + response.getUrl());
                TypedInput input = response.getBody();
                BufferedInputStream stream;
                Bitmap bitmap = null;
                try {
                    stream = new BufferedInputStream(input.in());
                    bitmap = BitmapFactory.decodeStream(stream);
                } catch (IOException e) {
                    Log.e(TAG, "error decoding stream");
                    e.printStackTrace();
                }
                return bitmap;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Bitmap>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted ");
                if (!isUnsubscribed())
                    unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onErrorCompleted ");
                Log.e(TAG, "caused by " + e.getCause());
                if (e instanceof RetrofitError)
                    Log.e(TAG, "url = " + ((RetrofitError) e).getUrl());
                Log.e(TAG, "message: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(final Bitmap bitmap) {
                Log.d(TAG, "onNext ");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null) {
                            Drawable[] layers = new Drawable[]{
                                    new BitmapDrawable(activity.getResources()),
                                    new BitmapDrawable(bitmap)};
                            TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
                            imageView.setImageDrawable(transitionDrawable);
                            transitionDrawable.startTransition(FADE_DURATION);
                        }
                    }
                });
            }
        });
    }

    private interface ImagesAPI {
        @GET("/{path}")
        rx.Observable<Response> getImage(@Path("path") String subString);
    }
}