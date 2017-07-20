# -*-coding:utf-8-*-
#qpy:webapp:QPython Web Editor
#qpy://127.0.0.1:10000/hello

"""
QPython Editor sourcecode
@Author sciooga
"""

import bottle
bottle.BaseRequest.MEMFILE_MAX = 1024 * 10240
from bottle import Bottle, ServerAdapter, static_file, view, request, response
# from bottle import run, debug, route, error, redirect, response

import os
import base64
import sys 
import shutil
import socket
import time
import zipfile
import urllib2
import json

# ---- INIT ----


DST_S = 'http://qpy.io'

if len(sys.argv)>1:
    token = sys.argv[1]
    PROJ_ROOT = '/sdcard/com.hipipal.qpyplus'
    ROOT = PROJ_ROOT
else:
    token = 'UNKNOW'
    token = '568f3c66'
    # TODO minify 时路径需删掉一层
    PROJ_ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    # PROJ_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    # FOR MAIN.PY WILL BE DILL
    # ROOT = os.path.dirname(os.path.abspath(__file__))
    ROOT = os.path.dirname(os.path.abspath(__file__))


# ASSETS = "/assets/"

# ---- QPYTHON WEB SERVER ----


class MyWSGIRefServer(ServerAdapter):
    server = None

    def run(self, handler):
        from wsgiref.simple_server import make_server, WSGIRequestHandler
        if self.quiet:
            class QuietHandler(WSGIRequestHandler):
                def log_request(*args, **kw):
                    pass
            self.options['handler_class'] = QuietHandler
        self.server = make_server(self.host, self.port, handler, **self.options)
        self.server.serve_forever()

    def stop(self):
        # sys.stderr.close()
        import threading
        threading.Thread(target=self.server.shutdown).start()
        # self.server.shutdown()
        self.server.server_close()
        print "# QWEBAPPEND"


# ---- BUILT-IN ROUTERS ----

def __exit():
    response.headers['Access-Control-Allow-Origin'] = '*'
    global server
    server.stop()


def __ping():
    response.headers['Access-Control-Allow-Origin'] = '*'
    return "ok"


def server_static(file_path):
    response.headers['Access-Control-Allow-Origin'] = '*'
    return static_file(file_path, root=ROOT+'/static')

if os.name != "nt":
    import fcntl
    import struct

    def get_interface_ip(ifname):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        return socket.inet_ntoa(fcntl.ioctl(s.fileno(), 0x8915, struct.pack('256s',
                                ifname[:15]))[20:24])


def get_lan_ip():
    ip = socket.gethostbyname(socket.gethostname())
    if ip.startswith("127.") and os.name != "nt":
        interfaces = [
            "eth0",
            "eth1",
            "eth2",
            "wlan0",
            "wlan1",
            "wifi0",
            "ath0",
            "ath1",
            "ppp0",
            ]
        for ifname in interfaces:
            try:
                ip = get_interface_ip(ifname)
                break
            except IOError:
                pass
    return ip


def hello():
    return "You are running QWE ...<br/>Please open http://" + get_lan_ip() + ":10000 on your PC browser"


def index():
    return """Online QEditor API is Serving"""

def api_file_tree():
    file_tree = file_tree_packer(PROJ_ROOT) + 'cache:{"type":"folder", "content":{}}'
    # response.content_type = 'application/json'
    # return json.dumps(file_tree)
    response.headers['Access-Control-Allow-Origin'] = '*'
    return file_tree


def file_tree_packer(path):
    response.headers['Access-Control-Allow-Origin'] = '*'
    this_step_folder = ''
    this_step_file = ''
    name_list = os.listdir(path)
    for name in name_list:
        file_path = os.path.join(path, name)
        if not name[0] == '.':
            if os.path.isdir(file_path) and not name[0] == '.':
                this_step_folder += '"' + name+'":{"type":"folder","content":{' + file_tree_packer(file_path) + '}},'
            else:
                this_step_file += '"' + name+'":{"type":"file"},'
    return this_step_folder + this_step_file


def api_file_content():
    response.headers['Access-Control-Allow-Origin'] = '*'
    path = PROJ_ROOT+'/'+request.GET.get('path')
    file_object = open(path)
    try:
        all_the_text = file_object.read()
    except:
        all_the_text = u'不支持编辑此文件'
    finally:
        file_object.close()
    return all_the_text


def api_img_pre(file_path):
    response.headers['Access-Control-Allow-Origin'] = '*'
    return static_file(file_path, root=PROJ_ROOT)


def api_new_folder():
    response.headers['Access-Control-Allow-Origin'] = '*'
    path = PROJ_ROOT+'/'+request.POST.get('path')
    try:
        os.mkdir(path)
    except:
        return 'err'
    return 'ok'


def api_del_file():
    response.headers['Access-Control-Allow-Origin'] = '*'
    path = PROJ_ROOT+'/'+request.POST.get('path')
    try:
        if os.path.isdir(path):
            shutil.rmtree(path[:-1])
        else:
            os.remove(path)
    except:
        return 'err'
    return 'ok'


