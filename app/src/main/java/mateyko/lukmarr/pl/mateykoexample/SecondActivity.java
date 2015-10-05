package mateyko.lukmarr.pl.mateykoexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        List<String> items = new LinkedList<>();
        items.add(getPoke("raichu"));
        items.add(getPoke("mew"));
        items.add(getPoke("onix"));
        items.add(getPoke("psyduck"));
        items.add(getPoke("bulbasaur"));
        items.add(getPoke("geodude"));
        items.add(getPoke("blastoise"));
        items.add(getPoke("vulpix"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MateykoAdapter(items, this));
    }

    private String getPoke(String pokemon) {
        return "http://img.pokemondb.net/sprites/heartgold-soulsilver/normal/" + pokemon + ".png";
    }
}
