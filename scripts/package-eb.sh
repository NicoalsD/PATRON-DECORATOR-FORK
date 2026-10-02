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
jar --create --no-manifest --file target/decorator-lab-eb.zip -C target/eb .
echo "Paquete listo: target/decorator-lab-eb.zip"
