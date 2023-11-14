#This script was tested on WSL2
#Version: 5.10.16.3-microsoft-standard-WSL2
##############################################
#Instructions for script:
#1.Make sure script is placed in RISCV-Core/ Folder
#2.Make sure python is installed
#3.Use command 'python binarytest.py' to run the script
#4.Choose either .dasm(compiled disassembly file) file or hexfile directly.  (Make sure the ending of the file is .dasm, change if necessary)
#5.In some cases datafile will also be required if the .dasm has .data section
#in this case one can generate data dump file using the following command:
#"riscv32-unknown-elf-objdump -s -j .data *nameofassemblyfile* > data.dump"
#6.Upon selection of required options the test unit will be created as "test_tb.scala"
#and the "sbt test" command will be initialized
#P.S. to increase java heap size use the following command: java -Xms<initial heap size> -Xmx<maximum heap size>

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


#function to convert data dump into data file needed for the prototype
#input: path to the .dump file
def dumptodata(filepath):
   allitems = []
   i = True
   addr=0
   j = False
   tempitems = []
   with open(filepath, 'r') as file:           
       for line in file:
           if j:
               tempitems.append(line)
           if line.endswith('.data:\n'):
               j = True

   if j:                    
    with open(filepath, 'w') as file:   
        for item in tempitems:
                file.write(item)


   with open(filepath, 'r') as file:
    for line in file:
        word_list = line.split()
        if i == True:
            addr = int(word_list[0],16) - 4      #take the address of the first input data location
            i = False
        word_list.pop(0)
        if len(word_list) > 4:
            word_list.pop(4)
        if len(word_list) > 4:
           word_list.pop(4)
        for j in range(0,len(word_list)):
            s = word_list[j]
            word_list[j] = "".join(map(str.__add__, s[-2::-2] ,s[-1::-2]))
            allitems.append(word_list[j])
        print(word_list)
   directory_path = os.getcwd()
   file_path = directory_path + "/src/main/scala/DataMemory/"
   with open(file_path + 'datadump', 'w') as file:
            for i in range(0, addr+4, 4):
                file.write("00000000" + "\n")
            for item in allitems:
                file.write(item + '\n')

#function to convert .dasm file into hex
#input: path to the .dasm file
def dasmtohex(filepath):
	addresses = []
	dump_val = []
	dump = []
	miss_alligned_val = []
	addr = 0x0
	addresses_log = []
	val_log = []

	t = 0
	hit_three_dots = 0

	writebool = 0
	with open(filepath, "r") as f:
	    lines = f.readlines()

	with open(filepath, "w") as f:
		for line in lines:
			#if line.find("00000000 <.text>:")!= -1:
			if line.find(".text")!= -1:
				writebool = 1
			if writebool:
				f.write(line)

	with open(filepath, 'r') as file:  # adjust the input file name/path
		for line in file:
			if line.find(".data:") != -1:
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
                            # for i in range (472,8188,4):
                            #     file.write("00000013" + "\n")
							# alligned long instruction just commit
                            
							split_id = 1
                            # if (inst_address - addr > 4):
                            #     for i in range (addr,inst_address,4):
                            #         addresses.append(hex(addr))
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

	fp = open("~/RISCV-Core/src/test/programs/hexfile", 'w')

	for x in dump_val:
		fp.write(x)
		fp.write("\n")

#helper function for printing menu
def printmenu():
    print("Select an option:")
    print("1. Run test using .dasm file")
    print("2. Run test using hex file")
    print("3. Exit program")

#function to create the test_tb.scala file and run the command "sbt test"
def sbttest(arg1, arg2 = ""):
    #create file
    os.chdir(initial_script_directory)
    file_path = path.relpath("src/test/scala/test_tb.scala")
    if arg2 == "":
        with open(file_path, 'w') as fp:
            fp.writelines(filetext)
            location = 'src/test/programs/' + arg1
            txt = '     test(new RISCV_TOP("' + location + '")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>\n'
            filemoretext = [txt,"      dut.clock.setTimeout(0)\n", "      for(i <- 0 until 50000){\n", "        dut.clock.step()\n", "      }\n",
                            "     }\n", "   }\n", "}\n"]
            fp.writelines(filemoretext)
            fp.close()
    else:
        with open(file_path, 'w') as fp:
            fp.writelines(filetext)
            location = 'src/test/programs/' + arg1
            datalocation = 'src/main/scala/DataMemory/' + arg2
            txt = '     test(new RISCV_TOP("' + location + '","' + datalocation + '")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>\n'
            filemoretext = [txt,"      dut.clock.setTimeout(0)\n", "      for(i <- 0 until 50000){\n", "        dut.clock.step()\n", "      }\n",
                            "     }\n", "   }\n", "}\n"]
            fp.writelines(filemoretext)
            fp.close()

    # os.system("sbt run")
    print("Running sbt test...")
    os.system("sbt test")
    #os.remove(file_path)

