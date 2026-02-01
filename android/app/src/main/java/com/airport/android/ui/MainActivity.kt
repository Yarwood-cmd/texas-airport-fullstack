package com.airport.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airport.android.AirportApplication
import com.airport.android.R
import com.airport.android.adapter.BookingAdapter
import com.airport.android.adapter.FlightAdapter
import com.airport.android.api.RetrofitClient
import com.airport.android.databinding.ActivityMainBinding
import com.airport.android.model.Booking
import com.airport.android.model.Flight
import kotlinx.coroutines.launch

/**
 * Main activity with flights and bookings tabs
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sessionManager by lazy { (application as AirportApplication).sessionManager }

    private lateinit var flightAdapter: FlightAdapter
    private lateinit var bookingAdapter: BookingAdapter

    private var currentTab = TAB_FLIGHTS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAdapters()
        setupBottomNav()
        setupSwipeRefresh()

        // Load initial data
        loadFlights()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Texas Airport"
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        // Show user info
        val user = sessionManager.getUser()
        if (user != null) {
            binding.tvWelcome.text = "Welcome, ${user.name}"
            binding.tvUserInfo.text = if (user.isFrequentFlyer()) {
                "${user.membershipLevel} Member • ${user.discountPercent}% discount"
            } else {
                "Regular Customer"
            }
        }
    }

    private fun setupAdapters() {
        // Flight adapter
        flightAdapter = FlightAdapter { flight ->
            openBookingScreen(flight)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = flightAdapter

        // Booking adapter
        bookingAdapter = BookingAdapter { booking ->
            confirmCancelBooking(booking)
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_flights -> {
                    currentTab = TAB_FLIGHTS
                    binding.recyclerView.adapter = flightAdapter
                    binding.tvSectionTitle.text = "Available Flights"
                    loadFlights()
                    true
                }
                R.id.nav_bookings -> {
                    currentTab = TAB_BOOKINGS
                    binding.recyclerView.adapter = bookingAdapter
                    binding.tvSectionTitle.text = "My Bookings"
                    loadBookings()
                    true
                }
                R.id.nav_profile -> {
                    showProfile()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            if (currentTab == TAB_FLIGHTS) {
                loadFlights()
            } else {
                loadBookings()
            }
        }
    }

    private fun loadFlights() {
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApi().getAvailableFlights()

                if (response.isSuccessful && response.body() != null) {
                    val flights = response.body()!!
                    flightAdapter.submitList(flights)

                    binding.tvEmpty.visibility = if (flights.isEmpty()) View.VISIBLE else View.GONE
                    binding.tvEmpty.text = "No flights available"
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load flights", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun loadBookings() {
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApi().getMyBookings()

                if (response.isSuccessful && response.body() != null) {
                    val bookings = response.body()!!
                    bookingAdapter.submitList(bookings)

                    binding.tvEmpty.visibility = if (bookings.isEmpty()) View.VISIBLE else View.GONE
                    binding.tvEmpty.text = "No bookings yet"
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load bookings", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun openBookingScreen(flight: Flight) {
        val intent = Intent(this, BookingActivity::class.java).apply {
            putExtra("flight_id", flight.id)
            putExtra("flight_number", flight.flightNumber)
            putExtra("flight_route", flight.getRoute())
            putExtra("flight_time", flight.departureTime)
            putExtra("flight_price", flight.basePrice)
        }
        startActivity(intent)
    }

    private fun confirmCancelBooking(booking: Booking) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel booking ${booking.bookingReference}?")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                cancelBooking(booking)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelBooking(booking: Booking) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApi().cancelBooking(booking.id)

                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Booking cancelled", Toast.LENGTH_SHORT).show()
                    loadBookings()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to cancel booking", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProfile() {
        val user = sessionManager.getUser()
        if (user != null) {
            AlertDialog.Builder(this)
                .setTitle("Profile")
                .setMessage("""
                    Name: ${user.name}
                    Email: ${user.email}
                    Type: ${if (user.isFrequentFlyer()) "Frequent Flyer" else "Regular"}
                    ${if (user.isFrequentFlyer()) "Level: ${user.membershipLevel}\nMiles: ${user.milesFlown}\nDiscount: ${user.discountPercent}%" else ""}
                """.trimIndent())
                .setPositiveButton("OK", null)
                .setNegativeButton("Logout") { _, _ -> logout() }
                .show()
        }
    }

    private fun logout() {
        sessionManager.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun setLoading(loading: Boolean) {
        binding.swipeRefresh.isRefreshing = loading
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning from booking
        if (currentTab == TAB_FLIGHTS) {
            loadFlights()
        } else if (currentTab == TAB_BOOKINGS) {
            loadBookings()
        }
    }

    companion object {
        private const val TAB_FLIGHTS = 0
        private const val TAB_BOOKINGS = 1
    }
}
