import pandas as pd


def crear_reporte(resultados, calidad, carpeta):
    plantilla = carpeta / "plantilla.html"
    contenido = plantilla.read_text(encoding="utf-8")
    tablas = dict(resultados)
    
    tablas["calidad"] = pd.DataFrame(
        list(calidad.items()), columns=["Métrica", "Registros"]
    )

    for nombre, tabla in tablas.items():
        html_tabla = tabla.to_html(
            index=False,
            border=0,
            classes="tabla-datos",
            escape=True,
            float_format=lambda val: f"{val:,.2f}"
        )
        contenido = contenido.replace("{{" + nombre + "}}", html_tabla)

    destino = carpeta / "index.html"
    destino.write_text(contenido, encoding="utf-8")
    return destino