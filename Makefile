.PHONY: help run test test-one db-up db-down clean

APP_NAME := acebook
MAVEN := mvn
SPRING_PROFILES := dev
TEST_PROFILE := test
DEV_DB := acebook_springboot_development
TEST_DB := acebook_springboot_test

help:
	@echo "Available targets:"
	@echo "  make run                        Start the app in dev mode"
	@echo "  make run-test                   Start the app in test mode"
	@echo "  make test                       Run the full Maven test suite"
	@echo "  make test-one TEST=SignUpTest   Run one specific test class"
	@echo "  make db-up                      Create local Postgres dev/test databases"
	@echo "  make db-down                    Drop local Postgres databases"
	@echo "  make clean                      Clean Maven build artifacts"

run:
	$(MAVEN) spring-boot:run

run-test:
	$(MAVEN) spring-boot:run -Dspring-boot.run.profiles=$(TEST_PROFILE)

test:
	$(MAVEN) test

test-one:
	$(MAVEN) test -Dtest=$(TEST)

db-up:
	createdb ${DEV_DB} 2>/dev/null || true
	createdb ${TEST_DB} 2>/dev/null || true

db-down:
	dropdb ${DEV_DB} 2>/dev/null || true
	dropdb ${TEST_DB} 2>/dev/null || true

clean:
	$(MAVEN) clean
