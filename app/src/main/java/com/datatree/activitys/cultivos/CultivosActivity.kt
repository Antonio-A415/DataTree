package com.datatree.activitys.cultivos

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.datatree.R
import com.datatree.infraestructure.adapters.CarouselAdapter
import kotlin.math.abs

// para las graficas

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class CultivosActivity : AppCompatActivity() {


    private lateinit var cuadroCombinado: CombinedChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cultivos)

        cuadroCombinado = findViewById(R.id.cuadro)
        val images = listOf(
            R.drawable.habanero,
            R.drawable.chile,
            R.drawable.chile_mash
        )

        showCarousel(images)

        //setupHumidityChart()

        //mostrar_informacion_detallada()

        setupCombinedChart()


    }

    private fun showCarousel(images: List<Int>){
        val viewPager: ViewPager2 = findViewById<ViewPager2>(R.id.viewPagerCultivo)

        //aplicar propiedades
        viewPager.apply {
            clipChildren = false
            clipToPadding = false
            offscreenPageLimit = 3
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
        }
        val adapatador = CarouselAdapter(images)

        viewPager.adapter = adapatador
//viewPager.setPageTransformer(composi)
        //Dado que MarginPageTransformer usa píxeles como valor de entrada, colocamos el valor en dp y lo multiplicamos por la densidad de la pantalla para convertirlo en píxeles.
        val compositePageTransformer = CompositePageTransformer()

        compositePageTransformer.addTransformer(MarginPageTransformer((40*Resources.getSystem().displayMetrics.density).toInt()))
        //agregar un efecto de  escala de zoom
        compositePageTransformer.addTransformer{page, position ->
            val radio= 1- abs(position)
            page.scaleY = (0.50f+ radio*0.20f)
        }

        viewPager.setPageTransformer(compositePageTransformer)
       // viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }
    /*
    fun mostrarCarousel(images : List<Int>){
        val recycler = findViewById<RecyclerView>(R.id.recyclerViewCarousel)

        val adaptador = CarouselAdapter(images)
        recycler.adapter = adaptador

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
                val centerX = recyclerView.width/2
                val childCount = recyclerView.childCount

                for (i in 0 until childCount){
                    val child = recyclerView.getChildAt(i)
                    val childCenterX = (child.left + child.right) / 2
                    val scale = 1- abs(centerX-childCenterX).toFloat() / recyclerView.width
                    child.scaleX = 0.8f + scale*0.2f
                    child.scaleY = 0.08f + scale*0.2f
                }
            }
        })
    }
*/


    private fun setupCombinedChart(){

        val ViewModel = ViewModelCultivosActivity()




        cuadroCombinado.apply {
            axisLeft.apply {
                axisMinimum=0f
                granularity =3f
            }
            axisRight.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setLabelCount(24, false)
                valueFormatter = IndexAxisValueFormatter((0..23).map { it.toString() })
            }

            description.isEnabled = false
            setDrawGridBackground(false)
        }

        val data = CombinedData()

        data.setData(ViewModel.getHumidityBarData())
        data.setData(ViewModel.getOptimalHumidityLineData())

        cuadroCombinado.data = data
        cuadroCombinado.invalidate()
    }
    private fun _setupHumidityChart() {

        val ViewModel = ViewModelCultivosActivity()
        val humidityChart: CombinedChart = findViewById<CombinedChart>(R.id.cuadro)

        val combinedData = CombinedData().apply {
            setData(ViewModel.getHumidityBarData())
            setData(ViewModel.getOptimalHumidityLineData())
        }

        // Configurar eje X
        val xAxis = humidityChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("T0", "T1", "T2", "T3", "T4")) // Etiquetas de tiempo

        // Configurar otros detalles del gráfico
        humidityChart.data = combinedData
        humidityChart.setDrawGridBackground(false)
        humidityChart.description.isEnabled = false
        humidityChart.setDrawOrder(
            arrayOf(
                CombinedChart.DrawOrder.BAR,
                CombinedChart.DrawOrder.LINE
            )
        )
        humidityChart.invalidate()
    }

    fun mostrar_informacion_detallada(){
        //Mostrar un showtext
        Toast.makeText(this,"Quickly te ayudará a entender estos datos, un momento por favor.",Toast.LENGTH_LONG).show()
    }

}