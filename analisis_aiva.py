import pandas as pd


def analizar_aiva(datos):
    # 1. Indicadores Generales
    indicadores = pd.DataFrame({
        "Indicador": [
            "Créditos Totales Consumidos",
            "Duración Promedio de Video (Segs)",
            "Costo Promedio por Segundo (Créditos/Seg)"
        ],
        "Valor": [
            datos["creditos_consumidos"].sum(),
            datos["duracion_seg"].mean(),
            datos["costo_por_segundo"].mean()
        ]
    })

    # 2 & 3. Análisis por Modelo de IA
    modelos = datos.groupby("modelo_ia", as_index=False).agg(
        videos_generados=("id_generacion", "count"),
        duracion_total_seg=("duracion_seg", "sum"),
        creditos_totales=("creditos_consumidos", "sum")
    )
    top_volumen = modelos.sort_values(
        "videos_generados", ascending=False).head(5)
    top_consumo = modelos.sort_values(
        "creditos_totales", ascending=False).head(5)

    # 4. Análisis por Usuario (excluyendo anónimos/no registrados)
    registrados = datos[datos["usuario"] != "SIN_REGISTRAR"]
    usuarios = registrados.groupby("usuario", as_index=False).agg(
        generaciones=("id_generacion", "count"),
        creditos_gastados=("creditos_consumidos", "sum"),
        promedio_creditos=("creditos_consumidos", "mean")
    ).sort_values("creditos_gastados", ascending=False)

    # 5. Análisis por Estado del Proyecto
    estados = datos.groupby("estado_proyecto", as_index=False).agg(
        creditos_totales=("creditos_consumidos", "sum"),
        total_videos=("id_generacion", "count")
    ).sort_values("creditos_totales", ascending=False)

    return {
        "indicadores": indicadores,
        "top_volumen": top_volumen,
        "top_consumo": top_consumo,
        "usuarios": usuarios,
        "estados": estados
    }