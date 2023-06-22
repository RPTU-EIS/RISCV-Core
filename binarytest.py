import os
from os import path
import sys
import re
import string

filetext = ["package scala_Test_tb\n","import RISCV_TOP.RISCV_TOP\n","import chiseltest._\n",
            "import org.scalatest.flatspec.AnyFlatSpec\n","import top_MC._\n","import chisel3._\n",
            "import DataTypes.Data._\n","import java.sql.Driver\n\n",
            "class scala_Test_tb extends AnyFlatSpec with ChiselScalatestTester {\n\n",
            '   "Scala_test" should "pass" in {\n']

hex_values = set(string.digits + 'a' + 'b' + 'c' + 'd' + 'e' + 'f')

script_path = os.path.abspath(__file__)
initial_script_directory = os.path.dirname(script_path)

def isBadLine(line):
    return line[0] == 'b' or line[0] == 'D'


def isHex(instruction):
    return set(instruction) <= hex_values and instruction != 'add'

def dasmtohex(filepath):
	addresses = []
	dump_val = []
	dump = []
	miss_alligned_val = []
	addr = 0x80000000
	addresses_log = []
	val_log = []

	t = 0
	hit_three_dots = 0

	writebool = 0
	with open(filepath, "r") as f:
		lines = f.readlines()

	with open(filepath, "w") as f:
		for line in lines:
			if line.find("00000000 <startup>:") != -1:
				writebool = 1
			if writebool:
				f.write(line)

	with open(filepath, 'r') as file:  # adjust the input file name/path
		for line in file:
			if line.find(".comment:") != -1:
				break
			if not line.isspace() and not isBadLine(line):

				# three possibilities for first word in line: address of new section -> skip over; address of instruction -> insert; ... -> mark and fill gaps with 00000000

				# print(hex(addr))

				if (line.split()[0] == '...'):
					addr = addr
				else:  # (int(inst_address,16) == int(addr)):
					# alligned
					inst_address = int(line.split()[0].split(':')[0], 16)
					if (not line.split()[1][0] == '<'):  # address of new section -> skip over;
						# in case of ...
						if (addr < inst_address):
							while (addr < inst_address):
								addr = addr + 2  # increment address
								miss_alligned_val.append('0000')
								if (len(miss_alligned_val) == 2):
									dump_val.append(miss_alligned_val[1] + miss_alligned_val[0])
									miss_alligned_val.clear()
									addresses.append((hex(addr - 2)))
									dump.append(zip(hex(addr - 2), dump_val[-1]))
						if ((addr) % 4 == 0):

							# alligned long instruction just commit
							split_id = 1
							while (isHex(line.split()[split_id])):
								if (len(line.split()[split_id]) > 4):
									addresses.append(hex(addr))
									dump_val.append(line.split()[split_id])
									dump.append(zip(hex(addr - 2), dump_val[-1]))
									addr = addr + 4  # increment address
								else:  # first compressed instruction mybe also third
									miss_alligned_val.append(line.split()[split_id])
									addr = addr + 2
									if (len(miss_alligned_val) == 2):
										dump_val.append(miss_alligned_val[1] + miss_alligned_val[0])
										miss_alligned_val.clear()
										addresses.append((hex(addr)))
										dump.append(zip(hex(addr - 2), dump_val[-1]))

									# miss_alligned_val.append(line.split()[1][len(miss_alligned_val)*4:len(miss_alligned_val)*4+4])

								# misalligned
								split_id = split_id + 1
						else:
							split_id = 1
							while (isHex(line.split()[split_id])):
								if (len(line.split()[1]) > 4):
									addr = addr + 4  # increment address
									miss_alligned_val.append(
										line.split()[split_id][len(miss_alligned_val) * 4:len(miss_alligned_val) * 4 + 4])
									if (len(miss_alligned_val) == 2):
										dump_val.append(miss_alligned_val[1] + miss_alligned_val[0])
										miss_alligned_val.clear()
										addresses.append((hex(addr - 4)))
										dump.append(zip(hex(addr - 4), dump_val[-1]))
									miss_alligned_val.append(
										line.split()[split_id][len(miss_alligned_val) * 4:len(miss_alligned_val) * 4 + 4])
								else:  # first compressed instruction
									miss_alligned_val.append(line.split()[split_id])
									addr = addr + 2
								# misalligned
								if (len(miss_alligned_val) == 2):
									dump_val.append(miss_alligned_val[1] + miss_alligned_val[0])
									miss_alligned_val.clear()
									addresses.append((hex(addr - 4)))
									dump.append(zip(hex(addr - 4), dump_val[-1]))
								# misalligned
								split_id = split_id + 1

	fp = open("/home/tsotne/Hiwijob/RISCV-Core/src/test/programs/hexfile", 'w')
	for x in dump_val:
		fp.write(x)
		fp.write("\n")


