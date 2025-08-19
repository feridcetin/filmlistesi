package com.feridcetin.filmlistesi

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    //val apiKey = "54fb75d3bf2763677d7e28279bde2c98"
    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView'ı bul ve ayarla
        recyclerView = findViewById(R.id.movie_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter'ı başlat ve RecyclerView'a bağla
        movieAdapter = MovieAdapter(emptyList())
        recyclerView.adapter = movieAdapter

        // API'den film verilerini çek
        fetchPopularMovies()
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
    private fun fetchPopularMovies() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "İnternet bağlantısı yok. Lütfen kontrol ediniz.", Toast.LENGTH_LONG).show()
            Log.e("MainActivity", "İnternet bağlantısı yok, API isteği yapılamadı.")
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(TmdbApiService::class.java)

        // TODO: Buraya kendi TMDb API anahtarınızı ekleyin.
        val apiKey = "54fb75d3bf2763677d7e28279bde2c98"

        service.getPopularMovies(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    movieAdapter.updateMovies(movies)
                } else {
                    Log.e("MainActivity", "API isteği başarısız: ${response.errorBody()?.string()}")
                    Toast.makeText(this@MainActivity, "Filmler yüklenirken bir hata oluştu.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("MainActivity", "API isteği sırasında hata oluştu. ${t.message}", t)
                Toast.makeText(this@MainActivity, "API'ye bağlanılamadı. Lütfen tekrar deneyin.", Toast.LENGTH_LONG).show()
            }
        })
    }
}