#!/bin/bash

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== Testing Observability Stack ===${NC}\n"

# Check if services are running
echo -e "${YELLOW}1. Checking services...${NC}"

SERVICES_OK=true

if ! docker ps --format '{{.Names}}' | grep -q '^start-app$'; then
    echo -e "${RED}❌ Application not running${NC}"
    echo -e "   Start with: ${GREEN}docker-compose up -d${NC}"
    SERVICES_OK=false
fi

if ! docker ps --format '{{.Names}}' | grep -q '^start-otel-collector$'; then
    echo -e "${RED}❌ OpenTelemetry Collector not running${NC}"
    echo -e "   Start with: ${GREEN}docker-compose -f docker-compose.observability.yml up -d${NC}"
    SERVICES_OK=false
fi

if ! docker ps --format '{{.Names}}' | grep -q '^start-loki$'; then
    echo -e "${RED}❌ Loki not running${NC}"
    SERVICES_OK=false
fi

if ! docker ps --format '{{.Names}}' | grep -q '^start-grafana$'; then
    echo -e "${RED}❌ Grafana not running${NC}"
    SERVICES_OK=false
fi

if [ "$SERVICES_OK" = false ]; then
    exit 1
fi

echo -e "${GREEN}✅ All services running${NC}\n"

# Test OpenTelemetry Collector
echo -e "${YELLOW}2. Testing OpenTelemetry Collector...${NC}"
if curl -sf http://localhost:4318/v1/logs &> /dev/null || curl -sf http://localhost:8888/metrics &> /dev/null; then
    echo -e "${GREEN}✅ OpenTelemetry Collector is accessible${NC}"
else
    echo -e "${YELLOW}⚠️  OpenTelemetry Collector endpoint check failed (may be normal)${NC}"
fi

# Test Loki
echo -e "\n${YELLOW}3. Testing Loki...${NC}"
if curl -sf http://localhost:3100/ready &> /dev/null; then
    echo -e "${GREEN}✅ Loki is ready${NC}"
    
    # Query for logs
    echo -e "${YELLOW}   Querying logs...${NC}"
    RESPONSE=$(curl -s -G "http://localhost:3100/loki/api/v1/query_range" \
        --data-urlencode 'query={service_name="start"}' \
        --data-urlencode 'limit=5' \
        --data-urlencode 'start='$(date -u -d '5 minutes ago' +%s)000000000 \
        --data-urlencode 'end='$(date -u +%s)000000000 2>/dev/null || echo '{"data":{"result":[]}}')
    
    LOG_COUNT=$(echo "$RESPONSE" | jq -r '.data.result | length' 2>/dev/null || echo "0")
    
    if [ "$LOG_COUNT" -gt 0 ]; then
        echo -e "${GREEN}✅ Found $LOG_COUNT log stream(s) in Loki${NC}"
        echo -e "${BLUE}   Sample log entries:${NC}"
        echo "$RESPONSE" | jq -r '.data.result[0].values[0:3][] | "   " + .[1]' 2>/dev/null || echo "   (unable to parse)"
    else
        echo -e "${YELLOW}⚠️  No logs found yet${NC}"
        echo -e "   Logs will appear after application generates them"
    fi
else
    echo -e "${RED}❌ Loki is not ready${NC}"
fi

# Test Grafana
echo -e "\n${YELLOW}4. Testing Grafana...${NC}"
if curl -sf http://localhost:3000/api/health &> /dev/null; then
    echo -e "${GREEN}✅ Grafana is healthy${NC}"
    echo -e "${BLUE}   URL: http://localhost:3000${NC}"
    echo -e "${BLUE}   Username: admin${NC}"
    echo -e "${BLUE}   Password: admin${NC}"
    
    # Check if Loki datasource is configured
    echo -e "${YELLOW}   Checking Loki datasource...${NC}"
    if curl -sf -u admin:admin http://localhost:3000/api/datasources/name/Loki &> /dev/null; then
        echo -e "${GREEN}✅ Loki datasource configured${NC}"
    else
        echo -e "${YELLOW}⚠️  Loki datasource may not be configured${NC}"
    fi
else
    echo -e "${RED}❌ Grafana is not healthy${NC}"
fi

# Generate test log
echo -e "\n${YELLOW}5. Generating test log entry...${NC}"
if curl -sf http://localhost:8080/actuator/health &> /dev/null; then
    echo -e "${GREEN}✅ Application is responding${NC}"
    echo -e "${BLUE}   Log entry should appear in Loki within a few seconds${NC}"
    echo -e "${BLUE}   Check Grafana: http://localhost:3000/explore${NC}"
    echo -e "${BLUE}   Query: {service_name=\"start\"}${NC}"
else
    echo -e "${YELLOW}⚠️  Application not responding${NC}"
fi

echo -e "\n${GREEN}=== Test Complete ===${NC}\n"
echo -e "${YELLOW}Next steps:${NC}"
echo -e "  1. Open Grafana: ${GREEN}http://localhost:3000${NC}"
echo -e "  2. Go to Explore (compass icon)"
echo -e "  3. Select Loki datasource"
echo -e "  4. Query: ${GREEN}{service_name=\"start\"}${NC}"
echo -e "  5. Click 'Run query'"

