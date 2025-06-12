# Nginx 기반 이미지 사용
FROM nginx:alpine

# 기존 default.conf 삭제 (optional)
RUN rm /etc/nginx/conf.d/default.conf

# 🔥 정적 파일과 nginx 설정 복사 (이게 있어야 nginx가 index.html을 서빙함)
COPY default.conf /etc/nginx/conf.d/default.conf
COPY . /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
