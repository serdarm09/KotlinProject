package com.ornek.cartrackingsystem.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ornek.cartrackingsystem.common.Resource
import com.ornek.cartrackingsystem.ui.main.MainContract.Vehicle
import com.ornek.cartrackingsystem.ui.main.MainContract.VehicleStatus
import com.ornek.cartrackingsystem.ui.main.MainContract.Location
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getVehicles(): Resource<List<Vehicle>> {
        return try {
            val snapshot = firestore.collection("vehicles").get().await()
            val vehicles = snapshot.documents.mapNotNull { document ->
                val locationStr = document.getString("location") ?: "0,0"
                val location = Location.fromString(locationStr) ?: Location(0.0, 0.0)
                
                Vehicle(
                    plate = document.getString("plate") ?: "Plaka Yok",
                    brand = document.getString("brand") ?: "Marka Yok",
                    model = document.getString("model") ?: "Model Yok",
                    status = try {
                        VehicleStatus.valueOf(document.getString("status") ?: "AVAILABLE")
                    } catch (e: Exception) {
                        VehicleStatus.AVAILABLE
                    },
                    location = location
                )
            }
            Resource.Success(vehicles)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    suspend fun addVehicle(vehicle: Vehicle): Resource<Unit> {
        return try {
            val data = mapOf(
                "plate" to vehicle.plate,
                "brand" to vehicle.brand,
                "model" to vehicle.model,
                "status" to vehicle.status.name,
                "location" to vehicle.location.toString()
            )
            firestore.collection("vehicles").add(data).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    suspend fun updateVehicleStatus(vehicleId: String, status: VehicleStatus): Resource<Unit> {
        return try {
            firestore.collection("vehicles")
                .document(vehicleId)
                .update("status", status.name)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    suspend fun updateVehicleLocation(vehicleId: String, location: Location): Resource<Unit> {
        return try {
            firestore.collection("vehicles")
                .document(vehicleId)
                .update("location", location.toString())
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
} 