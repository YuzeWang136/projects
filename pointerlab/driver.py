#!/usr/bin/python3

import json
import os
import re
import sys
from subprocess import PIPE,Popen,STDOUT

names = ['intSize()',
         'doubleSize()',
         'pointerSize()',
         'changeValue()', 
         'withinSameBlock(0x1, 0x48)', 'withinSameBlock(0x1, 0x4)', 'withinSameBlock(0x12345678, 0x1)', 'withinSameBlock(0x12345678, 0x12345658)', 'withinSameBlock(0x12345678, 0x12345686)',   
         'withinArray(0x1, 4, 0xd)', 'withinArray(0x1, 4, 0x11)', 'withinArray(0x14, 4, 0xd)', 
         'swapInts',
         'stringLength(hello)', 'stringLength(hello world)', 'stringLength()',                                 
         'endianExperiment()',                                            
         'selectionSort([2, 1], 2)', 'selectionSort([5, 2, 4, 3, 1], 5)',
         ]

grades = [0.25, 
          0.25, 
          0.25, 
          1.0, 
          0.2, 0.2, 0.2, 0.2, 0.2,
          0.5, 0.5, 0.5, 
          1.25, 
          0.5, 0.5, 0.5, 
          1.0,
          1.0, 1.0]

max_grade = round(sum(grades),1)
final_grade = 0.0

ret = os.system('make 1>/dev/null 2> ./make.err')

# error compiling
if ret != 0:
    print('ERROR: cannot compile your code:\n')
    print(open('./make.err').read())
    sys.exit(1)

try:
  os.remove('./make.err')
except:
  pass
  
# make sure ptest program was generated
if not (os.path.isfile("./ptest") and os.access("./ptest", os.X_OK)):
    print("ERROR: No executable btest binary.\n")
    sys.exit(1)

# make sure that an executable dlc (data lab compiler) exists
if not (os.path.isfile("./dlc") and os.access("./dlc", os.X_OK)):
    print("ERROR: No executable dlc binary.\n")
    sys.exit(1)

ptest_out = os.popen("./ptest").read()

# some error
if ptest_out == '':
    print('ERROR: ptest produced empty output\n')
    sys.exit(1)

proc = Popen(['python3','./dlc.py'], stdout=PIPE, stderr=STDOUT)
dlc_output = proc.communicate()[0]

try:
  dlc_list = eval(dlc_output)
except:    
  print('Error parsing dlc output:', dlc_output)
  exit(1)

dlc_tab = {}
for i in dlc_list: cols = i.split(':'); dlc_tab[cols[0]] = ':'.join(cols[1:])

output = [['Points', 'Max. Points','Function', 'Status']]

i = 0
for line in ptest_out.split('\n')[:-1]:

  if '[ fail ]' in line:

    #test_results.append({"score":0.0, "max_score":grades[i], "output":'Task: %s - FAIL' % (names[i])})
    output += [[0, grades[i], names[i], 'FAIL']]

    i += 1

  elif '[ OK ]' in line:

    func_name = line.split('(')[0]
    # code constraint not met!
    if func_name in dlc_tab:
      output += [[0, grades[i], names[i], dlc_tab[func_name]]]
      #test_results.append({"score":0.0, "max_score":grades[i], "output":'Task: %s - Illegal: %s' % (names[i],dlc_tab[func_name])})
    else:  
      #test_results.append({"score":grades[i], "max_score":grades[i], "output":'Task: %s - OK' % (names[i])})
      final_grade += grades[i]
      output += [[grades[i], grades[i], names[i], 'OK']]

    i += 1

for row in output:
    print("{: >10} {: >10} {: >40} {: <40}".format(*row))

print('Score = %d/%d' % (final_grade, max_grade))

