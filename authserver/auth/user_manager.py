from .db import get_db
 
import random
import string
 
#generates 32 symbol long token
def __generateToken():
    return ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(32))
 
#creates user in db
def create_user(login, password):
    db = get_db()
    cur = db.cursor()
    cur.execute('insert into users (login, password) values (?, ?)', (login, password))
    db.commit()
 
#generates and returns token for user if credentials are valid
def login_user(login, password):
    db = get_db()
    cur = db.cursor()
 
    token = __generateToken()
 
    try:
        cur.execute('update users set token=? where login=? and password=?', (token, login, password))
    except:
        pass
 
    db.commit()
 
    if cur.rowcount==1:
        return token
         
    return None
 
#clears token
def logout_user(token):
    db = get_db()
    cur = db.cursor()
 
    try:
        cur.execute('update users set token=null where token=?', (token,))
    except:
        pass
 
    db.commit()
 
#checks user authorization
def is_authorized(token):
    db = get_db()
    cur = db.cursor()
 
    try:
        cur.execute('select count(*) from users where token=?', (token,))
    except:
        pass
 
    cnt, = cur.fetchone()
 
    return cnt==1
