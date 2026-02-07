#!/bin/bash

# ================================================
# Générateur de certificats SSL auto-signés
# Pour développement local uniquement
# ================================================

set -e

CERT_DIR="./traefik/dynamic/certs"
DOMAIN=${1:-"*.localhost"}

# Définir tous les hôtes locaux dans une variable pour faciliter la maintenance
HOSTS=("localhost" "products.localhost" "traefik.localhost" "prometheus.localhost" "grafana.localhost")

# Couleurs pour affichage
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${YELLOW}Génération de certificat SSL auto-signé pour: $DOMAIN${NC}"

# Créer le répertoire si nécessaire
mkdir -p "$CERT_DIR"

# Vérifier si OpenSSL est installé
if ! command -v openssl &> /dev/null; then
    echo -e "${RED}Erreur: OpenSSL n'est pas installé${NC}"
    exit 1
fi

# Construire la liste pour subjectAltName
SAN=""
for host in "${HOSTS[@]}"; do
    SAN+="DNS:${host},"
done
SAN=${SAN%,}  # Supprime la dernière virgule

# Générer la clé privée et le certificat
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout "$CERT_DIR/key.pem" \
    -out "$CERT_DIR/cert.pem" \
    -subj "/C=FR/ST=France/L=Paris/O=Development/OU=IT/CN=$DOMAIN" \
    -addext "subjectAltName = $SAN"

# Permissions
chmod 600 "$CERT_DIR/key.pem"
chmod 644 "$CERT_DIR/cert.pem"

echo -e "${GREEN}✅ Certificat SSL généré avec succès!${NC}"
echo -e "${GREEN}   Certificat: $CERT_DIR/cert.pem${NC}"
echo -e "${GREEN}   Clé privée: $CERT_DIR/key.pem${NC}"
echo ""
echo -e "${YELLOW}⚠️  Ce certificat est auto-signé et uniquement pour le développement${NC}"
echo -e "${YELLOW}   En production, utilisez Let's Encrypt${NC}"
echo ""
echo -e "${GREEN}Pour utiliser:${NC}"
echo "  1. Ajoutez à /etc/hosts:"
echo "     127.0.0.1 ${HOSTS[*]}"
echo "  2. Lancez: docker compose -f compose.prod.yml up -d"
echo "  3. Accédez à: https://products.localhost:5443"
