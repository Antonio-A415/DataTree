package com.datatree.infraestructure.dataclass.databasemodels;

import java.util.ArrayList;
import java.util.List;

public class ModelPlanta {
    private long id;
    private String nombre;
    private String nombreIngles;
    private String fertilidad;
    private String fotoperiodo;
    private double temperatura;
    private double precipitacion;
    private double ph;
    private double horasLuz;
    private double intensidadLuz;
    private double humedadRelativa;
    private double nitrogeno;
    private double fosforo;
    private double potasio;
    private double rendimiento;
    private String categoriaPh;
    private String tipoSuelo;
    private String temporada;
    private double relacionN;
    private double relacionP;
    private double relacionK;
    private String fenologia;
    private String plagas;
    private String controlBiologico;
    private double alturaPromedio;
    private String tipoRaiz;
    private String cicloVida;
    private String velocidadCrecimiento;
    private String densidadSiembra;
    private String humedadSuelo;
    private String co2Nivel;
    private String fe;
    private String zn;
    private String mn;
    private String cu;
    private String b;
    private String mo;
    private String capacidadRetencionAgua;
    private String texturaSuelo;
    private String periodoCriticoPlagas;
    private String costoProduccion;
    private String precioPromedio;
    private String rentabilidad;
    private String usoFinal;
    private String origenGenetico;
    private String tipoMultiplicacion;
    private String compatibilidadCultivos;
    private String incompatibles;
    private String requerimientosPoda;
    private String siembraMexico;
    private String cosechaMexico;
    private String faseLunarSiembra;
    private String faseLunarPoda;
    private String faseLunarTrasplante;
    private String faseLunarCosecha;
    private String calendarioLunar;
    private String sensibilidadFasesLunares;
    private String clasificacion;

    // CAMPOS REFACTORIZADOS PARA IMÁGENES
    @Deprecated // Mantener por compatibilidad pero marcar como obsoleto
    private String urlFotos;

    private List<String> imageUrls; // Nueva lista de URLs de imágenes

    // Constructor vacío
    public ModelPlanta() {
        this.imageUrls = new ArrayList<>();
    }

    // Constructor con parámetros básicos
    public ModelPlanta(String nombre, String nombreIngles, String clasificacion) {
        this.nombre = nombre;
        this.nombreIngles = nombreIngles;
        this.clasificacion = clasificacion;
        this.imageUrls = new ArrayList<>();
    }

    // Constructor con imágenes
    public ModelPlanta(String nombre, String nombreIngles, String clasificacion, List<String> imageUrls) {
        this.nombre = nombre;
        this.nombreIngles = nombreIngles;
        this.clasificacion = clasificacion;
        this.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>();
    }

    // Getters y Setters básicos
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNombreIngles() { return nombreIngles; }
    public void setNombreIngles(String nombreIngles) { this.nombreIngles = nombreIngles; }

    public String getFertilidad() { return fertilidad; }
    public void setFertilidad(String fertilidad) { this.fertilidad = fertilidad; }

