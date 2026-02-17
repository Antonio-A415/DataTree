package com.datatree.fragments.home;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.AsyncTask;

import com.datatree.R;
import com.datatree.activitys.informationplanta.PlantaActivity;
import com.datatree.core.services.DATABASEHELPER;
import com.datatree.databinding.FragmentHomeBinding;
import com.datatree.infraestructure.adapters.CultivosAdapter;
import com.datatree.infraestructure.dataclass.Itemsplants;
import com.datatree.infraestructure.dataclass.databasemodels.ModelPlanta;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private FragmentHomeBinding binding;
    private DATABASEHELPER databaseHelper;
    private CultivosAdapter adapter;
    private List<Itemsplants> displayList;
    private List<ModelPlanta> plantasFromDB;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        DATABASEHELPER dbHelper = new DATABASEHELPER(requireContext());
        SQLiteDatabase db = dbHelper.openDatabaseForInspector();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Inicializar base de datos
        databaseHelper = new DATABASEHELPER(requireContext());
        plantasFromDB = new ArrayList<>();
        displayList = new ArrayList<>();

        mViewModel = new HomeViewModel();

        setupRecyclerView();
        loadPlantsFromDatabase();
        listenersIcons();
        listenerClickImageDone();
    }

    private void setupRecyclerView() {
        adapter = new CultivosAdapter(new ArrayList<>(displayList), requireContext(),
                new Function1<Itemsplants, Unit>() {
                    @Override
                    public Unit invoke(Itemsplants itemsplants) {
                        // Pasar el ID de la planta real a PlantaActivity
                        Intent intentInfoPlanta = new Intent(getContext(), PlantaActivity.class);
                        if (itemsplants.getPlantId() > 0) {
                            intentInfoPlanta.putExtra("plant_id", itemsplants.getPlantId());
                        }
                        startActivity(intentInfoPlanta);
                        return null;
                    }
                }, new Function2<Itemsplants, Integer, Unit>() {
            @Override
            public Unit invoke(Itemsplants itemsplants, Integer integer) {
                if (itemsplants.getUrlImagenes() != null && !itemsplants.getUrlImagenes().isEmpty()) {
                    ImageBottomSheet sheet = new ImageBottomSheet(itemsplants.getUrlImagenes(), integer);
                    sheet.show(getParentFragmentManager(), "ImageSheet");
                }
                return null;
            }
        });

        binding.recc.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recc.setAdapter(adapter);
    }

    private void loadPlantsFromDatabase() {
        // Usar AsyncTask para cargar datos en background
        new LoadPlantsTask().execute();
    }

    private class LoadPlantsTask extends AsyncTask<Void, Void, List<ModelPlanta>> {
        @Override
        protected List<ModelPlanta> doInBackground(Void... voids) {
            return databaseHelper.getAllRecords();
        }

        @Override
        protected void onPostExecute(List<ModelPlanta> plantas) {
            super.onPostExecute(plantas);
            plantasFromDB = plantas;

            if (plantas.isEmpty()) {
                // Si no hay datos, insertar datos de ejemplo
                insertSampleData();
            } else {
                // Convertir y mostrar datos
                convertAndDisplayPlants(plantas);
            }
        }
    }

    private void insertSampleData() {
        new InsertSampleDataTask().execute();
    }

    private class InsertSampleDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            // ====== TOMATE ======
            ModelPlanta tomate = new ModelPlanta("Tomate", "Solanum lycopersicum", "Hortaliza");
            List<String> tomateImages = new ArrayList<>();
            tomateImages.add("https://drive.google.com/file/d/1Xddnt1lRoVW6Z6JJsRNiPkcEp3wKT3Y3/view?usp=drive_link");
            tomateImages.add("https://drive.google.com/file/d/1tNrdbJugQHpW5WK6q8qalD4h4_HFnDi0/view?usp=drive_link");
            tomateImages.add("https://drive.google.com/file/d/1ysKuMDdKm49jfqcGTdAsnH09nrCqwkmf/view?usp=drive_link");
            tomate.setImageUrls(tomateImages);

            // Datos agronómicos
            tomate.setFertilidad("Alta");
            tomate.setFotoperiodo("Día neutro");
            tomate.setTemperatura(25.0);
            tomate.setPrecipitacion(600.0);
            tomate.setPh(6.2);
            tomate.setHorasLuz(8.0);
            tomate.setIntensidadLuz(70.0);
            tomate.setHumedadRelativa(60.0);
            tomate.setNitrogeno(150);
            tomate.setFosforo(50);
            tomate.setPotasio(200);
            tomate.setRendimiento(80); // ton/ha aprox
            tomate.setCategoriaPh("Ligeramente ácido");
            tomate.setTipoSuelo("Franco");
            tomate.setTemporada("Primavera-Verano");
            tomate.setRelacionN(3);
            tomate.setRelacionP(1);
            tomate.setRelacionK(4);
            tomate.setFenologia("Germinación, crecimiento vegetativo, floración, fructificación");
            tomate.setPlagas("Mosca blanca, trips, áfidos");
            tomate.setControlBiologico("Uso de parasitoides, extractos vegetales");
            tomate.setAlturaPromedio(1.5);
            tomate.setTipoRaiz("Pivotante con raíces secundarias");
            tomate.setCicloVida("Anual");
            tomate.setVelocidadCrecimiento("Rápida");
            tomate.setDensidadSiembra("20,000 – 25,000 plantas/ha");
            tomate.setHumedadSuelo("Media");
            tomate.setCo2Nivel("400 ppm");
            tomate.setFe("Alto");
            tomate.setZn("Medio");
            tomate.setMn("Medio");
            tomate.setCu("Bajo");
            tomate.setB("Medio");
            tomate.setMo("Bajo");
            tomate.setCapacidadRetencionAgua("Media");
            tomate.setTexturaSuelo("Franco-arenoso a franco-arcilloso");
            tomate.setPeriodoCriticoPlagas("Floración y fructificación");
            tomate.setCostoProduccion("$80,000 por ha aprox.");
            tomate.setPrecioPromedio("$15 – $25/kg");
            tomate.setRentabilidad("Alta");
            tomate.setUsoFinal("Consumo fresco, salsas, industria");
            tomate.setOrigenGenetico("Mesoamérica");
            tomate.setTipoMultiplicacion("Semilla");
            tomate.setCompatibilidadCultivos("Cebolla, zanahoria, albahaca");
            tomate.setIncompatibles("Papa");
            tomate.setRequerimientosPoda("Sí, poda apical y deshoje");
            tomate.setSiembraMexico("Febrero-Marzo");
            tomate.setCosechaMexico("Junio-Julio");
            tomate.setFaseLunarSiembra("Luna creciente");
            tomate.setFaseLunarPoda("Luna menguante");
            tomate.setFaseLunarTrasplante("Luna creciente");
            tomate.setFaseLunarCosecha("Luna llena");
            tomate.setCalendarioLunar("Agrícola tradicional");
            tomate.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(tomate);


            // ====== SANDÍA ======
            ModelPlanta sandia = new ModelPlanta("Sandía", "Citrullus lanatus", "Frutal");
            List<String> sandiaImages = new ArrayList<>();
            sandiaImages.add("https://drive.google.com/file/d/13fTpu6PMymp5bIXdabovKI22VLI5Lv2r/view?usp=drive_link");
            sandiaImages.add("https://drive.google.com/file/d/15XgYzd9nO2cEHs_aQCC8y0uV2dfLQSK5/view?usp=drive_link");
            sandiaImages.add("https://drive.google.com/file/d/1S1I0UfrlJEUestcNFryQYN7rlOgszxhZ/view?usp=drive_link");
            sandiaImages.add("https://drive.google.com/file/d/14UCZ1wKdQHlmq6Idom5ByWnS2P9rxKPK/view?usp=drive_link");
            sandiaImages.add("https://drive.google.com/file/d/13eqTG_jLD0zKDgT4BKE57PoK4RSXrXvh/view?usp=drive_link");
            sandia.setImageUrls(sandiaImages);

// Datos agronómicos
            sandia.setFertilidad("Media");
            sandia.setFotoperiodo("Día neutro");
            sandia.setTemperatura(28.0);
            sandia.setPrecipitacion(400.0);
            sandia.setPh(6.0);
            sandia.setHorasLuz(8.0);
            sandia.setIntensidadLuz(75.0);
            sandia.setHumedadRelativa(50.0);
            sandia.setNitrogeno(120);
            sandia.setFosforo(60);
            sandia.setPotasio(180);
            sandia.setRendimiento(35); // ton/ha aprox
            sandia.setCategoriaPh("Ligeramente ácido");
            sandia.setTipoSuelo("Franco-arenoso, bien drenado");
            sandia.setTemporada("Primavera-Verano");
            sandia.setRelacionN(2);
            sandia.setRelacionP(1);
            sandia.setRelacionK(3);
            sandia.setFenologia("Germinación, crecimiento vegetativo, floración, fructificación");
            sandia.setPlagas("Pulgones, mosca blanca, trips");
            sandia.setControlBiologico("Uso de extractos vegetales, trampas cromáticas");
            sandia.setAlturaPromedio(0.5);
            sandia.setTipoRaiz("Pivotante con raíces secundarias");
            sandia.setCicloVida("Anual");
            sandia.setVelocidadCrecimiento("Rápida");
            sandia.setDensidadSiembra("3,000 – 5,000 plantas/ha");
            sandia.setHumedadSuelo("Media");
            sandia.setCo2Nivel("400 ppm");
            sandia.setFe("Medio");
            sandia.setZn("Medio");
            sandia.setMn("Bajo");
            sandia.setCu("Bajo");
            sandia.setB("Medio");
            sandia.setMo("Bajo");
            sandia.setCapacidadRetencionAgua("Baja");
            sandia.setTexturaSuelo("Arenoso-franco");
            sandia.setPeriodoCriticoPlagas("Floración y fructificación");
            sandia.setCostoProduccion("$60,000 por ha aprox.");
            sandia.setPrecioPromedio("$8 – $12/kg");
            sandia.setRentabilidad("Media-Alta");
            sandia.setUsoFinal("Consumo fresco, jugos");
            sandia.setOrigenGenetico("África");
            sandia.setTipoMultiplicacion("Semilla");
            sandia.setCompatibilidadCultivos("Maíz, girasol, cebolla");
            sandia.setIncompatibles("Pepino, calabaza");
            sandia.setRequerimientosPoda("No, solo guías de control");
            sandia.setSiembraMexico("Febrero-Marzo");
            sandia.setCosechaMexico("Junio-Julio");
            sandia.setFaseLunarSiembra("Luna creciente");
            sandia.setFaseLunarPoda("Luna menguante");
            sandia.setFaseLunarTrasplante("Luna creciente");
            sandia.setFaseLunarCosecha("Luna llena");
            sandia.setCalendarioLunar("Agrícola tradicional");
            sandia.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(sandia);


            // ====== UVA ======
            ModelPlanta uva = new ModelPlanta("Uva", "Vitis vinifera", "Fruta");
            List<String> uvaImages = new ArrayList<>();
            uvaImages.add("https://drive.google.com/uc?export=download&id=1A4_m-WfyfDxz3kUVvFuMW42yMJ-w79qD");
            uvaImages.add("https://drive.google.com/uc?export=download&id=1dpIUn0s2iuBej-8xdrm3O_QFnSrQidZj");
            uvaImages.add("https://drive.google.com/uc?export=download&id=1kCn4SWP2jfgu1M4kPjXtN-BffD2KuQVU");
            uvaImages.add("https://drive.google.com/uc?export=download&id=1DBqjSSijhOdXE5xU4Nqi8iuPqd_P2DDH");
            uvaImages.add("https://drive.google.com/uc?export=download&id=172PpF3BMADpCkM4u7RMFzqLvPCHys7r1");
            uva.setImageUrls(uvaImages);

