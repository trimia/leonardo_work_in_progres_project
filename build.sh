OS=$(uname)

npm install --prefix ./front/eventify/
npm run dev --prefix ./front/eventify/ &

cp compose.yaml back/eventify/compose.yaml
cd back/eventify
mvn spring-boot:run &

if [ "$OS" = "Darwin" ]; then
	open http://localhost:3000
else
	xdg-open http://localhost:3000
fi
