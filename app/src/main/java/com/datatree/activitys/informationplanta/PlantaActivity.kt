package com.datatree.activitys.informationplanta

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.datatree.R
import com.datatree.core.services.DATABASEHELPER
import com.datatree.infraestructure.dataclass.databasemodels.ModelPlanta
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.*

class PlantaActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DATABASEHELPER
    private var currentPlanta: ModelPlanta? = null
    private var plantId: Long = -1
    private var isFavorite: Boolean = false

    // Views principales
    private lateinit var toolbar: MaterialToolbar
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var imageViewPager: ViewPager2
    private lateinit var fabFavorite: FloatingActionButton

    // Views de información básica
    private lateinit var txtNombrePrincipal: TextView
    private lateinit var txtNombreCientificoPrincipal: TextView
    private lateinit var chipClasificacion: Chip
    private lateinit var txtClasificacionDetalle: TextView
    private lateinit var txtAlturaPromedio: TextView
    private lateinit var txtTipoRaiz: TextView

    // Views de condiciones de cultivo
    private lateinit var txtTemperatura: TextView
    private lateinit var txtHumedadRelativa: TextView
    private lateinit var txtPh: TextView
    private lateinit var txtHorasLuz: TextView
    private lateinit var txtTipoSuelo: TextView

    // Views NPK
    private lateinit var progressNitrogeno: LinearProgressIndicator
    private lateinit var progressFosforo: LinearProgressIndicator
    private lateinit var progressPotasio: LinearProgressIndicator
    private lateinit var txtNitrogeno: TextView
    private lateinit var txtFosforo: TextView
    private lateinit var txtPotasio: TextView

    // Views cronograma
    private lateinit var txtSiembraMexico: TextView
    private lateinit var txtCosechaMexico: TextView
    private lateinit var txtFaseLunarSiembra: TextView
    private lateinit var txtFaseLunarCosecha: TextView
    private lateinit var txtTiempoGerminacion: TextView
    private lateinit var txtCicloVida: TextView
    private lateinit var txtDensidadSiembra: TextView

    // Views plagas y control
    private lateinit var txtPlagas: TextView
    private lateinit var txtControlBiologico: TextView

    // Views compatibilidad
    private lateinit var chipGroupCompatibles: ChipGroup
    private lateinit var chipGroupIncompatibles: ChipGroup

    // Views económicas
    private lateinit var txtCostoProduccion: TextView
    private lateinit var txtPrecioPromedio: TextView
    private lateinit var txtRendimiento: TextView
    private lateinit var txtRentabilidad: TextView

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planta)

        databaseHelper = DATABASEHELPER(this)
        plantId = intent.getLongExtra("plant_id", -1)

        initViews()
        setupToolbar()
        setupFab()

        if (plantId != -1L) {
            loadPlantData()
        } else {
            showError("No se pudo cargar la información de la planta")
            finish()
        }
    }

    private fun initViews() {
        // Views principales
        toolbar = findViewById(R.id.toolbar)
        collapsingToolbar = findViewById(R.id.collapsingToolbar)
        imageViewPager = findViewById(R.id.imageViewPager)
        fabFavorite = findViewById(R.id.fabFavorite)

        // Información básica
        txtNombrePrincipal = findViewById(R.id.txtNombrePrincipal)
        txtNombreCientificoPrincipal = findViewById(R.id.txtNombreCientificoPrincipal)
        chipClasificacion = findViewById(R.id.chipClasificacion)
        txtClasificacionDetalle = findViewById(R.id.txtClasificacionDetalle)
        txtAlturaPromedio = findViewById(R.id.txtAlturaPromedio)
        txtTipoRaiz = findViewById(R.id.txtTipoRaiz)

        // Condiciones de cultivo
        txtTemperatura = findViewById(R.id.txtTemperatura)
        txtHumedadRelativa = findViewById(R.id.txtHumedadRelativa)
        txtPh = findViewById(R.id.txtPh)
        txtHorasLuz = findViewById(R.id.txtHorasLuz)
        txtTipoSuelo = findViewById(R.id.txtTipoSuelo)

        // NPK
        progressNitrogeno = findViewById(R.id.progressNitrogeno)
        progressFosforo = findViewById(R.id.progressFosforo)
        progressPotasio = findViewById(R.id.progressPotasio)
        txtNitrogeno = findViewById(R.id.txtNitrogeno)
        txtFosforo = findViewById(R.id.txtFosforo)
        txtPotasio = findViewById(R.id.txtPotasio)

        // Cronograma
        txtSiembraMexico = findViewById(R.id.txtSiembraMexico)
        txtCosechaMexico = findViewById(R.id.txtCosechaMexico)
        txtFaseLunarSiembra = findViewById(R.id.txtFaseLunarSiembra)
        txtFaseLunarCosecha = findViewById(R.id.txtFaseLunarCosecha)
        txtTiempoGerminacion = findViewById(R.id.txtTiempoGerminacion)
        txtCicloVida = findViewById(R.id.txtCicloVida)
        txtDensidadSiembra = findViewById(R.id.txtDensidadSiembra)

        // Plagas y control
        txtPlagas = findViewById(R.id.txtPlagas)
        txtControlBiologico = findViewById(R.id.txtControlBiologico)

        // Compatibilidad
        chipGroupCompatibles = findViewById(R.id.chipGroupCompatibles)
        chipGroupIncompatibles = findViewById(R.id.chipGroupIncompatibles)

        // Económicas
        txtCostoProduccion = findViewById(R.id.txtCostoProduccion)
        txtPrecioPromedio = findViewById(R.id.txtPrecioPromedio)
        txtRendimiento = findViewById(R.id.txtRendimiento)
        txtRentabilidad = findViewById(R.id.txtRentabilidad)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = ""
        }
    }

    private fun setupFab() {
        fabFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun loadPlantData() {
        coroutineScope.launch {
            try {
                val planta = withContext(Dispatchers.IO) {
                    databaseHelper.getPlantaById(plantId)
                }

                if (planta != null) {
                    currentPlanta = planta
                    displayPlantInfo(planta)
                    animateContentEntry()
                } else {
                    showError("No se encontró información de la planta")
                    finish()
                }
            } catch (e: Exception) {
                showError("Error al cargar los datos: ${e.message}")
                finish()
            }
        }
    }

    private fun displayPlantInfo(planta: ModelPlanta) {
        // Información básica
        displayBasicInfo(planta)

        // Configurar imágenes
        setupImageCarousel(planta.imageUrls)

        // Condiciones de cultivo
        displayGrowingConditions(planta)

        // Nutrición NPK
        displayNutritionInfo(planta)

        // Cronograma
        displayScheduleInfo(planta)

        // Manejo fitosanitario
        displayPestManagement(planta)

        // Compatibilidad
        displayCompatibility(planta)

        // Información económica
        displayEconomicInfo(planta)
    }

    private fun displayBasicInfo(planta: ModelPlanta) {
        txtNombrePrincipal.text = planta.nombre ?: "Nombre no disponible"
        txtNombreCientificoPrincipal.text = planta.nombreIngles ?: "Nombre científico no disponible"

        // Configurar chip de clasificación
        val clasificacion = planta.clasificacion ?: "Sin clasificar"
        chipClasificacion.text = clasificacion
        txtClasificacionDetalle.text = clasificacion

        // Configurar coloring toolbar con el nombre
        collapsingToolbar.title = planta.nombre ?: "Planta"

        // Altura promedio
        txtAlturaPromedio.text = if (planta.alturaPromedio > 0) {
            "${planta.alturaPromedio.toInt()} cm"
        } else {
            "No especificado"
        }

        // Tipo de raíz
        txtTipoRaiz.text = planta.tipoRaiz ?: "No especificado"
    }

    private fun setupImageCarousel(imageUrls: List<String>) {
        if (imageUrls.isNotEmpty()) {
            val adapter = PlantImageAdapter(imageUrls) { imageUrl ->
                // Click en imagen para ver en pantalla completa
                openImageFullScreen(imageUrl)
            }
            imageViewPager.adapter = adapter

            // Configurar indicadores si hay múltiples imágenes
            if (imageUrls.size > 1) {
                setupImageIndicators(imageUrls.size)
            }
        } else {
            // Usar imagen por defecto
            val defaultImageAdapter = PlantImageAdapter(
                listOf(getDefaultImageUrl())
            ) { }
            imageViewPager.adapter = defaultImageAdapter
        }
    }

    private fun setupImageIndicators(count: Int) {
        // Implementar indicadores de página si es necesario
    }

    private fun displayGrowingConditions(planta: ModelPlanta) {
        // Temperatura
        txtTemperatura.text = if (planta.temperatura > 0) {
            "${planta.temperatura.toInt()}°C"
        } else {
            "Variable"
        }

        // Humedad relativa
        txtHumedadRelativa.text = if (planta.humedadRelativa > 0) {
            "${planta.humedadRelativa.toInt()}%"
        } else {
            "Moderada"
        }

        // pH
        txtPh.text = if (planta.ph > 0) {
            planta.ph.toString()
        } else {
            "6.0-7.0"
        }

        // Horas de luz
        txtHorasLuz.text = if (planta.horasLuz > 0) {
            "${planta.horasLuz.toInt()}h"
        } else {
            "6-8h"
        }

        // Tipo de suelo
        txtTipoSuelo.text = planta.tipoSuelo ?: "Franco, bien drenado"
    }

    private fun displayNutritionInfo(planta: ModelPlanta) {
        // Configurar barras de progreso NPK
        val maxNutrient = 200.0 // Valor máximo para normalizar

        // Nitrógeno
        val nitrogenValue = planta.nitrogeno.takeIf { it > 0 } ?: 120.0
        progressNitrogeno.progress = ((nitrogenValue / maxNutrient) * 100).toInt()
        txtNitrogeno.text = nitrogenValue.toInt().toString()
        animateProgress(progressNitrogeno, 0)

        // Fósforo
        val fosforoValue = planta.fosforo.takeIf { it > 0 } ?: 80.0
        progressFosforo.progress = ((fosforoValue / maxNutrient) * 100).toInt()
        txtFosforo.text = fosforoValue.toInt().toString()
        animateProgress(progressFosforo, 200)

        // Potasio
        val potasioValue = planta.potasio.takeIf { it > 0 } ?: 100.0
        progressPotasio.progress = ((potasioValue / maxNutrient) * 100).toInt()
        txtPotasio.text = potasioValue.toInt().toString()
        animateProgress(progressPotasio, 400)
    }

    private fun displayScheduleInfo(planta: ModelPlanta) {
        // Siembra
        txtSiembraMexico.text = planta.siembraMexico ?: "Marzo - Mayo"
        txtFaseLunarSiembra.text = planta.faseLunarSiembra ?: "Luna nueva"

        // Cosecha
        txtCosechaMexico.text = planta.cosechaMexico ?: "Julio - Septiembre"
        txtFaseLunarCosecha.text = planta.faseLunarCosecha ?: "Luna llena"

        // Tiempos
        txtTiempoGerminacion.text = getGerminationTime(planta.nombre)
        txtCicloVida.text = planta.cicloVida ?: getDurationByPlant(planta.nombre)
        txtDensidadSiembra.text = planta.densidadSiembra ?: getSpacingByPlant(planta.nombre)
    }

    private fun displayPestManagement(planta: ModelPlanta) {
        txtPlagas.text = planta.plagas ?: "Áfidos, mosca blanca, trips"
        txtControlBiologico.text = planta.controlBiologico ?: "Mariquitas, crisopas, jabón potásico"
    }

    private fun displayCompatibility(planta: ModelPlanta) {
        // Limpiar chips existentes
        chipGroupCompatibles.removeAllViews()
        chipGroupIncompatibles.removeAllViews()

        // Cultivos compatibles
        val compatibles =
            planta.compatibilidadCultivos?.split(",") ?: getDefaultCompatibleCrops(planta.nombre)

        compatibles.forEach { cultivo ->
            val chip = createCompatibilityChip(cultivo.trim(), true)
            chipGroupCompatibles.addView(chip)
        }

        // Cultivos incompatibles
        val incompatibles =
            planta.incompatibles?.split(",") ?: getDefaultIncompatibleCrops(planta.nombre)

        incompatibles.forEach { cultivo ->
            val chip = createCompatibilityChip(cultivo.trim(), false)
            chipGroupIncompatibles.addView(chip)
        }
    }

    private fun displayEconomicInfo(planta: ModelPlanta) {
        txtCostoProduccion.text = planta.costoProduccion ?: "$2,500/ha"
        txtPrecioPromedio.text = planta.precioPromedio ?: "$45/kg"

        val rendimiento = if (planta.rendimiento > 0) {
            "${planta.rendimiento.toInt()} ton/ha"
        } else {
            "15 ton/ha"
        }
        txtRendimiento.text = rendimiento

        txtRentabilidad.text = planta.rentabilidad ?: "Alto potencial - ROI del 180%"
    }

    private fun createCompatibilityChip(text: String, isCompatible: Boolean): Chip {
        val chip =
            layoutInflater.inflate(R.layout.chip_compatibility, chipGroupCompatibles, false) as Chip
        chip.text = text

        if (isCompatible) {
            chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.status_good_bg)
            chip.setTextColor(ContextCompat.getColor(this, R.color.status_good))
        } else {
            chip.chipBackgroundColor =
                ContextCompat.getColorStateList(this, R.color.status_warning_bg)
            chip.setTextColor(ContextCompat.getColor(this, R.color.status_warning))
        }

        return chip
    }

    private fun animateProgress(progressBar: LinearProgressIndicator, delay: Long) {
        val currentProgress = progressBar.progress
        progressBar.progress = 0

        progressBar.postDelayed({
            val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, currentProgress)
            animator.duration = 1000
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.start()
        }, delay)
    }

    private fun animateContentEntry() {
        // Animar la entrada del contenido
        val views = listOf(
            txtNombrePrincipal, txtNombreCientificoPrincipal, chipClasificacion
        )

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 50f

            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(index * 100L)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite

        val iconRes = if (isFavorite) R.drawable.favorite_24 else R.drawable.favorite_border_24
        fabFavorite.setImageResource(iconRes)

        // Animar el FAB
        fabFavorite.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(150)
            .withEndAction {
                fabFavorite.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()

        // Guardar en base de datos o SharedPreferences
        saveFavoriteStatus(plantId, isFavorite)

        val message = if (isFavorite) "Agregado a favoritos" else "Removido de favoritos"
        showSuccess(message)
    }

    private fun openImageFullScreen(imageUrl: String) {
        // Implementar vista de imagen en pantalla completa
        // Intent hacia ImageViewerActivity por ejemplo
    }

    // Métodos de utilidad para datos por defecto
    private fun getGerminationTime(nombre: String?): String {
        return when (nombre?.lowercase()) {
            "tomate" -> "5-7 días"
            "habanero", "chile" -> "10-14 días"
            "cilantro" -> "7-10 días"
            "epazote" -> "5-8 días"
            "rábano", "rabano" -> "3-5 días"
            "albahaca" -> "5-10 días"
            "lechuga" -> "4-7 días"
            "zanahoria" -> "10-17 días"
            "cebolla" -> "7-14 días"
            else -> "7-14 días"
        }
    }

    private fun getDurationByPlant(nombre: String?): String {
        return when (nombre?.lowercase()) {
            "tomate" -> "75-85 días"
            "habanero", "chile" -> "90-120 días"
            "cilantro" -> "25-30 días"
            "epazote" -> "40-50 días"
            "rábano", "rabano" -> "25-30 días"
            "albahaca" -> "60-90 días"
            "lechuga" -> "45-65 días"
            "zanahoria" -> "70-80 días"
            "cebolla" -> "90-120 días"
            else -> "60-90 días"
        }
    }

    private fun getSpacingByPlant(nombre: String?): String {
        return when (nombre?.lowercase()) {
            "tomate" -> "40-50 cm"
            "habanero", "chile" -> "30-40 cm"
            "cilantro" -> "10-15 cm"
            "epazote" -> "20-25 cm"
            "rábano", "rabano" -> "5-10 cm"
            "albahaca" -> "20-30 cm"
            "lechuga" -> "15-20 cm"
            "zanahoria" -> "3-5 cm"
            "cebolla" -> "10-15 cm"
            else -> "15-20 cm"
        }
    }

    private fun getDefaultCompatibleCrops(nombre: String?): List<String> {
        return when (nombre?.lowercase()) {
            "tomate" -> listOf("Albahaca", "Perejil", "Caléndula", "Zanahoria")
            "chile", "habanero" -> listOf("Tomate", "Cebolla", "Orégano", "Perejil")
            "cilantro" -> listOf("Tomate", "Espinaca", "Rábano", "Zanahoria")
            "lechuga" -> listOf("Zanahoria", "Rábano", "Cebolla", "Ajo")
            else -> listOf("Albahaca", "Caléndula", "Perejil", "Zanahoria")
        }
    }

    private fun getDefaultIncompatibleCrops(nombre: String?): List<String> {
        return when (nombre?.lowercase()) {
            "tomate" -> listOf("Brócoli", "Hinojo", "Maíz")
            "chile", "habanero" -> listOf("Frijol", "Brócoli")
            "cilantro" -> listOf("Hinojo", "Eneldo")
            "lechuga" -> listOf("Apio", "Perejil")
            else -> listOf("Hinojo", "Nogal", "Eucalipto")
        }
    }

    private fun getDefaultImageUrl(): String {
        return currentPlanta?.nombre?.let { nombre ->
            when (nombre.lowercase()) {
                "tomate" -> "android.resource://$packageName/${R.drawable.tomate}"
                "habanero" -> "android.resource://$packageName/${R.drawable.habanero}"
                "cilantro" -> "android.resource://$packageName/${R.drawable.cilantro}"
                "epazote" -> "android.resource://$packageName/${R.drawable.epazote}"
                "rábano", "rabano" -> "android.resource://$packageName/${R.drawable.rabano}"
                "chayote" -> "android.resource://$packageName/${R.drawable.chayote}"
                else -> "android.resource://$packageName/${R.drawable.tomate}"
            }
        } ?: "android.resource://$packageName/${R.drawable.tomate}"
    }

    private fun saveFavoriteStatus(plantId: Long, isFavorite: Boolean) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                // Implementar guardado en base de datos o SharedPreferences
                val prefs = getSharedPreferences("plant_favorites", MODE_PRIVATE)
                prefs.edit().putBoolean("plant_$plantId", isFavorite).apply()
            }
        }
    }

    private fun loadFavoriteStatus(plantId: Long): Boolean {
        val prefs = getSharedPreferences("plant_favorites", MODE_PRIVATE)
        return prefs.getBoolean("plant_$plantId", false)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        databaseHelper.close()
    }

    // Adapter para el carrusel de imágenes
    private inner class PlantImageAdapter(
        private val imageUrls: List<String>,
        private val onImageClick: (String) -> Unit
    ) : RecyclerView.Adapter<PlantImageAdapter.ImageViewHolder>() {

        override fun onCreateViewHolder(
            parent: android.view.ViewGroup,
            viewType: Int
        ): ImageViewHolder {
            val imageView = ImageView(parent.context).apply {
                layoutParams = CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            return ImageViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val imageUrl = imageUrls[position]

            Glide.with(holder.imageView.context)
                .load(imageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.tomate)
                        .error(R.drawable.tomate)
                        .centerCrop()
                )
                .into(holder.imageView)

            holder.imageView.setOnClickListener {
                onImageClick(imageUrl)
            }
        }

        override fun getItemCount(): Int = imageUrls.size

        inner class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }
}