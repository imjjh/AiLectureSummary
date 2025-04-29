# --- 빌드 스테이지 ---
FROM node:18-alpine AS builder

WORKDIR /app

COPY . .

RUN npm install && npm run build


# --- 운영 스테이지 ---
FROM node:18-alpine

WORKDIR /app

COPY --from=builder /app ./

EXPOSE 3000

CMD ["npm", "run", "start"]