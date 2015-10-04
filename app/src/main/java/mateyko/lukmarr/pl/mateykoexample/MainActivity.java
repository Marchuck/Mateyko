package mateyko.lukmarr.pl.mateykoexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mateyko.with(this)
                .load("http://www.pinakoteka.zascianek.pl/Matejko/Images/Autoportret_87.jpg")
                .into((ImageView) findViewById(R.id.imageView));
    }

}
