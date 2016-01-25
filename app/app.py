# coding: utf-8

from datetime import datetime
from flask import Flask
from leancloud import Object
from leancloud import Query
from leancloud import LeanCloudError
from flask import request


app = Flask(__name__)

class Todo(Object):pass


@app.route('/')
def index():
    return "hello"

'''http://192.168.13.57:3000/check_agent?value=电话号码'''
@app.route('/check_agent')
def _index1():
    data = request.args.get("phone")

    return data

'''http://192.168.13.57:3000/house_info?value=房源的ID'''
@app.route('/house_info')
def _index2():
    data = request.args.get("id")

    return data


@app.route('/time')
def time():
    return str(datetime.now())

import json 

@app.route('/app')
def show():
    try:
        todos = Query(Todo).descending('createdAt').find()
    except LeanCloudError, e:
        if e.code == 101:  # 服务端对应的 Class 还没创建
            todos = []
        else:
            raise e
    return json.dumps([x.get('content') for x in todos])


@app.route('/app/add')
def add():
    todo = Todo().set("hello",'hello').set('sex','man').set('age',123).save()
    return "ok"
