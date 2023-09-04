import os
from os import path
import sys
import re
import string
import array

input_data1 = [ 41, 454, 833, 335, 564,   1, 187, 989, 749, 365, 350, 572, 132,  64, 949, 153, 584, 216, 805, 140, 
                621, 210,   6, 572, 931, 339, 890, 593, 392, 898, 694, 228, 961,  12, 110, 883, 116, 750, 296, 646, 
                426, 500, 314, 436, 659, 701, 774, 812, 319, 981, 678, 150, 875, 696, 376, 564, 474, 272, 938, 258, 
                539, 647, 569, 509, 203,  88, 280, 703, 759, 669, 606, 375, 511, 551, 657, 936, 195, 592,  81, 569, 
                267, 952, 229, 800, 337, 584, 944, 643, 902, 368, 241, 489, 913, 328, 826, 313, 933, 592, 985, 388
]

input_data1hex = []
input_data2 = [ 195, 543, 960, 649, 566, 979, 350, 997, 649, 814, 657,  79, 181, 208, 111, 998, 859, 629,  65, 847, 
                288, 704, 349, 997, 141, 253, 905, 715, 886, 430, 264, 415, 576, 538, 979, 700, 761,   4, 241, 494, 
                478, 100, 499, 864, 403, 693, 222, 416, 444, 296, 721, 285, 676, 620, 317,  78, 224, 351, 937, 540, 
                288, 646, 119, 169, 615, 527, 606, 289, 389, 796, 351, 801, 455, 720, 278, 758, 367, 745, 358,  92, 
                584, 989,  62, 271, 985, 853, 403, 788, 346, 531, 517, 222, 559, 461, 908, 241, 775, 358, 255, 332
]
input_data2hex = []

verify_data = [ 7995, 246522, 799680, 217415, 319224, 979, 65450, 986033, 486101, 297110, 229950, 45188, 23892, 13312, 105339, 152694, 501656, 135864, 52325, 118580, 
                178848, 147840, 2094, 570284, 131271, 85767, 805450, 423995, 347312, 386140, 183216, 94620, 553536, 6456, 107690, 618100, 88276, 3000, 71336, 319124, 
                203628, 50000, 156686, 376704, 265577, 485793, 171828, 337792, 141636, 290376, 488838, 42750, 591500, 431520, 119192, 43992, 106176, 95472, 878906, 139320, 
                155232, 417962, 67711, 86021, 124845, 46376, 169680, 203167, 295251, 532524, 212706, 300375, 232505, 396720, 182646, 709488, 71565, 441040, 28998, 52348, 
                155928, 941528, 14198, 216800, 331945, 498152, 380432, 506684, 312092, 195408, 124597, 108558, 510367, 151208, 750008, 75433, 723075, 211936, 251175, 128816
]

verify_datahex = []

def checkvalues(allitems):
    inputdatabool = False
    j = 0
    for i in range(0,len(input_data1)):
        input_data1hex.append('%08X' % input_data1[i])
    for i in range(0,len(input_data1)):
        j = j + 1
        if allitems[i] != input_data1hex[i].lower():
            print(f"Error found at location {i}")
        else:
            inputdatabool = True
    if inputdatabool:
        print("No errors found in Input_data1")
    
    k = 0
    for i in range(0,len(input_data2)):
        input_data2hex.append('%08X' % input_data2[i])
    for i in range(j,len(input_data2)):
        k = k+1
        if allitems[j] != input_data2hex[i-j].lower():
            print(f"Error found at location {j}")
        else:
            inputdatabool = True
    if inputdatabool:
        print("No errors found in Input_data2")
    
    for i in range(0,len(verify_data)):
        verify_datahex.append('%08X' % verify_data[i])
    for i in range(k+j,len(verify_data)):
        if allitems[k+j] != verify_datahex[i-j-k].lower():
            print(f"Error found at location {k}")
        else:
            inputdatabool = True
    if inputdatabool:
        print("No errors found in verify_data")
            


def main():
   
   allitems = []
   i = True
   j = False
   tempitems = []
   with open('data63elements.dump', 'r') as file:           
       for line in file:
           if j:
               tempitems.append(line)
           if line.endswith('.data:\n'):
               j = True

   with open('data63elements.dump', 'w') as file:   
       for item in tempitems:
           file.write(item)
        
#    with open('data63elements.dump', 'r') as file:
#     for line in file:
#         word_list = line.split()
#         if i == True:
#             addr = int(word_list[0],16) - 4      #take the address of the first input data location
#             i = False
#         word_list.pop(0)
#         if len(word_list) > 4:
#             word_list.pop(4)
#         if len(word_list) > 4:
#            word_list.pop(4)
#         for j in range(0,len(word_list)):
#             s = word_list[j]
#             word_list[j] = "".join(map(str.__add__, s[-2::-2] ,s[-1::-2]))
#             allitems.append(word_list[j])
#         print(word_list)
#    #checkvalues(allitems)
#    with open('data63elements', 'w') as file:
#             for i in range(0, addr+4, 4):
#                 file.write("00000000" + "\n")
#             for item in allitems:
#                 file.write(item + '\n')



if __name__ == '__main__':
#    with open('instrdummy', 'w') as file:
    # for i in range (472,8188,4):
    #     file.write("00000013" + "\n")
   main()