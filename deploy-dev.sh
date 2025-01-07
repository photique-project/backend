#!/bin/bash

# 최초상태까지 고려해서 블루그린 배포 쉘 작성 후 배포진행
IS_GREEN_EXIST=$(grep -q "green-dev" "/home/ubuntu/nginx/conf.d/default.conf" && echo true || echo false)
IS_BLUE_EXIST=$(grep -q "blue-dev" "/home/ubuntu/nginx/conf.d/default.conf" && echo true || echo false)

# 최초상태이거나 green이 트래픽을 받고있을 때
# -z 는 문자열이 비어있을경우 true 반환하는 조건식
if { [ "$IS_GREEN_EXIST" = false ] && [ "$IS_BLUE_EXIST" = false ]; } || [ "$IS_BLUE_EXIST" = false ]; then
  echo "### BLUE ####"
  echo ">>> blue image를 pull합니다."
  sudo docker-compose -f docker-compose.backend.dev.yml pull blue-dev
  echo ">>> blue container를 up합니다."
  sudo docker-compose -f docker-compose.backend.dev.yml up -d blue-dev
  while true; do
    echo ">>> blue health check 중..."
    sleep 3
    REQUEST=$(curl -s http://127.0.0.1:8082/api/v1/health)
    if [ -n "$REQUEST" ]; then
      echo ">>> 🍃 health check success !"
      break
    fi
  done
  sleep 3
  echo ">>> nginx를 다시 실행 합니다."
  sudo cp /home/ubuntu/nginx.blue.dev.conf /home/ubuntu/nginx/conf.d/default.conf
  sudo docker exec -i nginx-dev nginx -s reload

# green이 트래픽을 받고 있을 때
else
  echo "### GREEN ####"
  echo ">>> green image를 pull합니다."
  sudo docker-compose -f docker-compose.backend.dev.yml pull green-dev
  echo ">>> green container를 up합니다."
  sudo docker-compose -f docker-compose.backend.dev.yml up -d green-dev
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
  echo ">>> nginx를 다시 실행 합니다."
  sudo cp /home/ubuntu/nginx.green.dev.conf /home/ubuntu/nginx/conf.d/default.conf
  sudo docker exec -i nginx-dev nginx -s reload
fi
