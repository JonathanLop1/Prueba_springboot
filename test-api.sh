#!/bin/bash

echo "=========================================="
echo " PRUEBA COMPLETA DEL SISTEMA COOPCREDIT"
echo "=========================================="
echo ""

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 1. LOGIN ADMIN
echo -e "${YELLOW}=== 1. LOGIN ADMIN (admin / Admin123) ===${NC}"
ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123"}')

ADMIN_TOKEN=$(echo $ADMIN_RESPONSE | jq -r '.token')
echo $ADMIN_RESPONSE | jq '{username, expiresIn, token_length: (.token | length)}'

if [ "$ADMIN_TOKEN" != "null" ] && [ -n "$ADMIN_TOKEN" ]; then
  echo -e "${GREEN}✓ Login admin exitoso${NC}"
else
  echo -e "${RED}✗ Login admin falló${NC}"
  exit 1
fi

echo ""

# 2. CREAR AFILIADO
echo -e "${YELLOW}=== 2. CREAR NUEVO AFILIADO ===${NC}"
AFFILIATE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/affiliates \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "document":"5555555555",
    "fullName":"Pedro Martínez Testing",
    "salary":7000000,
    "affiliationDate":"2024-01-01"
  }')

echo $AFFILIATE_RESPONSE | jq '.'
AFFILIATE_ID=$(echo $AFFILIATE_RESPONSE | jq -r '.id')

if [ "$AFFILIATE_ID" != "null" ] && [ -n "$AFFILIATE_ID" ]; then
  echo -e "${GREEN}✓ Afiliado creado (ID: $AFFILIATE_ID)${NC}"
else
  echo -e "${YELLOW}⚠ No se pudo crear afiliado nuevo, usando existente${NC}"
  AFFILIATE_ID=3
fi

echo ""

# 3. LISTAR AFILIADOS
echo -e "${YELLOW}=== 3. LISTAR AFILIADOS ===${NC}"
AFFILIATES=$(curl -s http://localhost:8080/api/affiliates \
  -H "Authorization: Bearer $ADMIN_TOKEN")
echo $AFFILIATES | jq 'length as $count | "Total afiliados: \($count)"'
echo $AFFILIATES | jq '.[0:2]'

echo ""

# 4. LOGIN ANALISTA
echo -e "${YELLOW}=== 4. LOGIN ANALISTA (analyst1 / Admin123) ===${NC}"
ANALYST_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"analyst1","password":"Admin123"}')

ANALYST_TOKEN=$(echo $ANALYST_RESPONSE | jq -r '.token')
echo $ANALYST_RESPONSE | jq '{username, expiresIn}'

if [ "$ANALYST_TOKEN" != "null" ] && [ -n "$ANALYST_TOKEN" ]; then
  echo -e "${GREEN}✓ Login analista exitoso${NC}"
else
  echo -e "${RED}✗ Login analista falló${NC}"
fi

echo ""

# 5. LOGIN AFILIADO
echo -e "${YELLOW}=== 5. LOGIN AFILIADO (affiliate1 / Admin123) ===${NC}"
AFFILIATE_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"affiliate1","password":"Admin123"}')

AFFILIATE_TOKEN=$(echo $AFFILIATE_LOGIN | jq -r '.token')
echo $AFFILIATE_LOGIN | jq '{username, expiresIn}'

if [ "$AFFILIATE_TOKEN" != "null" ] && [ -n "$AFFILIATE_TOKEN" ]; then
  echo -e "${GREEN}✓ Login afiliado exitoso${NC}"
else
  echo -e "${RED}✗ Login afiliado falló${NC}"
fi

echo ""

# 6. CREAR SOLICITUD
echo -e "${YELLOW}=== 6. CREAR SOLICITUD DE CRÉDITO (Affiliate ID: $AFFILIATE_ID) ===${NC}"
APPLICATION_RESPONSE=$(curl -s -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AFFILIATE_TOKEN" \
  -d "{
    \"affiliateId\": $AFFILIATE_ID,
    \"requestedAmount\": 10000000,
    \"termMonths\": 24,
    \"proposedRate\": 1.5
  }")

echo $APPLICATION_RESPONSE | jq '.'
APPLICATION_ID=$(echo $APPLICATION_RESPONSE | jq -r '.id')

if [ "$APPLICATION_ID" != "null" ] && [ -n "$APPLICATION_ID" ]; then
  echo -e "${GREEN}✓ Solicitud creada (ID: $APPLICATION_ID)${NC}"
else
  echo -e "${RED}✗ Creación de solicitud falló${NC}"
  APPLICATION_ID=1
fi

echo ""

# 7. EVALUAR SOLICITUD
echo -e "${YELLOW}=== 7. EVALUAR SOLICITUD (ID: $APPLICATION_ID) ===${NC}"
EVAL_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/applications/${APPLICATION_ID}/evaluate" \
  -H "Authorization: Bearer $ANALYST_TOKEN")

echo $EVAL_RESPONSE | jq '.'

if echo $EVAL_RESPONSE | jq -e '.status' > /dev/null 2>&1; then
  STATUS=$(echo $EVAL_RESPONSE | jq -r '.status')
  echo -e "${GREEN}✓ Evaluación completada - Estado: $STATUS${NC}"
else
  echo -e "${RED}✗ Evaluación falló${NC}"
fi

echo ""

# 8. LISTAR SOLICITUDES
echo -e "${YELLOW}=== 8. LISTAR SOLICITUDES ===${NC}"
APPLICATIONS=$(curl -s http://localhost:8080/api/applications \
  -H "Authorization: Bearer $ANALYST_TOKEN")
echo $APPLICATIONS | jq 'length as $count | "Total solicitudes: \($count)"'
if [ "$(echo $APPLICATIONS | jq 'length')" -gt "0" ]; then
  echo $APPLICATIONS | jq '.[0]'
fi

echo ""

# 9. GET SOLICITUD POR ID
if [ "$APPLICATION_ID" != "null" ] && [ -n "$APPLICATION_ID" ]; then
  echo -e "${YELLOW}=== 9. VER DETALLE DE SOLICITUD ${APPLICATION_ID} ===${NC}"
  DETAIL=$(curl -s "http://localhost:8080/api/applications/${APPLICATION_ID}" \
    -H "Authorization: Bearer $ANALYST_TOKEN")
  echo $DETAIL | jq '.'
fi

echo ""

# 10. HEALTH CHECK
echo -e "${YELLOW}=== 10. HEALTH CHECK ===${NC}"
HEALTH_RESPONSE=$(curl -s http://localhost:8080/actuator/health)
HEALTH=$(echo $HEALTH_RESPONSE | jq -r '.status')
echo "Status: $HEALTH"
echo $HEALTH_RESPONSE | jq '.components | keys'

if [ "$HEALTH" = "UP" ]; then
  echo -e "${GREEN}✓ Sistema saludable${NC}"
else
  echo -e "${RED}✗ Sistema con problemas${NC}"
fi

echo ""
echo "=========================================="
echo -e "${GREEN}   ✓ TODAS LAS PRUEBAS COMPLETADAS"
echo "=========================================="
echo ""
echo "Credenciales de prueba:"
echo "  admin      / Admin123  (ROLE_ADMIN)"
echo "  analyst1   / Admin123  (ROLE_ANALISTA)"
echo "  affiliate1 / Admin123  (ROLE_AFILIADO)"
