# coding: utf-8

from datetime import datetime

from flask import Flask
from leancloud import Object
from leancloud import Query
from leancloud import LeanCloudError
from flask import request
from flask import redirect
from flask import url_for


app = Flask(__name__)

class Todo(Object):pass


@app.route('/')
def index():
    return "hello"


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
    content = request.form['content']
    todo = Todo(content=content)
    todo.save()
    return "ok"
