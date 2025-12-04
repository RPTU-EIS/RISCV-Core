import sys

binfile = sys.argv[1]

with open(binfile, "rb") as f:
    while True:
        # Peek at 2 bytes
        first_two = f.read(2)
        if len(first_two) < 2:
            break

        first_byte = first_two[0]

        if first_byte & 0b11 != 0b11:
            # Compressed (16-bit) instruction
            val = int.from_bytes(first_two, "little")
            print(f"{val:08x}")
        else:
            # Standard (32-bit) instruction
            next_two = f.read(2)
            if len(next_two) < 2:
                break  # incomplete instruction
            full_instr = first_two + next_two
            val = int.from_bytes(full_instr, "little")
            print(f"{val:08x}")
