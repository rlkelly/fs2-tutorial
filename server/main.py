from flask import Flask, request


app = Flask(__name__)

@app.route('/login', methods=['POST'])
def hello_world():
    print(request.form)
    if request.form['username'] == 'boss':
        if request.form['password'] == 'deer7':
            return 'Shareholder Secrets $$$$'
    return 'INVALID USERNAME OR PASSWORD', 400


if __name__ == '__main__':
    app.run()
