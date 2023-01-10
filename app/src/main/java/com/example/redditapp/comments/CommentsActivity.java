package com.example.redditapp.comments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditapp.Account.LoginActivity;
import com.example.redditapp.ExtractXML;
import com.example.redditapp.FeedAPI;
import com.example.redditapp.R;
import com.example.redditapp.data.URLS;
import com.example.redditapp.WebViewActivity;
import com.example.redditapp.model.Feed;
import com.example.redditapp.model.entry.Entry;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class CommentsActivity extends AppCompatActivity implements CommentAdapter.OnCommentClickListener {

    private static final String TAG = "CommentsActivity";

    private static String postURL;
    private static String postThumbnailURL;
    private static String postTitle;
    private static String postAuthor;
    private static String postUpdated;
    private static String postID;

    private String username;
    private String modhash;
    private String cookie;

    private int defaultImage;

    private String currentFeed;
    private RecyclerView recyclerView;

    private ArrayList<Comment> mComments;
    private ProgressBar progressBarComments;
    private TextView commentsLoadingText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        progressBarComments = findViewById(R.id.commentsLoadingProgressBar);
        progressBarComments.setVisibility(View.VISIBLE);

        commentsLoadingText = findViewById(R.id.comments_loading_text);
        commentsLoadingText.setVisibility(View.VISIBLE);

        setUpToolBar();

        getSessionParams();

        setupImageLoader();

        initPost();

        initRetrofit();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getSessionParams();
        Log.d(TAG, "onPostResume: resumed");
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
                        Intent intent = new Intent(CommentsActivity.this, LoginActivity.class);
                        startActivity(intent);
                }

                return false;
            }
        });
//        myToolbar.setTitle(R.string.app_name);
//        myToolbar.inflateMenu(R.menu.toolbar_menu);

    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLS.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed(currentFeed);
        //Log.d(TAG, "initRetrofit: currentFeed: " + currentFeed);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                mComments = new ArrayList<Comment>();
                List<Entry> entrys = response.body().getEntrys();

                for (int i = 1; i < entrys.size(); i++) {
//                    Log.d(TAG, "onResponse: entry: " + entrys.get(i).toString() +
//                            "\n--------------------------------------------------\n");
                    ExtractXML extract = new ExtractXML(entrys.get(i).getContent(),
                            "<div class=\"md\"><p>",
                            "</p");

                    List<String> commentDetails = extract.start();

                    try {
                        mComments.add(new Comment(
                                commentDetails.get(0),
                                entrys.get(i).getAuthor().getName(),
                                entrys.get(i).getUpdated(),
                                entrys.get(i).getId()));

                    } catch (IndexOutOfBoundsException e) {
                        mComments.add(new Comment(
                                "Error reading comment",
                                "None",
                                "None",
                                "None"));
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException", e);
                    } catch (NullPointerException e) {
                        mComments.add(new Comment(
                                commentDetails.get(0),
                                "None",
                                entrys.get(i).getUpdated(),
                                entrys.get(i).getId()));
                        Log.e(TAG, "onResponse: NullPointerException", e);
                    }
                }

                recyclerView = findViewById(R.id.commentsRecyclerView);
                CommentAdapter adapter = new CommentAdapter(CommentsActivity.this, mComments, CommentsActivity.this);
                recyclerView.setAdapter(adapter);

                progressBarComments.setVisibility(View.GONE);
                commentsLoadingText.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS " + t.getMessage());
                Toast.makeText(CommentsActivity.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPost() {
        //Get all the extras
        Intent incomingIntent = getIntent();
        postURL = incomingIntent.getStringExtra("@string/post_url");
        postThumbnailURL = incomingIntent.getStringExtra("@string/post_thumbnail");
        postTitle = incomingIntent.getStringExtra("@string/post_title");
        postAuthor = incomingIntent.getStringExtra("@string/post_author");
        postUpdated = incomingIntent.getStringExtra("@string/post_updated");
        postID = incomingIntent.getStringExtra("@string/post_id");

        //Get all the widgets
        TextView title = findViewById(R.id.postTitle);
        TextView author = findViewById(R.id.postAuthor);
        TextView updated = findViewById(R.id.postUpdated);
        ImageView thumbnail = findViewById(R.id.postThumbnail);
        Button btnReply = findViewById(R.id.btnPostReply);
        ProgressBar progressBar = findViewById(R.id.postLoadingProgressBar);

        //Set data to widgets
        title.setText(postTitle);
        author.setText(postAuthor);
        updated.setText(postUpdated);
        displayImg(postThumbnailURL, thumbnail, progressBar);

        //Extract URL
        try {
            String[] splitURL = postURL.split(URLS.BASE_URL);
            currentFeed = splitURL[1];
            //currentFeed = currentFeed.substring(0, currentFeed.length() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "initPost: ArrayIndexOutOfBoundsException", e);
        }

        //Reply button click open post comment dialog
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Reply button clicked");
                getUserComment(postID);
            }
        });

        // Image click goes to webView of the post
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: Opening URL in a WebView clicked " + postURL);
                Intent intent = new Intent(CommentsActivity.this, WebViewActivity.class);
                intent.putExtra("url", postURL);
                startActivity(intent);
            }
        });
    }

    //Create comment post dialog view
    private void getUserComment(String post_ID) {
        final Dialog dialog = new Dialog(CommentsActivity.this);
        dialog.setTitle("dialog");
        dialog.setContentView(R.layout.comment_input_dialog);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);

        dialog.getWindow().setLayout(width, height);
        dialog.show();

        Button btnPostComment = dialog.findViewById(R.id.dialog_button_post);
        final EditText comment = dialog.findViewById(R.id.dialog_input_reply);

        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Dialog button post comment clicked");

                //post comment via retrofit
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URLS.API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                FeedAPI feedAPI = retrofit.create(FeedAPI.class);

                HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("User-Agent", username);
                headerMap.put("X-Modhash", modhash);
                headerMap.put("cookie", "reddit_session=" + cookie);

