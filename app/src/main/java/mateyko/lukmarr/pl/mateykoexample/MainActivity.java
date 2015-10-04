package mateyko.lukmarr.pl.mateykoexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private static int index = -1;
    private static final String[] images = new String[]{
            "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Kazanie_Skargi.jpg/1280px-Kazanie_Skargi.jpg",
            "http://www.pinakoteka.zascianek.pl/Matejko/Images/Autoportret_87.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/6/6e/Matejko_Wernyhora.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/Matejko_Battle_of_Grunwald.jpg/1920px-Matejko_Battle_of_Grunwald.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/4/46/The_Maid_of_Orl%C3%A9ans.PNG/1280px-The_Maid_of_Orl%C3%A9ans.PNG",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Lublin_Union_1569.PNG/1280px-Lublin_Union_1569.PNG",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d6/Matejko_Sta%C5%84czyk.jpg/1024px-Matejko_Sta%C5%84czyk.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/8/86/Rejtan_at_Sejm_of_1773_by_Jan_Matejko%2C_1866.png/1024px-Rejtan_at_Sejm_of_1773_by_Jan_Matejko%2C_1866.png",
            "https://upload.wikimedia.org/wikipedia/commons/1/11/Prussian_Homage.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);

        nextImage();
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                nextImage();
                return false;
            }
        });
    }

    private void nextImage() {
        Log.d("MainActivity", "nextImage " + index);
        index = (++index) % images.length;
        Mateyko.with(this)
                .load(images[index])
                .into(imageView);
    }
}
