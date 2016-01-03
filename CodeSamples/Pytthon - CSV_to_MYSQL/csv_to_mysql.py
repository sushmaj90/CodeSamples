CSV_to_MYSQL

import pymysql.cursors
from pprint import pprint
import csv
# Open the connection to the database (must be one you can write to!)
connection = pymysql.connect(host="localhost", # your host, usually localhost
    user="REPLACE_ME" # your username
    passwd="REPLACE_ME", # your password
    db="REPLACE_ME_empty_music_festival",
    autocommit=True,  # Otherwise you have to call cursor.commit() after each query
    cursorclass=pymysql.cursors.DictCursor)

cursor = connection.cursor() # now we can use cursor.execute() to talk to the server

# empty the database so that we can run this from a known state.
full_query = "TRUNCATE venues; TRUNCATE performances; TRUNCATE bands;"
pprint(full_query); cursor.execute(full_query)

#  Read the CSV, then as we move row by row we can put it into the database

with open('venues-header.csv', newline='') as csvfile:
  # tell python about the specific csv format
  myCSVReader = csv.DictReader(csvfile, delimiter=",", quotechar='"')
 
  # move row by row through the file
  for row in myCSVReader:

    placeholder_sql = "INSERT INTO venues(name, capacity) VALUE ('{}',{})"
        pprint(full_query); cursor.execute(full_query)

with open('new_venues_and_bands.csv', newline='') as csvfile:
  # tell python about the specific csv format
  myCSVReader = csv.DictReader(csvfile, delimiter=",", quotechar='"')
 
  # move row by row through the file
  for row in myCSVReader:
     #First create the venue
    print("---------")
    print("creating venue")
    placeholder_sql = "INSERT INTO venues(name, capacity) VALUE ('{}',{})"
    full_query = placeholder_sql.format(row['venue_name'],row['capacity'])
    pprint(full_query); cursor.execute(full_query)
    # store the id for the new venue
    new_venue_id = cursor.lastrowid
    
    # Now create the band
    print("creating band")
    placeholder_sql = "INSERT INTO bands(name) VALUE ('{}')"
    full_query = placeholder_sql.format(row['band_name'])
    pprint(full_query); cursor.execute(full_query)
    
    new_band_id = cursor.lastrowid
    
    print("insert performance")
    # Ok, now we have the new foreign keys we need to create the performance. Phew.
    placeholder_sql = "INSERT INTO performances(start,band_id,venue_id) VALUE ('{}', {}, {})"
    full_query = placeholder_sql.format(row['datetime'], new_band_id, new_venue_id)
    pprint(full_query); cursor.execute(full_query)

# Check in the database for the new venues, bands, and performances.

#exit()

print("\n\n\nNew Example:\n")
# First clear the tables, leaving venues.
# empty the database so that we can run this from a known state.
full_query = "TRUNCATE performances;"
pprint(full_query); cursor.execute(full_query)

with open('new_performances.csv', newline='') as csvfile:
  # tell python about the specific csv format
  myCSVReader = csv.DictReader(csvfile, delimiter=",", quotechar='"')
 
  # move row by row through the file
  for row in myCSVReader:
     #First find the id for the venue.
    placeholder_sql = "SELECT id FROM venues WHERE name = '{}'"
    full_query = placeholder_sql.format(row['venue_name'])
    pprint(full_query); cursor.execute(full_query)
    
    result = cursor.fetchone() # only one row.
    new_venue_id = result['id']
    print("  --> {}".format(new_venue_id))
    
    # Now get the band_id
    placeholder_sql = "SELECT id FROM bands WHERE name = '{}'"
    full_query = placeholder_sql.format(row['band_name'])
    pprint(full_query); cursor.execute(full_query)
    
    result = cursor.fetchone() # only one row.
    new_band_id = result['id']
    print("  --> {}".format(new_band_id))
    
    placeholder_sql = "INSERT INTO performances(start,band_id,venue_id) VALUE ('{}', {}, {})"
    full_query = placeholder_sql.format(row['datetime'], new_band_id, new_venue_id)
    pprint(full_query); cursor.execute(full_query)

with open('new_perf_mix_old_new_venues.csv', newline='') as csvfile:
  # tell python about the specific csv format
  myCSVReader = csv.DictReader(csvfile, delimiter=",", quotechar='"')
 
  # move row by row through the file
  for row in myCSVReader:
     #First find the id for the venue.
    placeholder_sql = "SELECT id FROM venues WHERE name = '{}'"
    full_query = placeholder_sql.format(row['venue_name'])
    pprint(full_query); cursor.execute(full_query)
    
    if(cursor.rowcount >= 1): 
        result = cursor.fetchone() 
        venue_id = result['id']
        print("  existing --> {}".format(venue_id))
    else: # there wasn't an existing one, we have to create it.
        print("  none found!")
        placeholder_sql = "INSERT INTO venues(name) VALUE ('{}')"
        full_query = placeholder_sql.format(row['venue_name']) # capacity not in csv
        pprint(full_query); cursor.execute(full_query)
        # store the id for the new venue
        venue_id = cursor.lastrowid
        print("  new --> {}".format(venue_id))
    
    # either way we now have what we need in venue_id. Now do same for band_id

    placeholder_sql = "SELECT id FROM bands WHERE name = '{}'"
    full_query = placeholder_sql.format(row['band_name'])
    pprint(full_query); cursor.execute(full_query)
    
    if(cursor.rowcount >= 1): 
        result = cursor.fetchone() 
        band_id = result['id']
    else:
        print("  none found!")
        placeholder_sql = "INSERT INTO bands(name) VALUE ('{}')" 
        full_query = placeholder_sql.format(row['band_name'])
        pprint(full_query); cursor.execute(full_query)
        # store the id for the new venue
        band_id = cursor.lastrowid
    
    placeholder_sql = "INSERT INTO performances(start,band_id,venue_id) VALUE ('{}', {}, {})"
    full_query = placeholder_sql.format(row['datetime'], new_band_id, new_venue_id)
    pprint(full_query); cursor.execute(full_query)



