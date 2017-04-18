import sys,os
with open(sys.argv[1], 'r') as f, open('/tmp/qpydoc.tmp', 'wb') as g, open(os.path.dirname(os.path.abspath(__file__))+'/extra.txt','r') as e:
    pth = sys.argv[1][1:]
    extra = "".join(e.readlines()).replace("{{PTH}}",pth)
    content = '\n'.join(
        filter(lambda s: len(s),
               map(lambda s:
                       s+('',extra+"<hr/>")[s=='<hr/>'],
                   map(str.strip, f.readlines()))))
    g.write(content)

os.rename('/tmp/qpydoc.tmp', sys.argv[1])
