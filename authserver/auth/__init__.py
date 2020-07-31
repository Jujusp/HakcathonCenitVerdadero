import os
from flask import Flask
from .user_manager import *
from flask import request, jsonify, current_app
import requests
import jwt
from datetime import datetime, timedelta

# create and configure the app
app = Flask(__name__)

#database initialization
app.config['DATABASE'] = 'auth.db'
db.init_app(app)

#CAS config
#CAS address
app.config['CAS_SERVER'] = 'https://media.sceenic.co:2828'
#CAS api key
app.config['API_KEY'] = '46e46383c877d62ab1e3cbf311c9209eab107898b3cf267f4e8ee1b9c098dc697f88718ae5efc720f7f25bc216c94cae9985dc20f83fa39d482158f435d6ed97'
#CAS secret key
app.config['API_SECRET_KEY'] = '3a780cfa9624e596daa520ad3abd0b44c7f5f1a57600f3761b4c9799f22846a5'
##############################

# a simple page that says hello
@app.route('/hello')
def hello():
    return 'Hello, World!'

@app.route('/register', methods=['POST'])
def register():
    data = request.json
 
    try:
        create_user(data['login'], data['password'])
    except Exception as e:
        print(str(e))
        return jsonify({"ok":0, "msg":"Failed to register user"})
 
    return jsonify({"ok":1})

@app.route('/login', methods=['POST'])
def login():
    data = request.json
 
    try:
        token = login_user(data['login'], data['password'])
        if token:
            return jsonify({'ok':1, 'token':token})
        else:
            return jsonify({'ok':0, 'msg':'Invalid credentials'})
    except Exception as e:
        print(str(e))
        return jsonify({'ok':0, 'msg':'Failed to login user'})

@app.route('/logout', methods=['POST'])
def logout():
    data = request.json
 
    try:
        logout_user(data['token'])
        return jsonify({'ok':1})
    except Exception as e:
        print(str(e))
        return jsonify({'ok':0, 'msg':'Failed to logout user'})

#get token for registered user
@app.route('/get_token', methods=['GET'])
def get_token():
    token = request.args.get('token')
    #check user authorization
    if not is_authorized(token):
        return jsonify({'ok':0, 'msg':'Not authorized'})
 
    #make request to auth server with API_KEY
    jwt_token = jwt.encode({'exp':datetime.utcnow()+timedelta(seconds=30), 'data':{}}, current_app.config['API_SECRET_KEY'], 'HS256')
    headers = {'X-ReqToken': jwt_token}
    r = requests.get(current_app.config['CAS_SERVER']+'/token', headers=headers)
 
    #check status code
    if r.status_code!=200:
        return jsonify({'ok':0,'msg':'Failed to retrieve token'})

    try:
        data = r.json()
        return jsonify({'ok':1, 'data':data})
    except:
        return jsonify({'ok':0, 'msg':'Invalid response from CAS'})