// Datos agronómicos
            uva.setFertilidad("Media");
            uva.setFotoperiodo("Día largo");
            uva.setTemperatura(22.0);
            uva.setPrecipitacion(500.0);
            uva.setPh(6.5);
            uva.setHorasLuz(10.0);
            uva.setIntensidadLuz(65.0);
            uva.setHumedadRelativa(55.0);
            uva.setNitrogeno(100);
            uva.setFosforo(40);
            uva.setPotasio(120);
            uva.setRendimiento(15); // ton/ha aprox
            uva.setCategoriaPh("Neutro");
            uva.setTipoSuelo("Franco-limoso con buen drenaje");
            uva.setTemporada("Primavera-Verano");
            uva.setRelacionN(2.5);
            uva.setRelacionP(1);
            uva.setRelacionK(3);
            uva.setFenologia("Germinación, crecimiento vegetativo, floración, cuajado, maduración");
            uva.setPlagas("Polilla de la vid, araña roja, oídio");
            uva.setControlBiologico("Trampeo, control biológico con hongos entomopatógenos");
            uva.setAlturaPromedio(2.0);
            uva.setTipoRaiz("Fibrosa con raíces adventicias");
            uva.setCicloVida("Perenne (en vides comerciales)");
            uva.setVelocidadCrecimiento("Moderada");
            uva.setDensidadSiembra("3,000 – 4,000 plantas/ha (en parrones)");
            uva.setHumedadSuelo("Media");
            uva.setCo2Nivel("400 ppm");
            uva.setFe("Medio");
            uva.setZn("Medio");
            uva.setMn("Bajo");
            uva.setCu("Bajo");
            uva.setB("Medio");
            uva.setMo("Bajo");
            uva.setCapacidadRetencionAgua("Media");
            uva.setTexturaSuelo("Franco-limoso");
            uva.setPeriodoCriticoPlagas("Floración y maduración");
            uva.setCostoProduccion("$90,000 por ha aprox.");
            uva.setPrecioPromedio("$20 – $35/kg");
            uva.setRentabilidad("Alta");
            uva.setUsoFinal("Uvas de mesa, pasas, vino");
            uva.setOrigenGenetico("África / Mediterráneo");
            uva.setTipoMultiplicacion("Esqueje o semilla (según variedad)");
            uva.setCompatibilidadCultivos("Cereales ligeros, trébol");
            uva.setIncompatibles("Girasol, maíz en competencia directa");
            uva.setRequerimientosPoda("Poda de fructificación y formación");
            uva.setSiembraMexico("Febrero – Marzo");
            uva.setCosechaMexico("Agosto – Octubre");
            uva.setFaseLunarSiembra("Luna creciente");
            uva.setFaseLunarPoda("Luna menguante");
            uva.setFaseLunarTrasplante("Luna creciente");
            uva.setFaseLunarCosecha("Luna llena");
            uva.setCalendarioLunar("Tradicional vitícola");
            uva.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(uva);


            // ====== FRESA ======
            ModelPlanta fresa = new ModelPlanta("Fresa", "Fragaria × ananassa", "Fruta");
            List<String> fresaImages = new ArrayList<>();
            fresaImages.add("https://drive.google.com/uc?export=download&id=1_iz83rzdTZXKtsxoHH2yCbOqfmtT3uXz");
            fresaImages.add("https://drive.google.com/uc?export=download&id=1L1QTjU5vJUwdsviLGyzuN4LaVrNueQe4");
            fresaImages.add("https://drive.google.com/uc?export=download&id=1lb66079X2dmjSRi2ouqPVO0oIqfJFmS4");
            fresaImages.add("https://drive.google.com/uc?export=download&id=121M0NVl_IIf8VHIVldhG917vZDiWRMm5");
            fresaImages.add("https://drive.google.com/uc?export=download&id=12lOX2eWlbgskPxygbM3gABOiZ6BYjGDY");
            fresa.setImageUrls(fresaImages);

// Datos agronómicos
            fresa.setFertilidad("Media-Alta");
            fresa.setFotoperiodo("Día neutro a días cortos");
            fresa.setTemperatura(20.0);
            fresa.setPrecipitacion(700.0);
            fresa.setPh(5.5);
            fresa.setHorasLuz(10.0);
            fresa.setIntensidadLuz(60.0);
            fresa.setHumedadRelativa(65.0);
            fresa.setNitrogeno(120);
            fresa.setFosforo(60);
            fresa.setPotasio(140);
            fresa.setRendimiento(20); // ton/ha aprox
            fresa.setCategoriaPh("Ácido ligero");
            fresa.setTipoSuelo("Franco con buen drenaje");
            fresa.setTemporada("Primavera – Otoño");
            fresa.setRelacionN(2);
            fresa.setRelacionP(1);
            fresa.setRelacionK(3);
            fresa.setFenologia("Germinación, formación vegetativa, floración, fructificación");
            fresa.setPlagas("Araña roja, trips, pudrición de raíz");
            fresa.setControlBiologico("Hongos benéficos, trampas, manejo de suelo");
            fresa.setAlturaPromedio(0.2);
            fresa.setTipoRaiz("Fibrosa superficial");
            fresa.setCicloVida("Perene (en muchos sistemas comerciales)");
            fresa.setVelocidadCrecimiento("Moderada");
            fresa.setDensidadSiembra("30,000 – 40,000 plantas/ha");
            fresa.setHumedadSuelo("Media alta");
            fresa.setCo2Nivel("400 ppm");
            fresa.setFe("Medio");
            fresa.setZn("Medio");
            fresa.setMn("Medio");
            fresa.setCu("Bajo");
            fresa.setB("Medio");
            fresa.setMo("Bajo");
            fresa.setCapacidadRetencionAgua("Media");
            fresa.setTexturaSuelo("Franco-arenoso a franco-limoso");
            fresa.setPeriodoCriticoPlagas("Floración y fructificación");
            fresa.setCostoProduccion("$70,000 por ha aprox.");
            fresa.setPrecioPromedio("$25 – $40/kg");
            fresa.setRentabilidad("Alta");
            fresa.setUsoFinal("Fruta fresca, productos procesados, postres");
            fresa.setOrigenGenetico("Europa / América del Norte (mejoras modernas)");
            fresa.setTipoMultiplicacion("Estolones / esquejes");
            fresa.setCompatibilidadCultivos("Lechuga, espinaca, cebolla");
            fresa.setIncompatibles("Tomate (compiten por nutrientes)");
            fresa.setRequerimientosPoda("Eliminación de hojas viejas, deshoje leve");
            fresa.setSiembraMexico("Febrero – Marzo");
            fresa.setCosechaMexico("Abril – Junio, y otoños según región");
            fresa.setFaseLunarSiembra("Luna creciente");
            fresa.setFaseLunarPoda("Luna menguante");
            fresa.setFaseLunarTrasplante("Luna creciente");
            fresa.setFaseLunarCosecha("Luna llena");
            fresa.setCalendarioLunar("Tradicional agrícola");
            fresa.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(fresa);

            // ====== RÚCULA ======
            ModelPlanta rucula = new ModelPlanta("Rúcula", "Eruca sativa", "Hortaliza");
            List<String> ruculaImages = new ArrayList<>();
            ruculaImages.add("https://drive.google.com/uc?export=download&id=18Ce-XLGasIxeqFnaVE2qny4GcBdDsPv");
            ruculaImages.add("https://drive.google.com/uc?export=download&id=1O2EbkM4-Xe-qthZmWeHXDy5y_yf26g3p");
            ruculaImages.add("https://drive.google.com/uc?export=download&id=11vI-IZnn5Fel3bJOvcVHE5pWlnmS-bOA");
            ruculaImages.add("https://drive.google.com/uc?export=download&id=1koq1prv5mkG7k_smwpKEUJRRfU9_GFaq");
            ruculaImages.add("https://drive.google.com/uc?export=download&id=14nwx-qI7cysxTwL2CD3FnjgVw33uOOy3");
            rucula.setImageUrls(ruculaImages);

// Datos agronómicos
            rucula.setFertilidad("Media");
            rucula.setFotoperiodo("Día neutro");
            rucula.setTemperatura(20.0);
            rucula.setPrecipitacion(600.0);
            rucula.setPh(6.0);
            rucula.setHorasLuz(10.0);
            rucula.setIntensidadLuz(65.0);
            rucula.setHumedadRelativa(60.0);
            rucula.setNitrogeno(100);
            rucula.setFosforo(50);
            rucula.setPotasio(150);
            rucula.setRendimiento(15); // ton/ha aprox
            rucula.setCategoriaPh("Ligeramente ácido");
            rucula.setTipoSuelo("Franco-limoso con buen drenaje");
            rucula.setTemporada("Primavera – Otoño");
            rucula.setRelacionN(2);
            rucula.setRelacionP(1);
            rucula.setRelacionK(3);
            rucula.setFenologia("Germinación, crecimiento vegetativo, floración, fructificación");
            rucula.setPlagas("Orugas, pulgones, nematodos");
            rucula.setControlBiologico("Uso de insectos benéficos, trampas amarillas");
            rucula.setAlturaPromedio(0.3);
            rucula.setTipoRaiz("Pivotante con raíces secundarias");
            rucula.setCicloVida("Anual");
            rucula.setVelocidadCrecimiento("Rápida");
            rucula.setDensidadSiembra("20,000 – 25,000 plantas/ha");
            rucula.setHumedadSuelo("Media");
            rucula.setCo2Nivel("400 ppm");
            rucula.setFe("Medio");
            rucula.setZn("Medio");
            rucula.setMn("Medio");
            rucula.setCu("Bajo");
            rucula.setB("Medio");
            rucula.setMo("Bajo");
            rucula.setCapacidadRetencionAgua("Media");
            rucula.setTexturaSuelo("Franco-limoso");
            rucula.setPeriodoCriticoPlagas("Floración y fructificación");
            rucula.setCostoProduccion("$60,000 por ha aprox.");
            rucula.setPrecioPromedio("$30 – $50/kg");
            rucula.setRentabilidad("Alta");
            rucula.setUsoFinal("Ensaladas, salsas, guarniciones");
            rucula.setOrigenGenetico("Mediterráneo");
            rucula.setTipoMultiplicacion("Semilla");
            rucula.setCompatibilidadCultivos("Tomate, zanahoria, cebolla");
            rucula.setIncompatibles("Brócoli, coliflor");
            rucula.setRequerimientosPoda("Eliminación de hojas viejas y flores");
            rucula.setSiembraMexico("Marzo – Junio");
            rucula.setCosechaMexico("Mayo – Agosto");
            rucula.setFaseLunarSiembra("Luna creciente");
            rucula.setFaseLunarPoda("Luna menguante");
            rucula.setFaseLunarTrasplante("Luna creciente");
            rucula.setFaseLunarCosecha("Luna llena");
            rucula.setCalendarioLunar("Tradicional agrícola");
            rucula.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(rucula);


            // ====== BETABEL ======
            ModelPlanta betabel = new ModelPlanta("Betabel", "Beta vulgaris", "Hortaliza");
            List<String> betabelImages = new ArrayList<>();
            betabelImages.add("https://drive.google.com/uc?export=download&id=1NTYedxa3dw59KZ-hwuRBezvYXJovi_ae");
            betabelImages.add("https://drive.google.com/uc?export=download&id=1ankBZUq6lyG6r_4zi7qpw1SpRwsODQH0");
            betabelImages.add("https://drive.google.com/uc?export=download&id=1NcMA4ahk6KoFxmkorILVFDCXWNBd9-SZ");
            betabelImages.add("https://drive.google.com/uc?export=download&id=1ItGsGxWwWXVHWRvBnRLORuywSfGlPS1P");
            betabelImages.add("https://drive.google.com/uc?export=download&id=1BOLGu_pwY2XUmTrNuM2p_ZiG7yGSF6KW");
            betabel.setImageUrls(betabelImages);

