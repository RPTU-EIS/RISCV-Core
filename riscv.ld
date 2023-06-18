OUTPUT_FORMAT("elf32-littleriscv", "elf32-littleriscv", "elf32-littleriscv")
OUTPUT_ARCH(riscv)
ENTRY(startup)
MEMORY {
	rom (rx) : ORIGIN = 0x00000000, LENGTH = 16K
	ram (!rx) : ORIGIN = 0x10000000, LENGTH = 16K /*Trick the linker script into thinkin we have contiguous memory address space*/
}												  /*We consider only the least 12 significant bits for addressing the data memory*/

SECTIONS {
	.text_entry : {
		. = 0x00000000;
		*(.text_entry);
	} > rom

	.text : {
		*(.text);
		. = ALIGN(4);
	} > rom
	.rodata : {
		*(.rodata);
		. = ALIGN(4);
	} > rom
	.bss : {
		_bss_start = .;
		*(.bss);
		_bss_end = .;
		. = ALIGN(4);
	} > ram
	_bss_size = _bss_end - _bss_start;
	.sbss : {
		_sbss_start = .;
		*(.sbss);
		_sbss_end = .;
		. = ALIGN(4);
	} > ram
	_sbss_size = _sbss_end - _sbss_start;
	.data : {
		_data_start = .;
		_data_load = LOADADDR(.data);
		*(.data);
		_data_end = .;
		. = ALIGN(4);
	} > ram AT > rom
	_data_size = _data_end - _data_start;
	.sdata : {
		_sdata_start = .;
		_sdata_load = LOADADDR(.sdata);
		*(.sdata);
		_sdata_end = .;
		. = ALIGN(4);
	} > ram AT > rom
	_sdata_size = _sdata_end - _sdata_start;
}
