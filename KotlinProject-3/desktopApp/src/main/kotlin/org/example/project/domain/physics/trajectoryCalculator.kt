// physics/TrajectoryCalculator.kt
package physics

import data.models.*
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.*
import kotlin.random.Random

class TrajectoryCalculator {

    companion object {
        private const val DT = 0.005
        private const val GAS_CONSTANT_DRY_AIR = 287.05   // J/(kg·K)
        private const val GAMMA_AIR = 1.4
    }

    private fun dragCoefficient(mach: Double, model: DragModel, bc: Double): Double {
        return when (model) {
            DragModel.SPHERE -> when {
                mach < 0.6 -> 0.47
                mach < 1.0 -> 0.47 + (mach - 0.6) * (0.5 / 0.4)
                mach < 1.2 -> 0.97 - (mach - 1.0) * (0.3 / 0.2)
                else -> 0.67 - min(0.2, (mach - 1.2) * 0.1)
            }
            DragModel.G1 -> {
                val cdStd = when {
                    mach < 0.7 -> 0.18
                    mach < 0.9 -> 0.18 + (mach - 0.7) * (0.10 / 0.2)
                    mach < 1.0 -> 0.28 + (mach - 0.9) * (0.25 / 0.1)
                    mach < 1.05 -> 0.53
                    mach < 1.2 -> 0.53 - (mach - 1.05) * (0.08 / 0.15)
                    mach < 2.0 -> 0.45 - (mach - 1.2) * (0.15 / 0.8)
                    else -> 0.30
                }
                cdStd / max(bc, 0.01)
            }
            DragModel.G7 -> {
                val cdStd = when {
                    mach < 0.7 -> 0.12
                    mach < 0.9 -> 0.12 + (mach - 0.7) * (0.06 / 0.2)
                    mach < 1.0 -> 0.18 + (mach - 0.9) * (0.16 / 0.1)
                    mach < 1.05 -> 0.34
                    mach < 1.2 -> 0.34 - (mach - 1.05) * (0.05 / 0.15)
                    mach < 2.0 -> 0.29 - (mach - 1.2) * (0.10 / 0.8)
                    else -> 0.19
                }
                cdStd / max(bc, 0.01)
            }
            DragModel.CUSTOM_CD -> 0.47
        }
    }

    /**
     * Returns Pair(airDensity kg/m3, temperature K) at given altitude above launch site,
     * using the EnvironmentData's launch-site conditions as the base.
     */
    private fun airDensityAt(altitudeAboveLaunch: Double, env: EnvironmentData): Pair<Double, Double> {
        val seaLevelTempK = env.seaLevelTemperatureK
        val totalAltitude = env.altitude + altitudeAboveLaunch

        val temperature = (seaLevelTempK - env.temperatureLapseRate * totalAltitude)
            .coerceAtLeast(150.0)

        val lapseExp = if (env.temperatureLapseRate == 0.0) 1.0
        else env.gravity / (GAS_CONSTANT_DRY_AIR * env.temperatureLapseRate)

        val pressure = env.pressure * (temperature / (seaLevelTempK - env.temperatureLapseRate * env.altitude))
            .pow(lapseExp)

        var density = pressure / (GAS_CONSTANT_DRY_AIR * temperature)

        if (env.humidity > 0.0) {
            val tempC = temperature - 273.15
            val satVaporPressure = 610.78 * exp((17.27 * tempC) / (tempC + 237.3))
            val vaporPressure = env.humidity.coerceIn(0.0, 1.0) * satVaporPressure
            val gasConstantWaterVapor = 461.495
            val dryPartialPressure = pressure - vaporPressure
            density = (dryPartialPressure / (GAS_CONSTANT_DRY_AIR * temperature)) +
                    (vaporPressure / (gasConstantWaterVapor * temperature))
        }

        return Pair(density, temperature)
    }

    private fun speedOfSoundAt(temperature: Double): Double =
        sqrt(GAMMA_AIR * GAS_CONSTANT_DRY_AIR * temperature)

