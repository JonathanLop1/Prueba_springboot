#!/bin/sh

# Fix Render PostgreSQL URL (postgres:// -> jdbc:postgresql://)
if echo "$SPRING_DATASOURCE_URL" | grep -q "^postgres://"; then
  export SPRING_DATASOURCE_URL=$(echo "$SPRING_DATASOURCE_URL" | sed 's/^postgres:\/\//jdbc:postgresql:\/\//')
  echo "Fixed SPRING_DATASOURCE_URL for JDBC"
fi

exec java -jar app.jar
