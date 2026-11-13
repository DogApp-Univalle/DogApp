package com.univalle.dogapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.univalle.dogapp.databinding.ActivityMainBinding

/**
 * Unica Activity de la app: hospeda el NavHostFragment con las cinco pantallas.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
