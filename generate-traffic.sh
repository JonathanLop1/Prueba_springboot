#!/bin/bash

echo "🔄 Continuous Traffic Generator for Grafana"
echo "=============================================="
echo ""
echo "Press Ctrl+C to stop..."
echo ""

COUNTER=1

while true; do
    echo "[$COUNTER] Running test-api.sh..."
    ./test-api.sh > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        echo "  ✅ Test completed"
    else
        echo "  ❌ Test failed"
    fi
    
    # Show current metrics every 5 tests
    if [ $((COUNTER % 5)) -eq 0 ]; then
        echo ""
        echo "📊 Current Metrics:"
        CREATED=$(curl -s 'http://localhost:19090/api/v1/query?query=credit_applications_created_total{status="success"}' | jq -r '.data.result[0].value[1]' 2>/dev/null || echo "N/A")
        EVALUATED=$(curl -s 'http://localhost:19090/api/v1/query?query=credit_applications_evaluated_total{status="success"}' | jq -r '.data.result[0].value[1]' 2>/dev/null || echo "N/A")
        echo "  Applications created: $CREATED"
        echo "  Applications evaluated: $EVALUATED"
        echo ""
    fi
    
    COUNTER=$((COUNTER + 1))
    sleep 5
done
