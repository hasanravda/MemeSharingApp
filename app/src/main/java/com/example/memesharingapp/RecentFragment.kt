package com.example.memesharingapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.memesharingapp.database.DatabaseHelper
import com.example.memesharingapp.database.DatabaseHelper.Companion.COLUMN_SUBREDDIT
import com.example.memesharingapp.database.DatabaseHelper.Companion.COLUMN_TITLE
import com.example.memesharingapp.databinding.FragmentRecentBinding
import com.example.memesharingapp.models.PostData


class RecentFragment : Fragment() {

    private lateinit var binding:FragmentRecentBinding
    private lateinit var postArrayList:ArrayList<PostData>
    private lateinit var myAdapter:PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true) // This enables the fragment to handle option menu events


        // Inflate the layout for this fragment
        binding =  FragmentRecentBinding.inflate(inflater, container, false)

        val title= arrayOf("Item 1", "Item 2", "Item 3")
        val img = arrayOf(R.drawable.user1, R.drawable.user1, R.drawable.user1)


        // Retrieve posts from the database
        val dbHelper = DatabaseHelper(requireContext())
        val cursor = dbHelper.getAllPosts()

        postArrayList = ArrayList()

        if (cursor != null) {
            while (cursor.moveToNext()) {
//                val postId = cursor.getLong(cursor.getColumnIndex(COLUMN_POST_ID))
                // Extract other columns as needed
                // Use the data as required

//                val postId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_POST_ID))
                val subreddit = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUBREDDIT))
                val title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE))
                val url = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_URL))
                val author = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR))
                val ups = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_UPS))

                val post = PostData(subreddit, title, url, author, ups)
                postArrayList.add(post)
            }
            cursor.close() // Don't forget to close the cursor when done
        }
        else{   // cursor == null
            binding.emptyPostText.isVisible = true
        }


// Initialize ListView and set its adapter
//        val adapter = PostAdapter(posts)
//        binding.listView.adapter = adapter

//        postArrayList = ArrayList()
//        postArrayList.add(PostData("me_irl", "Me_irl", "https://i.redd.it/rxsjatg7hcyb1.jpg", "KarlBark", 10668))
//        postArrayList.add(PostData("me_irl", "Me_irl", "https://i.redd.it/rxsjatg7hcyb1.jpg", "KarlBark", 10668))
//        postArrayList.add(PostData("dankmemes", "I miss my bed already", "https://i.redd.it/tm23mvekhxyb1.jpg", "_Bussey_", 19))


        myAdapter= PostAdapter(requireActivity(),postArrayList)



//        binding.postClear.setOnClickListener{
//            val dbHelper = DatabaseHelper(requireContext())
//            dbHelper.deleteAllPosts()
//            postArrayList.clear()
//            myAdapter.notifyDataSetChanged()
//        }

        binding.listView.adapter=myAdapter


        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_history -> {
                // Handle the clear history option
                clearPostHistory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearPostHistory(){


        val dbHelper = DatabaseHelper(requireContext())
        dbHelper.deleteAllPosts()
        postArrayList.clear()
        myAdapter.notifyDataSetChanged()


        // To enable empty post text!
        binding.emptyPostText.isVisible = true

    }

    companion object {


    }
}