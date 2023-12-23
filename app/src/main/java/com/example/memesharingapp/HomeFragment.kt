package com.example.memesharingapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.memesharingapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var imgUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        loadMeme()



        return binding.root
    }

    companion object {

    }


     fun loadMeme() {

        binding.progressBar.visibility = View.VISIBLE

        var subreddit = ""
        subreddit = binding.SearchSubreddit.text.toString()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(requireContext())
        val url = "https://meme-api.com/gimme/$subreddit"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                imgUrl = response.getString("url")
                Glide.with(this).load(imgUrl).into(binding.memeImage)

                binding.progressBar.visibility = View.INVISIBLE
                binding.like.setImageResource(R.drawable.baseline_favorite_border_24)


            },
            {
                Glide.with(this).load("https://media.giphy.com/media/3ohhwoRm2M1ZUjIEn2/ giphy.gif")
                    .into(binding.memeImage)
                Toast.makeText(context, "Cannot load meme !", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.INVISIBLE
            }
        )


         binding.nextBtn.setOnClickListener {
             loadMeme()
         }
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)


         binding.shareBtn.setOnClickListener {
             val intent = Intent(Intent.ACTION_SEND)
             intent.type = "text/plain"
             intent.putExtra(
                 Intent.EXTRA_TEXT,
                 imgUrl
             )

             val chooser = Intent.createChooser(intent, "Share this meme using .... ")
             startActivity(chooser)
         }

    }

}