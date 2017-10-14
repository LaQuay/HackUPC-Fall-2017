from flask import Flask, jsonify
import json

app = Flask(__name__)




@app.route('/user/<id>', methods=['GET'])
def get_user(id):
    return jsonify(
        e = id
    )

if __name__ == "__main__":
    app.run()