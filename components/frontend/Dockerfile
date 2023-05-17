FROM node:16.18.1 as build 
WORKDIR /react-app
COPY package.json .
RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000