// Datos agronómicos
            betabel.setFertilidad("Media");
            betabel.setFotoperiodo("Día neutro");
            betabel.setTemperatura(18.0);
            betabel.setPrecipitacion(500.0);
            betabel.setPh(6.0);
            betabel.setHorasLuz(8.0);
            betabel.setIntensidadLuz(65.0);
            betabel.setHumedadRelativa(60.0);
            betabel.setNitrogeno(120);
            betabel.setFosforo(40);
            betabel.setPotasio(150);
            betabel.setRendimiento(40); // ton/ha aprox
            betabel.setCategoriaPh("Ligeramente ácido");
            betabel.setTipoSuelo("Franco-arenoso con buen drenaje");
            betabel.setTemporada("Primavera – Otoño");
            betabel.setRelacionN(3);
            betabel.setRelacionP(1);
            betabel.setRelacionK(3);
            betabel.setFenologia("Germinación, crecimiento vegetativo, engrosamiento de raíz");
            betabel.setPlagas("Pulgones, mosca blanca, nematodos");
            betabel.setControlBiologico("Insectos benéficos, rotación de cultivos");
            betabel.setAlturaPromedio(0.5);
            betabel.setTipoRaiz("Principal pivotante con raíces laterales");
            betabel.setCicloVida("Anual");
            betabel.setVelocidadCrecimiento("Moderada");
            betabel.setDensidadSiembra("50,000 – 60,000 plantas/ha");
            betabel.setHumedadSuelo("Media");
            betabel.setCo2Nivel("400 ppm");
            betabel.setFe("Medio");
            betabel.setZn("Medio");
            betabel.setMn("Medio");
            betabel.setCu("Bajo");
            betabel.setB("Medio");
            betabel.setMo("Bajo");
            betabel.setCapacidadRetencionAgua("Media");
            betabel.setTexturaSuelo("Franco-arenoso a franco-limoso");
            betabel.setPeriodoCriticoPlagas("Engrosamiento de raíz");
            betabel.setCostoProduccion("$50,000 por ha aprox.");
            betabel.setPrecioPromedio("$12 – $25/kg");
            betabel.setRentabilidad("Alta");
            betabel.setUsoFinal("Consumo fresco, jugos, ensaladas");
            betabel.setOrigenGenetico("Mediterráneo");
            betabel.setTipoMultiplicacion("Semilla");
            betabel.setCompatibilidadCultivos("Zanahoria, lechuga, cebolla");
            betabel.setIncompatibles("Papa");
            betabel.setRequerimientosPoda("No requiere poda significativa");
            betabel.setSiembraMexico("Febrero – Mayo");
            betabel.setCosechaMexico("Mayo – Octubre");
            betabel.setFaseLunarSiembra("Luna creciente");
            betabel.setFaseLunarPoda("Luna menguante");
            betabel.setFaseLunarTrasplante("Luna creciente");
            betabel.setFaseLunarCosecha("Luna llena");
            betabel.setCalendarioLunar("Tradicional agrícola");
            betabel.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(betabel);


            // ====== ACELGA ======
            ModelPlanta acelga = new ModelPlanta("Acelga", "Beta vulgaris var. cicla", "Hortaliza");
            List<String> acelgaImages = new ArrayList<>();
            acelgaImages.add("https://drive.google.com/uc?export=download&id=1H29WoDiJZyilVxyCj5mTVf-MflJUL5e8");
            acelgaImages.add("https://drive.google.com/uc?export=download&id=1K8ndrBQbDpnNdgGTqVYdBKWnjtbKSIYf");
            acelgaImages.add("https://drive.google.com/uc?export=download&id=1w4x7sSCShU4amAw8ft_f9PSfAEND_iun");
            acelgaImages.add("https://drive.google.com/uc?export=download&id=15eItvduNGsMUGTDBOO12IxqwIwQaeqGs");
            acelgaImages.add("https://drive.google.com/uc?export=download&id=1J19-LezNvM2ksfRwmYs0L3TNFiXakl9f");
            acelga.setImageUrls(acelgaImages);

// Datos agronómicos
            acelga.setFertilidad("Media");
            acelga.setFotoperiodo("Día neutro");
            acelga.setTemperatura(18.0);
            acelga.setPrecipitacion(600.0);
            acelga.setPh(6.0);
            acelga.setHorasLuz(8.0);
            acelga.setIntensidadLuz(70.0);
            acelga.setHumedadRelativa(65.0);
            acelga.setNitrogeno(120);
            acelga.setFosforo(50);
            acelga.setPotasio(150);
            acelga.setRendimiento(35); // ton/ha aprox
            acelga.setCategoriaPh("Ligeramente ácido");
            acelga.setTipoSuelo("Franco-arenoso, bien drenado");
            acelga.setTemporada("Primavera – Otoño");
            acelga.setRelacionN(3);
            acelga.setRelacionP(1);
            acelga.setRelacionK(3);
            acelga.setFenologia("Germinación, crecimiento vegetativo, engrosamiento de tallos");
            acelga.setPlagas("Pulgones, mosca blanca, nematodos");
            acelga.setControlBiologico("Uso de insectos benéficos, rotación de cultivos");
            acelga.setAlturaPromedio(0.4);
            acelga.setTipoRaiz("Principal pivotante con raíces laterales");
            acelga.setCicloVida("Anual");
            acelga.setVelocidadCrecimiento("Rápida");
            acelga.setDensidadSiembra("30,000 – 40,000 plantas/ha");
            acelga.setHumedadSuelo("Media");
            acelga.setCo2Nivel("400 ppm");
            acelga.setFe("Medio");
            acelga.setZn("Medio");
            acelga.setMn("Medio");
            acelga.setCu("Bajo");
            acelga.setB("Medio");
            acelga.setMo("Bajo");
            acelga.setCapacidadRetencionAgua("Alta");
            acelga.setTexturaSuelo("Franco-arenoso a franco-limoso");
            acelga.setPeriodoCriticoPlagas("Floración y engrosamiento de tallos");
            acelga.setCostoProduccion("$45,000 por ha aprox.");
            acelga.setPrecioPromedio("$10 – $18/kg");
            acelga.setRentabilidad("Alta");
            acelga.setUsoFinal("Consumo fresco, sopas, guisos");
            acelga.setOrigenGenetico("Mediterráneo");
            acelga.setTipoMultiplicacion("Semilla");
            acelga.setCompatibilidadCultivos("Zanahoria, cebolla, pimiento");
            acelga.setIncompatibles("Papa");
            acelga.setRequerimientosPoda("Poda ligera para eliminar hojas viejas");
            acelga.setSiembraMexico("Febrero – Mayo");
            acelga.setCosechaMexico("Mayo – Octubre");
            acelga.setFaseLunarSiembra("Luna creciente");
            acelga.setFaseLunarPoda("Luna menguante");
            acelga.setFaseLunarTrasplante("Luna creciente");
            acelga.setFaseLunarCosecha("Luna llena");
            acelga.setCalendarioLunar("Agrícola tradicional");
            acelga.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(acelga);


            // ====== BERRO ======
            ModelPlanta berro = new ModelPlanta("Berro", "Nasturtium officinale", "Hortaliza acuática");
            List<String> berroImages = new ArrayList<>();
            berroImages.add("https://drive.google.com/uc?export=download&id=1yauw-D1FuNWj9aU54fSbph0nvQB-v6cN");
            berroImages.add("https://drive.google.com/uc?export=download&id=1omyPSdbQ3Y4suSnnqhjqVZuF9ovznVLm");
            berroImages.add("https://drive.google.com/uc?export=download&id=17XT6Nj0sq-Ti08CcT4nXnaX-k5GtV-Q6");
            berroImages.add("https://drive.google.com/uc?export=download&id=1OnfCT8_Hx4hvboatYSgJWLePX6Pgu5lI");
            berroImages.add("https://drive.google.com/uc?export=download&id=1VDoDfUzqsuaecdmyA2fEhdsiKu-TB5Cg");
            berro.setImageUrls(berroImages);

// Datos agronómicos
            berro.setFertilidad("Alta");
            berro.setFotoperiodo("Día neutro");
            berro.setTemperatura(18.0);
            berro.setPrecipitacion(800.0);
            berro.setPh(6.5);
            berro.setHorasLuz(8.0);
            berro.setIntensidadLuz(70.0);
            berro.setHumedadRelativa(80.0);
            berro.setNitrogeno(150);
            berro.setFosforo(50);
            berro.setPotasio(100);
            berro.setRendimiento(25); // ton/ha aprox
            berro.setCategoriaPh("Ligeramente ácido");
            berro.setTipoSuelo("Acuático, bien drenado");
            berro.setTemporada("Primavera – Otoño");
            berro.setRelacionN(3);
            berro.setRelacionP(1);
            berro.setRelacionK(2);
            berro.setFenologia("Germinación, crecimiento vegetativo, desarrollo de hojas");
            berro.setPlagas("Pulgones, mosca blanca, nematodos");
            berro.setControlBiologico("Uso de insectos benéficos, rotación de cultivos");
            berro.setAlturaPromedio(0.3);
            berro.setTipoRaiz("Fibrosa, adaptada a ambientes húmedos");
            berro.setCicloVida("Anual");
            berro.setVelocidadCrecimiento("Rápida");
            berro.setDensidadSiembra("200,000 – 300,000 plantas/ha");
            berro.setHumedadSuelo("Alta");
            berro.setCo2Nivel("400 ppm");
            berro.setFe("Medio");
            berro.setZn("Medio");
            berro.setMn("Medio");
            berro.setCu("Bajo");
            berro.setB("Medio");
            berro.setMo("Bajo");
            berro.setCapacidadRetencionAgua("Alta");
            berro.setTexturaSuelo("Arenoso-limoso");
            berro.setPeriodoCriticoPlagas("Germinación y crecimiento inicial");
            berro.setCostoProduccion("$35,000 por ha aprox.");
            berro.setPrecioPromedio("$12 – $18/kg");
            berro.setRentabilidad("Alta");
            berro.setUsoFinal("Consumo fresco, ensaladas, sopas");
            berro.setOrigenGenetico("Europa y Asia Central");
            berro.setTipoMultiplicacion("Semilla");
            berro.setCompatibilidadCultivos("Lechuga, espinaca, cebollín");
            berro.setIncompatibles("Ninguno conocido");
            berro.setRequerimientosPoda("Poda ligera para eliminar hojas viejas");
            berro.setSiembraMexico("Enero – Marzo");
            berro.setCosechaMexico("Mayo – Octubre");
            berro.setFaseLunarSiembra("Luna creciente");
            berro.setFaseLunarPoda("Luna menguante");
            berro.setFaseLunarTrasplante("Luna creciente");
            berro.setFaseLunarCosecha("Luna llena");
            berro.setCalendarioLunar("Agrícola tradicional");
            berro.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(berro);

            // ====== ENDIBIA ======
            ModelPlanta endibia = new ModelPlanta("Endibia", "Cichorium intybus var. foliosum", "Hortaliza bianual");
            List<String> endibiaImages = new ArrayList<>();
            endibiaImages.add("https://drive.google.com/uc?export=download&id=1-Wx1OrJtQmWg8_qVcfaPedFvqOMpb2K9");
            endibiaImages.add("https://drive.google.com/uc?export=download&id=1u4TBTWpfnBfqee69zbp-EEg39t1EppWG");
            endibiaImages.add("https://drive.google.com/uc?export=download&id=1HF6tGCkjvy3EGse9YlM5a-CfLH7dagwn");
            endibiaImages.add("https://drive.google.com/uc?export=download&id=1-q3iQoUDKmIcYW9JOrQW7WK62sjVJcyv");
            endibiaImages.add("https://drive.google.com/uc?export=download&id=1WPDNwHCepJdjcgTXOaVlpztOb1UCu3Tk");
            endibia.setImageUrls(endibiaImages);

