.PHONY: build run stop rm clean

build:
	@echo "빌드 중... (ai_lecture_frontend)"
	docker build -t ai_lecture_frontend -f Dockerfile.dev .

run:
	@echo "기존에 'front' 컨테이너가 있으면 삭제합니다."
	-@docker rm -f front 2>/dev/null || true
	@echo "컨테이너 실행 중... (포트 3000, 이름: front)"
	docker run --name front -p 3000:3000 ai_lecture_frontend

stop:
	@echo "컨테이너 중지 중... (front)"
	-@docker stop front

rm:
	@echo "컨테이너 삭제 중... (front)"
	-@docker rm front

clean: stop rm
	@echo "컨테이너 중지 및 삭제 완료!"