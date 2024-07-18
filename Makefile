SHELL = /bin/bash
.ONESHELL:
.DEFAULT_GOAL: jar.build

jar.build:
	@sh gradlew build

native.build:
	@native-image \
     	-jar build/libs/rinha-2024-all.jar

native.start:
	@./rinha-2024-all

docker.stats:
	@docker stats --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}"

docker.up:
	@docker compose down
	@docker compose up
