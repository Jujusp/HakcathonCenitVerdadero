#REQUIRED PACKAGES
Install python3 sqlite3

#INSTALLING
Clone this repository

Enter base directory and execute **python3 -m venv venv**

This creates virtual environment for python application

Then install required python packages with command **venv/bin/pip install -r requirements.txt**

#CONFIGURING

Create empty sqlite database by using command **sqlite3 < init_db.sql**

Insert proper API_KEY and API_SECRET_KEY into auth/\__init__.py

#RUNNING

To run application execute following command **FLASK_APP=auth venv/bin/flask run**
