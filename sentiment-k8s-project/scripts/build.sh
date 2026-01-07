#!/usr/bin/env bash
set -euo pipefail

IMAGE_NAME="sentiment-api:1.0"

docker build -t "$IMAGE_NAME" .