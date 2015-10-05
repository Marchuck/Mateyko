package mateyko.lukmarr.pl.mateykoexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ImageView view = (ImageView) findViewById(R.id.imageView);
        Mateyko.with(this)
                .load("http://img.pokemondb.net/artwork/pikachu.jpg")
                .into(view);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(FirstActivity.this, SecondActivity.class));
                return false;
            }
        });
    }
}
