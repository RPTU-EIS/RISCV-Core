ISA = rv32im
#PREFIX = /opt/gcc-$(ISA)-epl/bin/riscv32-unknown-elf-
PREFIX = /opt/riscv/bin/riscv32-unknown-linux-gnu-

LD = $(PREFIX)ld
CC = $(PREFIX)gcc
OBJCOPY = $(PREFIX)objcopy
OBJDUMP = $(PREFIX)objdump

CFLAGS = -O1 -Wall -march=$(ISA) -mabi=ilp32 -Wa,-adghlmns=$(@:.o=.s) -std=c99 -mpreferred-stack-boundary=4 -nostartfiles -ffreestanding
LDFLAGS = -Wl,-Bstatic,-A,$(ISA),-Map,$(TARGET).map,--gc-sections,-lgcc,-static,--print-memory-usage

SDIR = .
SRC = $(wildcard $(SDIR)/*.c)
DEP = $(SRC:.c=.d)
OBJ = $(SRC:.c=.o)
TARGET = main
LINKER_SCR = riscv.ld

all: $(TARGET).out
#vscode-remote://wsl%2Bubuntu/home/tsotne/riscv-tests/benchmarks/multiply.riscv.dasm
$(TARGET).out: $(DEP) $(OBJ) $(LINKER_SCR)
	$(CC) $(CFLAGS) $(OBJ) -o $(TARGET).elf -T $(LINKER_SCR) $(LDFLAGS) 
	$(OBJDUMP) --disassemble-all -Mno-aliases $(TARGET).elf	> main.dasm
	$(OBJDUMP) -t $(TARGET).elf > $(TARGET).elfdump
	$(OBJCOPY) -O verilog $(TARGET).elf $(TARGET).out


-include $(DEP)

%.d: %.c
	$(CC) $(CFLAGS) -c -MF"$@" -MG -MM $<

.PHONY sim: all 
	 
	cd ../modelsim/; vsim -do modelsim.tcl

.PHONY: clean
clean:
	@rm -f $(OBJ) $(DEP) $(SRC:.c=.s) $(TARGET).elf $(TARGET).map $(TARGET).out