def api_save():
    response.headers['Access-Control-Allow-Origin'] = '*'
    path = PROJ_ROOT+'/'+request.POST.get('path')
    content = request.POST.get('content')
    try:
        file_object = open(path, 'w')
        file_object.write(content)
    except:
        return u'err'
    finally:
        file_object.close()
    return u'ok'


def api_run():
    response.headers['Access-Control-Allow-Origin'] = '*'
    path = PROJ_ROOT+'/'+request.POST.get('path')
    try:
        import androidhelper
        droid = androidhelper.Android()
        droid.executeQPy(path)
    except:
        return 'err'
    return 'ok'

def api_lastlog():
    response.headers['Access-Control-Allow-Origin'] = '*'
    path = PROJ_ROOT+'/'+request.POST.get('path')
    try:
        target = open(path).read(64)
        if 'qpy:kivy' in target:
            return open(os.path.dirname(path) + "/.run.log").read()
        elif 'qpy:webapp' or 'qpy:qpyapp' in target:
            return open(PROJ_ROOT + "/.run/.run.log").read()
    except:
        return '#qpy: No log file for %s until now' % path

def api_export():
    response.headers['Access-Control-Allow-Origin'] = '*'
    title = request.POST.get('title')
    id = request.POST.get('id')
    print request.files
    icon = request.POST.get('icon')
    target = request.POST.get('target')
    target = 'projects/' + target if target.find('/')==-1 else target
    '''
    print '-----'
    print title
    print id
    print icon
    print target
    print '-----'
    '''
    icon_newname = 'qpy_app_logo.png'
    os.chdir(PROJ_ROOT + '/cache')
    with open(icon_newname, 'wb') as new_icon:
        new_icon.write(base64.b64decode(icon[22:]))

    # 生成压缩包
    zip_name = title + '__' + id + '__' + token + '__' + str(int(time.time())) + '.zip'
    export_zip = zipfile.ZipFile(zip_name, 'w' ,zipfile.ZIP_DEFLATED)
    export_zip.write(icon_newname)
    os.remove(icon_newname)
    if os.path.isdir(PROJ_ROOT + '/' + target):
        os.chdir(PROJ_ROOT + '/' + target)
        for dirpath, dirnames, filenames in os.walk('.'):
            for filename in filenames:
                export_zip.write(os.path.join(dirpath,filename))
    else:
        name_split = target.rfind('/')
        os.chdir(PROJ_ROOT + '/' + target[:name_split])
        export_zip.write(target[name_split+1:])
    export_zip.close()

    # 上传压缩包
    # 构造 post 报文
    # http://www.ietf.org/rfc/rfc1867.txt
    os.chdir(PROJ_ROOT+'/cache')
    boundary = '----------a06d9c029c222ebd'
    with open(zip_name,'rb') as f:
        zip_data = f.read()
    data = [
        '--' + boundary,
        'Content-Disposition: form-data; name="%s"\r\n' % 'title',
        title,
        '--' + boundary,
        'Content-Disposition: form-data; name="%s"\r\n' % 'id',
        id,
        '--' + boundary,
        'Content-Disposition: form-data; name="%s"; filename="%s"' % ('zip', zip_name),
        'Content-Type: %s\r\n' % 'application/zip',
        zip_data,
        '--%s--\r\n' % boundary,
    ]
    http_url = DST_S + '/export/upload/' + token
    http_body='\r\n'.join(data)

    resp_json = {'msg':'not set'}
    try:
        req=urllib2.Request(http_url, data=http_body)
        req.add_header('Content-Type', 'multipart/form-data; boundary=%s' % boundary)
        resp = urllib2.urlopen(req)
        resp_json = json.loads(resp.read())
        if resp_json['errno'] != '0':
            print 'export error:'
            print resp_json['msg']
    except Exception,e:
        print e
        print 'http error when upload export_zip'

    #os.remove(zip_name)
    return resp_json['msg']



# ---- WEBAPP ROUTERS ----


app = Bottle()
app.route('/', method='GET')(index)
app.route('/hello', method='GET')(hello)
app.route('/__exit', method=['GET', 'HEAD'])(__exit)
app.route('/__ping', method=['GET', 'HEAD'])(__ping)
app.route('/static/<file_path:path>', method='GET')(server_static)
app.route('/api/file-tree', method='GET')(api_file_tree)
app.route('/api/file-content', method='GET')(api_file_content)
app.route('/api/img-pre/<file_path:path>', method='GET')(api_img_pre)
app.route('/api/new-folder', method='POST')(api_new_folder)
app.route('/api/del-file', method='POST')(api_del_file)
app.route('/api/save', method='POST')(api_save)
app.route('/api/run', method='POST')(api_run)
app.route('/api/lastlog', method='POST')(api_lastlog)
app.route('/api/export', method='POST')(api_export)

try:
    server = MyWSGIRefServer(host="0.0.0.0", port="10000")
    app.run(server=server, reloader=False)
except Exception, ex:
    print "Exception: %s" % repr(ex)
