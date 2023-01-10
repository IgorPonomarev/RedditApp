package com.example.redditapp;

import static androidx.recyclerview.widget.DividerItemDecoration.HORIZONTAL;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.redditapp.Account.LoginActivity;
import com.example.redditapp.Post.Post;
import com.example.redditapp.Post.PostAdapter;
import com.example.redditapp.comments.CommentAdapter;
import com.example.redditapp.comments.CommentsActivity;
import com.example.redditapp.data.URLS;
import com.example.redditapp.model.Feed;
import com.example.redditapp.model.entry.Entry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity implements PostAdapter.OnPostClickListener {

    private static final String TAG = "MainActivity";

    private Button btnRefreshFeed;
    private EditText etFeedName;
    private String currentFeed;

    private RecyclerView recyclerView;
    private ArrayList<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRefreshFeed = findViewById(R.id.btnRefreshFeed);
        etFeedName = findViewById(R.id.et_feed_name);

        setUpToolBar();

        currentFeed = "funny";

        init();

        btnRefreshFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedName = etFeedName.getText().toString();
                if (!feedName.equals("")) {
                    currentFeed = feedName;
                    init();
                } else {
                    init();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(com.google.android.material.R.color.design_default_color_on_primary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    private void setUpToolBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: clicked " + item);

                switch (item.getItemId()) {
                    case R.id.action_login:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                }

                return false;
            }
        });
//        myToolbar.setTitle(R.string.app_name);
//        myToolbar.inflateMenu(R.menu.toolbar_menu);

    }

    private void init() {

        // Initialize retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLS.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        // Initialize custom API
        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        // Init call
        Call<Feed> call = feedAPI.getFeed(currentFeed);

        // Call feed
        call.enqueue(new Callback<Feed>() {
            // Got response
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                //Log.d(TAG, "onResponse: feed: " + response.body().getEntrys());
                //Log.d(TAG, "onResponse: Server Response:" + response.toString());

                //Collect response
                List<Entry> entrys = response.body().getEntrys();

                posts = new ArrayList<Post>();

                for (int i = 0; i < entrys.size(); i++) {

                    // Get links
                    ExtractXML extractXML1 = new ExtractXML(entrys.get(i).getContent(), "<a href=");
                    List<String> postContent = extractXML1.start();

                    // Get img sources
                    ExtractXML extractXML2 = new ExtractXML(entrys.get(i).getContent(), "<img src=");
                    try {
                        postContent.add(extractXML2.start().get(0));
                    } catch (NullPointerException e) {
                        postContent.add(null);
                        Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());
                    } catch (IndexOutOfBoundsException e) {
                        postContent.add(null);
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException: " + e.getMessage());
                    }

                    // Add collected info to posts
                    try {
                        posts.add(new Post(
                                entrys.get(i).getTitle(),
                                entrys.get(i).getAuthor().getName(),
                                entrys.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(postContent.size() - 1),
                                entrys.get(i).getId()
                        ));
                    } catch (NullPointerException e) {
                        posts.add(new Post(
                                entrys.get(i).getTitle(),
                                "None",
                                entrys.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(postContent.size() - 1),
                                entrys.get(i).getId()
                        ));
                        Log.e(TAG, "onResponse: NullPointerException", e);
                    }
                }

//                for (int j = 0; j < posts.size(); j++) {
//                    Log.d(TAG, "onResponse: imageURL: " + posts.get(j).getThumbnailURL());
//                }

//                ListView listView = (ListView) findViewById(R.id.listView);
//                CustomListAdapter customListAdapter = new CustomListAdapter(
//                        MainActivity.this,
//                        R.layout.card_layout_main,
//                        posts);
//                listView.setAdapter(customListAdapter);
//
//                // Navigate inside post
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        Log.d(TAG, "onItemClick: Clicked: " + posts.get(i).toString());
//                        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
//                        intent.putExtra("@string/post_url", posts.get(i).getPostURL());
//                        intent.putExtra("@string/post_thumbnail", posts.get(i).getThumbnailURL());
//                        intent.putExtra("@string/post_title", posts.get(i).getTitle());
//                        intent.putExtra("@string/post_author", posts.get(i).getAuthor());
//                        intent.putExtra("@string/post_updated", posts.get(i).getDate_updated());
//                        intent.putExtra("@string/post_id", posts.get(i).getId());
//                        startActivity(intent);
//                    }
//                });

                //inflate recycler view
                recyclerView = findViewById(R.id.postsRecyclerView);
                PostAdapter adapter = new PostAdapter(MainActivity.this, posts, MainActivity.this);
                recyclerView.setAdapter(adapter);
                //set horizontal divider
                recyclerView.addItemDecoration(new DividerItemDecoration(
                        recyclerView.getContext(),
                        VERTICAL
                ));
            }

            //Response Error
            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS " + t.getMessage());
                Toast.makeText(MainActivity.this, "An Error Occurred", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onPostClick(int position) {
        Log.d(TAG, "onItemClick: Clicked: " + posts.get(position).toString());
        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
        intent.putExtra("@string/post_url", posts.get(position).getPostURL());
        intent.putExtra("@string/post_thumbnail", posts.get(position).getThumbnailURL());
        intent.putExtra("@string/post_title", posts.get(position).getTitle());
        intent.putExtra("@string/post_author", posts.get(position).getAuthor());
        intent.putExtra("@string/post_updated", posts.get(position).getDate_updated());
        intent.putExtra("@string/post_id", posts.get(position).getId());
        startActivity(intent);
    }
}