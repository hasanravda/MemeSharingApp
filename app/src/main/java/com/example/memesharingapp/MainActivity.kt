package com.example.memesharingapp

import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.memesharingapp.database.DatabaseHelper
import com.example.memesharingapp.databinding.ActivityMainBinding
import com.example.memesharingapp.models.PostData
import com.example.memesharingapp.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var responseArrayList: ArrayList<PostData>
    private lateinit var imgUrl: String


    override fun onCreate(savedInstanceState: Bundle?) {


        responseArrayList = ArrayList()


//         For Full Screen Activity .......

//        val windowInsetsController =
//            WindowCompat.getInsetsController(window, window.decorView) ?: return
//        windowInsetsController.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.shareBtn.setOnClickListener {
            shareMeme()
        }

        loadMeme()


        // To load meme after user enter subreddit and click on enter

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.SearchSubreddit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                loadMeme()
                // To hide keyboard after clicked enter
                inputMethodManager.hideSoftInputFromWindow(binding.SearchSubreddit.windowToken,0)
                binding.SearchSubreddit.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }


        // Adding toolbar
        setSupportActionBar(binding.toolbar)
        binding.navigationView.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.OpenDrawer,
            R.string.CloseDrawer
        )

        binding.drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container,HomeFragment()).commit()
            binding.navigationView.setCheckedItem(R.id.nav_home)
        }


        supportActionBar?.setHomeButtonEnabled(true)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_launcher_foreground)


        // To handle logout item below the header_layout
        binding.logoutLayout.setOnClickListener {
            showLogoutDialog()
        }


        // To handle swipe gesture !!

        binding.relativeLayout.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                return super.onTouch(view, motionEvent)
            }


            override fun onSwipeRight() {
                super.onSwipeRight()
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                loadMeme()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                shareMeme()
            }

        })




    }


    private fun loadMeme() {

        val sp = getSharedPreferences("My-storage", Context.MODE_PRIVATE)
        var editor=sp.edit()


        binding.progressBar.visibility = View.VISIBLE

        var subreddit = ""
        subreddit = binding.SearchSubreddit.text.toString()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://meme-api.com/gimme/$subreddit"


        // Request a string response from the provided URL.

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->

                imgUrl = response.getString("url")
                val img = findViewById<ImageView>(R.id.memeImage)
                Glide.with(this)
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img)

                val bar = findViewById<ProgressBar>(R.id.progressBar)
                bar.visibility = View.INVISIBLE
                binding.like.setImageResource(R.drawable.baseline_favorite_border_24)



                // Storing response data in database

                val db = DatabaseHelper(this)
                val post =PostData(response.getString("subreddit"), response.getString("title"), imgUrl, response.getString("author"), response.getInt("ups"))
                db.insertPost(post)

            },
            { error ->
                Glide.with(this).load("https://media.giphy.com/media/3ohhwoRm2M1ZUjIEn2/giphy.gif")
                    .into(binding.memeImage)

                // To get API 's error message
                val errorMessage = if (error.networkResponse != null && error.networkResponse.data != null) {
                    try {
                        val errorJson = JSONObject(String(error.networkResponse.data))
                        errorJson.getString("message")
                    } catch (e: JSONException) {
                        "Unknown error occurred"
                    }
                } else {
                    "No internet connection or server unreachable"
                }

                Toast.makeText(this, errorMessage , Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.INVISIBLE
            }
        )

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }


    fun nextMeme(view: View) {
        loadMeme()
    }

    private fun shareMeme() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey ! , Checkout this meme I got from Reddit  \n$imgUrl"
        )

        val chooser = Intent.createChooser(intent, "Share this meme using .... ")
        startActivity(chooser)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val bundle = Bundle()
        when(item.itemId){
            R.id.nav_home -> startActivity(Intent(this,MainActivity::class.java))
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container,HomeFragment()).commit()

//            R.id.nav_upvoted -> Toast.makeText(this,"Upvoted",Toast.LENGTH_SHORT).show()

            R.id.nav_recent ->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,RecentFragment()).commit()
                binding.toolbar.setBackgroundResource(R.color.black)
            }



        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun onStart() {
        super.onStart()


        // Displaying Current user details in Navigation menu Header
            // Getting current user details from FireStore
            Firebase.firestore.collection("User").document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {

                    val user:User=it.toObject<User>()!!

                    val headerName=findViewById<TextView>(R.id.header_username)
                    headerName.text=user.name
                    val headerEmail=findViewById<TextView>(R.id.header_email)
                    headerEmail.text=user.email
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }



    }


    // Alert Dialog for Logout confirmation
    private fun showLogoutDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout? ")
        builder.setPositiveButton("Logout") { dialog, _ ->

            logoutUser()

            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun logoutUser()
    {
        // Firebase Sign out operation
        Firebase.auth.signOut()

        // Redirect to Login page
        startActivity(Intent(this,Login::class.java))
        finish()
    }

}