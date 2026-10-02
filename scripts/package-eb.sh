#!/usr/bin/env sh
# Genera target/decorator-lab-eb.zip para AWS Elastic Beanstalk (plataforma Corretto 21).
# El zip lleva el JAR y el Procfile en la raíz; nginx de Beanstalk reenvía al puerto 5000.
set -eu
PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$PROJECT_DIR"
mvn -q -B clean package
rm -rf target/eb && mkdir -p target/eb
cp target/decorator-lab-1.0.0.jar deploy/elastic-beanstalk/Procfile target/eb/
rm -f target/decorator-lab-eb.zip
# Zip normal (no "jar"): la herramienta jar marca el archivo como JAR y Beanstalk lo ejecutaría como tal.
if command -v zip >/dev/null 2>&1; then
  (cd target/eb && zip -q -X ../decorator-lab-eb.zip Procfile decorator-lab-1.0.0.jar)
elif command -v python3 >/dev/null 2>&1; then
  python3 -c 'import zipfile; z = zipfile.ZipFile("target/decorator-lab-eb.zip", "w", zipfile.ZIP_DEFLATED); [z.write("target/eb/" + n, n) for n in ("Procfile", "decorator-lab-1.0.0.jar")]; z.close()'
else
  echo "Instala zip o python3 para generar el paquete." >&2; exit 1
fi
echo "Paquete listo: target/decorator-lab-eb.zip"