def printmenu():
    print("Select an option:")
    print("1. Run test using .dasm file")
    print("2. Run test using hex file")
    print("3. Exit program")


def sbttest(arg1):
    #create file
    os.chdir(initial_script_directory)
    file_path = path.relpath("src/test/scala/test_tb.scala")
    with open(file_path, 'w') as fp:
        fp.writelines(filetext)
        location = 'src/test/programs/' + arg1
        txt = '     test(new RISCV_TOP("' + location + '")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>\n'
        filemoretext = [txt, "      for(i <- 0 until 200){\n", "        dut.clock.step()\n", "      }\n",
                        "     }\n", "   }\n", "}\n"]
        fp.writelines(filemoretext)
        fp.close()
    # os.system("sbt run")
    print("Running sbt test...")
    os.system("sbt test")
    #os.remove(file_path)


def get_digits_between_1_and_3():
    printmenu() 
    user_input = input("Enter a number between 1 and 3: ")
    while not (user_input.isdigit() and 1 <= int(user_input) <= 3):
        print("\n\n\nInvalid input. Please enter a digit between 1 and 3!")
        printmenu() 
        user_input = input("Enter your choice: ")
    return int(user_input)


def print_files_and_folders_in_current_directory():
    os.system('clear')
    print("Current Directory:")
    print("-----------------")
    current_directory = os.getcwd()
    i = 1
    print(f"{i}) ../")
    i += 1
    curr_dir = ['../']
    for item in os.listdir(current_directory):
        item_path = os.path.join(current_directory, item)
        if os.path.isdir(item_path):
            print(f"{i}) {item}/")
            curr_dir.append(item)
            i+=1
        elif os.path.isfile(item_path) and item.endswith(".dasm"):
            print(f"{i})", item)
            curr_dir.append(item)
            i+=1
    print("X) Exit program")
    print("-----------------")
    return curr_dir


def check_and_create_file(file_path):
    if not os.path.exists(file_path):
        with open(file_path, 'w'):
            pass
    else:
       with open(file_path, 'r') as file:
        line = file.readline()
        os.chdir(line)
        
def dasmfunction():
    user_input = ""
    directory_path = os.getcwd()
    file_path = directory_path + "/dasmfilepath.txt"
    check_and_create_file(file_path)
    while not user_input.endswith(".dasm"):
        curr_dir = print_files_and_folders_in_current_directory()
        directory_path = os.getcwd()

        user_input = input("Please select a .dasm file or go into a folder by entering respective digit: ")
        
        if user_input.lower() == 'x':
            return -1
        else:
            if curr_dir[int(user_input)-1].endswith(".dasm"):
                break
            else:
                os.chdir(curr_dir[int(user_input)-1])

    file_namelocation = directory_path + "/" + curr_dir[int(user_input)-1]

    with open(file_path, 'w') as file:
        file.write(directory_path)
	
    dasmtohex(file_namelocation)
    print("Hex file generated successfully!")
    return file_namelocation



def hexfunction():
    pass

def main():
    number = None
    while number!=3:
        number = get_digits_between_1_and_3()
        if number == 1:
            filelocation = dasmfunction()
            if(filelocation == -1):
                break
            sbttest('hexfile')
            break
        elif number == 2:
            hexfunction()
            break
        else:
            pass
	
    print("Exiting program")
    
if __name__ == '__main__':
   main()