// Datos agronómicos
            endibia.setFertilidad("Alta");
            endibia.setFotoperiodo("Día neutro");
            endibia.setTemperatura(20.0);
            endibia.setPrecipitacion(800.0);
            endibia.setPh(7.0);
            endibia.setHorasLuz(8.0);
            endibia.setIntensidadLuz(70.0);
            endibia.setHumedadRelativa(70.0);
            endibia.setNitrogeno(150);
            endibia.setFosforo(50);
            endibia.setPotasio(250);
            endibia.setRendimiento(30); // ton/ha aprox
            endibia.setCategoriaPh("Neutro");
            endibia.setTipoSuelo("Bien drenado, ligeramente ácido");
            endibia.setTemporada("Otoño – Invierno");
            endibia.setRelacionN(3);
            endibia.setRelacionP(1);
            endibia.setRelacionK(5);
            endibia.setFenologia("Germinación, crecimiento vegetativo, forzado");
            endibia.setPlagas("Pulgones, mosca de la achicoria, gusanos de raíz");
            endibia.setControlBiologico("Uso de insectos benéficos, rotación de cultivos");
            endibia.setAlturaPromedio(0.4);
            endibia.setTipoRaiz("Pivotante, gruesa");
            endibia.setCicloVida("Bienal");
            endibia.setVelocidadCrecimiento("Media");
            endibia.setDensidadSiembra("25 – 30 plantas/m²");
            endibia.setHumedadSuelo("Alta");
            endibia.setCo2Nivel("400 ppm");
            endibia.setFe("Medio");
            endibia.setZn("Medio");
            endibia.setMn("Medio");
            endibia.setCu("Bajo");
            endibia.setB("Medio");
            endibia.setMo("Bajo");
            endibia.setCapacidadRetencionAgua("Alta");
            endibia.setTexturaSuelo("Arenoso-limoso");
            endibia.setPeriodoCriticoPlagas("Germinación y crecimiento inicial");
            endibia.setCostoProduccion("$40,000 por ha aprox.");
            endibia.setPrecioPromedio("$25 – $35/kg");
            endibia.setRentabilidad("Alta");
            endibia.setUsoFinal("Consumo fresco, ensaladas");
            endibia.setOrigenGenetico("Bélgica");
            endibia.setTipoMultiplicacion("Semilla");
            endibia.setCompatibilidadCultivos("Lechuga, espinaca, cebollín");
            endibia.setIncompatibles("Ninguno conocido");
            endibia.setRequerimientosPoda("Poda ligera para eliminar hojas viejas");
            endibia.setSiembraMexico("Mayo – Junio");
            endibia.setCosechaMexico("Noviembre – Enero");
            endibia.setFaseLunarSiembra("Luna creciente");
            endibia.setFaseLunarPoda("Luna menguante");
            endibia.setFaseLunarTrasplante("Luna creciente");
            endibia.setFaseLunarCosecha("Luna llena");
            endibia.setCalendarioLunar("Agrícola tradicional");
            endibia.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(endibia);

            // ====== COL RIZADA ======
            ModelPlanta colRizada = new ModelPlanta("Col rizada", "Brassica oleracea var. acephala", "Hortaliza bianual");
            List<String> colRizadaImages = new ArrayList<>();
            colRizadaImages.add("https://drive.google.com/uc?export=download&id=1v8KuaWrtS1-t48J7d7OQXLlL9sYQIK7r");
            colRizadaImages.add("https://drive.google.com/uc?export=download&id=1dydKDqbtIzOeH6b4eCJxfXugqJTOcmbM");
            colRizadaImages.add("https://drive.google.com/uc?export=download&id=1XJDggVSWl8X9vI4ZClpyZJ35bOmUIrSF");
            colRizadaImages.add("https://drive.google.com/uc?export=download&id=1E0tJ7Zii9U8Z3wtc8vi6jM8VJHuDvK-e");
            colRizadaImages.add("https://drive.google.com/uc?export=download&id=1Q9N7xEo_PvkTTAAuB_9fU8M3KBl-1grZ");
            colRizada.setImageUrls(colRizadaImages);

// Datos agronómicos
            colRizada.setFertilidad("Alta");
            colRizada.setFotoperiodo("Día neutro");
            colRizada.setTemperatura(15.0);
            colRizada.setPrecipitacion(700.0);
            colRizada.setPh(6.5);
            colRizada.setHorasLuz(8.0);
            colRizada.setIntensidadLuz(70.0);
            colRizada.setHumedadRelativa(70.0);
            colRizada.setNitrogeno(150);
            colRizada.setFosforo(50);
            colRizada.setPotasio(250);
            colRizada.setRendimiento(40); // ton/ha aprox
            colRizada.setCategoriaPh("Ligeramente ácido");
            colRizada.setTipoSuelo("Franco-limoso");
            colRizada.setTemporada("Otoño – Invierno");
            colRizada.setRelacionN(3);
            colRizada.setRelacionP(1);
            colRizada.setRelacionK(5);
            colRizada.setFenologia("Germinación, crecimiento vegetativo, floración");
            colRizada.setPlagas("Pulgones, orugas, mosca de la col");
            colRizada.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            colRizada.setAlturaPromedio(0.5);
            colRizada.setTipoRaiz("Pivotante");
            colRizada.setCicloVida("Bienal");
            colRizada.setVelocidadCrecimiento("Media");
            colRizada.setDensidadSiembra("25 – 30 plantas/m²");
            colRizada.setHumedadSuelo("Alta");
            colRizada.setCo2Nivel("400 ppm");
            colRizada.setFe("Medio");
            colRizada.setZn("Medio");
            colRizada.setMn("Medio");
            colRizada.setCu("Bajo");
            colRizada.setB("Medio");
            colRizada.setMo("Bajo");
            colRizada.setCapacidadRetencionAgua("Alta");
            colRizada.setTexturaSuelo("Franco-limoso");
            colRizada.setPeriodoCriticoPlagas("Germinación y crecimiento inicial");
            colRizada.setCostoProduccion("$35,000 por ha aprox.");
            colRizada.setPrecioPromedio("$20 – $30/kg");
            colRizada.setRentabilidad("Alta");
            colRizada.setUsoFinal("Consumo fresco, ensaladas, sopas");
            colRizada.setOrigenGenetico("Mediterráneo");
            colRizada.setTipoMultiplicacion("Semilla");
            colRizada.setCompatibilidadCultivos("Acelga, cebolla, zanahoria");
            colRizada.setIncompatibles("Ninguno conocido");
            colRizada.setRequerimientosPoda("Poda ligera para eliminar hojas viejas");
            colRizada.setSiembraMexico("Marzo – Abril");
            colRizada.setCosechaMexico("Agosto – Octubre");
            colRizada.setFaseLunarSiembra("Luna creciente");
            colRizada.setFaseLunarPoda("Luna menguante");
            colRizada.setFaseLunarTrasplante("Luna creciente");
            colRizada.setFaseLunarCosecha("Luna llena");
            colRizada.setCalendarioLunar("Agrícola tradicional");
            colRizada.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(colRizada);

            // ====== LECHUGA ======
            ModelPlanta lechuga = new ModelPlanta("Lechuga", "Lactuca sativa", "Hortaliza anual");
            List<String> lechugaImages = new ArrayList<>();
            lechugaImages.add("https://drive.google.com/uc?export=download&id=1Moqh4j2AMVWXILwF7UE2YxsEJ2tuGJhk");
            lechugaImages.add("https://drive.google.com/uc?export=download&id=12BsbZgWeEZ27HLJEHolDz_8G1XUQRTL_");
            lechugaImages.add("https://drive.google.com/uc?export=download&id=1Co4jHVnPi2X9CiKDWd9xQ_Y0lAGrLXgk");
            lechugaImages.add("https://drive.google.com/uc?export=download&id=1wLG6GvejDtZTXXP-xZn4m35Rl8Cnsb7l");
            lechugaImages.add("https://drive.google.com/uc?export=download&id=1Ndc8I89wlhbv8luivhQaETF2RnY2WNQu");
            lechuga.setImageUrls(lechugaImages);

// Datos agronómicos
            lechuga.setFertilidad("Alta");
            lechuga.setFotoperiodo("Día neutro");
            lechuga.setTemperatura(18.0);
            lechuga.setPrecipitacion(800.0);
            lechuga.setPh(6.5);
            lechuga.setHorasLuz(8.0);
            lechuga.setIntensidadLuz(70.0);
            lechuga.setHumedadRelativa(70.0);
            lechuga.setNitrogeno(150);
            lechuga.setFosforo(50);
            lechuga.setPotasio(200);
            lechuga.setRendimiento(25); // ton/ha aprox
            lechuga.setCategoriaPh("Ligeramente ácido");
            lechuga.setTipoSuelo("Arenoso-limoso, bien drenado");
            lechuga.setTemporada("Otoño – Invierno");
            lechuga.setRelacionN(3);
            lechuga.setRelacionP(1);
            lechuga.setRelacionK(4);
            lechuga.setFenologia("Germinación, crecimiento vegetativo, floración");
            lechuga.setPlagas("Pulgones, orugas, mosca de la col");
            lechuga.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            lechuga.setAlturaPromedio(0.3);
            lechuga.setTipoRaiz("Fibrosa");
            lechuga.setCicloVida("Anual");
            lechuga.setVelocidadCrecimiento("Rápida");
            lechuga.setDensidadSiembra("30 – 40 plantas/m²");
            lechuga.setHumedadSuelo("Alta");
            lechuga.setCo2Nivel("400 ppm");
            lechuga.setFe("Medio");
            lechuga.setZn("Medio");
            lechuga.setMn("Medio");
            lechuga.setCu("Bajo");
            lechuga.setB("Medio");
            lechuga.setMo("Bajo");
            lechuga.setCapacidadRetencionAgua("Media");
            lechuga.setTexturaSuelo("Arenoso-limoso");
            lechuga.setPeriodoCriticoPlagas("Germinación y crecimiento inicial");
            lechuga.setCostoProduccion("$30,000 por ha aprox.");
            lechuga.setPrecioPromedio("$20 – $30/kg");
            lechuga.setRentabilidad("Alta");
            lechuga.setUsoFinal("Consumo fresco, ensaladas");
            lechuga.setOrigenGenetico("Mediterráneo");
            lechuga.setTipoMultiplicacion("Semilla");
            lechuga.setCompatibilidadCultivos("Acelga, cebolla, zanahoria");
            lechuga.setIncompatibles("Ninguno conocido");
            lechuga.setRequerimientosPoda("Poda ligera para eliminar hojas viejas");
            lechuga.setSiembraMexico("Enero – Marzo");
            lechuga.setCosechaMexico("Mayo – Julio");
            lechuga.setFaseLunarSiembra("Luna creciente");
            lechuga.setFaseLunarPoda("Luna menguante");
            lechuga.setFaseLunarTrasplante("Luna creciente");
            lechuga.setFaseLunarCosecha("Luna llena");
            lechuga.setCalendarioLunar("Agrícola tradicional");
            lechuga.setSensibilidadFasesLunares("Alta");

            databaseHelper.insertarRegistro(lechuga);

            // ====== ACHICORIA ======
            ModelPlanta achicoria = new ModelPlanta("Achicoria", "Cichorium intybus", "Hortaliza bianual");
            List<String> achicoriaImages = new ArrayList<>();
            achicoriaImages.add("https://drive.google.com/uc?export=download&id=1TCktbr9RMEgY6-JLsMu_M3lJToOQ9roh");
            achicoriaImages.add("https://drive.google.com/uc?export=download&id=1o4dg-6ver-rOK4j_q0NHRzOxHtiWgkcn");
            achicoriaImages.add("https://drive.google.com/uc?export=download&id=13MWFS0CduDS2mp-li-HPPqQ2yGCKJEGB");
            achicoriaImages.add("https://drive.google.com/uc?export=download&id=1Cz8vnc6KLHtaprexhVaymm9G95LtEnTr");
            achicoriaImages.add("https://drive.google.com/uc?export=download&id=1-uX9Xjv_KGtivC7tPxXJNcaF2g5rbDA0");
            achicoria.setImageUrls(achicoriaImages);

