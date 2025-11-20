# Frontend (BoardBox demo)

Dev:
npm install
npm run dev

Build & test preview:
npm run build
npm run preview

Docker:
docker build -t boardbox_frontend:local .

# run with infra up, nginx proxies to services

docker run -p 80:80 --network <your_compose_network> boardbox_frontend:local
