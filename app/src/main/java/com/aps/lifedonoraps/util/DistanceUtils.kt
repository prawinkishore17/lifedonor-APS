package com.aps.lifedonoraps.util

import kotlin.math.*

/**
 * Haversine distance calculation.
 * Used for "nearby donor" filtering. Accounts for Earth's curvature,
 * unlike simple Euclidean distance.
 *
 * Eq: d = 2r * arcsin( sqrt( sin^2(dLat/2) + cos(lat1)cos(lat2)sin^2(dLon/2) ) )
 */
object DistanceUtils {

    private const val EARTH_RADIUS_KM = 6371.0

    /** Returns distance in kilometres between two coordinates. */
    fun haversineKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val rLat1 = Math.toRadians(lat1)
        val rLat2 = Math.toRadians(lat2)

        val a = sin(dLat / 2).pow(2.0) +
                cos(rLat1) * cos(rLat2) * sin(dLon / 2).pow(2.0)

        return 2 * EARTH_RADIUS_KM * asin(sqrt(a))
    }

    /** Convenience: is the point within the given radius? */
    fun isWithin(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
        radiusKm: Double
    ): Boolean = haversineKm(lat1, lon1, lat2, lon2) <= radiusKm
}
