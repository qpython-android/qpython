#qpy:console
import os,os.path,sys
#sys.dont_write_bytecode = True

def modcmd(arg):
  os.system(sys.executable+" "+sys.prefix+"/bin/"+arg)

if not(os.path.exists(sys.prefix+"/bin/pip")):
  print("You need to install pip first.")
print("Input pip commands, ie: pip install {module}")
while(True):
  cmd=raw_input("-->")
  if (cmd==""): break;
  modcmd(cmd)
