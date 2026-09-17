from pathlib import Path
from limpieza_datos import (
    cargar_datos, inspeccionar_datos,
    limpiar_datos, guardar_avances
)
from analisis_aiva import analizar_aiva
from generar_reporte import crear_reporte


def main():
    base = Path(__file__).resolve().parent
    origen = base / "data" / "raw" / "generaciones_aiva.csv"
    procesados = base / "data" / "processed"

    print("=== INICIANDO PIPELINE DE ANALÍTICA AIVA STUDIO ===")
    
    # 1. Cargar e Inspeccionar
    originales = cargar_datos(origen)
    inspeccionar_datos(originales)
    
    # 2. Depuración y Control de Calidad
    datos, rechazadas, calidad = limpiar_datos(originales)
    guardar_avances(datos, rechazadas, procesados)
    print("\nRESUMEN CONTROL DE CALIDAD:", calidad)

    # 3. Análisis de Métricas
    resultados = analizar_aiva(datos)
    for nombre, tabla in resultados.items():
        print(f"\n--- {nombre.upper()} ---")
        print(tabla.to_string(index=False))

    # 4. Generación de Reporte Web
    destino = crear_reporte(resultados, calidad, base / "reporte")
    print(f"\n✅ Reporte de AIVA Studio generado con éxito en: {destino}")


if __name__ == "__main__":
    main()