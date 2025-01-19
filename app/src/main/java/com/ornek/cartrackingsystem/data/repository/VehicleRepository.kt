package com.ornek.cartrackingsystem.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ornek.cartrackingsystem.common.Resource
import com.ornek.cartrackingsystem.ui.main.MainContract.Vehicle
import com.ornek.cartrackingsystem.ui.main.MainContract.VehicleStatus
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
                try {
                    Vehicle(
                        plate = document.getString("plate") ?: return@mapNotNull null,
                        brand = document.getString("brand") ?: return@mapNotNull null,
                        model = document.getString("model") ?: return@mapNotNull null,
                        status = VehicleStatus.valueOf(document.getString("status") ?: return@mapNotNull null),
                        location = document.getString("location") ?: "Haritada Gör"
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Resource.Success(vehicles)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    suspend fun addVehicle(vehicle: Vehicle): Resource<Unit> {
        return try {
            val vehicleData = hashMapOf(
                "plate" to vehicle.plate,
                "brand" to vehicle.brand,
                "model" to vehicle.model,
                "status" to vehicle.status.name,
                "location" to vehicle.location
            )
            firestore.collection("vehicles").document(vehicle.plate).set(vehicleData).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    suspend fun updateVehicleStatus(plate: String, status: VehicleStatus): Resource<Unit> {
        return try {
            firestore.collection("vehicles")
                .document(plate)
                .update("status", status.name)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
} 