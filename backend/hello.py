from flask import Flask, jsonify, request
from flask_pymongo import PyMongo
from bson.objectid import ObjectId
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
    answers = mongo.db.answers
    answers.drop()
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
        output.append({'_id': str(s['_id']), 'text' : s['text'], 'user' : s['user']})
    return jsonify({'result' : output})

@app.route('/question/<username>', methods=['POST'])
def add_question(username):
    #TODO: check user exist
    questionsDB = mongo.db.questions
    text = request.json['text']
    question = questionsDB.insert({'text': text, 'user': username})
    new_question = questionsDB.find_one({'_id': question })
    output = {'_id': str(question), 'text' : new_question['text']}
    return jsonify({'result' : output})

########################################################################################################################
## ANSWERS
########################################################################################################################

@app.route('/answers/<questionid>', methods=['GET'])
def get_all_answers(questionid):
    questionsDB = mongo.db.questions
    s1 = questionsDB.find_one({'_id': ObjectId(questionid)})
    if s1:
        answersDB = mongo.db.answers
        output = {"question": s1['text'], "answers": []}
        for s2 in answersDB.find({'questionId': questionid}):
            output['answers'].append({'user': s2['user'], 'text': s2['text']})
    else:
        output = "No such question"

    return jsonify({'result' : output})

@app.route('/answer/<username>/<questionid>', methods=['POST'])
def add_answer(username, questionid):
    #TODO: check user and question exist
    answersDB = mongo.db.answers
    text = request.json['text']
    answer = answersDB.insert({'text': text, 'user': username, 'questionId': questionid})
    new_answer = answersDB.find_one({'_id': answer })
    output = {'text' : new_answer['text']}
    return jsonify({'result' : output})

########################################################################################################################

if __name__ == "__main__":
    app.run()