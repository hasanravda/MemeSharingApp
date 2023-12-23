package com.example.memesharingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.memesharingapp.databinding.ActivitySignupBinding
import com.example.memesharingapp.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class Signup : AppCompatActivity() {
    private lateinit var binding : ActivitySignupBinding

    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

//        Firebase.initializeApp(this)


        user=User()

        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginText.setOnClickListener {
            var intent = Intent(this,Login::class.java)
            startActivity(intent)
        }

        binding.signupBtn.setOnClickListener{
            if (binding.signupName.editText?.text.toString().equals("") or
                binding.signupEmail.editText?.text.toString().equals("") or
                binding.signupEmail.editText?.text.toString().equals("")
                    ){
                Toast.makeText(this, "Please fill all the Information", Toast.LENGTH_SHORT).show()
            }
            else{

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.signupEmail .editText?.text.toString(),
                    binding.signupPass.editText?.text.toString()
                ).addOnCompleteListener {
                    result->
                    if (result.isSuccessful){
                        user.name = binding.signupName.editText?.text.toString()
                        user.email = binding.signupEmail.editText?.text.toString()
                        user.password = binding.signupPass.editText?.text.toString()

                        // To add User information in the firestore database
                        Firebase.firestore.collection("User")
                            .document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT)
                                    .show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                            }

                    }else{
                        Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
}