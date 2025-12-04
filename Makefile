# Makefile

#changed to automatically convert elf into hex and dump
#.PHONY: all clean

#all:
#	sbt run

#test:
#	sbt test

#clean:
#	rm -rf generated-src target project test_run_dir


.PHONY: all clean test run-binary-debug

#default values if not defined otherwise
RISCV := /import/public/Linux/riscv/RISCV64/bin/riscv64-unknown-elf-
BINARY ?= src/test/programs/beq_test
BINARY_NOEXT := $(strip $(basename $(BINARY)))


HEX := $(BINARY_NOEXT).hex
DUMP := $(BINARY_NOEXT).dump


HEX_BASENAME := $(notdir $(HEX))
MEM_FILE := src/main/resources/mem.hex



all:
	sbt run

test:
	sbt test

clean:
	rm -rf generated-src target project test_run_dir src/main/resources/*.hex *.hex *.dump *.bin



run-binary-debug:
	@echo "--------------------Change mem entry in IF.scala line 76--------------------"
	@echo "Convert ELF to binary"
	$(RISCV)objcopy -O binary $(BINARY) $(BINARY_NOEXT).bin

	@echo "Convert binary to pure hex (one 32-bit instruction per line)"
	python3 bin2hex.py $(BINARY_NOEXT).bin > $(HEX)
	$(RISCV)objdump -d $(BINARY) > $(DUMP)

	@echo "Load .hex into memory"
	@echo "Loaded $(HEX) into $(MEM_FILE)"
	cp $(HEX) $(MEM_FILE)
	
	@echo "Running main_tb, change in Makefile for other testbenches"
	sbt "testOnly main_tb.main_tb"
