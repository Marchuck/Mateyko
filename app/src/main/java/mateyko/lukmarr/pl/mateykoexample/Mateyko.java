package mateyko.lukmarr.pl.mateykoexample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
    //private static final Mateyko instance = new Mateyko();
    private static final int FADE_DURATION = 200;

    private Activity activity;
    private String endpoint;
    private String query;
    private Bitmap errorBitmap;
    private Drawable errorDrawable;

    private Mateyko(Activity activity) {
        this.activity = activity;
    }

    public static Mateyko with(@NonNull Activity activity) {
        return new Mateyko(activity);
    }

    public Mateyko load(@NonNull String url) {
        //this.url  =url;
        String[] pieces = url.split("/");
        query = pieces[pieces.length - 1];
        endpoint = url.replace(query, "");
        Log.d(TAG, "endpoint: " + endpoint + ", query: " + query);
        return this;
    }

    public Mateyko errorImage(Bitmap errorBitmap) {
        this.errorBitmap = errorBitmap;
        return this;
    }

    public Mateyko errorImage(Drawable errorDrawable) {
        this.errorDrawable = errorDrawable;
        return this;
    }

    public void into(@NonNull final ImageView imageView) {
        ImagesAPI api = new Retrofit.Builder().baseUrl(endpoint).build().create(ImagesAPI.class);
        wrapWithRx(api.getImage(query)).map(new Func1<ResponseBody, Bitmap>() {
            @Override
            public Bitmap call(ResponseBody response) {
                BufferedInputStream stream = null;
                Bitmap bitmap = null;
                try {
                    stream = new BufferedInputStream(response.byteStream());
                    bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
                } catch (IOException e) {
                    Log.e(TAG, "failed to decode stream");
                    e.printStackTrace();
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException ex) {
                            Log.e(TAG, "failed to close stream");
                            ex.printStackTrace();
                        }
                        return null;
                    }
                }
                return bitmap;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(final Bitmap bitmap) {
                        Log.d(TAG, "onNext, bitmap is " + (bitmap == null ? "null" : "not null"));
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap != null) {
                                    doTransitionFade(bitmap, imageView);
                                } else if (errorBitmap != null) {
                                    doTransitionFade(errorBitmap, imageView);
                                } else if (errorDrawable != null) {
                                    doTransitionFade(errorDrawable, imageView);
                                }
                            }
                        });
                    }
                });
    }

    private void doTransitionFade(Bitmap bmp, ImageView imageView) {
        Drawable[] layers = new Drawable[]{new BitmapDrawable(activity.getResources()), new BitmapDrawable(bmp)};
        doTransitionFade(layers, imageView);
    }

    private void doTransitionFade(Drawable drawable, ImageView imageView) {
        Drawable[] layers = new Drawable[]{new BitmapDrawable(activity.getResources()), drawable};
        doTransitionFade(layers, imageView);
    }

    private void doTransitionFade(Drawable[] layers, ImageView imageView) {
        TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(FADE_DURATION);
    }

    private static <T> Observable<T> wrapWithRx(final Call<T> call) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                call.enqueue(new Callback<T>() {
                    @Override
                    public void onResponse(Call<T> call, Response<T> response) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onNext(response.body());
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Call<T> call, Throwable t) {
                        subscriber.onError(t);
                    }
                });
            }
        });
    }

    private interface ImagesAPI {
        @GET("{path}")
        Call<ResponseBody> getImage(@Path("path") String subString);
    }
}
