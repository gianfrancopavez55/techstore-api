FROM node:20-alpine

WORKDIR /app

COPY package.json package.json

RUN npm install --only=production

COPY index.js index.js

CMD ["npm", "start"]
