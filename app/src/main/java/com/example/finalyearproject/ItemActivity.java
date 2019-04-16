package com.example.finalyearproject;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ItemActivity extends AppCompatActivity {

    private RecyclerView bathingRV;
    private RecyclerView transferringRV;
    private RecyclerView dressingRV;
    private RecyclerView foodRV;
    private RecyclerView toiletingRV;

    private Button speakButton;
    private Button clearSentenceButton;
    private Button helpButton;

    private TextToSpeech textToSpeech;


    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> bathingAdapter;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> transferringAdapter;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> dressingAdapter;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> foodAdapter;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> toiletingAdapter;




    public String sentence;
    public String lastPhrase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        sentence = "";
        lastPhrase = "";

        speakButton = findViewById(R.id.speakButton);
        clearSentenceButton = findViewById(R.id.clearSentenceButton);
        helpButton = findViewById(R.id.helpButton);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.UK);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        clearSentenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentence = "";
                lastPhrase = "";
                TextView txtSentence = findViewById(R.id.txtViewSentence);
                txtSentence.setText("Please press a button!");

            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ItemActivity.this, helpActivity.class);
                startActivity(i);

            }
        });






        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int speechStatus = textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);

            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);


        bathingRV = findViewById(R.id.recyclerViewBathing);
        transferringRV = findViewById(R.id.recyclerViewTransferring);
        dressingRV = findViewById(R.id.recyclerViewDressing);
        foodRV = findViewById(R.id.recyclerViewFood);
        toiletingRV = findViewById(R.id.recyclerViewToileting);




        DatabaseReference bathingRef = FirebaseDatabase.getInstance().getReference().child("categoryBathing");
        DatabaseReference transferringRef = FirebaseDatabase.getInstance().getReference().child("categoryTransferring");
        DatabaseReference dressingRef = FirebaseDatabase.getInstance().getReference().child("categoryDressing");
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference().child("categoryFood");
        DatabaseReference toiletingRef = FirebaseDatabase.getInstance().getReference().child("categoryToileting");





        Query bathingQuery = bathingRef.orderByKey();
        Query transferringQuery = transferringRef.orderByKey();
        Query dressingQuery = dressingRef.orderByKey();
        Query foodQuery = foodRef.orderByKey();
        Query toiletingQuery = toiletingRef.orderByKey();


        bathingRV.hasFixedSize();
        transferringRV.hasFixedSize();
        dressingRV.hasFixedSize();
        foodRV.hasFixedSize();
        toiletingRV.hasFixedSize();

        bathingRV.setLayoutManager(new LinearLayoutManager(this));
        transferringRV.setLayoutManager(new LinearLayoutManager(this));
        dressingRV.setLayoutManager(new LinearLayoutManager(this));
        foodRV.setLayoutManager(new LinearLayoutManager(this));
        toiletingRV.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions bathingOptions = new FirebaseRecyclerOptions.Builder<Item>().setQuery(bathingQuery, Item.class).build();
        FirebaseRecyclerOptions transferringOptions = new FirebaseRecyclerOptions.Builder<Item>().setQuery(transferringQuery, Item.class).build();
        FirebaseRecyclerOptions dressingOptions = new FirebaseRecyclerOptions.Builder<Item>().setQuery(dressingQuery, Item.class).build();
        FirebaseRecyclerOptions foodOptions = new FirebaseRecyclerOptions.Builder<Item>().setQuery(foodQuery, Item.class).build();
        FirebaseRecyclerOptions toiletingOptions = new FirebaseRecyclerOptions.Builder<Item>().setQuery(toiletingQuery, Item.class).build();


        GridLayoutManager bathingLayoutManager = new GridLayoutManager(this, 2);
        GridLayoutManager transferringLayoutManager = new GridLayoutManager(this, 2);
        GridLayoutManager dressingLayoutManager = new GridLayoutManager(this, 2);
        GridLayoutManager foodLayoutManager = new GridLayoutManager(this, 2);
        GridLayoutManager toiletingLayoutManager = new GridLayoutManager(this, 2);


        bathingRV.setLayoutManager(bathingLayoutManager);
        transferringRV.setLayoutManager(transferringLayoutManager);
        dressingRV.setLayoutManager(dressingLayoutManager);
        foodRV.setLayoutManager(foodLayoutManager);
        toiletingRV.setLayoutManager(toiletingLayoutManager);


        bathingAdapter = new FirebaseRecyclerAdapter<Item, ItemActivity.ItemViewHolder>(bathingOptions) {
            @Override
            protected void onBindViewHolder(ItemActivity.ItemViewHolder holder, final int position, final Item model) {
                holder.setTitle(model.getTitle());
                holder.setImage(getBaseContext(), model.getImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String title;
                        title = model.getTitle();
                        Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                        intent.putExtra("id", title);
                        addText(title);
                        //      Toast.makeText(ItemActivity.this, title, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public ItemActivity.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_row, parent, false);

                return new ItemActivity.ItemViewHolder(view);
            }
        };
        //======//
        transferringAdapter = new FirebaseRecyclerAdapter<Item, ItemActivity.ItemViewHolder>(transferringOptions) {
            @Override
            protected void onBindViewHolder(ItemActivity.ItemViewHolder holder, final int position, final Item model) {
                holder.setTitle(model.getTitle());
                holder.setImage(getBaseContext(), model.getImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String title;
                        title = model.getTitle();
                        Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                        intent.putExtra("id", title);
                        addText(title);
                        //      Toast.makeText(ItemActivity.this, title, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public ItemActivity.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_row, parent, false);

                return new ItemActivity.ItemViewHolder(view);
            }
        };
        //======//
        dressingAdapter = new FirebaseRecyclerAdapter<Item, ItemActivity.ItemViewHolder>(dressingOptions) {
            @Override
            protected void onBindViewHolder(ItemActivity.ItemViewHolder holder, final int position, final Item model) {
                holder.setTitle(model.getTitle());
                holder.setImage(getBaseContext(), model.getImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String title;
                        title = model.getTitle();
                        Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                        intent.putExtra("id", title);
                        addText(title);
                        //      Toast.makeText(ItemActivity.this, title, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public ItemActivity.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_row, parent, false);

                return new ItemActivity.ItemViewHolder(view);
            }
        };
        //======//
        foodAdapter = new FirebaseRecyclerAdapter<Item, ItemActivity.ItemViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(ItemActivity.ItemViewHolder holder, final int position, final Item model) {
                holder.setTitle(model.getTitle());
                holder.setImage(getBaseContext(), model.getImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String title;
                        title = model.getTitle();
                        Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                        intent.putExtra("id", title);
                        addText(title);
                        //      Toast.makeText(ItemActivity.this, title, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public ItemActivity.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_row, parent, false);

                return new ItemActivity.ItemViewHolder(view);
            }
        };
        // ======//
        toiletingAdapter = new FirebaseRecyclerAdapter<Item, ItemActivity.ItemViewHolder>(toiletingOptions) {
            @Override
            protected void onBindViewHolder(ItemActivity.ItemViewHolder holder, final int position, final Item model) {
                holder.setTitle(model.getTitle());
                holder.setImage(getBaseContext(), model.getImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String title;
                        title = model.getTitle();
                        Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                        intent.putExtra("id", title);
                        addText(title);
                        //      Toast.makeText(ItemActivity.this, title, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public ItemActivity.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_row, parent, false);

                return new ItemActivity.ItemViewHolder(view);
            }
        };
        // ======//

        bathingRV.setAdapter(bathingAdapter);
        transferringRV.setAdapter(transferringAdapter);
        dressingRV.setAdapter(dressingAdapter);
        foodRV.setAdapter(foodAdapter);
        toiletingRV.setAdapter(toiletingAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        bathingAdapter.startListening();
        transferringAdapter.startListening();
        dressingAdapter.startListening();
        foodAdapter.startListening();
        toiletingAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        bathingAdapter.stopListening();
        transferringAdapter.stopListening();
        dressingAdapter.stopListening();
        foodAdapter.stopListening();
        toiletingAdapter.stopListening();


    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            TextView post_title = mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setImage(Context ctx, String image){
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    void addText(String title){
        sentence = sentence.trim() + " " + title;
        lastPhrase = title;

        TextView txtSentence = findViewById(R.id.txtViewSentence);
        txtSentence.setText(sentence.trim());


    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int buttonPressed = event.getAction();
        int keyCode = event.getKeyCode();
        ScrollView scrollButtons = (ScrollView) findViewById(R.id.topScrollView);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (buttonPressed == KeyEvent.ACTION_DOWN) {
                    scrollButtons.pageScroll(View.FOCUS_UP);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (buttonPressed == KeyEvent.ACTION_DOWN) {
                    scrollButtons.pageScroll(View.FOCUS_DOWN);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }



}