#!/bin/bash

IS_GREEN_EXIST=$(grep -q "green-dev" "/home/ubuntu/nginx/conf.d/default.conf" && echo true || echo false)
IS_BLUE_EXIST=$(grep -q "blue-dev" "/home/ubuntu/nginx/conf.d/default.conf" && echo true || echo false)

# 최초상태이거나 green이 트래픽 받고있을 때
# -z 는 문자열이 비어있을경우 true 반환하는 조건식
if { [ "$IS_GREEN_EXIST" = false ] && [ "$IS_BLUE_EXIST" = false ]; } || [ "$IS_BLUE_EXIST" = false ]; then
  echo "### BLUE ####"
  if [ "$(sudo docker ps -q -f name="blue-dev")" ]; then
      echo ">>> blue 컨테이너 종료 중..."
      sudo sudo docker stop "blue-dev"
      echo ">>> blue 컨테이너 삭제 중..."
      sudo sudo docker rm "blue-dev"
      echo ">>> blue 이미지 삭제 중..."
      sudo sudo docker rmi "photique/backend-blue-dev:0.1.0"
  fi

  echo ">>> blue image를 pull합니다."
  sudo docker-compose -f docker-compose.backend.dev.yml pull blue-dev
  echo ">>> blue container를 up합니다."
  sudo docker-compose -f docker-compose.backend.dev.yml up -d blue-dev
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
  echo ">>> nginx를 다시 실행 합니다."
  sudo cp /home/ubuntu/nginx.blue.dev.conf /home/ubuntu/nginx/conf.d/default.conf
  sudo docker exec -i nginx-dev nginx -s reload

# blue가 트래픽 받고 있을 때
else
  echo "### GREEN ####"
  if [ "$(sudo docker ps -q -f name="green-dev")" ]; then
        echo ">>> green 컨테이너 종료 중..."
        sudo sudo docker stop "green-dev"
        echo ">>> green 컨테이너 삭제 중..."
        sudo sudo docker rm "green-dev"
        echo ">>> green 이미지 삭제 중..."
        sudo sudo docker rmi "photique/backend-green-dev:0.1.0"
  fi
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