// Datos agronómicos
            achicoria.setFertilidad("Alta");
            achicoria.setFotoperiodo("Día neutro");
            achicoria.setTemperatura(18.0);
            achicoria.setPrecipitacion(800.0);
            achicoria.setPh(6.5);
            achicoria.setHorasLuz(8.0);
            achicoria.setIntensidadLuz(70.0);
            achicoria.setHumedadRelativa(70.0);
            achicoria.setNitrogeno(150);
            achicoria.setFosforo(50);
            achicoria.setPotasio(200);
            achicoria.setRendimiento(25); // ton/ha aprox
            achicoria.setCategoriaPh("Ligeramente ácido");
            achicoria.setTipoSuelo("Arenoso-limoso, bien drenado");
            achicoria.setTemporada("Otoño – Invierno");
            achicoria.setRelacionN(3);
            achicoria.setRelacionP(1);
            achicoria.setRelacionK(4);
            achicoria.setFenologia("Germinación, crecimiento vegetativo, floración");
            achicoria.setPlagas("Pulgones, orugas, mosca de la col");
            achicoria.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            achicoria.setAlturaPromedio(0.3);
            achicoria.setTipoRaiz("Pivotante");
            achicoria.setCicloVida("Bianual");
            achicoria.setVelocidadCrecimiento("Moderada");
            achicoria.setDensidadSiembra("30 – 40 plantas/m²");
            achicoria.setHumedadSuelo("Alta");
            achicoria.setCo2Nivel("400 ppm");
            achicoria.setFe("Medio");
            achicoria.setZn("Medio");
            achicoria.setMn("Medio");
            achicoria.setCu("Bajo");
            achicoria.setB("Medio");
            achicoria.setMo("Bajo");
            achicoria.setCapacidadRetencionAgua("Media");
            achicoria.setTexturaSuelo("Arenoso-limoso");
            achicoria.setPeriodoCriticoPlagas("Germinación y crecimiento inicial");
            achicoria.setCostoProduccion("$30,000 por ha aprox.");
            achicoria.setPrecioPromedio("$20 – $30/kg");
            achicoria.setRentabilidad("Alta");
            achicoria.setUsoFinal("Consumo fresco, ensaladas");
            achicoria.setOrigenGenetico("Mediterráneo");
            achicoria.setTipoMultiplicacion("Semilla");
            achicoria.setCompatibilidadCultivos("Acelga, cebolla, zanahoria");
            achicoria.setIncompatibles("Ninguno conocido");
            achicoria.setRequerimientosPoda("Poda ligera para eliminar hojas viejas");
            achicoria.setSiembraMexico("Enero – Marzo");
            achicoria.setCosechaMexico("Mayo – Julio");
            achicoria.setFaseLunarSiembra("Luna creciente");
            achicoria.setFaseLunarPoda("Luna menguante");
            achicoria.setFaseLunarTrasplante("Luna creciente");
            achicoria.setFaseLunarCosecha("Luna llena");
            achicoria.setCalendarioLunar("Agrícola tradicional");
            achicoria.setSensibilidadFasesLunares("Alta");

            databaseHelper.insertarRegistro(achicoria);


            // ====== ESPINACA ======
            ModelPlanta espinaca = new ModelPlanta("Espinaca", "Spinacia oleracea", "Hortaliza anual");
            List<String> espinacaImages = new ArrayList<>();
            espinacaImages.add("https://drive.google.com/uc?export=download&id=1sc0Z9fv_DLlOKm3-2v4Mc7k0KNYd7w5u");
            espinacaImages.add("https://drive.google.com/uc?export=download&id=159QVG6rgN5oucV5uMUIxdnA1mTon3l5O");
            espinacaImages.add("https://drive.google.com/uc?export=download&id=1blDKbjuet7RMM5_cUgyjy83yHhiIw9Mq");
            espinacaImages.add("https://drive.google.com/uc?export=download&id=1s8Gkaw1dNiJV4XgSjZxtm2b-GOHwVCZq");
            espinacaImages.add("https://drive.google.com/uc?export=download&id=1tnywJO26OguQ_6OvfKMQQt9Jl7owyGRA");
            espinaca.setImageUrls(espinacaImages);

// Datos agronómicos
            espinaca.setFertilidad("Alta");
            espinaca.setFotoperiodo("Día neutro");
            espinaca.setTemperatura(15.0);
            espinaca.setPrecipitacion(600.0);
            espinaca.setPh(6.5);
            espinaca.setHorasLuz(10.0);
            espinaca.setIntensidadLuz(60.0);
            espinaca.setHumedadRelativa(80.0);
            espinaca.setNitrogeno(100);
            espinaca.setFosforo(50);
            espinaca.setPotasio(150);
            espinaca.setRendimiento(20); // ton/ha aprox
            espinaca.setCategoriaPh("Ligeramente ácido");
            espinaca.setTipoSuelo("Arenoso-limoso, bien drenado");
            espinaca.setTemporada("Otoño – Primavera");
            espinaca.setRelacionN(4);
            espinaca.setRelacionP(1);
            espinaca.setRelacionK(3);
            espinaca.setFenologia("Germinación, crecimiento vegetativo, floración");
            espinaca.setPlagas("Pulgones, orugas, mosca de la col");
            espinaca.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            espinaca.setAlturaPromedio(0.3);
            espinaca.setTipoRaiz("Pivotante");
            espinaca.setCicloVida("Anual");
            espinaca.setVelocidadCrecimiento("Rápida");
            espinaca.setDensidadSiembra("50 – 60 plantas/m²");
            espinaca.setHumedadSuelo("Alta");
            espinaca.setCo2Nivel("400 ppm");
            espinaca.setFe("Medio");
            espinaca.setZn("Medio");
            espinaca.setMn("Medio");
            espinaca.setCu("Bajo");
            espinaca.setB("Medio");
            espinaca.setMo("Bajo");
            espinaca.setCapacidadRetencionAgua("Alta");
            espinaca.setTexturaSuelo("Arenoso-limoso");
            espinaca.setPeriodoCriticoPlagas("Germinación y crecimiento inicial");
            espinaca.setCostoProduccion("$25,000 por ha aprox.");
            espinaca.setPrecioPromedio("$15 – $25/kg");
            espinaca.setRentabilidad("Alta");
            espinaca.setUsoFinal("Consumo fresco, ensaladas");
            espinaca.setOrigenGenetico("Asia");
            espinaca.setTipoMultiplicacion("Semilla");
            espinaca.setCompatibilidadCultivos("Acelga, cebolla, zanahoria");
            espinaca.setIncompatibles("Ninguno conocido");
            espinaca.setRequerimientosPoda("Poda ligera para eliminar hojas viejas");
            espinaca.setSiembraMexico("Septiembre – Octubre");
            espinaca.setCosechaMexico("Diciembre – Febrero");
            espinaca.setFaseLunarSiembra("Luna creciente");
            espinaca.setFaseLunarPoda("Luna menguante");
            espinaca.setFaseLunarTrasplante("Luna creciente");
            espinaca.setFaseLunarCosecha("Luna llena");
            espinaca.setCalendarioLunar("Agrícola tradicional");
            espinaca.setSensibilidadFasesLunares("Alta");

            databaseHelper.insertarRegistro(espinaca);

            // ====== BERENJENA ======
            ModelPlanta berenjena = new ModelPlanta("Berenjena", "Solanum melongena", "Hortaliza perenne cultivada como anual");
            List<String> berenjenaImages = new ArrayList<>();
            berenjenaImages.add("https://drive.google.com/uc?export=download&id=1F8awYiTPdyX7FBcodniQUSrGKlj7rVcB");
            berenjenaImages.add("https://drive.google.com/uc?export=download&id=1MBvQtnl8QJ9598OouFw9AQksGEV7YpGy");
            berenjenaImages.add("https://drive.google.com/uc?export=download&id=1Y5RiwIbo_jNnjZMpA467uTfmaFAD4sJq");
            berenjenaImages.add("https://drive.google.com/uc?export=download&id=15im2XAgz0CxK4BIUfO6hRFemuV2ZNpRl");
            berenjenaImages.add("https://drive.google.com/uc?export=download&id=1woCaKrHWj0ISh4s8MRHGz5bf-ql5bZlZ");
            berenjena.setImageUrls(berenjenaImages);

// Datos agronómicos
            berenjena.setFertilidad("Alta");
            berenjena.setFotoperiodo("Neutro");
            berenjena.setTemperatura(23.0);
            berenjena.setPrecipitacion(800.0);
            berenjena.setPh(6.5);
            berenjena.setHorasLuz(10.0);
            berenjena.setIntensidadLuz(70.0);
            berenjena.setHumedadRelativa(60.0);
            berenjena.setNitrogeno(160);
            berenjena.setFosforo(90);
            berenjena.setPotasio(200);
            berenjena.setRendimiento(25); // ton/ha aprox
            berenjena.setCategoriaPh("Ligeramente ácido");
            berenjena.setTipoSuelo("Franco arcilloso, bien drenado");
            berenjena.setTemporada("Primavera – Verano");
            berenjena.setRelacionN(4);
            berenjena.setRelacionP(1);
            berenjena.setRelacionK(3);
            berenjena.setFenologia("Germinación, crecimiento vegetativo, floración, fructificación");
            berenjena.setPlagas("Pulgones, mosca blanca, escarabajo de la patata");
            berenjena.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            berenjena.setAlturaPromedio(0.6);
            berenjena.setTipoRaiz("Pivotante");
            berenjena.setCicloVida("Anual");
            berenjena.setVelocidadCrecimiento("Media");
            berenjena.setDensidadSiembra("40,000 – 50,000 plantas/ha");
            berenjena.setHumedadSuelo("Media");
            berenjena.setCo2Nivel("400 ppm");
            berenjena.setFe("Medio");
            berenjena.setZn("Medio");
            berenjena.setMn("Medio");
            berenjena.setCu("Bajo");
            berenjena.setB("Medio");
            berenjena.setMo("Bajo");
            berenjena.setCapacidadRetencionAgua("Media");
            berenjena.setTexturaSuelo("Franco arcilloso");
            berenjena.setPeriodoCriticoPlagas("Floración y fructificación");
            berenjena.setCostoProduccion("$30,000 por ha aprox.");
            berenjena.setPrecioPromedio("$20 – $35/kg");
            berenjena.setRentabilidad("Alta");
            berenjena.setUsoFinal("Consumo fresco, guisos, conservas");
            berenjena.setOrigenGenetico("Asia");
            berenjena.setTipoMultiplicacion("Semilla");
            berenjena.setCompatibilidadCultivos("Tomate, pimiento, albahaca");
            berenjena.setIncompatibles("Patata, tabaco");
            berenjena.setRequerimientosPoda("Poda ligera para eliminar brotes laterales");
            berenjena.setSiembraMexico("Febrero – Marzo");
            berenjena.setCosechaMexico("Junio – Agosto");
            berenjena.setFaseLunarSiembra("Luna creciente");
            berenjena.setFaseLunarPoda("Luna menguante");
            berenjena.setFaseLunarTrasplante("Luna creciente");
            berenjena.setFaseLunarCosecha("Luna llena");
            berenjena.setCalendarioLunar("Agrícola tradicional");
            berenjena.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(berenjena);

            // ====== ESPÁRRAGOS ======
            ModelPlanta esparrago = new ModelPlanta("Espárragos", "Asparagus officinalis", "Hortaliza perenne cultivada como anual");
            List<String> esparragoImages = new ArrayList<>();
            esparragoImages.add("https://drive.google.com/uc?export=download&id=1FtdoYe4Pr_FC1WVBKlfRKJ6xCQu0mTYI");
            esparragoImages.add("https://drive.google.com/uc?export=download&id=1ZYrPHhCIOX7ufL3ZxYY9StCYcsrW-9rI");
            esparragoImages.add("https://drive.google.com/uc?export=download&id=17K0Eg1xs_rlbaMi9yDHenPtBbhK1FNBx");
            esparragoImages.add("https://drive.google.com/uc?export=download&id=1CD6drLE4sFlmtzxajZ2R9Dm80knN0QvO");
            esparragoImages.add("https://drive.google.com/uc?export=download&id=1lHujRxRMFjOxYJwEeW2d8ka555JA6INn");
            esparrago.setImageUrls(esparragoImages);

