package com.aps.lifedonoraps.util

/**
 * Reward point rules for the donor recognition system.
 *
 * Points = BASE + URGENT_BONUS (if the donation answered an urgent request)
 *                + STREAK_BONUS (if donated again within the active window)
 */
object RewardUtils {

    const val BASE_POINTS = 10L
    const val URGENT_BONUS = 5L
    const val STREAK_BONUS = 5L
    const val STREAK_WINDOW_DAYS = 120L

    fun calculatePoints(
        wasUrgent: Boolean,
        daysSinceLastDonation: Long?
    ): Long {
        var points = BASE_POINTS
        if (wasUrgent) points += URGENT_BONUS
        if (daysSinceLastDonation != null && daysSinceLastDonation <= STREAK_WINDOW_DAYS) {
            points += STREAK_BONUS
        }
        return points
    }
}
