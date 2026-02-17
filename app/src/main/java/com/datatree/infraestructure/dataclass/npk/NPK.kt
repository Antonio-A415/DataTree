package com.datatree.infraestructure.dataclass.npk

data class NPKData(
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val recommendation: String
)

data class NPKEstimationInput(
    val temperature: Float,
    val airHumidity: Float,
    val soilHumidity: Int,
    val previousCrop: String,
    val soilType: String
)

object NPKEstimator {

    // Dataset simplificado para estimación NPK basado en frijol como cultivo antecesor
    private val beanCropData = mapOf(
        "arcilloso" to Triple(45f, 35f, 40f),
        "arenoso" to Triple(65f, 50f, 55f),
        "limoso" to Triple(50f, 40f, 45f),
        "franco" to Triple(55f, 45f, 50f)
    )

    fun estimateNPK(input: NPKEstimationInput): NPKData {
        // Obtener valores base según tipo de suelo
        val baseValues = beanCropData[input.soilType.lowercase()] ?: Triple(55f, 45f, 50f)

        // Ajustes basados en condiciones ambientales
        val tempFactor = when {
            input.temperature < 15f -> 1.15f // Más fertilizante en frío
            input.temperature > 30f -> 1.10f // Más fertilizante en calor
            else -> 1.0f
        }

        val humidityFactor = when {
            input.soilHumidity > 3000 -> 1.20f // Suelo seco necesita más nutrientes
            input.soilHumidity > 2000 -> 1.10f
            else -> 1.0f
        }

        val airHumidityFactor = when {
            input.airHumidity < 40f -> 1.08f
            input.airHumidity > 80f -> 0.95f
            else -> 1.0f
        }

        // Calcular NPK ajustado
        val nitrogen = (baseValues.first * tempFactor * humidityFactor * airHumidityFactor)
            .coerceIn(20f, 120f)
        val phosphorus = (baseValues.second * humidityFactor * airHumidityFactor)
            .coerceIn(15f, 80f)
        val potassium = (baseValues.third * tempFactor * humidityFactor)
            .coerceIn(20f, 100f)

        val recommendation = generateRecommendation(nitrogen, phosphorus, potassium, input)

        return NPKData(nitrogen, phosphorus, potassium, recommendation)
    }

    private fun generateRecommendation(n: Float, p: Float, k: Float, input: NPKEstimationInput): String {
        val nLevel = when {
            n < 40 -> "bajo"
            n > 80 -> "alto"
            else -> "adecuado"
        }
        val pLevel = when {
            p < 30 -> "bajo"
            p > 60 -> "alto"
            else -> "adecuado"
        }
        val kLevel = when {
            k < 35 -> "bajo"
            k > 70 -> "alto"
            else -> "adecuado"
        }

        return buildString {
            append("Después de frijol en suelo ${input.soilType}: ")
            append("N $nLevel, P $pLevel, K $kLevel. ")

            if (input.soilHumidity > 2500) {
                append("Regar antes de fertilizar. ")
            }
            if (input.temperature > 28) {
                append("Aplicar en horas frescas. ")
            }
            append("Fertilización recomendada para siguiente ciclo.")
        }
    }
}