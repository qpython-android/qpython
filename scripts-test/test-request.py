#-*-coding:utf8;-*-
#qpy:3
#qpy:console
from urllib.request import urlopen
from urllib.request import Request
import re
uid=int(input())
header={
        'host':'app.bjtu.edu.cn:8395',
        'Connection': 'keep-alive',
        'Accept': '''application/json, text/plain, */*''',
        'Origin':'''file://''',
        'token': '''506855609_eNJck2OsmWdatWQixc7wMc_O0CJcdXJTn2hvk2OJmCIFMtwim5a2lXypk2GJmCIFMtwilWGZkLabIcow0CJsk2dpkaupkWrixc7LyzQKxTfzyTQGyT_sIZGpn2SxnWKaIcoiIiwij5hvkZrixiIi0CJNmW6sTZ6SmtIFIRexi-lKS-lXUtIsIYJvk5rixc7sIYqRlXQixiIwIiwidXyajbabIcoOMTnHMTIsIYqzmXJxnWKaIcoiMTnNMT7Nycfi0CJKdrabIcoixW7wxTmZncdaM56aMTaamiJ9''',
        'Content-Type': '''application/json;charset=UTF-8''',
        'Accept-Encoding': 'gzip, deflate',
        'Accept-Language': 'zh-cn',
        'User-Agent': '''Mozilla/5.0 (Linux; Android 6.0.1; SM-G9300 Build/MMB29M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Crosswalk/15.44.384.13 Mobile Safari/537.36'''
        }
d={"xm":"","xh":uid,"xb":"","jg":"","yxsMc":"","sznj":"","szbh":"","zy":"","offset":"1"}
d=str(d)
d=bytes(d, 'utf-8')
postdata=(d)
m={}
url='http://app.bjtu.edu.cn:8395/mobilecampus/bks/getBkxList.do'
req = Request(url,postdata,header)
f = urlopen(req,timeout=120).read().decode('utf-8')
xm=re.findall(u'"xm":"([\s\S]*?)","',f)#提示此处有错误
csrq=re.findall(u'"csrq":"([\s\S]*?)","',f)
jg=re.findall(u'"jg":"([\s\S]*?)","',f)
mzmc=re.findall(u'"mzmc":"([\s\S]*?)","',f)
banji=re.findall(u'"szbj":"([\s\S]*?)","',f)
rxnj=re.findall(u'"sznj":"([\s\S]*?)","',f)
xyxx=re.findall(u'"yxmc":"([\s\S]*?)","',f)
xm=re.findall(u'"xm":"([\s\S]*?)","',f)
mail=re.findall(u'"mail":"([\s\S]*?)","',f)
try:
    print("姓名:",xm[0])
    print("籍贯:",jg[0])
    print("民族:",mzmc[0])
    print("出生日期:",csrq[0])
    print("学院:",xyxx[0])
    print("班级:",banji[0])
    print("邮件地址:",mail[0])
except IndexError:
    print("学号错误!")
