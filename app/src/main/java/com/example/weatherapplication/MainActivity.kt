package com.example.weatherapplication

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import com.example.weatherapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    lateinit var  binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchWeatherData("Palampur")
        searchCity()
    }

    private fun searchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName:String) {
        var retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response= retrofit.GetWeatherData(cityName,"42f8477b315007bc0008deb4a0e43ee7","metric")
        response.enqueue(object:Callback<Weatherdata>{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<Weatherdata>, response: Response<Weatherdata>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperature=responseBody.main.temp.toInt()
                    val maxtemp=responseBody.main.temp_max.toString()
                    val mintemp=responseBody.main.temp_min.toString()
                    val humidity=responseBody.main.humidity.toString()
                    val sealevel=responseBody.main.sea_level.toString()
                    val windspeed=responseBody.wind.speed.toString()
                    val sunrise=responseBody.sys.sunrise.toLong()
                    val sunset=responseBody.sys.sunset.toLong()
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    binding.temp.text="${fahrenheitToCelsius(temperature) } ?"
                    binding.maxTime.text=" max $maxtemp  ?"
                    binding.minTime.text=" min $mintemp  ?"
                    binding.humidity.text=" $humidity %"
                    binding.sea.text="$sealevel hPa"
                    binding.wind.text="$windspeed Km/h"
                    binding.weather.text=condition
                    binding.conditon.text=condition
                    binding.sunrise.text="${time(sunrise)} "
                    binding.sunset.text="${day(sunset)} "
                    binding.cityName.text="$cityName"
                    binding.day.text=SimpleDateFormat("EEEE").format(Date())
                    binding.date.text=SimpleDateFormat("dd/MM/yyyy").format(Date())

                    changebackaccoundingcondition(condition)
                }
            }

            private fun changebackaccoundingcondition(conditions:String) {
                when(conditions)
                {
                    "Haze","Partly Cloud","Could","Overcast","Mist","Foggy"->
                    {
                        binding.root.setBackgroundResource(R.drawable.colud_background)
                        binding.lottieAnimationView.setAnimation(R.raw.cloud)

                    }
                    "Clear Sky","Sunny","Clear" ->
                    {
                        binding.root.setBackgroundResource(R.drawable.sunny_background)
                        binding.lottieAnimationView.setAnimation(R.raw.sun)
                    }
                    "Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->
                    {
                        binding.root.setBackgroundResource(R.drawable.rain_background)
                        binding.lottieAnimationView.setAnimation(R.raw.rain)
                    }
                    "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->
                    {
                        binding.root.setBackgroundResource(R.drawable.snow_background)
                        binding.lottieAnimationView.setAnimation(R.raw.snow)
                    }

                }
                binding.lottieAnimationView.playAnimation()
            }

            override fun onFailure(call: Call<Weatherdata>, t: Throwable) {
                Log.d("TAG","fetch data failed")
            }
        })


    }
    fun time(timestamp: Long):String
    {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return  sdf.format(Date(timestamp*1000))
    }
    fun day(timestamp: Long):String
    {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return  sdf.format(Date(timestamp*1000))
    }


    private fun fahrenheitToCelsius(tempertaure: Int): Int {
        return (tempertaure - 32) * 5 / 9
    }
}
