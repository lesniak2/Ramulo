
class DB_Location:
    '''
    A simple class containing Location attributes. This class makes the creation
    of a JSON data object easier.
    '''
    def __init__(self, location_id, name, city, latitude, longitude):
        self.location_id = location_id
        self.name = name
        self.city = city
        self.latitude = latitude
        self.longitude = longitude


class DB_Ranking:
    '''
    A simple class containing Ranking attributes. This class makes the creation
    of a JSON data object easier.
    '''
    def __init__(self, song_id, title, artist, album, rank):
        self.song_id = song_id
        self.title = title
        self.artist = artist
        self.album = album
        self.rank = rank
