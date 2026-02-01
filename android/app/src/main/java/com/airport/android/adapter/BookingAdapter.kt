package com.airport.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airport.android.R
import com.airport.android.databinding.ItemBookingBinding
import com.airport.android.model.Booking

/**
 * Adapter for displaying bookings in RecyclerView
 */
class BookingAdapter(
    private val onCancelClick: (Booking) -> Unit
) : ListAdapter<Booking, BookingAdapter.BookingViewHolder>(BookingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position), onCancelClick)
    }

    class BookingViewHolder(
        private val binding: ItemBookingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking, onCancelClick: (Booking) -> Unit) {
            binding.apply {
                tvBookingRef.text = "Ref: ${booking.bookingReference}"
                tvFlightNumber.text = booking.flightNumber
                tvRoute.text = booking.getRoute()
                tvDepartureTime.text = "Departure: ${booking.departureTime}"
                tvPassenger.text = "Passenger: ${booking.passengerName}"
                tvSeat.text = "Seat: ${booking.seatNumber}"
                tvPrice.text = booking.getFormattedPrice()
                tvStatus.text = booking.status

                // Style status
                val statusColor = when {
                    booking.isActive() -> ContextCompat.getColor(itemView.context, R.color.status_confirmed)
                    booking.isCancelled() -> ContextCompat.getColor(itemView.context, R.color.status_cancelled)
                    else -> ContextCompat.getColor(itemView.context, R.color.status_pending)
                }
                tvStatus.setTextColor(statusColor)

                // Show/hide cancel button
                btnCancel.visibility = if (booking.isActive()) View.VISIBLE else View.GONE
                btnCancel.setOnClickListener { onCancelClick(booking) }
            }
        }
    }

    class BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Booking, newItem: Booking) = oldItem == newItem
    }
}
