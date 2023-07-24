# Makefile

.PHONY: all clean

all:
	sbt run

test:
	sbt test

clean:
	rm -rf generated-src target project test_run_dir