    public String getFotoperiodo() { return fotoperiodo; }
    public void setFotoperiodo(String fotoperiodo) { this.fotoperiodo = fotoperiodo; }

    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }

    public double getPrecipitacion() { return precipitacion; }
    public void setPrecipitacion(double precipitacion) { this.precipitacion = precipitacion; }

    public double getPh() { return ph; }
    public void setPh(double ph) { this.ph = ph; }

    public double getHorasLuz() { return horasLuz; }
    public void setHorasLuz(double horasLuz) { this.horasLuz = horasLuz; }

    public double getIntensidadLuz() { return intensidadLuz; }
    public void setIntensidadLuz(double intensidadLuz) { this.intensidadLuz = intensidadLuz; }

    public double getHumedadRelativa() { return humedadRelativa; }
    public void setHumedadRelativa(double humedadRelativa) { this.humedadRelativa = humedadRelativa; }

    public double getNitrogeno() { return nitrogeno; }
    public void setNitrogeno(double nitrogeno) { this.nitrogeno = nitrogeno; }

    public double getFosforo() { return fosforo; }
    public void setFosforo(double fosforo) { this.fosforo = fosforo; }

    public double getPotasio() { return potasio; }
    public void setPotasio(double potasio) { this.potasio = potasio; }

    public double getRendimiento() { return rendimiento; }
    public void setRendimiento(double rendimiento) { this.rendimiento = rendimiento; }

    public String getCategoriaPh() { return categoriaPh; }
    public void setCategoriaPh(String categoriaPh) { this.categoriaPh = categoriaPh; }

    public String getTipoSuelo() { return tipoSuelo; }
    public void setTipoSuelo(String tipoSuelo) { this.tipoSuelo = tipoSuelo; }

    public String getTemporada() { return temporada; }
    public void setTemporada(String temporada) { this.temporada = temporada; }

    public double getRelacionN() { return relacionN; }
    public void setRelacionN(double relacionN) { this.relacionN = relacionN; }

    public double getRelacionP() { return relacionP; }
    public void setRelacionP(double relacionP) { this.relacionP = relacionP; }

    public double getRelacionK() { return relacionK; }
    public void setRelacionK(double relacionK) { this.relacionK = relacionK; }

    public String getFenologia() { return fenologia; }
    public void setFenologia(String fenologia) { this.fenologia = fenologia; }

    public String getPlagas() { return plagas; }
    public void setPlagas(String plagas) { this.plagas = plagas; }

    public String getControlBiologico() { return controlBiologico; }
    public void setControlBiologico(String controlBiologico) { this.controlBiologico = controlBiologico; }

    public double getAlturaPromedio() { return alturaPromedio; }
    public void setAlturaPromedio(double alturaPromedio) { this.alturaPromedio = alturaPromedio; }

    public String getTipoRaiz() { return tipoRaiz; }
    public void setTipoRaiz(String tipoRaiz) { this.tipoRaiz = tipoRaiz; }

    public String getCicloVida() { return cicloVida; }
    public void setCicloVida(String cicloVida) { this.cicloVida = cicloVida; }

    public String getVelocidadCrecimiento() { return velocidadCrecimiento; }
    public void setVelocidadCrecimiento(String velocidadCrecimiento) { this.velocidadCrecimiento = velocidadCrecimiento; }

    public String getDensidadSiembra() { return densidadSiembra; }
    public void setDensidadSiembra(String densidadSiembra) { this.densidadSiembra = densidadSiembra; }

    public String getHumedadSuelo() { return humedadSuelo; }
    public void setHumedadSuelo(String humedadSuelo) { this.humedadSuelo = humedadSuelo; }

    public String getCo2Nivel() { return co2Nivel; }
    public void setCo2Nivel(String co2Nivel) { this.co2Nivel = co2Nivel; }

    public String getFe() { return fe; }
    public void setFe(String fe) { this.fe = fe; }

    public String getZn() { return zn; }
    public void setZn(String zn) { this.zn = zn; }

    public String getMn() { return mn; }
    public void setMn(String mn) { this.mn = mn; }

    public String getCu() { return cu; }
    public void setCu(String cu) { this.cu = cu; }

    public String getB() { return b; }
    public void setB(String b) { this.b = b; }

    public String getMo() { return mo; }
    public void setMo(String mo) { this.mo = mo; }

    public String getCapacidadRetencionAgua() { return capacidadRetencionAgua; }
    public void setCapacidadRetencionAgua(String capacidadRetencionAgua) { this.capacidadRetencionAgua = capacidadRetencionAgua; }

    public String getTexturaSuelo() { return texturaSuelo; }
    public void setTexturaSuelo(String texturaSuelo) { this.texturaSuelo = texturaSuelo; }

    public String getPeriodoCriticoPlagas() { return periodoCriticoPlagas; }
    public void setPeriodoCriticoPlagas(String periodoCriticoPlagas) { this.periodoCriticoPlagas = periodoCriticoPlagas; }

    public String getCostoProduccion() { return costoProduccion; }
    public void setCostoProduccion(String costoProduccion) { this.costoProduccion = costoProduccion; }

    public String getPrecioPromedio() { return precioPromedio; }
    public void setPrecioPromedio(String precioPromedio) { this.precioPromedio = precioPromedio; }

    public String getRentabilidad() { return rentabilidad; }
    public void setRentabilidad(String rentabilidad) { this.rentabilidad = rentabilidad; }

    public String getUsoFinal() { return usoFinal; }
    public void setUsoFinal(String usoFinal) { this.usoFinal = usoFinal; }

    public String getOrigenGenetico() { return origenGenetico; }
    public void setOrigenGenetico(String origenGenetico) { this.origenGenetico = origenGenetico; }

    public String getTipoMultiplicacion() { return tipoMultiplicacion; }
    public void setTipoMultiplicacion(String tipoMultiplicacion) { this.tipoMultiplicacion = tipoMultiplicacion; }

    public String getCompatibilidadCultivos() { return compatibilidadCultivos; }
    public void setCompatibilidadCultivos(String compatibilidadCultivos) { this.compatibilidadCultivos = compatibilidadCultivos; }

    public String getIncompatibles() { return incompatibles; }
    public void setIncompatibles(String incompatibles) { this.incompatibles = incompatibles; }

    public String getRequerimientosPoda() { return requerimientosPoda; }
    public void setRequerimientosPoda(String requerimientosPoda) { this.requerimientosPoda = requerimientosPoda; }

    public String getSiembraMexico() { return siembraMexico; }
    public void setSiembraMexico(String siembraMexico) { this.siembraMexico = siembraMexico; }

    public String getCosechaMexico() { return cosechaMexico; }
    public void setCosechaMexico(String cosechaMexico) { this.cosechaMexico = cosechaMexico; }

    public String getFaseLunarSiembra() { return faseLunarSiembra; }
    public void setFaseLunarSiembra(String faseLunarSiembra) { this.faseLunarSiembra = faseLunarSiembra; }

    public String getFaseLunarPoda() { return faseLunarPoda; }
    public void setFaseLunarPoda(String faseLunarPoda) { this.faseLunarPoda = faseLunarPoda; }

    public String getFaseLunarTrasplante() { return faseLunarTrasplante; }
    public void setFaseLunarTrasplante(String faseLunarTrasplante) { this.faseLunarTrasplante = faseLunarTrasplante; }

    public String getFaseLunarCosecha() { return faseLunarCosecha; }
    public void setFaseLunarCosecha(String faseLunarCosecha) { this.faseLunarCosecha = faseLunarCosecha; }

    public String getCalendarioLunar() { return calendarioLunar; }
    public void setCalendarioLunar(String calendarioLunar) { this.calendarioLunar = calendarioLunar; }

    public String getSensibilidadFasesLunares() { return sensibilidadFasesLunares; }
    public void setSensibilidadFasesLunares(String sensibilidadFasesLunares) { this.sensibilidadFasesLunares = sensibilidadFasesLunares; }

    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }

    // MÉTODOS PARA MANEJO DE IMÁGENES

    @Deprecated // Mantener por compatibilidad
    public String getUrlFotos() { return urlFotos; }

    @Deprecated // Mantener por compatibilidad
    public void setUrlFotos(String urlFotos) { this.urlFotos = urlFotos; }

    // Nuevos métodos para manejar múltiples imágenes
    public List<String> getImageUrls() {
        return imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>();
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>();
    }

    public void addImageUrl(String imageUrl) {
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        }
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            this.imageUrls.add(imageUrl);
        }
    }

    public void removeImageUrl(String imageUrl) {
        if (this.imageUrls != null) {
            this.imageUrls.remove(imageUrl);
        }
    }

    public void clearImages() {
        if (this.imageUrls != null) {
            this.imageUrls.clear();
        }
    }

    public String getPrimaryImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }
        return null;
    }

    public boolean hasImages() {
        return imageUrls != null && !imageUrls.isEmpty();
    }

    public int getImageCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    // Métodos utilitarios
    @Override
    public String toString() {
        return "ModelPlanta{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", nombreIngles='" + nombreIngles + '\'' +
                ", clasificacion='" + clasificacion + '\'' +
                ", imageCount=" + getImageCount() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelPlanta that = (ModelPlanta) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    // Método para verificar si la planta tiene datos completos
    public boolean isDatosCompletos() {
        return nombre != null && !nombre.trim().isEmpty() &&
                nombreIngles != null && !nombreIngles.trim().isEmpty() &&
                clasificacion != null && !clasificacion.trim().isEmpty();
    }

    // Método para obtener resumen de la planta
    public String getResumen() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("Nombre: ").append(nombre).append("\n");
        resumen.append("Nombre científico: ").append(nombreIngles).append("\n");
        resumen.append("Clasificación: ").append(clasificacion).append("\n");
        if (temporada != null) resumen.append("Temporada: ").append(temporada).append("\n");
        if (tipoSuelo != null) resumen.append("Tipo de suelo: ").append(tipoSuelo).append("\n");
        resumen.append("Imágenes: ").append(getImageCount()).append("\n");
        return resumen.toString();
    }

    // Método de utilidad para convertir de Itemsplants a ModelPlanta
    public static ModelPlanta fromItemsplants(com.datatree.infraestructure.dataclass.Itemsplants item) {
        ModelPlanta planta = new ModelPlanta();
        planta.setNombre(item.getNombre());
        planta.setNombreIngles(item.getNombre2());

        // Convertir las URLs de imágenes
        if (item.getUrlImagenes() != null) {
            planta.setImageUrls(new ArrayList<>(item.getUrlImagenes()));
        }

        return planta;
    }
}