# Makefile
.PHONY: all clean build run logs kill

# JAR 파일 이름
JAR_NAME = ii-0.0.1-SNAPSHOT.jar
BUILD_DIR = ./build/libs
NOHUP_FILE = nohup.out

# 애플리케이션 실행에 사용하는 포트 (예: 8080)
PORT = 8080

# 기본 타겟
all: build kill run logs

# Gradle clean build
build:
        @echo "Building the project with Gradle..."
        ./gradlew clean build

# 포트를 점유한 프로세스 종료
kill:
        @echo "Checking for processes on port $(PORT)..."
        @PID=$$(ss -ntpl | grep ":$(PORT)" | awk '{print $$6}' | awk -F',' '{print $$2}' | sed 's/pid=//'); \
        if [ -n "$$PID" ]; then \
                echo "Killing process $$PID on port $(PORT)..."; \
                kill -9 $$PID; \
        else \
                echo "No process found on port $(PORT)."; \
        fi

# 애플리케이션 실행
run:
        @echo "Running the application..."
        @if [ -f "$(BUILD_DIR)/$(NOHUP_FILE)" ]; then rm -f $(BUILD_DIR)/$(NOHUP_FILE); fi
        cd $(BUILD_DIR) && nohup java -jar $(JAR_NAME) &

# 로그 보기
logs:
        @echo "Tailing logs from nohup.out..."
        @while [ ! -f "$(BUILD_DIR)/$(NOHUP_FILE)" ]; do \
        echo "Waiting for nohup.out to be created..."; \
                sleep 1; \
        done
        tail -f $(BUILD_DIR)/$(NOHUP_FILE)

# Clean target
clean:
        @echo "Cleaning the project..."
        ./gradlew clean