//                Log.d(TAG, "onClick: username: " + username);
//                Log.d(TAG, "onClick: modhash: " + modhash);
//                Log.d(TAG, "onClick: cookie: " + cookie);

                Call<CheckComment> call = feedAPI.submitComment(headerMap, "comment", post_ID, comment.getText().toString());
                Log.d(TAG, "POST URL: " + call.request().url());
                Log.d(TAG, "POST Headers: " + call.request().headers().toString());

                call.enqueue(new Callback<CheckComment>() {
                    @Override
                    public void onResponse(Call<CheckComment> call, Response<CheckComment> response) {

                        try {
                            Log.d(TAG, "onResponse: Got server response: " + response.body().toString());

                            String postSuccess = response.body().getSuccess();

                            if (postSuccess.equals("true")) {
                                dialog.dismiss();
                                Toast.makeText(CommentsActivity.this, "Post successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CommentsActivity.this, "Post unsuccessful. Did you sign in?", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) {

                        }
                    }

                    @Override
                    public void onFailure(Call<CheckComment> call, Throwable t) {
                        Log.e(TAG, "onFailure: Unable to Log In " + t.getMessage());
                        Toast.makeText(CommentsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void displayImg(String imageURL, ImageView imageView, final ProgressBar progressBar) {
        //create the imageloader object
        ImageLoader imageLoader = ImageLoader.getInstance();

        //create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        //download and display image from url
        imageLoader.displayImage(imageURL, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader() {
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                CommentsActivity.this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP

        defaultImage = CommentsActivity.this.getResources().getIdentifier(
                "@drawable/image_failed",
                null, CommentsActivity.this.getPackageName());
    }

    @Override
    public void onCommentClick(int position) {
        Log.d(TAG, "onCommentClick: clicked");
        getUserComment(mComments.get(position).getId());
    }

    private void getSessionParams() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CommentsActivity.this);

        username = preferences.getString("@string/session_username", "");
        modhash = preferences.getString("@string/session_modhash", "");
        cookie = preferences.getString("@string/session_cookie", "");

        Log.d(TAG, "getSessionParams: username: " + username);
        Log.d(TAG, "getSessionParams: modhash: " + modhash);
        Log.d(TAG, "getSessionParams: cookie: " + cookie);
    }
}