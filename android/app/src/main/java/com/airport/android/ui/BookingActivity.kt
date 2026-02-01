package com.airport.android.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.airport.android.AirportApplication
import com.airport.android.api.RetrofitClient
import com.airport.android.databinding.ActivityBookingBinding
import com.airport.android.model.BookingRequest
import kotlinx.coroutines.launch

/**
 * Screen for creating a new booking
 */
class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private val sessionManager by lazy { (application as AirportApplication).sessionManager }

    private var flightId: Long = 0
    private var flightPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get flight info from intent
        flightId = intent.getLongExtra("flight_id", 0)
        val flightNumber = intent.getStringExtra("flight_number") ?: ""
        val flightRoute = intent.getStringExtra("flight_route") ?: ""
        val flightTime = intent.getStringExtra("flight_time") ?: ""
        flightPrice = intent.getDoubleExtra("flight_price", 0.0)

        setupUI(flightNumber, flightRoute, flightTime)
    }

    private fun setupUI(flightNumber: String, flightRoute: String, flightTime: String) {
        binding.apply {
            // Back button
            toolbar.setNavigationOnClickListener { finish() }
            toolbar.title = "Book Flight"

            // Flight info
            tvFlightNumber.text = flightNumber
            tvRoute.text = flightRoute
            tvDepartureTime.text = "Departure: $flightTime"

            // Calculate price with discount
            val user = sessionManager.getUser()
            val discount = user?.discountPercent ?: 0
            val discountAmount = flightPrice * (discount / 100.0)
            val finalPrice = flightPrice - discountAmount

            tvBasePrice.text = "Base Price: $${String.format("%.2f", flightPrice)}"
            if (discount > 0) {
                tvDiscount.visibility = View.VISIBLE
                tvDiscount.text = "Discount ($discount%): -$${String.format("%.2f", discountAmount)}"
            } else {
                tvDiscount.visibility = View.GONE
            }
            tvTotalPrice.text = "Total: $${String.format("%.2f", finalPrice)}"

            // Seat preference dropdown
            val seatPreferences = arrayOf("WINDOW", "AISLE", "MIDDLE", "NO_PREFERENCE")
            val adapter = ArrayAdapter(this@BookingActivity, android.R.layout.simple_dropdown_item_1line, seatPreferences)
            spinnerSeatPref.adapter = adapter

            // Pre-fill with user name if available
            user?.let {
                val nameParts = it.name.split(" ")
                if (nameParts.isNotEmpty()) {
                    etFirstName.setText(nameParts[0])
                    if (nameParts.size > 1) {
                        etLastName.setText(nameParts.drop(1).joinToString(" "))
                    }
                }
            }

            // Book button
            btnConfirmBooking.setOnClickListener {
                createBooking()
            }
        }
    }

    private fun createBooking() {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val ageStr = binding.etAge.text.toString().trim()
        val seatNumber = binding.etSeatNumber.text.toString().trim()
        val seatPref = binding.spinnerSeatPref.selectedItem.toString()

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || ageStr.isEmpty() || seatNumber.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageStr.toIntOrNull()
        if (age == null || age < 0 || age > 120) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val request = BookingRequest(
            flightId = flightId,
            passengerFirstName = firstName,
            passengerLastName = lastName,
            passengerAge = age,
            seatPreference = seatPref,
            seatNumber = seatNumber.uppercase()
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApi().createBooking(request)

                if (response.isSuccessful && response.body() != null) {
                    val booking = response.body()!!
                    Toast.makeText(
                        this@BookingActivity,
                        "Booking confirmed! Ref: ${booking.bookingReference}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    val error = response.errorBody()?.string() ?: "Booking failed"
                    Toast.makeText(this@BookingActivity, error, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BookingActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            btnConfirmBooking.isEnabled = !loading
        }
    }
}
