package com.example.memesharingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.memesharingapp.models.PostData
import com.google.api.Distribution.BucketOptions.Linear

class PostAdapter (private val context: Activity, private val arrayList:ArrayList<PostData>):
    ArrayAdapter<PostData>(context,R.layout.custom_item,arrayList){

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater = LayoutInflater.from(context)
        val view =inflater.inflate(R.layout.custom_item,null,true)


        val imageView = view.findViewById<ImageView>(R.id.postImg)
        val title = view.findViewById<TextView>(R.id.post_title)
        val subreddit = view.findViewById<TextView>(R.id.post_subreddit)
        val upvote = view.findViewById<TextView>(R.id.post_upvote)


        val postShareBtn=view.findViewById<LinearLayout>(R.id.post_share)
        val url =arrayList[position].url
//        imageView.setImageResource(arrayList[position].url)
        title.text=arrayList[position].title
        subreddit.text=arrayList[position].subreddit
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
        upvote.text = arrayList[position].ups.toString()


        // For sharing a post in the Recent/History feed
        postShareBtn.setOnClickListener {
            shareMeme(url)
        }

        return view
    }

    private fun shareMeme(imgUrl:String){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey ! , Checkout this Meme  \n" +
                    "$imgUrl"
        )

        val chooser = Intent.createChooser(intent,"Share this meme using ..... ")
        context.startActivity(chooser)
    }
}