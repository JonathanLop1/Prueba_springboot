#!/bin/sh

echo "Entrypoint script started..."

if [ -n "$SPRING_DATASOURCE_URL" ]; then
    echo "Checking SPRING_DATASOURCE_URL..."
    # Replace postgres:// with jdbc:postgresql://
    export SPRING_DATASOURCE_URL=$(echo "$SPRING_DATASOURCE_URL" | sed 's/^postgres:\/\//jdbc:postgresql:\/\//')
    # Replace postgresql:// with jdbc:postgresql://
    export SPRING_DATASOURCE_URL=$(echo "$SPRING_DATASOURCE_URL" | sed 's/^postgresql:\/\//jdbc:postgresql:\/\//')
    echo "SPRING_DATASOURCE_URL updated (if applicable)."
fi

echo "Starting Java application..."
exec java -jar app.jar
