from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import func, TypeDecorator, type_coerce
from sqlalchemy import Column, String, INTEGER, VARCHAR, CHAR, DATE, FLOAT
import datetime

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://ramulo_tester:tester@localhost/ramulo_test'

db = SQLAlchemy(app)


# Models
class SHA2Password(TypeDecorator):
    """Applies the sha2 function to incoming passwords."""
    impl = CHAR(64)

    def bind_expression(self, bindvalue):
        return func.sha2(bindvalue, 256)

    class ComparatorFactory(CHAR.comparator_factory):
        def __eq__(self, other):
            # we coerce our own "expression" down to String,
            # so that invoking == doesn't cause an endless loop
            # back into __eq__() here
            local_pw = type_coerce(self.expr, String)
            return local_pw == func.sha2(other, 256)


class User(db.Model):
    __tablename__ = 'users'
    _id = Column('user_id', INTEGER(unsigned=True), primary_key=True, unique=True)
    username = Column(VARCHAR(length=64), nullable=False, unique=True)
    password = Column(SHA2Password(length=64), nullable=False)

    def __repr__(self):
        return '<User %r>' % self.username

    def __init__(self, username, password):
        self.username = username
        self.password = password


class Song(db.Model):
    __tablename__ = 'songs'

    _id = Column('song_id', INTEGER(unsigned=True), primary_key=True)
    artist = Column(VARCHAR(length=60), nullable=False)
    title = Column(VARCHAR(400), nullable=False)
    album = Column(VARCHAR(400), nullable=False)


    def __init__(self, artist, title, album):
        self.artist = artist
        self.title = title
        self.album = album


class Ranking(db.Model):
    __tablename__ = 'rankings'
    #__table_args__ = (db.UniqueConstraint('date_active', 'location_id', 'song_id'), {})
    date_active = Column(DATE(), primary_key=True, autoincrement=False, nullable=False)
    location_id = Column(INTEGER(unsigned=True), primary_key=True, autoincrement=False, nullable=False)
    song_id = Column(INTEGER(unsigned=True), primary_key=True, nullable=False, autoincrement=False)
    rank = Column(INTEGER())

    def __init__(self, location_id, song_id, rank):
        self.date_active = datetime.datetime.now().strftime("%y%m%d")
        self.location_id = location_id
        self.song_id = song_id
        self.rank = rank


class Location(db.Model):
    __tablename__ = 'locations'
    location_id = Column(INTEGER(unsigned=True), primary_key=True)
    name = Column(VARCHAR(100), nullable=False, unique=True)
    city = Column(VARCHAR(100), nullable=False)
    latitude = Column(FLOAT(), nullable=False)
    longitude = Column(FLOAT(), nullable=False)

    def __init__(self, name, city, latitude, longitude):
        self.name = name
        self. city = city
        self.latitude = latitude
        self.longitude = longitude
