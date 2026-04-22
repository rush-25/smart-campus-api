#!/usr/bin/env bash
# =============================================================
#  Smart Campus API — Sample curl Test Commands
#  Run:  chmod +x test-api.sh && ./test-api.sh
#  Or copy individual commands and run them manually.
# =============================================================

BASE="http://localhost:8080/api/v1"
DIVIDER="──────────────────────────────────────────────────────"

pp() {
  # Pretty-print JSON if python3 is available, else raw output
  command -v python3 &>/dev/null && python3 -m json.tool || cat
}

echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║     Smart Campus API — Integration Test Script       ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

# ──────────────────────────────────────────────────────────────
echo "1. DISCOVERY — GET /api/v1"
echo $DIVIDER
curl -s -X GET "$BASE" \
  -H "Accept: application/json" | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "2. LIST ALL ROOMS — GET /rooms"
echo $DIVIDER
curl -s -X GET "$BASE/rooms" \
  -H "Accept: application/json" | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "3. GET SINGLE ROOM — GET /rooms/1"
echo $DIVIDER
curl -s -X GET "$BASE/rooms/1" \
  -H "Accept: application/json" | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "4. CREATE ROOM — POST /rooms"
echo $DIVIDER
curl -s -X POST "$BASE/rooms" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name":     "Library Study Pod D10",
    "location": "Building D, Floor 1",
    "capacity": 8
  }' | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "5. GET NON-EXISTENT ROOM — GET /rooms/9999  (expect 404)"
echo $DIVIDER
curl -s -X GET "$BASE/rooms/9999" \
  -H "Accept: application/json" | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "6. LIST ALL SENSORS — GET /sensors"
echo $DIVIDER
curl -s -X GET "$BASE/sensors" \
  -H "Accept: application/json" | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "7. FILTER SENSORS BY TYPE — GET /sensors?type=CO2"
echo $DIVIDER
curl -s -X GET "$BASE/sensors?type=CO2" \
  -H "Accept: application/json" | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "8. CREATE SENSOR — POST /sensors"
echo $DIVIDER
curl -s -X POST "$BASE/sensors" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "type":   "Motion",
    "unit":   "boolean",
    "roomId": 1
  }' | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "9. CREATE SENSOR WITH INVALID ROOM — POST /sensors  (expect 422)"
echo $DIVIDER
curl -s -X POST "$BASE/sensors" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "type":   "Smoke",
    "unit":   "ppm",
    "roomId": 9999
  }' | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "10. GET READINGS FOR SENSOR 1 — GET /sensors/1/readings"
echo $DIVIDER
curl -s -X GET "$BASE/sensors/1/readings" \
  -H "Accept: application/json" | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "11. ADD READING TO SENSOR 1 — POST /sensors/1/readings"
echo $DIVIDER
curl -s -X POST "$BASE/sensors/1/readings" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "value": 512.3
  }' | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "12. POST READING TO INACTIVE SENSOR 4 — (expect 403)"
echo $DIVIDER
curl -s -X POST "$BASE/sensors/4/readings" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "value": 700.0
  }' | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "13. DELETE ROOM WITH SENSORS — DELETE /rooms/1  (expect 409)"
echo $DIVIDER
curl -s -X DELETE "$BASE/rooms/1" \
  -H "Accept: application/json" | pp
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "14. DELETE EMPTY ROOM — DELETE /rooms/3  (expect 204)"
echo $DIVIDER
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" \
  -X DELETE "$BASE/rooms/3"
echo -e "\n"

# ──────────────────────────────────────────────────────────────
echo "15. WRONG CONTENT TYPE — POST /rooms with text/plain  (expect 415)"
echo $DIVIDER
curl -s -X POST "$BASE/rooms" \
  -H "Content-Type: text/plain" \
  -d "name=TestRoom" | pp
echo -e "\n"

echo "✅ All test commands completed."
