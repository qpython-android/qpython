import sys,os
with open(sys.argv[1], 'r') as f, open('/tmp/qpydoc.tmp', 'wb') as g, open(os.path.dirname(os.path.abspath(__file__))+'/extra.txt','r') as e:
    pth = sys.argv[1][1:]
    extra = "".join(e.readlines()).replace("{{PTH}}",pth)
    g.write('\n'.join(
        filter(lambda s: len(s),
               map(lambda s:
                       ('',extra+"<hr/>")[s=='<div role="contentinfo">']+s,
                   map(str.strip, f.readlines())))))
os.rename('/tmp/qpydoc.tmp', sys.argv[1])