    fun calculateTrajectory(
        projectile: ProjectileData,
        environment: EnvironmentData,
        initialVelocity: Double,
        launchElevation: Double,
        launchAzimuth: Double = 0.0,
        initialHeight: Double = 0.0,
        randomSeed: Long? = null
    ): TrajectoryResult {

        val points = mutableListOf<TrajectoryPoint>()
        val elevRad = toRadians(launchElevation)
        val azimRad = toRadians(launchAzimuth)
        val rng = if (randomSeed != null) Random(randomSeed) else Random.Default

        var x = 0.0
        var y = initialHeight
        var z = 0.0

        var vx = initialVelocity * cos(elevRad) * cos(azimRad)
        var vy = initialVelocity * sin(elevRad)
        var vz = initialVelocity * cos(elevRad) * sin(azimRad)

        var t = 0.0

        val windRad = toRadians(environment.windDirection)
        val baseWindVx = environment.windSpeed * cos(windRad)
        val baseWindVz = environment.windSpeed * sin(windRad)

        val spinYawRad = toRadians(projectile.spinAxisYaw)
        val spinPitchRad = toRadians(projectile.spinAxisPitch)
        val spinX = projectile.spinRate * cos(spinPitchRad) * cos(spinYawRad)
        val spinY = projectile.spinRate * sin(spinPitchRad)
        val spinZ = projectile.spinRate * cos(spinPitchRad) * sin(spinYawRad)

        var maxHeight = y
        var maxHorizontalDist = 0.0

        while (y >= 0 && t < 120.0) {

            val (airDensity, temperature) = airDensityAt(y, environment)
            val speedOfSound = speedOfSoundAt(temperature)

            var windVx = baseWindVx
            var windVz = baseWindVz
            if (environment.windGustSpeed != 0.0 && environment.windGustFrequency != 0.0) {
                val gust = environment.windGustSpeed * sin(2.0 * PI * environment.windGustFrequency * t)
                windVx += gust * cos(windRad)
                windVz += gust * sin(windRad)
            }
            if (environment.turbulenceIntensity > 0.0) {
                val sigma = environment.windSpeed * environment.turbulenceIntensity
                windVx += rng.nextDouble(-1.0, 1.0) * sigma
                windVz += rng.nextDouble(-1.0, 1.0) * sigma
            }

            val relativeVx = vx - windVx
            val relativeVz = vz - windVz
            val relativeVy = vy
            val relSpeed = sqrt(relativeVx * relativeVx + relativeVy * relativeVy + relativeVz * relativeVz)
            val mach = if (speedOfSound > 0.0) relSpeed / speedOfSound else 0.0

            var ax = 0.0
            var ay = -environment.gravity
            var az = 0.0

            val cd = dragCoefficient(mach, projectile.dragModel, projectile.ballisticCoefficient)

            if (relSpeed > 0.1) {
                val dragForce = 0.5 * airDensity * relSpeed * relSpeed *
                        projectile.crossSectionalArea * cd

                val mass = projectile.mass
                ax += -dragForce * relativeVx / (relSpeed * mass)
                ay += -dragForce * relativeVy / (relSpeed * mass)
                az += -dragForce * relativeVz / (relSpeed * mass)

                if (projectile.spinRate != 0.0) {
                    val magnusCoefficient = 0.5 * airDensity * relSpeed *
                            projectile.crossSectionalArea * (projectile.diameter / 2.0) * 0.25
                    val fmx = (spinY * relativeVz - spinZ * relativeVy)
                    val fmy = (spinZ * relativeVx - spinX * relativeVz)
                    val fmz = (spinX * relativeVy - spinY * relativeVx)

                    ax += magnusCoefficient * fmx / projectile.mass
                    ay += magnusCoefficient * fmy / projectile.mass
                    az += magnusCoefficient * fmz / projectile.mass
                }
            }

            vx += ax * DT
            vy += ay * DT
            vz += az * DT

            x += vx * DT
            y += vy * DT
            z += vz * DT
            t += DT

            if (y > maxHeight) maxHeight = y
            val horizDist = sqrt(x * x + z * z)
            if (horizDist > maxHorizontalDist) maxHorizontalDist = horizDist

            val speed = sqrt(vx * vx + vy * vy + vz * vz)
            val ke = 0.5 * projectile.mass * speed * speed
            val pe = projectile.mass * environment.gravity * max(0.0, y)

            val flightAngle = toDegrees(atan2(vy, sqrt(vx * vx + vz * vz)))
            val currentAzimuth = toDegrees(atan2(vz, vx))

            points.add(
                TrajectoryPoint(
                    time = t, x = x, y = max(0.0, y), z = z,
                    vx = vx, vy = vy, vz = vz,
                    speed = speed,
                    angle = flightAngle,
                    azimuth = currentAzimuth,
                    kineticEnergy = ke,
                    potentialEnergy = pe,
                    mach = mach,
                    dragCoefficient = cd,
                    airDensity = airDensity,
                    temperature = temperature
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