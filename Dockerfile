FROM node:lts-alpine3.10

RUN apk add --update \
  nginx

COPY nginx.conf /etc/nginx/conf.d/default.conf

RUN npm install -g @angular/cli

RUN mkdir -p /tmp/build; \
  mkdir -p /run/nginx;

WORKDIR /tmp/build

COPY . .

RUN npm install && \
  npm run build && \
  cp -r ./dist/* /var/www/;\
  chown -R nginx:nginx /var/www/ ;\
  chown -R nginx:nginx /var/lib/nginx/ ;\
  chown -R nginx:nginx /var/log/nginx/ ;\
  chown -R nginx:nginx /run/nginx ;\
  chmod -R 755 /var/www/ ;\
  chmod -R 755 /var/lib/nginx/ ;\
  chmod -R 755 /var/log/nginx/ ;\
  chmod -R 755 /run/nginx

RUN rm -rf /tmp/build

WORKDIR /var/www/

EXPOSE 80/tcp

ENTRYPOINT ["/usr/sbin/nginx","-g","daemon off;"]
