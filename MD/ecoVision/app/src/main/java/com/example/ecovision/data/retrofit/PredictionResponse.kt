package com.example.ecovision.data.retrofit

import com.google.gson.annotations.SerializedName

data class PredictionResponse(
    @SerializedName("predicted_class") val predictedClass: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("prediction_time") val predictionTime: String
)