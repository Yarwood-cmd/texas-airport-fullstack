package com.airport.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airport.android.databinding.ItemFlightBinding
import com.airport.android.model.Flight

/**
 * Adapter for displaying flights in RecyclerView
 */
class FlightAdapter(
    private val onBookClick: (Flight) -> Unit
) : ListAdapter<Flight, FlightAdapter.FlightViewHolder>(FlightDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val binding = ItemFlightBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FlightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(getItem(position), onBookClick)
    }

    class FlightViewHolder(
        private val binding: ItemFlightBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(flight: Flight, onBookClick: (Flight) -> Unit) {
            binding.apply {
                tvFlightNumber.text = flight.flightNumber
                tvRoute.text = flight.getRoute()
                tvDepartureTime.text = "Departure: ${flight.departureTime}"
                tvSeats.text = "${flight.availableSeats}/${flight.capacity} seats"
                tvPrice.text = flight.getFormattedPrice()

                btnBook.isEnabled = flight.hasAvailableSeats()
                btnBook.text = if (flight.hasAvailableSeats()) "Book" else "Full"

                btnBook.setOnClickListener { onBookClick(flight) }
            }
        }
    }

    class FlightDiffCallback : DiffUtil.ItemCallback<Flight>() {
        override fun areItemsTheSame(oldItem: Flight, newItem: Flight) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Flight, newItem: Flight) = oldItem == newItem
    }
}