// Datos agronómicos
            esparrago.setFertilidad("Alta");
            esparrago.setFotoperiodo("Neutro");
            esparrago.setTemperatura(18.0);
            esparrago.setPrecipitacion(500.0);
            esparrago.setPh(6.5);
            esparrago.setHorasLuz(12.0);
            esparrago.setIntensidadLuz(80.0);
            esparrago.setHumedadRelativa(60.0);
            esparrago.setNitrogeno(100);
            esparrago.setFosforo(50);
            esparrago.setPotasio(150);
            esparrago.setRendimiento(15); // ton/ha aprox
            esparrago.setCategoriaPh("Ligeramente ácido");
            esparrago.setTipoSuelo("Franco arenoso, bien drenado");
            esparrago.setTemporada("Primavera – Verano");
            esparrago.setRelacionN(3);
            esparrago.setRelacionP(1);
            esparrago.setRelacionK(2);
            esparrago.setFenologia("Germinación, crecimiento vegetativo, floración, fructificación");
            esparrago.setPlagas("Pulgones, escarabajo del espárrago, roya");
            esparrago.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            esparrago.setAlturaPromedio(0.5);
            esparrago.setTipoRaiz("Cilíndrica, profunda");
            esparrago.setCicloVida("Perenne");
            esparrago.setVelocidadCrecimiento("Lenta");
            esparrago.setDensidadSiembra("50,000 – 60,000 plantas/ha");
            esparrago.setHumedadSuelo("Media");
            esparrago.setCo2Nivel("400 ppm");
            esparrago.setFe("Medio");
            esparrago.setZn("Medio");
            esparrago.setMn("Medio");
            esparrago.setCu("Bajo");
            esparrago.setB("Medio");
            esparrago.setMo("Bajo");
            esparrago.setCapacidadRetencionAgua("Alta");
            esparrago.setTexturaSuelo("Franco arenoso");
            esparrago.setPeriodoCriticoPlagas("Floración y fructificación");
            esparrago.setCostoProduccion("$40,000 por ha aprox.");
            esparrago.setPrecioPromedio("$50 – $80/kg");
            esparrago.setRentabilidad("Alta");
            esparrago.setUsoFinal("Consumo fresco, conservas, guisos");
            esparrago.setOrigenGenetico("Europa y Asia");
            esparrago.setTipoMultiplicacion("Cebas");
            esparrago.setCompatibilidadCultivos("Tomate, zanahoria, cebollín");
            esparrago.setIncompatibles("Ajo, cebolla");
            esparrago.setRequerimientosPoda("Poda de brotes laterales y eliminación de esparragos viejos");
            esparrago.setSiembraMexico("Febrero – Marzo");
            esparrago.setCosechaMexico("Abril – Junio");
            esparrago.setFaseLunarSiembra("Luna creciente");
            esparrago.setFaseLunarPoda("Luna menguante");
            esparrago.setFaseLunarTrasplante("Luna creciente");
            esparrago.setFaseLunarCosecha("Luna llena");
            esparrago.setCalendarioLunar("Agrícola tradicional");
            esparrago.setSensibilidadFasesLunares("Media");

            databaseHelper.insertarRegistro(esparrago);


            // ====== CHILE (Capsicum annuum) ======
            ModelPlanta chile = new ModelPlanta("Chile", "Capsicum annuum", "Hortaliza anual de clima cálido");
            List<String> chileImages = new ArrayList<>();
            chileImages.add("https://drive.google.com/uc?export=download&id=13-b76BGO5vWLaZQGp-T5auV2k2fKXyoL");
            chileImages.add("https://drive.google.com/uc?export=download&id=1Ge4BJxhICWwFzpYdhNxtWdvEWVAtEaqL");
            chileImages.add("https://drive.google.com/uc?export=download&id=1SrUxeMLEZngHej6g7sTmso1XATJyDw5y");
            chileImages.add("https://drive.google.com/uc?export=download&id=1c44WG4jotxh8i-3-5H3C0__Dky1V9aWr");
            chileImages.add("https://drive.google.com/uc?export=download&id=1uJs9TtBLQ_OXN5x2u8rXulYsjFMUHYeh");
            chile.setImageUrls(chileImages);

// Datos agronómicos
            chile.setFertilidad("Media");
            chile.setFotoperiodo("Neutro");
            chile.setTemperatura(24.0);
            chile.setPrecipitacion(600.0);
            chile.setPh(6.0);
            chile.setHorasLuz(12.0);
            chile.setIntensidadLuz(80.0);
            chile.setHumedadRelativa(60.0);
            chile.setNitrogeno(100);
            chile.setFosforo(50);
            chile.setPotasio(150);
            chile.setRendimiento(25); // ton/ha aprox
            chile.setCategoriaPh("Ligeramente ácido");
            chile.setTipoSuelo("Franco arenoso, bien drenado");
            chile.setTemporada("Primavera – Verano");
            chile.setRelacionN(3);
            chile.setRelacionP(1);
            chile.setRelacionK(2);
            chile.setFenologia("Germinación, crecimiento vegetativo, floración, fructificación");
            chile.setPlagas("Pulgones, mosca blanca, trips");
            chile.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            chile.setAlturaPromedio(0.6);
            chile.setTipoRaiz("Cilíndrica, profunda");
            chile.setCicloVida("Anual");
            chile.setVelocidadCrecimiento("Rápida");
            chile.setDensidadSiembra("50,000 – 60,000 plantas/ha");
            chile.setHumedadSuelo("Media");
            chile.setCo2Nivel("400 ppm");
            chile.setFe("Medio");
            chile.setZn("Medio");
            chile.setMn("Medio");
            chile.setCu("Bajo");
            chile.setB("Medio");
            chile.setMo("Bajo");
            chile.setCapacidadRetencionAgua("Media");
            chile.setTexturaSuelo("Franco arenoso");
            chile.setPeriodoCriticoPlagas("Floración y fructificación");
            chile.setCostoProduccion("$35,000 por ha aprox.");
            chile.setPrecioPromedio("$40 – $70/kg");
            chile.setRentabilidad("Alta");
            chile.setUsoFinal("Consumo fresco, conservas, salsas");
            chile.setOrigenGenetico("América Central y México");
            chile.setTipoMultiplicacion("Semilla");
            chile.setCompatibilidadCultivos("Tomate, zanahoria, cebollín");
            chile.setIncompatibles("Ajo, cebolla");
            chile.setRequerimientosPoda("Poda de brotes laterales y eliminación de frutos dañados");
            chile.setSiembraMexico("Febrero – Marzo");
            chile.setCosechaMexico("Junio – Agosto");
            chile.setFaseLunarSiembra("Luna creciente");
            chile.setFaseLunarPoda("Luna menguante");
            chile.setFaseLunarTrasplante("Luna creciente");
            chile.setFaseLunarCosecha("Luna llena");
            chile.setCalendarioLunar("Agrícola tradicional");
            chile.setSensibilidadFasesLunares("Alta");

            databaseHelper.insertarRegistro(chile);

            // ====== REPOLLO (Brassica oleracea var. capitata) ======
            ModelPlanta repollo = new ModelPlanta("Repollo", "Brassica oleracea var. capitata", "Hortaliza bienal cultivada como anual");
            List<String> repolloImages = new ArrayList<>();
            repolloImages.add("https://drive.google.com/uc?export=download&id=1158RYi5ThNrJj4-fINY8fweBsu_pF-vZ");
            repolloImages.add("https://drive.google.com/uc?export=download&id=1a406TOb8ZLW-n7amdGLWacxkvmPQhTw4");
            repolloImages.add("https://drive.google.com/uc?export=download&id=1dBkOnsqyiccXJNAbCQUVNhNI9epGfEVz");
            repolloImages.add("https://drive.google.com/uc?export=download&id=1SCcUWKQ8cLw7A27CE3fXC-wGkS3Mjkul");
            repolloImages.add("https://drive.google.com/uc?export=download&id=19Doc1sWkD1_hGDtdrJmaBpAzGSbKZE3A");
            repollo.setImageUrls(repolloImages);

// Datos agronómicos
            repollo.setFertilidad("Alta");
            repollo.setFotoperiodo("Neutro");
            repollo.setTemperatura(15.0);
            repollo.setPrecipitacion(600.0);
            repollo.setPh(6.0);
            repollo.setHorasLuz(12.0);
            repollo.setIntensidadLuz(70.0);
            repollo.setHumedadRelativa(80.0);
            repollo.setNitrogeno(120);
            repollo.setFosforo(60);
            repollo.setPotasio(150);
            repollo.setRendimiento(30); // ton/ha aprox
            repollo.setCategoriaPh("Ligeramente ácido");
            repollo.setTipoSuelo("Franco arcilloso, bien drenado");
            repollo.setTemporada("Primavera – Otoño");
            repollo.setRelacionN(3);
            repollo.setRelacionP(1);
            repollo.setRelacionK(2);
            repollo.setFenologia("Germinación, crecimiento vegetativo, formación de cabeza, maduración");
            repollo.setPlagas("Pulgones, orugas, mosca de la col");
            repollo.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            repollo.setAlturaPromedio(0.5);
            repollo.setTipoRaiz("Fibrosa, superficial");
            repollo.setCicloVida("Bienal, cultivado como anual");
            repollo.setVelocidadCrecimiento("Moderada");
            repollo.setDensidadSiembra("40,000 – 50,000 plantas/ha");
            repollo.setHumedadSuelo("Media");
            repollo.setCo2Nivel("400 ppm");
            repollo.setFe("Medio");
            repollo.setZn("Medio");
            repollo.setMn("Medio");
            repollo.setCu("Bajo");
            repollo.setB("Medio");
            repollo.setMo("Bajo");
            repollo.setCapacidadRetencionAgua("Alta");
            repollo.setTexturaSuelo("Franco arcilloso");
            repollo.setPeriodoCriticoPlagas("Formación de cabeza");
            repollo.setCostoProduccion("$30,000 por ha aprox.");
            repollo.setPrecioPromedio("$20 – $40/kg");
            repollo.setRentabilidad("Moderada");
            repollo.setUsoFinal("Consumo fresco, cocido, ensaladas");
            repollo.setOrigenGenetico("Europa Occidental");
            repollo.setTipoMultiplicacion("Semilla");
            repollo.setCompatibilidadCultivos("Zanahoria, cebollín, lechuga");
            repollo.setIncompatibles("Fresas, tomates");
            repollo.setRequerimientosPoda("Eliminación de hojas externas dañadas");
            repollo.setSiembraMexico("Enero – Marzo");
            repollo.setCosechaMexico("Mayo – Julio");
            repollo.setFaseLunarSiembra("Luna creciente");
            repollo.setFaseLunarPoda("Luna menguante");
            repollo.setFaseLunarTrasplante("Luna creciente");
            repollo.setFaseLunarCosecha("Luna llena");
            repollo.setCalendarioLunar("Agrícola tradicional");
            repollo.setSensibilidadFasesLunares("Moderada");

            databaseHelper.insertarRegistro(repollo);


            // ====== PEPINO (Cucumis sativus) ======
            ModelPlanta pepino = new ModelPlanta("Pepino", "Cucumis sativus", "Hortaliza anual de la familia Cucurbitaceae");
            List<String> pepinoImages = new ArrayList<>();
            pepinoImages.add("https://drive.google.com/uc?export=download&id=1GTGPT08xc-l--Lvp4TlWtYIDkB4ljUE3");
            pepinoImages.add("https://drive.google.com/uc?export=download&id=1bTKrI-1rGtpvk3nb8L-Ni0UyrhKFIU9F");
            pepinoImages.add("https://drive.google.com/uc?export=download&id=1DeP4-OfLp9z6pVg_KWnAmcP0pfGNtSU3");
            pepinoImages.add("https://drive.google.com/uc?export=download&id=1Unnrp7y2fn43wi_N8sIZhcTgh9JCesLo");
            pepinoImages.add("https://drive.google.com/uc?export=download&id=1wAa7K9iPzSsZVZztupoxyToCuLlzQvWh");
            pepino.setImageUrls(pepinoImages);

