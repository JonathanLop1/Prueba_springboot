#!/bin/bash

echo "=========================================="
echo " COOPCREDIT SYSTEM FULL TEST"
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
  echo -e "${GREEN}✓ Admin login successful${NC}"
else
  echo -e "${RED}✗ Admin login failed${NC}"
  exit 1
fi

echo ""

# 2. CREATE AFFILIATE
echo -e "${YELLOW}=== 2. CREATE NEW AFFILIATE ===${NC}"
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
  echo -e "${GREEN}✓ Affiliate created (ID: $AFFILIATE_ID)${NC}"
else
  echo -e "${YELLOW}⚠ Could not create new affiliate, using existing${NC}"
  AFFILIATE_ID=3
fi

echo ""

# 3. LIST AFFILIATES
echo -e "${YELLOW}=== 3. LIST AFFILIATES ===${NC}"
AFFILIATES=$(curl -s http://localhost:8080/api/affiliates \
  -H "Authorization: Bearer $ADMIN_TOKEN")
echo $AFFILIATES | jq 'length as $count | "Total affiliates: \($count)"'
echo $AFFILIATES | jq '.[0:2]'

echo ""

# 4. LOGIN ANALYST
echo -e "${YELLOW}=== 4. LOGIN ANALYST (analyst1 / Admin123) ===${NC}"
ANALYST_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"analyst1","password":"Admin123"}')

ANALYST_TOKEN=$(echo $ANALYST_RESPONSE | jq -r '.token')
echo $ANALYST_RESPONSE | jq '{username, expiresIn}'

if [ "$ANALYST_TOKEN" != "null" ] && [ -n "$ANALYST_TOKEN" ]; then
  echo -e "${GREEN}✓ Analyst login successful${NC}"
else
  echo -e "${RED}✗ Analyst login failed${NC}"
fi

echo ""

# 5. LOGIN AFFILIATE
echo -e "${YELLOW}=== 5. LOGIN AFFILIATE (affiliate1 / Admin123) ===${NC}"
AFFILIATE_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"affiliate1","password":"Admin123"}')

AFFILIATE_TOKEN=$(echo $AFFILIATE_LOGIN | jq -r '.token')
echo $AFFILIATE_LOGIN | jq '{username, expiresIn}'

if [ "$AFFILIATE_TOKEN" != "null" ] && [ -n "$AFFILIATE_TOKEN" ]; then
  echo -e "${GREEN}✓ Affiliate login successful${NC}"
else
  echo -e "${RED}✗ Affiliate login failed${NC}"
fi

echo ""

# 6. CREATE APPLICATION
echo -e "${YELLOW}=== 6. CREATE CREDIT APPLICATION (Affiliate ID: $AFFILIATE_ID) ===${NC}"
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
  echo -e "${GREEN}✓ Application created (ID: $APPLICATION_ID)${NC}"
else
  echo -e "${RED}✗ Application creation failed${NC}"
  APPLICATION_ID=1
fi

echo ""

# 7. EVALUATE APPLICATION
echo -e "${YELLOW}=== 7. EVALUATE APPLICATION (ID: $APPLICATION_ID) ===${NC}"
EVAL_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/applications/${APPLICATION_ID}/evaluate" \
  -H "Authorization: Bearer $ANALYST_TOKEN")

echo $EVAL_RESPONSE | jq '.'

if echo $EVAL_RESPONSE | jq -e '.status' > /dev/null 2>&1; then
  STATUS=$(echo $EVAL_RESPONSE | jq -r '.status')
  echo -e "${GREEN}✓ Evaluation completed - Status: $STATUS${NC}"
else
  echo -e "${RED}✗ Evaluation failed${NC}"
fi

echo ""

# 8. LIST APPLICATIONS
echo -e "${YELLOW}=== 8. LIST APPLICATIONS ===${NC}"
APPLICATIONS=$(curl -s http://localhost:8080/api/applications \
  -H "Authorization: Bearer $ANALYST_TOKEN")
echo $APPLICATIONS | jq 'length as $count | "Total applications: \($count)"'
if [ "$(echo $APPLICATIONS | jq 'length')" -gt "0" ]; then
  echo $APPLICATIONS | jq '.[0]'
fi

echo ""

# 9. GET APPLICATION BY ID
if [ "$APPLICATION_ID" != "null" ] && [ -n "$APPLICATION_ID" ]; then
  echo -e "${YELLOW}=== 9. VIEW APPLICATION DETAIL ${APPLICATION_ID} ===${NC}"
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
  echo -e "${GREEN}✓ System healthy${NC}"
else
  echo -e "${RED}✗ System with issues${NC}"
fi

echo ""
echo "=========================================="
echo -e "${GREEN}   ✓ ALL TESTS COMPLETED"
echo "=========================================="
echo ""
echo "Test Credentials:"
echo "  admin      / Admin123  (ROLE_ADMIN)"
echo "  analyst1   / Admin123  (ROLE_ANALISTA)"
echo "  affiliate1 / Admin123  (ROLE_AFILIADO)"
