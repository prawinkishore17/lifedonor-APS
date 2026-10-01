package com.aps.lifedonoraps.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aps.lifedonoraps.R
import com.aps.lifedonoraps.model.User

class LeaderboardAdapter(
    private val items: MutableList<User>
) : RecyclerView.Adapter<LeaderboardAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvBloodGroup: TextView = view.findViewById(R.id.tvBloodGroup)
        val tvPoints: TextView = view.findViewById(R.id.tvPoints)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val u = items[position]
        val rank = position + 1
        holder.tvRank.text = when (rank) {
            1 -> "1st"
            2 -> "2nd"
            3 -> "3rd"
            else -> "$rank"
        }
        holder.tvName.text = u.fullName
        holder.tvBloodGroup.text = u.bloodGroup
        holder.tvPoints.text = "${u.points} pts"
    }

    fun updateData(newItems: List<User>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
