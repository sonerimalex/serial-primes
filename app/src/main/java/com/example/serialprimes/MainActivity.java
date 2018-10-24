package com.example.serialprimes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;

public class MainActivity extends AppCompatActivity {

    long defaultRange = 100L;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.list_primes)
    RecyclerView listPrimes;

    @BindView(R.id.list_spectres)
    RecyclerView listSpectres;

    @BindView(R.id.list_toppings)
    RecyclerView listToppings;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.range)
    EditText range;

    @BindView(R.id.select_primes)
    Button selectPrimes;

    @BindView(R.id.select_spectres)
    Button selectSpectres;

    @BindView(R.id.select_toppings)
    Button selectToppings;

    @OnClick(R.id.select_primes)
    public void onSelectPrimes(){
        listPrimes.setVisibility(View.VISIBLE);
        listSpectres.setVisibility(View.GONE);
        listToppings.setVisibility(View.GONE);
    }

    @OnClick(R.id.select_spectres)
    public void onSelectSpectres(){
        listPrimes.setVisibility(View.GONE);
        listSpectres.setVisibility(View.VISIBLE);
        listToppings.setVisibility(View.GONE);
    }

    @OnClick(R.id.select_toppings)
    public void onSelectToppings(){
        listPrimes.setVisibility(View.GONE);
        listSpectres.setVisibility(View.GONE);
        listToppings.setVisibility(View.VISIBLE);
    }

    HashMap<Long, Long> primes;
    HashMap<Long, ArrayList<Long>> spectres;
    HashMap<Long, ArrayList<Long>> toppings;
    long startTime;

    @OnClick(R.id.fab)
    public void onFabClick(View view){
        Date date = new Date();
        startTime = date.getTime();
        Completable.fromAction(this::serialPrimes).andThen(Completable.fromAction(this::updateList)).subscribe();
    }

    public void updateList() {
        long stopTime = new Date().getTime();
        long timeValue = stopTime - startTime;
        String timeString = String.format("%.2fms/item", (double)timeValue / primes.size());
        Snackbar.make(fab, timeString, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        toolbar.setTitle(getString(R.string.app_name) + " (" + primes.size() + ") " + timeValue + "ms; " + timeString);

        selectPrimes.setText("Primes [" + this.primes.size() + "]");
        selectSpectres.setText("Spectres [" + this.spectres.size() + "]");
        selectToppings.setText("Toppings [" + this.toppings.size() + "]");

        TreeMap<Long, Long> primes = new TreeMap<>(this.primes);
        PrimesAdapter adapterPrimes = new PrimesAdapter(primes);
        listPrimes.setAdapter(adapterPrimes);
        listPrimes.setLayoutManager(new LinearLayoutManager(this));

        TreeMap<Long, ArrayList<Long>> spectres = new TreeMap<>(this.spectres);
        SpectresAdapter adapterSpectres = new SpectresAdapter(spectres);
        listSpectres.setAdapter(adapterSpectres);
        listSpectres.setLayoutManager(new LinearLayoutManager(this));

        TreeMap<Long, ArrayList<Long>> toppings = new TreeMap<>(this.toppings);
        ToppingsAdapter adapterToppings = new ToppingsAdapter(toppings);
        listToppings.setAdapter(adapterToppings);
        listToppings.setLayoutManager(new LinearLayoutManager(this));
    }

    public HashMap<Long, Long> serialPrimes() {
        long range = Long.parseLong(this.range.getText().toString());
        HashMap<Long, Long> primes = new HashMap<>(); //primes and their toppings
        HashMap<Long, ArrayList<Long>> spectres = new HashMap<>(); // not primes with their spectres (sets of dividers)
        HashMap<Long, ArrayList<Long>> toppings = new HashMap<>(); // toppings with accumulated spectres
        for(long i = 2; i < range; i++){
            if(toppings.keySet().contains(i)) { // if toppings table contains key == i
                //move spectre to its table
                ArrayList<Long> spectre = toppings.get(i);
                spectres.put(i, spectre);
                toppings.remove(i);
                for(long spectreValue : spectre) {
                    // update topping with one step up
                    long topping = primes.get(spectreValue) + spectreValue;
                    primes.put(spectreValue, topping);
                    // this is not prime
                    if(toppings.keySet().contains(topping)) {
                        toppings.get(topping).add(spectreValue);
                    } else {
                        ArrayList<Long> newSpectre = new ArrayList<>();
                        newSpectre.add(spectreValue);
                        toppings.put(topping, newSpectre);
                    }
                }
            } else { // if table of toppings doesn't contains the key == i
                // this is prime
                primes.put(i, i + i); // topping value always more than counter (i), nearest to it key-divisable
                // add topping to the table
                ArrayList<Long> newSpectre = new ArrayList<>();
                newSpectre.add(i);
                toppings.put(i + i, newSpectre);
            }
        }
        this.primes = primes;
        this.spectres = spectres;
        this.toppings = toppings;
        return primes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        range.setText(Long.toString(defaultRange));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean menuEnable = false;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return menuEnable;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
