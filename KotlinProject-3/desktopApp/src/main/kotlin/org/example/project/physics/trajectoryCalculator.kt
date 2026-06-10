// physics/TrajectoryCalculator.kt
package physics

import data.models.*
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.*

class TrajectoryCalculator {

    companion object {
        private const val DT = 0.005
        private const val DRAG_COEFFICIENT = 0.47
    }

    fun calculateTrajectory(
        projectile: ProjectileData,
        environment: EnvironmentData,
        initialVelocity: Double,
        launchElevation: Double,   // vertical angle
        launchAzimuth: Double = 0.0, // horizontal direction
        initialHeight: Double = 0.0
    ): TrajectoryResult {

        val points = mutableListOf<TrajectoryPoint>()
        val elevRad = toRadians(launchElevation)
        val azimRad = toRadians(launchAzimuth)

        var x = 0.0
        var y = initialHeight
        var z = 0.0

        var vx = initialVelocity * cos(elevRad) * cos(azimRad)
        var vy = initialVelocity * sin(elevRad)
        var vz = initialVelocity * cos(elevRad) * sin(azimRad)

        var t = 0.0

        val windRad = toRadians(environment.windDirection)
        val windVx = environment.windSpeed * cos(windRad)
        val windVz = environment.windSpeed * sin(windRad)  // assuming wind in XZ plane

        var maxHeight = y
        var maxHorizontalDist = 0.0

        while (y >= 0 && t < 120.0) {
            val relativeVx = vx - windVx
            val relativeVz = vz - windVz
            val relativeVy = vy
            val relSpeed = sqrt(relativeVx*relativeVx + relativeVy*relativeVy + relativeVz*relativeVz)

            var ax = 0.0
            var ay = -environment.gravity
            var az = 0.0

            if (relSpeed > 0.1) {
                val dragForce = 0.5 * environment.airDensity * relSpeed * relSpeed *
                        projectile.crossSectionalArea * DRAG_COEFFICIENT

                val mass = projectile.mass
                ax = -dragForce * relativeVx / (relSpeed * mass)
                ay += -dragForce * relativeVy / (relSpeed * mass)
                az = -dragForce * relativeVz / (relSpeed * mass)
            }

            vx += ax * DT
            vy += ay * DT
            vz += az * DT

            x += vx * DT
            y += vy * DT
            z += vz * DT
            t += DT

            if (y > maxHeight) maxHeight = y
            val horizDist = sqrt(x*x + z*z)
            if (horizDist > maxHorizontalDist) maxHorizontalDist = horizDist

            val speed = sqrt(vx*vx + vy*vy + vz*vz)
            val ke = 0.5 * projectile.mass * speed * speed
            val pe = projectile.mass * environment.gravity * max(0.0, y)

            val flightAngle = toDegrees(atan2(vy, sqrt(vx*vx + vz*vz)))
            val currentAzimuth = toDegrees(atan2(vz, vx))

            points.add(
                TrajectoryPoint(
                    time = t, x = x, y = max(0.0, y), z = z,
                    vx = vx, vy = vy, vz = vz,
                    speed = speed,
                    angle = flightAngle,
                    azimuth = currentAzimuth,
                    kineticEnergy = ke,
                    potentialEnergy = pe
                )
            )

            if (abs(vx) < 0.05 && abs(vz) < 0.05 && y < 0.1 && t > 1.0) break
        }

        val last = points.lastOrNull()
        return TrajectoryResult(
            id = System.currentTimeMillis().toString(),
            timestamp = System.currentTimeMillis(),
            projectileData = projectile,
            environmentData = environment,
            initialVelocity = initialVelocity,
            launchElevation = launchElevation,
            launchAzimuth = launchAzimuth,
            initialHeight = initialHeight,
            points = points,
            maxHeight = maxHeight,
            maxDistance = maxHorizontalDist,
            timeOfFlight = last?.time ?: 0.0,
            impactVelocity = last?.speed ?: 0.0,
            impactAngle = last?.angle ?: 0.0
        )
    }
}