#!/bin/bash

IS_GREEN_EXIST=$(grep -q "green-prod" "/home/photique0538/nginx/conf.d/default.conf" && echo true || echo false)
IS_BLUE_EXIST=$(grep -q "blue-prod" "/home/photique0538/nginx/conf.d/default.conf" && echo true || echo false)

# 최초상태이거나 green이 트래픽 받고있을 때
# -z 는 문자열이 비어있을경우 true 반환하는 조건식
if { [ "$IS_GREEN_EXIST" = false ] && [ "$IS_BLUE_EXIST" = false ]; } || [ "$IS_BLUE_EXIST" = false ]; then
  echo "### BLUE ####"
  if [ "$(docker ps -q -f name="blue-prod")" ]; then
      echo ">>> blue 컨테이너 종료 중..."
      docker stop "blue-prod"
      echo ">>> blue 컨테이너 삭제 중..."
      docker rm "blue-prod"
      echo ">>> blue 이미지 삭제 중..."
      docker rmi "photique/backend-blue-prod:0.1.0"
  fi

  echo ">>> blue image를 pull합니다."
  docker-compose -f /home/photique0538/spring/docker-compose.backend.prod.yml pull blue-prod
  echo ">>> blue container를 up합니다."
  docker-compose -f /home/photique0538/spring/docker-compose.backend.prod.yml up -d blue-prod
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
  sudo cp /home/photique0538/nginx/nginx.blue.prod.conf /home/photique0538/nginx/conf.d/default.conf
  docker exec -i nginx-prod nginx -s reload

# blue가 트래픽 받고 있을 때
else
  echo "### GREEN ####"
  if [ "$(sudo docker ps -q -f name="green-prod")" ]; then
        echo ">>> green 컨테이너 종료 중..."
        docker stop "green-prod"
        echo ">>> green 컨테이너 삭제 중..."
        docker rm "green-prod"
        echo ">>> green 이미지 삭제 중..."
        docker rmi "photique/backend-green-prod:0.1.0"
  fi
  echo ">>> green image를 pull합니다."
  docker-compose -f /home/photique0538/spring/docker-compose.backend.prod.yml pull green-prod
  echo ">>> green container를 up합니다."
  docker-compose -f /home/photique0538/spring/docker-compose.backend.prod.yml up -d green-prod
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
  sudo cp /home/photique0538/nginx/nginx.green.prod.conf /home/photique0538/nginx/conf.d/default.conf
  docker exec -i nginx-prod nginx -s reload
fi
