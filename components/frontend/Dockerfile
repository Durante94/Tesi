FROM node:16.18.1 as build 
WORKDIR /react-app
COPY package.json .
RUN npm install
COPY . .
RUN npm run build

FROM docker.io/nginxinc/nginx-unprivileged:1.21-alpine
USER 0
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=build /react-app/build .
# COPY nginx.conf /etc/nginx/conf.d/default.conf
USER 101
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]