from flask import Flask, jsonify, request
from flask_pymongo import PyMongo
import json

app = Flask(__name__)

app.config['MONGO_DBNAME'] = 'restdb'
app.config['MONGO_URI'] = 'mongodb://localhost:27017/restdb'

mongo = PyMongo(app)

@app.route('/clear', methods=['GET'])
def clear_db():
    users = mongo.db.users
    users.drop()
    questions = mongo.db.questions
    questions.drop()
    return "Clear done"

########################################################################################################################
## USERS
########################################################################################################################

@app.route('/users', methods=['GET'])
def get_all_users():
    usersDB = mongo.db.users
    output = []
    for s in usersDB.find():
        output.append({'username' : s['username']})
    return jsonify({'result' : output})

@app.route('/user/<username>', methods=['GET'])
def get_user(username):
  usersDB = mongo.db.users
  s = usersDB.find_one({'username' : username})
  if s:
    output = {'username' : s['username']}
  else:
    output = "No such username"
  return jsonify({'result' : output})

@app.route('/user', methods=['POST'])
def add_user():
    usersDB = mongo.db.users
    username = request.json['username']
    user = usersDB.insert({'username': username})
    new_user = usersDB.find_one({'_id': user })
    output = {'username' : new_user['username']}
    return jsonify({'result' : output})

########################################################################################################################
## QUESTIONS
########################################################################################################################

@app.route('/questions', methods=['GET'])
def get_all_questions():
    questionsDB = mongo.db.questions
    output = []
    for s in questionsDB.find():
        print s
        output.append({'text' : s['text'], 'user' : s['user']})
    return jsonify({'result' : output})

@app.route('/question/<username>', methods=['POST'])
def add_question(username):
    #TODO: check user exist
    questionsDB = mongo.db.questions
    text = request.json['text']
    question = questionsDB.insert({'text': text, 'user': username})
    new_question = questionsDB.find_one({'_id': question })
    output = {'text' : new_question['text']}
    return jsonify({'result' : output})

########################################################################################################################

if __name__ == "__main__":
    app.run()