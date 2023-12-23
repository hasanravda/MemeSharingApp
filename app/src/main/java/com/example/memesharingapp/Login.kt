package com.example.memesharingapp

import android.content.Context
import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.memesharingapp.databinding.ActivityLoginBinding
import com.example.memesharingapp.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class Login : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // To check if user is Signed In or not

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            // User is already signed in
            // Redirect to the main screen or dashboard
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }




        // To Store Login information in local storage
        val sp = getSharedPreferences("My-storage", Context.MODE_PRIVATE)
        var editor=sp.edit()


        binding.loginBtn.setOnClickListener {

            if (binding.loginEmail.editText?.text.toString().equals("") or
                binding.loginPass.editText?.text.toString().equals("")
            ) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()

            } else {
                var user = User(
                    binding.loginEmail.editText?.text.toString(),
                    binding.loginPass.editText?.text.toString()
                )

                Firebase.auth.signInWithEmailAndPassword(user.email!!, user.password!!)
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                result.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                // Saving credentials in Local Storage
                editor.putString("email",binding.loginEmail.editText?.text.toString())
                editor.putString("pass",binding.loginPass.editText?.text.toString())
                editor.putBoolean("remember",binding.rememberMeCheckbox.isChecked)
                editor.commit()
            }


        }
        binding.signupText.setOnClickListener {
            var intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }





//         Remember me Functionalities using Shared Preference

        // Retrieving remember me checkbox value from local storage

        var check = sp.getBoolean("remember",false)

        if(check)
        {
            var email =sp.getString("email","")
            var pass = sp.getString("pass","")

            binding.loginEmail.editText?.setText(email)
            binding.loginPass.editText?.setText(pass)
            binding.rememberMeCheckbox.isChecked=true
        }
    }
}