from flask import *
import json
app = Flask(__name__)
app.secret_key = "this is a terrible secret key"
account = {}
chatRooms = {}
history = {}

class ChatRoom:
    def __init__(self, creater, history=["current message"], members=[]):
        self.creater = creater
        self.history = history
        self.members = members


@app.before_first_request
def default():

    session["username"] = None
    session["chatRoom"] = None


@app.route('/')
def homepage():
    username = session["username"]
    return render_template('homepage.html', username=username, chatRooms=chatRooms)


@app.route("/register/", methods=["GET", "POST"])
def register():
    if session["username"] is not None:
        return redirect(url_for("homepage"))
    error = None
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        if not username:
            error = 'You have to enter a username'
        elif not password:
            error = 'You have to enter a password'
        elif password != request.form['password2']:
            error = 'The two passwords do not match'
        elif username in account:
            error = 'The username is already taken'
        else:
            account[username] = password
            flash('You were successfully registered and can login now')
            return redirect(url_for('login'))
    return render_template('register.html', error=error)


@app.route("/login/", methods=["GET", "POST"])
def login():
    if session["username"] is not None:
        return redirect(url_for("homepage"))
    error = None
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        if username in account:
            if account[username] == password:
                flash('You were logged in')
                session["username"] = username
                session[session["username"]] = None
                return redirect(url_for('homepage'))
            else:
                error = "Invalid password"
        else:
            error = "Invalid username"
    return render_template('login.html', error=error)


@app.route("/logout/")
def logout():
    flash('You were logged out')
    session["username"] = None
    return redirect(url_for('homepage'))


@app.route("/create/", methods=["GET", "POST"])
def create():
    if session["username"] is None:
        flash('Please login first')
        return redirect(url_for('login'))
    error = None
    if request.method == 'POST':
        roomname = request.form['roomname']
        if roomname not in chatRooms:
            session[session["username"]] = roomname
            newroom = ChatRoom(session["username"])
            chatRooms[roomname] = newroom
            return redirect(url_for('chatRoom', name=roomname))
        else:
            error = "This room already exist"

    return render_template("create.html", error=error)


@app.route("/chatRoom/<name>")
def chatRoom(name):
    if session["username"] is None:
        flash('Please log in first')
        return redirect(url_for('login'))
    judge = False
    if session[session["username"]] is not None:
        if name != session[session["username"]]:
            flash('You have already in this chatRoom')
            return redirect(url_for('chatRoom', name=session[session["username"]]))
    if name not in chatRooms:
        flash('The chat room does not exist')
        return redirect(url_for('homepage'))
    return render_template('chatRoom.html', name=name, history=chatRooms[name].history)


@app.route("/new_message", methods=["POST"])
def add():
    chatRooms[session[session["username"]]].history.append(request.form["content"])
    return "OK!"


@app.route("/exit")
def exit():
    session[session["username"]] = None
    flash("exit successfully")
    return redirect(url_for('homepage'))


@app.route("/delete/<name>")
def delete(name):
    if session["username"] is None:
        flash('Please log in first')
        return redirect(url_for('login'))
    if chatRooms[name].creater != session["username"]:
        flash("You are not the creator of this chat room")
        return redirect(url_for('hompage'))
    del chatRooms[name]
    flash("Delete Successfully!")
    return redirect(url_for('homepage'))


@app.route("/test")
def test():
    return render_template('test.html')

if __name__ == "__main__":
    app.run()
