#!/bin/bash

echo "Build Docker image..."
docker build -t tradesimulator .

echo "Start TradeSimulator..."
docker run -p 8080:8080 tradesimulator

echo "Application running at http://localhost:8080"