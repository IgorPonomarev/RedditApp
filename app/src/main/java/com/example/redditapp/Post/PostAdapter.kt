package com.example.redditapp.Post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redditapp.R
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

class PostAdapter(
    private val context: Context,
    private val dataset: List<Post>,
    private val onPostClickListener: OnPostClickListener
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    lateinit var displayOptions: DisplayImageOptions
    lateinit var imageLoader: ImageLoader
    lateinit var postImageUrl: String

    class PostViewHolder(private val view: View, val onPostClickListener: OnPostClickListener) :
        RecyclerView.ViewHolder(view), OnClickListener {
        val postTextView: TextView = view.findViewById(R.id.post_title)
        val postAuthorTextView: TextView = view.findViewById(R.id.post_author)
        val postUpdatedTextView: TextView = view.findViewById(R.id.post_updated)
        val postProgressBar: ProgressBar = view.findViewById(R.id.post_in_list_progress_bar)
        val postImage: ImageView = view.findViewById(R.id.post_image)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onPostClickListener.onPostClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_list_item_layout, parent, false)

        return PostViewHolder(adapterLayout, onPostClickListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = dataset[position]
        holder.postTextView.text = item.title
        holder.postAuthorTextView.text = item.author
        holder.postUpdatedTextView.text = item.date_updatedFormatted
        postImageUrl = item.thumbnailURL ?: "";

        setUpImageLoader()
        // calling image loader to display our image in our image view from image url
        imageLoader.displayImage(postImageUrl, holder.postImage, displayOptions, null)

        //hide progressbar
        holder.postProgressBar.visibility = View.GONE
    }

    override fun getItemCount() = dataset.size

    interface OnPostClickListener {
        fun onPostClick(position: Int)
    }

    fun setUpImageLoader() {
        // on below line we are
        // initializing our image loader.
        imageLoader = ImageLoader.getInstance()

        // on below line we are initializing image loader with its configuration.
        imageLoader.init(ImageLoaderConfiguration.createDefault(this.context))

        // on below line we are initializing our display options
        displayOptions = DisplayImageOptions.Builder()

            // on below line we are adding a
            // stub image as error image.
            .showImageOnLoading(R.drawable.image_failed)

            // on below line we are adding an error
            // image this image will be displayed
            // when the image url is empty
            .showImageForEmptyUri(R.drawable.image_failed)

            // on below line we are calling
            // cache in memory and then calling build.
            .cacheInMemory(true).build();
    }

}