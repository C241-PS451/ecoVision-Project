package com.example.ecovision.data.retrofit

data class PredictionResponse(
    val predicted_class: String,
    val image_url: String
)