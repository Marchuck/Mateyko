package mateyko.lukmarr.pl.mateykoexample;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

/**
 * Created by Lukasz Marczak on 2015-10-05.
 */
public class MateykoAdapter extends RecyclerView.Adapter<MateykoAdapter.MateykoHolder> {
    public static final String TAG = MateykoAdapter.class.getSimpleName();
    private List<String> items;
    private Activity context;

    public MateykoAdapter(List<String> items, Activity context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public MateykoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final android.view.LayoutInflater inflater = android.view.LayoutInflater.from(viewGroup.getContext());
        return new MateykoHolder(inflater.inflate(R.layout.item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MateykoHolder vh, int i) {
        Log.d("onBindViewHolder", "item: " + items.get(i));
        Mateyko.with(context).load(items.get(i)).into(vh.imageView);

        vh.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick ");
                Collections.shuffle(items);
                notifyDataSetChanged();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        int size = items.size();
        Log.d(TAG, "getItemCount: " + size);
        return size;
    }

    public class MateykoHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MateykoHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}
