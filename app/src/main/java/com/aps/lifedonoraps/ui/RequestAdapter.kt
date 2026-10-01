package com.aps.lifedonoraps.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aps.lifedonoraps.R
import com.aps.lifedonoraps.model.BloodRequest

class RequestAdapter(
    private val items: MutableList<BloodRequest>,
    private val onCallClick: (BloodRequest) -> Unit
) : RecyclerView.Adapter<RequestAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvBloodGroup: TextView = view.findViewById(R.id.tvBloodGroup)
        val tvPatient: TextView = view.findViewById(R.id.tvPatient)
        val tvHospital: TextView = view.findViewById(R.id.tvHospital)
        val tvUrgency: TextView = view.findViewById(R.id.tvUrgency)
        val btnCall: TextView = view.findViewById(R.id.btnCall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = items[position]
        holder.tvBloodGroup.text = r.bloodGroup
        holder.tvPatient.text = "Patient: ${r.patientName}"
        holder.tvHospital.text = r.hospital
        holder.tvUrgency.text = r.urgency
        holder.tvUrgency.setBackgroundResource(
            if (r.urgency == "Urgent") R.drawable.bg_urgent else R.drawable.bg_normal
        )
        holder.btnCall.setOnClickListener { onCallClick(r) }
    }

    fun updateData(newItems: List<BloodRequest>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
