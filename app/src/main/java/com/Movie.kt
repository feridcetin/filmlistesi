package com.feridcetin.filmlistesi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

// API'den gelen yanıtın veri modeli
data class MovieResponse(
    val results: List<Movie>
)

// Film veri modeli
data class Movie(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val overview: String,
    val vote_average: Double
) {
    // Adapter için uygun isimlere dönüştüren yardımcı metotlar
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500$poster_path"

    val rating: Double
        get() = vote_average
}

// Retrofit için API servis arayüzü
interface TmdbApiService {
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String
    ): Call<MovieResponse>
}

// RecyclerView için Adapter
class MovieAdapter(private var movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.movie_title)
        val poster: ImageView = view.findViewById(R.id.movie_poster)
        val rating: TextView = view.findViewById(R.id.movie_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.title.text = movie.title
        holder.rating.text = "Puan: ${movie.rating}"
        Glide.with(holder.itemView.context)
            .load(movie.posterUrl)
            .placeholder(R.drawable.placeholder_image) // Afiş yoksa gösterilecek yer tutucu resim
            .into(holder.poster)
    }

    override fun getItemCount() = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        this.movies = newMovies
        notifyDataSetChanged()
    }
}