// Datos agronómicos
            pepino.setFertilidad("Alta");
            pepino.setFotoperiodo("Días cortos");
            pepino.setTemperatura(21.0);
            pepino.setPrecipitacion(600.0);
            pepino.setPh(6.0);
            pepino.setHorasLuz(12.0);
            pepino.setIntensidadLuz(70.0);
            pepino.setHumedadRelativa(60.0);
            pepino.setNitrogeno(100);
            pepino.setFosforo(50);
            pepino.setPotasio(150);
            pepino.setRendimiento(35); // ton/ha aprox
            pepino.setCategoriaPh("Ligeramente ácido");
            pepino.setTipoSuelo("Franco arenoso, bien drenado");
            pepino.setTemporada("Primavera – Otoño");
            pepino.setRelacionN(3);
            pepino.setRelacionP(1);
            pepino.setRelacionK(2);
            pepino.setFenologia("Germinación, crecimiento vegetativo, formación de frutos, maduración");
            pepino.setPlagas("Mosca blanca, ácaros, pulgones");
            pepino.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            pepino.setAlturaPromedio(0.3);
            pepino.setTipoRaiz("Fibrosa, superficial");
            pepino.setCicloVida("Anual");
            pepino.setVelocidadCrecimiento("Rápida");
            pepino.setDensidadSiembra("50,000 – 60,000 plantas/ha");
            pepino.setHumedadSuelo("Alta");
            pepino.setCo2Nivel("400 ppm");
            pepino.setFe("Medio");
            pepino.setZn("Medio");
            pepino.setMn("Medio");
            pepino.setCu("Bajo");
            pepino.setB("Medio");
            pepino.setMo("Bajo");
            pepino.setCapacidadRetencionAgua("Alta");
            pepino.setTexturaSuelo("Franco arenoso");
            pepino.setPeriodoCriticoPlagas("Formación de frutos");
            pepino.setCostoProduccion("$25,000 por ha aprox.");
            pepino.setPrecioPromedio("$15 – $30/kg");
            pepino.setRentabilidad("Alta");
            pepino.setUsoFinal("Consumo fresco, encurtidos");
            pepino.setOrigenGenetico("India");
            pepino.setTipoMultiplicacion("Semilla");
            pepino.setCompatibilidadCultivos("Zanahoria, lechuga, cebollín");
            pepino.setIncompatibles("Melón, sandía, calabaza");
            pepino.setRequerimientosPoda("Eliminación de hojas secas y frutos dañados");
            pepino.setSiembraMexico("Febrero – Abril");
            pepino.setCosechaMexico("Mayo – Julio");
            pepino.setFaseLunarSiembra("Luna creciente");
            pepino.setFaseLunarPoda("Luna menguante");
            pepino.setFaseLunarTrasplante("Luna creciente");
            pepino.setFaseLunarCosecha("Luna llena");
            pepino.setCalendarioLunar("Agrícola tradicional");
            pepino.setSensibilidadFasesLunares("Moderada");

            databaseHelper.insertarRegistro(pepino);

            // ====== PAPA (Solanum tuberosum) ======
            ModelPlanta papa = new ModelPlanta("Papa", "Solanum tuberosum", "Hortaliza tuberosa de la familia Solanaceae");
            List<String> papaImages = new ArrayList<>();
            papaImages.add("https://drive.google.com/uc?export=download&id=1v0PgMOyYKownbqNEM_ZKuptsUcYQJ3no");
            papaImages.add("https://drive.google.com/uc?export=download&id=1E4FB5dp4MRIRP97Hm7ChLULSSrOIaWR6");
            papaImages.add("https://drive.google.com/uc?export=download&id=1v56LpvKMytUAcilUWvNGqkbXT2q1kjRf");
            papaImages.add("https://drive.google.com/uc?export=download&id=1O3DaK_ffWtRiQTgZ1AxnriTWjtnrcOfC");
            papaImages.add("https://drive.google.com/uc?export=download&id=1ynt5poutd_405eSrWUlAdN0MzJZ_XT07");
            papa.setImageUrls(papaImages);

// Datos agronómicos
            papa.setFertilidad("Alta");
            papa.setFotoperiodo("Neutro");
            papa.setTemperatura(15.0);
            papa.setPrecipitacion(600.0);
            papa.setPh(6.0);
            papa.setHorasLuz(12.0);
            papa.setIntensidadLuz(70.0);
            papa.setHumedadRelativa(80.0);
            papa.setNitrogeno(120);
            papa.setFosforo(60);
            papa.setPotasio(150);
            papa.setRendimiento(30); // ton/ha aprox
            papa.setCategoriaPh("Ligeramente ácido");
            papa.setTipoSuelo("Franco arcilloso, bien drenado");
            papa.setTemporada("Primavera – Otoño");
            papa.setRelacionN(3);
            papa.setRelacionP(1);
            papa.setRelacionK(2);
            papa.setFenologia("Germinación, crecimiento vegetativo, formación de tubérculos, maduración");
            papa.setPlagas("Pulgones, escarabajo de la papa, mosca de la papa");
            papa.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            papa.setAlturaPromedio(0.8);
            papa.setTipoRaiz("Tubérculo subterráneo");
            papa.setCicloVida("Anual");
            papa.setVelocidadCrecimiento("Rápida");
            papa.setDensidadSiembra("40,000 – 50,000 plantas/ha");
            papa.setHumedadSuelo("Alta");
            papa.setCo2Nivel("400 ppm");
            papa.setFe("Medio");
            papa.setZn("Medio");
            papa.setMn("Medio");
            papa.setCu("Bajo");
            papa.setB("Medio");
            papa.setMo("Bajo");
            papa.setCapacidadRetencionAgua("Alta");
            papa.setTexturaSuelo("Franco arcilloso");
            papa.setPeriodoCriticoPlagas("Formación de tubérculos");
            papa.setCostoProduccion("$25,000 por ha aprox.");
            papa.setPrecioPromedio("$15 – $30/kg");
            papa.setRentabilidad("Alta");
            papa.setUsoFinal("Consumo fresco, procesado, harinas");
            papa.setOrigenGenetico("Andes Peruanos");
            papa.setTipoMultiplicacion("Semilla de papa");
            papa.setCompatibilidadCultivos("Maíz, frijol, zanahoria");
            papa.setIncompatibles("Tomate, berenjena");
            papa.setRequerimientosPoda("Eliminación de brotes laterales y hojas secas");
            papa.setSiembraMexico("Febrero – Marzo");
            papa.setCosechaMexico("Junio – Julio");
            papa.setFaseLunarSiembra("Luna creciente");
            papa.setFaseLunarPoda("Luna menguante");
            papa.setFaseLunarTrasplante("Luna creciente");
            papa.setFaseLunarCosecha("Luna llena");
            papa.setCalendarioLunar("Agrícola tradicional");
            papa.setSensibilidadFasesLunares("Moderada");

            databaseHelper.insertarRegistro(papa);


            // ====== COLIFLOR (Brassica oleracea var. botrytis) ======
            ModelPlanta coliflor = new ModelPlanta("Coliflor", "Brassica oleracea var. botrytis", "Hortaliza anual de la familia Brassicaceae");
            List<String> coliflorImages = new ArrayList<>();
            coliflorImages.add("https://drive.google.com/uc?export=download&id=1Mn3p5jJ1Yk8-t7tsJowuBtU6bdNL5v4o");
            coliflorImages.add("https://drive.google.com/uc?export=download&id=149bshjo8rt89N3dvRp0qu_qYHiinL-VF");
            coliflorImages.add("https://drive.google.com/uc?export=download&id=1fMk3niDPd-mdjdHlKJsxDzP6nrgYkKAR");
            coliflorImages.add("https://drive.google.com/uc?export=download&id=1mJdvNY8w4IWxIvIAgLxqtveFffVGckOo");
            coliflorImages.add("https://drive.google.com/uc?export=download&id=1rAkdcekCHUBe6Q8PfllOBMQiDuWQOW7Q");
            coliflor.setImageUrls(coliflorImages);

// Datos agronómicos
            coliflor.setFertilidad("Alta");
            coliflor.setFotoperiodo("Neutro");
            coliflor.setTemperatura(21.0);
            coliflor.setPrecipitacion(600.0);
            coliflor.setPh(6.0);
            coliflor.setHorasLuz(12.0);
            coliflor.setIntensidadLuz(70.0);
            coliflor.setHumedadRelativa(80.0);
            coliflor.setNitrogeno(120);
            coliflor.setFosforo(60);
            coliflor.setPotasio(150);
            coliflor.setRendimiento(30); // ton/ha aprox
            coliflor.setCategoriaPh("Ligeramente ácido");
            coliflor.setTipoSuelo("Franco arcilloso, bien drenado");
            coliflor.setTemporada("Primavera – Otoño");
            coliflor.setRelacionN(3);
            coliflor.setRelacionP(1);
            coliflor.setRelacionK(2);
            coliflor.setFenologia("Germinación, crecimiento vegetativo, formación de pella, maduración");
            coliflor.setPlagas("Pulgones, mosca blanca, orugas");
            coliflor.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            coliflor.setAlturaPromedio(0.6);
            coliflor.setTipoRaiz("Fibrosa");
            coliflor.setCicloVida("Anual");
            coliflor.setVelocidadCrecimiento("Moderada");
            coliflor.setDensidadSiembra("40,000 – 50,000 plantas/ha");
            coliflor.setHumedadSuelo("Alta");
            coliflor.setCo2Nivel("400 ppm");
            coliflor.setFe("Medio");
            coliflor.setZn("Medio");
            coliflor.setMn("Medio");
            coliflor.setCu("Bajo");
            coliflor.setB("Medio");
            coliflor.setMo("Bajo");
            coliflor.setCapacidadRetencionAgua("Alta");
            coliflor.setTexturaSuelo("Franco arcilloso");
            coliflor.setPeriodoCriticoPlagas("Formación de pella");
            coliflor.setCostoProduccion("$30,000 por ha aprox.");
            coliflor.setPrecioPromedio("$20 – $40/kg");
            coliflor.setRentabilidad("Alta");
            coliflor.setUsoFinal("Consumo fresco, procesado");
            coliflor.setOrigenGenetico("Asia");
            coliflor.setTipoMultiplicacion("Semilla");
            coliflor.setCompatibilidadCultivos("Lechuga, zanahoria, cebollín");
            coliflor.setIncompatibles("Tomate, pimientos");
            coliflor.setRequerimientosPoda("Eliminación de hojas secas y brotes laterales");
            coliflor.setSiembraMexico("Septiembre – Octubre");
            coliflor.setCosechaMexico("Diciembre – Febrero");
            coliflor.setFaseLunarSiembra("Luna creciente");
            coliflor.setFaseLunarPoda("Luna menguante");
            coliflor.setFaseLunarTrasplante("Luna creciente");
            coliflor.setFaseLunarCosecha("Luna llena");
            coliflor.setCalendarioLunar("Agrícola tradicional");
            coliflor.setSensibilidadFasesLunares("Moderada");

            databaseHelper.insertarRegistro(coliflor);

            // ====== BRÓCOLI (Brassica oleracea var. italica) ======
            ModelPlanta brocoli = new ModelPlanta("Brócoli", "Brassica oleracea var. italica", "Hortaliza crucífera de clima fresco");
            List<String> brocoliImages = new ArrayList<>();
            brocoliImages.add("https://drive.google.com/uc?export=download&id=1xgG17JW17vR9YJC_t_7Ih65TJRDCtsZo");
            brocoliImages.add("https://drive.google.com/uc?export=download&id=1gNJWFDOXT4jwlHsE50_XlRq-DVkDUF0r");
            brocoliImages.add("https://drive.google.com/uc?export=download&id=1tSnceklG8Z-mv1hkGWFB9h-elykrX43r");
            brocoliImages.add("https://drive.google.com/uc?export=download&id=1aey5hXzZ47wvxSc-XSTPrt59kRFScUCY");
            brocoliImages.add("https://drive.google.com/uc?export=download&id=1nVwrKvniKESW3UjUhwWnPyJ3Q33I1M9-");
            brocoli.setImageUrls(brocoliImages);

