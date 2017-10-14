from flask import Flask, jsonify, request, send_file, redirect, url_for, send_from_directory
from flask_pymongo import PyMongo
from bson.objectid import ObjectId
import os

ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

app = Flask(__name__)

app.config['MONGO_DBNAME'] = 'restdb'
app.config['MONGO_URI'] = 'mongodb://localhost:27017/restdb'
app.config['OCR_UPLOAD_FOLDER'] = './ocruploads'
app.config['UPLOAD_FOLDER'] = './uploads'
app.config['TEMPLATE_FOLDER'] = '/templates'

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

@app.route('/clearOCR', methods=['GET'])
def clear_ocr_db():
    images = mongo.db.images
    images.drop()
    return "Clear OCR done"

########################################################################################################################
## USERS
########################################################################################################################

@app.route('/users', methods=['GET'])
def get_all_users():
    usersDB = mongo.db.users
    output = []
    for user in usersDB.find():
        output.append({'username' : user['username']})
    return jsonify({'result' : output})

@app.route('/user/<username>', methods=['GET'])
def get_user(username):
    usersDB = mongo.db.users
    user = usersDB.find_one({'username' : username})
    if user:
        output = {'username' : user['username']}
    else:
        output = "No such username"
    return jsonify({'result' : output})

@app.route('/user', methods=['POST'])
def add_user():
    # TODO: check username does not exist
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
    for question in questionsDB.find():
        output.append({'_id': str(question['_id']), 'text' : question['text'], 'user' : question['user']})
    return jsonify({'result' : output})

@app.route('/question/<username>', methods=['POST'])
def add_question(username):
    print request.files

    usersDB = mongo.db.users
    user = usersDB.find_one({'username': username})
    if user:
        if 'image' not in request.files:
            output = 'No image part'
        f = request.files['image']
        # if user does not select file, browser also
        # submit a empty part without filename
        if f.filename == '':
            output = 'No selected image'
        if f:
            questionsDB = mongo.db.questions
            text = request.form['text']
            question = questionsDB.insert({'text': text, 'user': username})
            new_question = questionsDB.find_one({'_id': question})
            output = {'_id': str(question), 'text': new_question['text']}

            filename = str(question) + ".png"
            path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            f.save(path)
            output = 'image uploaded successfully'
    else:
        output = "No such user"
    return jsonify({'result' : output})

@app.route('/question/<questionid>', methods=['GET'])
def get_question(questionid):
    questionsDB = mongo.db.questions
    question = questionsDB.find_one({'_id': ObjectId(questionid)})
    if question:
        filename = questionid + ".png"
        path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        return send_file(path, mimetype='image/png')
    else:
        output = "No such question"
    return jsonify({'result': output})

########################################################################################################################
## ANSWERS
########################################################################################################################

@app.route('/answers/<questionid>', methods=['GET'])
def get_all_answers(questionid):
    questionsDB = mongo.db.questions
    question = questionsDB.find_one({'_id': ObjectId(questionid)})
    if question:
        answersDB = mongo.db.answers
        output = {"question": question['text'], "answers": []}
        for answer in answersDB.find({'questionId': questionid}):
            output['answers'].append({'user': answer['user'], 'text': answer['text']})
    else:
        output = "No such question"

    return jsonify({'result' : output})

@app.route('/answer/<username>/<questionid>', methods=['POST'])
def add_answer(username, questionid):
    usersDB = mongo.db.users
    user = usersDB.find_one({'username': username})
    if user:
        questionsDB = mongo.db.questions
        question = questionsDB.find_one({'_id': ObjectId(questionid)})
        if question:
            answersDB = mongo.db.answers
            text = request.json['text']
            answer = answersDB.insert({'text': text, 'user': username, 'questionId': questionid})
            new_answer = answersDB.find_one({'_id': answer})
            output = {'text': new_answer['text']}
            return jsonify({'result': output})
        else:
            output = "No such question"
    else:
        output = "No such user"
    return jsonify({'result' : output})

########################################################################################################################

@app.route('/image', methods=['POST'])
def add_image():
    if 'image' not in request.files:
        output = 'No image part'
    f = request.files['image']
    # if user does not select file, browser also
    # submit a empty part without filename
    if f.filename == '':
        output = 'No selected image'
    if f:
        imagesDB = mongo.db.images
        image = imagesDB.find_one(sort=[("id", -1)])
        if image:
            filenum = str(int(image['id']) + 1)
        else:
            filenum = "1"
        filename = filenum + ".png"
        imagesDB.insert({'id': filenum})

        path = os.path.join(app.config['OCR_UPLOAD_FOLDER'], filename)
        f.save(path)

        output = filenum

    return jsonify({'result' : output})

@app.route('/image/<imageid>', methods=['GET'])
def get_image_url(imageid):
    filenum = imageid
    filename = filenum + ".png"

    path = os.path.join(app.config['OCR_UPLOAD_FOLDER'], filename)
    #f.save(path)

    uploads = os.path.join(app.root_path, app.config['OCR_UPLOAD_FOLDER'])
    return send_from_directory(directory=uploads, filename=filename)

########################################################################################################################

if __name__ == "__main__":
    app.run()