# Nginx 기반 이미지 사용
FROM nginx:alpine

# 기존 default.conf 삭제 (optional)
RUN rm /etc/nginx/conf.d/default.conf

# 정적 파일 복사
COPY . /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