// Datos agronómicos
            brocoli.setFertilidad("Alta");
            brocoli.setFotoperiodo("Neutro");
            brocoli.setTemperatura(18.0);
            brocoli.setPrecipitacion(600.0);
            brocoli.setPh(6.5);
            brocoli.setHorasLuz(12.0);
            brocoli.setIntensidadLuz(70.0);
            brocoli.setHumedadRelativa(80.0);
            brocoli.setNitrogeno(120);
            brocoli.setFosforo(60);
            brocoli.setPotasio(150);
            brocoli.setRendimiento(25); // ton/ha aprox
            brocoli.setCategoriaPh("Ligeramente ácido");
            brocoli.setTipoSuelo("Franco arcilloso, bien drenado");
            brocoli.setTemporada("Primavera – Otoño");
            brocoli.setRelacionN(3);
            brocoli.setRelacionP(1);
            brocoli.setRelacionK(2);
            brocoli.setFenologia("Germinación, crecimiento vegetativo, formación de cabeza, maduración");
            brocoli.setPlagas("Pulgones, mosca blanca, orugas");
            brocoli.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            brocoli.setAlturaPromedio(0.6);
            brocoli.setTipoRaiz("Fibrosa");
            brocoli.setCicloVida("Anual");
            brocoli.setVelocidadCrecimiento("Moderada");
            brocoli.setDensidadSiembra("35,000 – 45,000 plantas/ha");
            brocoli.setHumedadSuelo("Alta");
            brocoli.setCo2Nivel("400 ppm");
            brocoli.setFe("Medio");
            brocoli.setZn("Medio");
            brocoli.setMn("Medio");
            brocoli.setCu("Bajo");
            brocoli.setB("Medio");
            brocoli.setMo("Bajo");
            brocoli.setCapacidadRetencionAgua("Alta");
            brocoli.setTexturaSuelo("Franco arcilloso");
            brocoli.setPeriodoCriticoPlagas("Formación de cabeza");
            brocoli.setCostoProduccion("$28,000 por ha aprox.");
            brocoli.setPrecioPromedio("$25 – $45/kg");
            brocoli.setRentabilidad("Alta");
            brocoli.setUsoFinal("Consumo fresco, procesado");
            brocoli.setOrigenGenetico("Mediterráneo");
            brocoli.setTipoMultiplicacion("Semilla");
            brocoli.setCompatibilidadCultivos("Lechuga, zanahoria, cebollín");
            brocoli.setIncompatibles("Tomate, pimientos");
            brocoli.setRequerimientosPoda("Eliminación de hojas secas y brotes laterales");
            brocoli.setSiembraMexico("Septiembre – Octubre");
            brocoli.setCosechaMexico("Diciembre – Febrero");
            brocoli.setFaseLunarSiembra("Luna creciente");
            brocoli.setFaseLunarPoda("Luna menguante");
            brocoli.setFaseLunarTrasplante("Luna creciente");
            brocoli.setFaseLunarCosecha("Luna llena");
            brocoli.setCalendarioLunar("Agrícola tradicional");
            brocoli.setSensibilidadFasesLunares("Moderada");

            databaseHelper.insertarRegistro(brocoli);

            // ====== GUISANTE (Pisum sativum) ======
            ModelPlanta guisante = new ModelPlanta("Guisante", "Pisum sativum", "Leguminosa anual de clima templado");
            List<String> guisanteImages = new ArrayList<>();
            guisanteImages.add("https://drive.google.com/uc?export=download&id=125UABK6nn0eACW9ilSxaNgtoQhOxxD25");
            guisanteImages.add("https://drive.google.com/uc?export=download&id=1Mt99gUV2ZFNV-Dp5T7qLjOmk4wK5tb6Y");
            guisanteImages.add("https://drive.google.com/uc?export=download&id=1ua29mHU6m8j62IWtwFPkt70_uxVtVN4p");
            guisanteImages.add("https://drive.google.com/uc?export=download&id=1HCz07fpWRMNpoiGDy5CDGlHkxM1tmwQe");
            guisanteImages.add("https://drive.google.com/uc?export=download&id=1juJlm0hHh11fjGpjYawKN2l-JSwYLzXb");
            guisante.setImageUrls(guisanteImages);

// Datos agronómicos
            guisante.setFertilidad("Media");
            guisante.setFotoperiodo("Neutro");
            guisante.setTemperatura(16.0);
            guisante.setPrecipitacion(500.0);
            guisante.setPh(6.0);
            guisante.setHorasLuz(12.0);
            guisante.setIntensidadLuz(70.0);
            guisante.setHumedadRelativa(80.0);
            guisante.setNitrogeno(100);
            guisante.setFosforo(50);
            guisante.setPotasio(120);
            guisante.setRendimiento(3.5); // ton/ha aprox
            guisante.setCategoriaPh("Ligeramente ácido");
            guisante.setTipoSuelo("Franco arcilloso, bien drenado");
            guisante.setTemporada("Invierno – Primavera");
            guisante.setRelacionN(3);
            guisante.setRelacionP(1);
            guisante.setRelacionK(2);
            guisante.setFenologia("Germinación, crecimiento vegetativo, floración, formación de vaina, maduración");
            guisante.setPlagas("Pulgones, mosca blanca, orugas");
            guisante.setControlBiologico("Uso de insectos benéficos, trampas, rotación de cultivos");
            guisante.setAlturaPromedio(1.5);
            guisante.setTipoRaiz("Pivotante");
            guisante.setCicloVida("Anual");
            guisante.setVelocidadCrecimiento("Rápida");
            guisante.setDensidadSiembra("100,000 – 120,000 plantas/ha");
            guisante.setHumedadSuelo("Media");
            guisante.setCo2Nivel("400 ppm");
            guisante.setFe("Medio");
            guisante.setZn("Medio");
            guisante.setMn("Medio");
            guisante.setCu("Bajo");
            guisante.setB("Medio");
            guisante.setMo("Bajo");
            guisante.setCapacidadRetencionAgua("Media");
            guisante.setTexturaSuelo("Franco arcilloso");
            guisante.setPeriodoCriticoPlagas("Floración y formación de vaina");
            guisante.setCostoProduccion("$15,000 por ha aprox.");
            guisante.setPrecioPromedio("$20 – $30/kg");
            guisante.setRentabilidad("Media");
            guisante.setUsoFinal("Consumo fresco, procesado");
            guisante.setOrigenGenetico("Asia Occidental");
            guisante.setTipoMultiplicacion("Semilla");
            guisante.setCompatibilidadCultivos("Zanahoria, cebollín, lechuga");
            guisante.setIncompatibles("Cebolla, ajo");
            guisante.setRequerimientosPoda("Eliminación de hojas secas y brotes laterales");
            guisante.setSiembraMexico("Noviembre – Diciembre");
            guisante.setCosechaMexico("Febrero – Abril");
            guisante.setFaseLunarSiembra("Luna creciente");
            guisante.setFaseLunarPoda("Luna menguante");
            guisante.setFaseLunarTrasplante("Luna creciente");
            guisante.setFaseLunarCosecha("Luna llena");
            guisante.setCalendarioLunar("Agrícola tradicional");
            guisante.setSensibilidadFasesLunares("Moderada");

            databaseHelper.insertarRegistro(guisante);


            /*
            // Insertar plantas de ejemplo
            ModelPlanta tomate = new ModelPlanta("Tomate", "Solanum lycopersicum", "Hortaliza");
            List<String> tomateImages = new ArrayList<>();
            tomateImages.add("https://drive.google.com/file/d/1Xddnt1lRoVW6Z6JJsRNiPkcEp3wKT3Y3/view?usp=drive_link");
            tomateImages.add("https://drive.google.com/file/d/1tNrdbJugQHpW5WK6q8qalD4h4_HFnDi0/view?usp=drive_link");
            tomateImages.add("https://drive.google.com/file/d/1ysKuMDdKm49jfqcGTdAsnH09nrCqwkmf/view?usp=drive_link");
            tomateImages.add("https://drive.google.com/file/d/1ysKuMDdKm49jfqcGTdAsnH09nrCqwkmf/view?usp=drive_link");
            tomateImages.add("https://drive.google.com/file/d/1RCjRShTEjLZBDoah8QwmiPg4v1f3ZOHr/view?usp=drive_link");
            tomateImages.add("https://drive.google.com/file/d/1j3Vjqg60MEVVpr7ATMCiiiaINd5OpV64/view?usp=drive_link");

            tomate.setImageUrls(tomateImages);
            tomate.setTipoSuelo("Franco");
            tomate.setTemporada("Primavera-Verano");
            databaseHelper.insertarRegistro(tomate);

            ModelPlanta habanero = new ModelPlanta("Habanero", "Capsicum chinense", "Hortaliza");
            List<String> habaneroImages = new ArrayList<>();
            habaneroImages.add("https://drive.google.com/file/d/13-b76BGO5vWLaZQGp-T5auV2k2fKXyoL/view?usp=drive_link");
            habaneroImages.add("https://drive.google.com/file/d/1Ge4BJxhICWwFzpYdhNxtWdvEWVAtEaqL/view?usp=drive_link");
            habaneroImages.add("https://picsum.photos/seed/habanero3/600/800");
            habanero.setImageUrls(habaneroImages);
            habanero.setTipoSuelo("Franco-arenoso");
            habanero.setTemporada("Primavera-Verano");
            databaseHelper.insertarRegistro(habanero);

            ModelPlanta cilantro = new ModelPlanta("Cilantro", "Coriandrum sativum", "Hierba");
            List<String> cilantroImages = new ArrayList<>();
            cilantroImages.add("https://picsum.photos/seed/cilantro/600/800");
            cilantro.setImageUrls(cilantroImages);
            cilantro.setTipoSuelo("Franco");
            cilantro.setTemporada("Todo el año");
            databaseHelper.insertarRegistro(cilantro);

            ModelPlanta epazote = new ModelPlanta("Epazote", "Dysphania ambrosioides", "Hierba");
            List<String> epazoteImages = new ArrayList<>();
            epazoteImages.add("https://picsum.photos/seed/epazote/600/800");
            epazoteImages.add("https://picsum.photos/seed/epazote2/600/800");
            epazote.setImageUrls(epazoteImages);
            epazote.setTipoSuelo("Franco-arcilloso");
            epazote.setTemporada("Primavera-Otoño");
            databaseHelper.insertarRegistro(epazote);

            */

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Recargar datos después de insertar
            loadPlantsFromDatabase();
        }
    }

    private void convertAndDisplayPlants(List<ModelPlanta> plantas) {
        displayList.clear();

        // Agrupar por clasificación
        List<String> clasificaciones = new ArrayList<>();
        for (ModelPlanta planta : plantas) {
            String clasificacion = planta.getClasificacion();
            if (clasificacion != null && !clasificaciones.contains(clasificacion)) {
                clasificaciones.add(clasificacion);
            }
        }

        // Crear items para el RecyclerView
        for (String clasificacion : clasificaciones) {
            // Agregar header de clasificación
            displayList.add(new Itemsplants(0, clasificacion, getImageResourceForCategory(clasificacion)));

            // Agregar plantas de esta clasificación
            for (ModelPlanta planta : plantas) {
                if (clasificacion.equals(planta.getClasificacion())) {
                    Itemsplants item = new Itemsplants(
                            1,
                            planta.getNombre(),
                            planta.getNombreIngles(),
                            getImageResourceForPlant(planta.getNombre()),
                            planta.getImageUrls()
                    );
                    item.setPlantId(planta.getId()); // Establecer el ID real de la planta
                    displayList.add(item);
                }
            }
        }

        // Actualizar el adapter
        if (adapter != null) {
            adapter.updateList(displayList);
        }
    }

    private int getImageResourceForCategory(String categoria) {
        switch (categoria.toLowerCase()) {
            case "hortaliza":
            case "hortalizas":
                return R.drawable.hortalizas;
            case "hierba":
            case "hierbas":
                return R.drawable.cilantro;
            case "fruta":
            case "frutas":
                return R.drawable.frutas;
            default:
                return R.drawable.frutas;
        }
    }

    private int getImageResourceForPlant(String nombre) {
        if (nombre == null) return R.drawable.tomate;

        switch (nombre.toLowerCase()) {
            case "tomate":
                return R.drawable.tomate;
            case "habanero":
                return R.drawable.habanero;
            case "cilantro":
                return R.drawable.cilantro;
            case "epazote":
                return R.drawable.epazote;
            case "rábano":
            case "rabano":
                return R.drawable.rabano;
            case "chayote":
                return R.drawable.chayote;
            default:
                return R.drawable.tomate;
        }
    }

    private void searchPlants(String query) {
        if (query.trim().isEmpty()) {
            convertAndDisplayPlants(plantasFromDB);
            return;
        }

        new SearchPlantsTask().execute(query);
    }

    private class SearchPlantsTask extends AsyncTask<String, Void, List<ModelPlanta>> {
        @Override
        protected List<ModelPlanta> doInBackground(String... queries) {
            String query = queries[0];
            return databaseHelper.searchPlantasByName(query);
        }

        @Override
        protected void onPostExecute(List<ModelPlanta> plantas) {
            super.onPostExecute(plantas);
            convertAndDisplayPlants(plantas);
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenerClickImageDone() {
        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = binding.editQuery.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchPlants(query);
                    showToast("Buscando: " + query);
                } else {
                    showToast("Ingresa un término de búsqueda");
                }
            }
        });
    }

    public void listenersIcons() {
        binding.editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!binding.editQuery.getText().toString().isEmpty()) {
                    binding.done.setVisibility(View.VISIBLE);
                } else {
                    binding.done.setVisibility(View.GONE);
                    // Restaurar vista completa cuando se borra el texto
                    convertAndDisplayPlants(plantasFromDB);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        binding = null;
    }
}