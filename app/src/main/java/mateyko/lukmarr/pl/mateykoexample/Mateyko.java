package mateyko.lukmarr.pl.mateykoexample;
/** The MIT License

Copyright (c) 2015 Lukasz Marczak

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 * */

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
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.mime.TypedInput;
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
        Log.d(TAG, "endpoint: " + endpoint + ", query: " + query);
        return instance;
    }

    public void into(@NonNull final ImageView imageView) {
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(endpoint).build();
        ImagesAPI api = adapter.create(ImagesAPI.class);

        api.getImage(query).map(new Func1<Response, Bitmap>() {
            @Override
            public Bitmap call(Response response) {
                Log.d(TAG, "url = " + response.getUrl());
                TypedInput input = response.getBody();
                BufferedInputStream stream = null;
                Bitmap bitmap = null;
                try {
                    stream = new BufferedInputStream(input.in());
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
                    }
                }
                return bitmap;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(final Bitmap bitmap) {
                        Log.d(TAG, "onNext, bitmap is " + (bitmap == null ? "null" : "not null"));
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
