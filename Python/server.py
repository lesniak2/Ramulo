from flask import Flask
from flask import request
import json
from flask_sqlalchemy import SQLAlchemy
from models import User, Location, Ranking, Song
from db_objects import DB_Location, DB_Ranking
import datetime

server = Flask(__name__)

server.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://ramulo_tester:tester@localhost/ramulo_test'

db = SQLAlchemy(server)


def get_locations_from_database(latitude, longitude):
    # get list of locations with rankings for today
    today = datetime.datetime.now().strftime('%y%m%d')
    ranked_locations = Ranking.query.filter_by(date_active=today).all()

    location_ids = []
    for loc in ranked_locations:
        location_ids.append(loc.location_id)

    # remove duplicate locations
    location_ids = set(location_ids)

    # store all the names of the locations
    locations = []
    for _id in location_ids:
        # TODO: Filter by lat/long
        result = Location.query.get(_id)
        locations.append(DB_Location(result.location_id, result.name, result.city, result.latitude, result.longitude))
    return locations


@server.route('/locations', methods=['GET', 'POST'])
def show_locations():
    lat = request.args.get('latitude')
    lon = request.args.get('longitude')
    locations = get_locations_from_database(lat, lon)
    # format json in an associative matter corresponding with the variables in the DB_Location object
    json_data = json.dumps(locations, default=lambda o: o.__dict__)
    # prepend the locations tag
    json_data = '{"locations":' + json_data
    # append a success tag
    json_data += ',"success":1}'
    return json_data


def get_rankings_from_database(location_id):
    # get list of rankings for a specified location
    rankings_results = Ranking.query.filter_by(location_id=location_id).all()
    # gather the artist, title, and album information for each song
    rankings = []
    for song in rankings_results:
        rs = Song.query.get(song.song_id)
        rankings.append(DB_Ranking(song.song_id, rs.title, rs.artist, rs.album, song.rank))
    return rankings

@server.route('/rankings', methods=['GET', 'POST'])
def show_rankings():
    location_id = request.args.get('id')
    rankings = get_rankings_from_database(location_id)
    # format json in an associative matter corresponding with the variables in DB_Ranking object
    json_data = json.dumps(rankings, default=lambda o: o.__dict__)
    # prepend the rankings tag
    json_data = '{"rankings":' + json_data
    # append a success tag
    json_data += ',"success":1}'
    return json_data

@server.route('/update', methods=['GET', 'POST'])
def update_rankings():
    location_id = request.args.get('loc')
    song_id = request.args.get('song')
    today = datetime.datetime.now().strftime('%y%m%d')
    rank_change = request.args.get('change')  # rank changed by 1 or -1
    # get the current rank from the specified parameters and update it
    ranking = Ranking.query.filter_by(location_id=location_id, song_id=song_id, date_active=today).first()
    new_rank = ranking.rank + int(rank_change)
    # update the ranking in the database
    db.session.query(Ranking).filter_by(location_id=location_id, song_id=song_id, date_active=today).update({"rank": new_rank})
    db.session.commit()
    return '{"success":1}'


if __name__ == '__main__':
    server.run()