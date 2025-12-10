#!/bin/sh

echo "Entrypoint script started..."

if [ -n "$SPRING_DATASOURCE_URL" ]; then
    echo "Checking SPRING_DATASOURCE_URL..."
    
    # 1. Try to replace 'postgres://user:pass@host' with 'jdbc:postgresql://host' (Strip credentials)
    CLEANED_URL=$(echo "$SPRING_DATASOURCE_URL" | sed -E 's|^postgres(ql)?://[^@]+@|jdbc:postgresql://|')
    
    # 2. If no credentials were found (URL didn't change), just replace the protocol
    if [ "$CLEANED_URL" = "$SPRING_DATASOURCE_URL" ]; then
        CLEANED_URL=$(echo "$SPRING_DATASOURCE_URL" | sed -E 's|^postgres(ql)?://|jdbc:postgresql://|')
    fi
    
    export SPRING_DATASOURCE_URL="$CLEANED_URL"
    echo "SPRING_DATASOURCE_URL updated."
fi

echo "Starting Java application..."
exec java -jar app.jar
