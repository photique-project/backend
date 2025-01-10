#!/bin/bash

IS_GREEN_EXIST=$(grep -q "green-dev" "/home/ubuntu/nginx/conf.d/default.conf" && echo true || echo false)
IS_BLUE_EXIST=$(grep -q "blue-dev" "/home/ubuntu/nginx/conf.d/default.conf" && echo true || echo false)

# green이 트래픽 받고있을 때 => blue로 롤백
if [ "$IS_GREEN_EXIST" = true ]; then
  echo "### GREEN => BLUE ####"

  while true; do
    echo ">>> blue health check 중..."
    sleep 3
    REQUEST=$(curl -s http://127.0.0.1:8081/api/v1/health)
    if [ -n "$REQUEST" ]; then
      echo ">>> 🍃 health check success !"
      break
    fi
  done
  sleep 3
  echo ">>> blue 컨테이너로 롤백"
  sudo cp /home/ubuntu/nginx.blue.dev.conf /home/ubuntu/nginx/conf.d/default.conf
  sudo docker exec -i nginx-dev nginx -s reload

# blue가 트래픽 받고 있을 때 => green으로 롤백
else
  echo "### BLUE => GREEN ####"

  while true; do
    echo ">>> green health check 중..."
    sleep 3
    REQUEST=$(curl -s http://127.0.0.1:8082/api/v1/health)
    if [ -n "$REQUEST" ]; then
      echo ">>> 🍃 health check success !"
      break
    fi
  done
  sleep 3
  echo ">>> green 컨테이너로 롤백"
  sudo cp /home/ubuntu/nginx.green.dev.conf /home/ubuntu/nginx/conf.d/default.conf
  sudo docker exec -i nginx-dev nginx -s reload
fi

# bash /home/ubuntu/rollback-dev.sh