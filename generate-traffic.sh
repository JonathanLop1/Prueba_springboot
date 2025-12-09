#!/bin/bash

echo "🔄 Generador de Tráfico Continuo para Grafana"
echo "=============================================="
echo ""
echo "Presiona Ctrl+C para detener..."
echo ""

COUNTER=1

while true; do
    echo "[$COUNTER] Ejecutando test-api.sh..."
    ./test-api.sh > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        echo "  ✅ Test completado"
    else
        echo "  ❌ Test falló"
    fi
    
    # Mostrar métricas actuales cada 5 tests
    if [ $((COUNTER % 5)) -eq 0 ]; then
        echo ""
        echo "📊 Métricas actuales:"
        CREATED=$(curl -s 'http://localhost:19090/api/v1/query?query=credit_applications_created_total{status="success"}' | jq -r '.data.result[0].value[1]' 2>/dev/null || echo "N/A")
        EVALUATED=$(curl -s 'http://localhost:19090/api/v1/query?query=credit_applications_evaluated_total{status="success"}' | jq -r '.data.result[0].value[1]' 2>/dev/null || echo "N/A")
        echo "  Solicitudes creadas: $CREATED"
        echo "  Solicitudes evaluadas: $EVALUATED"
        echo ""
    fi
    
    COUNTER=$((COUNTER + 1))
    sleep 5
done
