#!/bin/bash

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== DevOps Infrastructure Check ===${NC}\n"

# Check Docker
echo -e "${YELLOW}1. Checking Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker not found${NC}"
    exit 1
fi
if ! docker info &> /dev/null; then
    echo -e "${RED}❌ Docker daemon not running${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Docker is running${NC}\n"

# Check Docker Compose
echo -e "${YELLOW}2. Checking Docker Compose...${NC}"
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo -e "${RED}❌ Docker Compose not found${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Docker Compose available${NC}\n"

# Check if services are running
echo -e "${YELLOW}3. Checking running services...${NC}"

# Check PostgreSQL
if docker ps --format '{{.Names}}' | grep -q '^start-postgres$'; then
    echo -e "${GREEN}✅ PostgreSQL container is running${NC}"
    
    # Check PostgreSQL health
    if docker exec start-postgres pg_isready -U start -d start &> /dev/null; then
        echo -e "${GREEN}✅ PostgreSQL is healthy${NC}"
    else
        echo -e "${YELLOW}⚠️  PostgreSQL container running but not ready${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  PostgreSQL container not running${NC}"
    echo -e "   Start with: ${GREEN}docker-compose up -d postgres${NC}"
fi

# Check Application
if docker ps --format '{{.Names}}' | grep -q '^start-app$'; then
    echo -e "${GREEN}✅ Application container is running${NC}"
    
    # Check application health
    if curl -sf http://localhost:8080/actuator/health &> /dev/null; then
        echo -e "${GREEN}✅ Application is healthy${NC}"
        echo -e "   URL: ${GREEN}http://localhost:8080${NC}"
    else
        echo -e "${YELLOW}⚠️  Application container running but not responding${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Application container not running${NC}"
    echo -e "   Start with: ${GREEN}docker-compose up -d${NC}"
fi

# Check Observability stack
echo -e "\n${YELLOW}4. Checking Observability stack...${NC}"

if docker ps --format '{{.Names}}' | grep -q '^start-otel-collector$'; then
    echo -e "${GREEN}✅ OpenTelemetry Collector is running${NC}"
else
    echo -e "${YELLOW}⚠️  OpenTelemetry Collector not running${NC}"
    echo -e "   Start with: ${GREEN}docker-compose -f docker-compose.observability.yml up -d${NC}"
fi

if docker ps --format '{{.Names}}' | grep -q '^start-loki$'; then
    echo -e "${GREEN}✅ Loki is running${NC}"
else
    echo -e "${YELLOW}⚠️  Loki not running${NC}"
fi

if docker ps --format '{{.Names}}' | grep -q '^start-grafana$'; then
    echo -e "${GREEN}✅ Grafana is running${NC}"
    
    # Check Grafana health
    if curl -sf http://localhost:3000/api/health &> /dev/null; then
        echo -e "${GREEN}✅ Grafana is healthy${NC}"
        echo -e "   URL: ${GREEN}http://localhost:3000${NC} (admin/admin)"
    else
        echo -e "${YELLOW}⚠️  Grafana container running but not responding${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Grafana not running${NC}"
fi

# Check logs in Grafana
echo -e "\n${YELLOW}5. Checking logs in Loki...${NC}"
if docker ps --format '{{.Names}}' | grep -q '^start-loki$'; then
    # Wait a bit for logs to appear
    sleep 2
    
    # Query Loki for logs
    if curl -sf "http://localhost:3100/ready" &> /dev/null; then
        LOG_COUNT=$(curl -s -G "http://localhost:3100/loki/api/v1/query_range" \
            --data-urlencode 'query={service_name="start"}' \
            --data-urlencode 'limit=1' 2>/dev/null | jq -r '.data.result | length' 2>/dev/null || echo "0")
        
        if [ "$LOG_COUNT" -gt 0 ]; then
            echo -e "${GREEN}✅ Logs found in Loki${NC}"
            echo -e "   View in Grafana: ${GREEN}http://localhost:3000/explore${NC}"
        else
            echo -e "${YELLOW}⚠️  No logs found in Loki yet${NC}"
            echo -e "   Logs will appear after application starts"
        fi
    else
        echo -e "${YELLOW}⚠️  Loki not ready${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Loki not running${NC}"
fi

echo -e "\n${GREEN}=== Check Complete ===${NC}\n"

echo -e "${YELLOW}Quick Start Commands:${NC}"
echo -e "  ${GREEN}docker-compose up -d${NC}                    # Start app + PostgreSQL"
echo -e "  ${GREEN}docker-compose -f docker-compose.observability.yml up -d${NC}  # Start observability stack"
echo -e "  ${GREEN}docker-compose logs -f app${NC}             # View application logs"
echo -e "  ${GREEN}docker-compose ps${NC}                      # List running containers"