#helper function for menu
def get_digits_between_1_and_3():
    printmenu() 
    user_input = input("Enter a number between 1 and 3: ")
    while not (user_input.isdigit() and 1 <= int(user_input) <= 3):
        print("\n\n\nInvalid input. Please enter a digit between 1 and 3!")
        printmenu() 
        user_input = input("Enter your choice: ")
    return int(user_input)

#helper function for menu
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

#helper function for menu
def print_files_and_folders_in_current_directory_all():
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
            curr_dir.append(item + "/")
            i+=1
        elif os.path.isfile(item_path):
            print(f"{i})", item)
            curr_dir.append(item)
            i+=1
    print("X) Exit program")
    print("-----------------")
    return curr_dir

#helper function for menu
def print_files_and_folders_in_current_directory_dump():
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
        elif os.path.isfile(item_path) and item.endswith(".dump"):
            print(f"{i})", item)
            curr_dir.append(item)
            i+=1
    print("X) Exit program")
    print("-----------------")
    return curr_dir

#helper function for menu
def check_and_create_file(file_path):
    if not os.path.exists(file_path):
        pass
    else:
       with open(file_path, 'r') as file:
        line = file.readline()
        os.chdir(line)

#helper function for selection of .dasm file
def dasmfunction():
    user_input = ""
    directory_path = os.getcwd()
    curr_dir = []
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


#helper function for selection of hex file
def hexfunction():
    user_input = ""
    directory_path = os.getcwd()
    file_path = directory_path + "/hexfilepath.txt"
    curr_dir = []
    check_and_create_file(file_path)
    while user_input != "x":
        curr_dir = print_files_and_folders_in_current_directory_all()
        directory_path = os.getcwd()
        user_input = input("Please select a HEX file or go into a folder by entering respective digit: ")
        if user_input.lower() == 'x':
            return -1
        else:
            if curr_dir[int(user_input)-1].find(".") == -1 and not curr_dir[int(user_input)-1].endswith("/"):
                break
            else:
                os.chdir(curr_dir[int(user_input)-1])

    file_namelocation = directory_path + "/" + curr_dir[int(user_input)-1]

    with open(file_path, 'w') as file:
        file.write(directory_path)

    return curr_dir[int(user_input)-1]


#helper function for selection of data .dump file
def datafunction():
    user_input = ""
    directory_path = os.getcwd()
    curr_dir = []
    file_path = directory_path + "/datafilepath.txt"
    check_and_create_file(file_path)
    while not user_input.endswith(".dump"):
        curr_dir = print_files_and_folders_in_current_directory_dump()
        directory_path = os.getcwd()

        user_input = input("Please select a .dump file or go into a folder by entering respective digit: ")
        
        if user_input.lower() == 'x':
            return -1
        else:
            if curr_dir[int(user_input)-1].endswith(".dump"):
                break
            else:
                os.chdir(curr_dir[int(user_input)-1])

    file_namelocation = directory_path + "/" + curr_dir[int(user_input)-1]

    with open(file_path, 'w') as file:
        file.write(directory_path)
	
    dumptodata(file_namelocation)
    print("Data dump generated successfully!\n")

    return "datadump"



def main():
    number = None
    while number!=3:
        number = get_digits_between_1_and_3()
        if number == 1:
            filelocation = dasmfunction()
            if(filelocation == -1):
                print("Error has occured!\n")
                break
            if((input("Would you like to select a data file? (y/n)\n")).lower() == "y"):
                datalocation = datafunction()
                sbttest('hexfile','datadump')
            else:
                sbttest('hexfile')
            break
        elif number == 2:
            sbttest(hexfunction())
            break
        else:
            pass
	
    print("Exiting program.")
    
if __name__ == '__main__':
   main()
