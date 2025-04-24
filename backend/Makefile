.PHONY: build run stop rm clean

build:
	@echo "빌드 중... (ai_lecture_backend)"
	docker build -t ai_lecture_backend .

run:
	@echo "기존에 'back' 컨테이너가 있으면 삭제합니다."
	-@docker rm -f back 2>/dev/null || true
	@echo "컨테이너 실행 중... (포트 8080, 이름: back)"
	docker run --name back -p 8080:8080 ai_lecture_backend

stop:
	@echo "컨테이너 중지 중... (back)"
	-@docker stop back

rm:
	@echo "컨테이너 삭제 중... (back)"
	-@docker rm back

clean: stop rm
	@echo "컨테이너 중지 및 삭제 완료!"