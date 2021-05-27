package com.example.funnycatsfacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.funnycatsfacts.api.ApiRequest
import com.example.funnycatsfacts.api.Constants
import com.example.funnycatsfacts.api.Constants.Companion.BASE_URL
import com.example.funnycatsfacts.databinding.ActivityMainBinding
import com.example.funnycatsfacts.databinding.ActivityMainBinding.inflate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        getCurrentData()

        binding.layoutGenerateNewFact.setOnClickListener {
            getCurrentData()
        }
    }

    private fun getCurrentData() {

        binding.apply {
            tvTextView.visibility = View.INVISIBLE
            tvTimeStamp.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE

            val api = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiRequest::class.java)

            GlobalScope.launch(Dispatchers.IO) {

                try {
                    val response = api.getCatFacts().awaitResponse()
                    if (response.isSuccessful) {
                        val data = response.body()!!
                        Log.d(TAG, data.text)

                        withContext(Dispatchers.Main) {
                            binding.apply {
                                tvTextView.visibility = View.VISIBLE
                                tvTimeStamp.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                tvTextView.text = data.text
                                tvTimeStamp.text = data.text

                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext, "Something went wrong with conection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}