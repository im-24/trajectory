package data.mappers

import data.models.TrajectoryResult
import data.models.ProjectileData
import data.models.EnvironmentData
import data.models.TrajectoryPoint
import data.models.DragModel as DataDragModel
import org.example.project.domain.Trajectory
import org.example.project.domain.Projectile
import org.example.project.domain.Environment
import org.example.project.domain.TrajectoryPoint as DomainTrajectoryPoint
import org.example.project.domain.DragModel as DomainDragModel

class TrajectoryMapper {

    fun toDomainModel(data: TrajectoryResult): Trajectory {
        return Trajectory(
            id = data.id,
            timestamp = data.timestamp,
            projectile = toDomainProjectile(data.projectileData),
            environment = toDomainEnvironment(data.environmentData),
            initialVelocity = data.initialVelocity,
            launchElevation = data.launchElevation,
            launchAzimuth = data.launchAzimuth,
            initialHeight = data.initialHeight,
            points = data.points.map { toDomainPoint(it) },
            maxHeight = data.maxHeight,
            maxDistance = data.maxDistance,
            timeOfFlight = data.timeOfFlight,
            impactVelocity = data.impactVelocity,
            impactAngle = data.impactAngle
        )
    }

    fun toDataModel(domain: Trajectory): TrajectoryResult {
        return TrajectoryResult(
            id = domain.id,
            timestamp = domain.timestamp,
            projectileData = toDataProjectile(domain.projectile),
            environmentData = toDataEnvironment(domain.environment),
            initialVelocity = domain.initialVelocity,
            launchElevation = domain.launchElevation,
            launchAzimuth = domain.launchAzimuth,
            initialHeight = domain.initialHeight,
            points = domain.points.map { toDataPoint(it) },
            maxHeight = domain.maxHeight,
            maxDistance = domain.maxDistance,
            timeOfFlight = domain.timeOfFlight,
            impactVelocity = domain.impactVelocity,
            impactAngle = domain.impactAngle
        )
    }

    // Domain to Data conversions
    fun toDataProjectile(domain: Projectile): ProjectileData {
        return ProjectileData(
            name = domain.name,
            mass = domain.mass,
            radius = domain.radius,
            diameter = domain.diameter,
            volume = domain.volume,
            material = domain.material,
            colorRed = 123,
            colorGreen = 94,
            colorBlue = 167,
            dragModel = toDataDragModel(domain.dragModel),
            ballisticCoefficient = domain.ballisticCoefficient,
            spinRate = domain.spinRate,
            spinAxisYaw = domain.spinAxisYaw,
            spinAxisPitch = domain.spinAxisPitch
        )
    }

    fun toDataEnvironment(domain: Environment): EnvironmentData {
        return EnvironmentData(
            gravity = domain.gravity,
            airDensity = domain.airDensity,
            windSpeed = domain.windSpeed,
            windDirection = domain.windDirection,
            temperature = domain.temperature,
            pressure = domain.pressure,
            humidity = domain.humidity,
            altitude = domain.altitude,
            temperatureLapseRate = domain.temperatureLapseRate,
            windGustSpeed = domain.windGustSpeed,
            windGustFrequency = domain.windGustFrequency,
            turbulenceIntensity = domain.turbulenceIntensity
        )
    }

    fun toDataPoint(domain: DomainTrajectoryPoint): TrajectoryPoint {
        return TrajectoryPoint(
            time = domain.time,
            x = domain.x,
            y = domain.y,
            z = domain.z,
            vx = domain.vx,
            vy = domain.vy,
            vz = domain.vz,
            speed = domain.speed,
            angle = domain.angle,
            azimuth = domain.azimuth,
            kineticEnergy = domain.kineticEnergy,
            potentialEnergy = domain.potentialEnergy,
            mach = domain.mach,
            dragCoefficient = domain.dragCoefficient,
            airDensity = domain.airDensity,
            temperature = domain.temperature
        )
    }

    // Data to Domain conversions
    fun toDomainProjectile(data: ProjectileData): Projectile {
        return Projectile(
            name = data.name,
            mass = data.mass,
            radius = data.radius,
            material = data.material,
            dragModel = toDomainDragModel(data.dragModel),
            ballisticCoefficient = data.ballisticCoefficient,
            spinRate = data.spinRate,
            spinAxisYaw = data.spinAxisYaw,
            spinAxisPitch = data.spinAxisPitch
        )
    }

    fun toDomainEnvironment(data: EnvironmentData): Environment {
        return Environment(
            gravity = data.gravity,
            airDensity = data.airDensity,
            windSpeed = data.windSpeed,
            windDirection = data.windDirection,
            temperature = data.temperature,
            pressure = data.pressure,
            humidity = data.humidity,
            altitude = data.altitude,
            temperatureLapseRate = data.temperatureLapseRate,
            windGustSpeed = data.windGustSpeed,
            windGustFrequency = data.windGustFrequency,
            turbulenceIntensity = data.turbulenceIntensity
        )
    }

    fun toDomainPoint(data: TrajectoryPoint): DomainTrajectoryPoint {
        return DomainTrajectoryPoint(
            time = data.time,
            x = data.x,
            y = data.y,
            z = data.z,
            vx = data.vx,
            vy = data.vy,
            vz = data.vz,
            speed = data.speed,
            angle = data.angle,
            azimuth = data.azimuth,
            kineticEnergy = data.kineticEnergy,
            potentialEnergy = data.potentialEnergy,
            mach = data.mach,
            dragCoefficient = data.dragCoefficient,
            airDensity = data.airDensity,
            temperature = data.temperature
        )
    }

    // DragModel conversions - FIXED with exhaustive when
    private fun toDataDragModel(domain: DomainDragModel): DataDragModel {
        return when (domain) {
            DomainDragModel.G1 -> DataDragModel.G1
            DomainDragModel.G7 -> DataDragModel.G7
            DomainDragModel.SPHERE -> DataDragModel.SPHERE
            DomainDragModel.CUSTOM_CD -> DataDragModel.CUSTOM_CD
        }
    }

    private fun toDomainDragModel(data: DataDragModel): DomainDragModel {
        return when (data) {
            DataDragModel.G1 -> DomainDragModel.G1
            DataDragModel.G7 -> DomainDragModel.G7
            DataDragModel.SPHERE -> DomainDragModel.SPHERE
            DataDragModel.CUSTOM_CD -> DomainDragModel.CUSTOM_CD
        }
    }
}