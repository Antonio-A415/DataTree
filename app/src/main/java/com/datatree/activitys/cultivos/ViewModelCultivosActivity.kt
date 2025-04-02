package com.datatree.activitys.cultivos
import android.graphics.Color
import com.github.mikephil.charting.data.*


class ViewModelCultivosActivity {


    fun getHumidityBarData(): BarData {
        val humedadEntrada = mutableListOf<BarEntry>()
/*
        humedadEntrada.add(BarEntry(0F, 55.0F)) //paar el tiempo 0
        humedadEntrada.add(BarEntry(1F, 66.0F))
        humedadEntrada.add(BarEntry(2F, 10.0F))
        humedadEntrada.add(BarEntry(3F, 36.0F))

        */

        for( i in 0 until 24){
            val humedad = (Math.random()*100).toFloat()
            humedadEntrada.add(BarEntry(i.toFloat(),humedad))
        }

        val barDataSet = BarDataSet(humedadEntrada, "Niveles de humedad").apply {
            color =  Color.parseColor("#133A94")
            valueTextColor = Color.BLACK
            valueTextSize = 10.0f
        }
        return BarData(barDataSet)
    }

    fun getOptimalHumidityLineData(): LineData {

        val entradasOptimas = mutableListOf<Entry>()
        entradasOptimas.add(Entry(6.0f,88.0f))
        entradasOptimas.add(Entry(12.0f, 80.0f)) // Un ejemplo a la hora 12
        entradasOptimas.add(Entry(18.0f, 70.0f)) // Otro ejemplo a la hora 18
        entradasOptimas.add(Entry(23.0f, 65.0f)) // Otro ejemplo a la hora 23


        val lineDataSet = LineDataSet(entradasOptimas, "Humedad óptimas").apply {
            color =  Color.GREEN
            circleColors = listOf( Color.GREEN)
            circleHoleColor = Color.GREEN
            lineWidth = 1.5f
            circleRadius = 0.65f
            valueTextColor= 10
            setDrawCircles(true)
            valueTextSize = 10f

        }
        return LineData(lineDataSet)

    }



}