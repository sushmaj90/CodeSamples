#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from google.appengine.api import users
from google.appengine.ext import ndb
from google.appengine.api import images
from google.appengine.api import mail
from datetime import *
import webapp2
import urllib
import urllib2
import json
import cgi
import re
import random

loginhtml = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Login Page</title>
</head>

<body>
<h1>Welcome to Connexus!</h1>
<h2>Share the World!</h2>
<form method="post">
  <input type="text" name="username" placeholder="userID" required/>
  <br> <br>
  <input type="password" name="password" placeholder="Password" required/>
  <br><br>
  <input type="submit" value="Login"/>
</form>
</body>
</html>
"""

html = """
<!doctype html>
<html>
<head>
<meta charset="utf-8"/>
<title> Create Stream</title>
<link rel="stylesheet" type="text/css" href="/Users/sushma/mp1/dropzone.css">
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<style>
h2 {
font-size: 3em;
color: #00CED1;
font-style: italic;}
</style>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

<script type="text/javascript" src="/Users/sushma/mp1/dropzone.js"></script>
<script>
function myFunction(){
     if (document.getElementById("urlID").files.length == 0 ){
         alert("Error! Choose cover image to upload..");
         return false;}
         }
</script>
</head>
<body> <div class="container"><h2>Connex.us</h2> 
<ul class="nav nav-tabs">
<li><a href="/Manage">Manage</a></li> 
<li class="active"><a href="/Create">Create</a></li>  
<li><a href="/View">View</a></li>  
<li><a href="/Search">Search</a></li>  
<li><a href="/Trending">Trending</a></li>  
<li><a href="social.html">Social</a></li>
</ul>
<h4>Streams I own</h4>
<form method="post" enctype="multipart/form-data">
<table>
    <tr style="height:20px;"></tr>
    <tr>
        <td><input type = "text" name="streamNameId" style="height: 20px; width:180px" /></td>
        <td style="row-span:4"></td>
        <td><input type = "text" name="streamTagId" style="height: 20px; width:180px" /></td>
    </tr>
    <tr>
        <td><label for="streamNameId">Name your stream</label></td>
        <td style="row-span:4"></td>
        <td><label for="streamTagId">Tag your stream</label></td>
    </tr>
    <tr style="height:40px;"></tr>
    <tr>
        <td>
            <table>
                <tr><td><input type="text" name="streamSubscriberId" placeholder="Email IDs of subscribers" style="height: 60px; width:180px"></td></tr>
                <tr style="height:15px;"></tr>
                <tr><td><input type="text" name="optionalMessageId" placeholder="Optional message for invite" style="height: 60px; width:180px"></td></tr>
                <tr><td><label for="streamSubscriberId">Add Subscribers</label></td></tr>
            </table>
        </td>
        <td style="width:40px"></td>
        <td>
            <table>
                 <tr><td><input type="file" name="urlId" id="urlID" style="height:25px; width:180px"></td></tr>
                 <tr><td><label for="urlId">URL to cover image</label></td></tr>
            </table>

        </td>
    </tr>
    <tr style="height:20px;"></tr>
    <tr><td><input type="submit" id="btnCreateStream" class="btn btn-primary start" value="Create Stream" onclick="return myFunction()"></td></tr>
</table>

</form>
</div>
</body>
</html>
"""

managehtml = """<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Management Page</title>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<style>

h2 {
font-size: 3em;
color: #00CED1;
font-style: italic;}

table {
  border-collapse: collapse;
}

td, th {
  border: 1px solid #999;
  padding: 0.5rem;
  text-align: left;
}
</style>
    <script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
    <script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
        $("#del_stream").click(function(){
        var checekedlist = $('input[name="del_checked"]:checked').map(function(){
            return $(this).val();
        }).get();
       if(checekedlist.length == 0)
       {
        alert("No checkboxes selected");
       }
       else
       {
         $.ajax({
                type: "POST",
                url: "/delete_checked",
                data: JSON.stringify(checekedlist),
                contentType: "application/json",
                dataType: 'json',
            success: function(){
            alert("Delete Successful");
            $('input:checkbox').removeAttr('checked');
            window.location.reload();
         }
        });
        }
      });
      $("#unsub_stream").click(function(){
        var sublist = $('input[name="unsubscribe_checked"]:checked').map(function(){
            return $(this).val();
        }).get();
       if(sublist.length == 0)
       {
        alert("No checkboxes selected");
       }
       else
       {
         $.ajax({
                type: "POST",
                url: "/delete_unsub",
                data: JSON.stringify(sublist),
                contentType: "application/json",
                dataType: 'json',
            success: function(){
            alert("Delete Successful");
            $('input:checkbox').removeAttr('checked');
            window.location.reload();
         }
        });
        }
      });
    });
    </script>
</head>
<body>
<div class="container"><h2>Connex.us</h2> 
<ul class="nav nav-tabs">
<li class="active"><a href="/Manage">Manage</a></li> 
<li><a href="/Create">Create</a></li>  
<li><a href="/View">View</a></li>  
<li><a href="/Search">Search</a></li>  
<li><a href="/Trending">Trending</a></li>  
<li><a href="social.html">Social</a></li>
</ul>
<h4>Streams I own</h4></div>
</body></html>"""

errorhtml = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Error Page</title>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <style>
h2 {
font-size: 3em;
color: #00CED1;
font-style: italic;}
</style>
</head>

<body>
<div class="container"><h2>Connex.us</h2> 
<ul class="nav nav-tabs">
<li><a href="/Manage">Manage</a></li> 
<li><a href="/Create">Create</a></li>  
<li><a href="/View">View</a></li>  
<li><a href="/Search">Search</a></li>  
<li><a href="/Trending">Trending</a></li>  
<li><a href="social.html">Social</a></li>
</ul>
<br><br><br>
<img src="error.jpg" align="left" alt=""/><p>Error:You tried to create a new stream whose name is the same as an existing stream or you have not specified a stream name,operation did not complete</p>
</div></body>
</html> """

viewstreamhtml = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus View Stream Page</title>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <style>
h2 {
font-size: 3em;
color: #00CED1;
font-style: italic;}
</style>
</head>

<div class="container"><h2>Connex.us</h2> 
<ul class="nav nav-tabs">
<li><a href="/Manage">Manage</a></li> 
<li><a href="/Create">Create</a></li>  
<li class="active"><a href="/View">View</a></li>  
<li><a href="/Search">Search</a></li>  
<li><a href="/Trending">Trending</a></li>  
<li><a href="social.html">Social</a></li>
</ul><br><br><br>
"""

viewall = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus View Stream Page</title>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <style>
h2 {
font-size: 3em;
color: #00CED1;
font-style: italic;}
</style>
</head>
<body>
<div class="container"><h2>Connex.us</h2> 
<ul class="nav nav-tabs">
<li><a href="/Manage">Manage</a></li> 
<li><a href="/Create">Create</a></li>  
<li class="active"><a href="/View">View</a></li>  
<li><a href="/Search">Search</a></li>  
<li><a href="/Trending">Trending</a></li>  
<li><a href="social.html">Social</a></li>
</ul>
<h3>View All Streams</h3>
</body>
</html>
"""

searchhtml = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Search Page</title>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<link href="http://code.jquery.com/ui/1.10.4/themes/ui-lightness/jquery-ui.css" rel="stylesheet">
<style>
h2 {
font-size: 3em;
color: #00CED1;
font-style: italic;}
</style>
   <script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
<script type="text/javascript">

       $(document).ready(function() {
           var cache = {};
           $( "#keyword" ).autocomplete({
               minLength: 1,
               source: function( request, response ) {
                  var term = request.term;
                  if ( term in cache ) {
                      console.log('in cache');
                      response( cache[ term ].slice(0,20));
                    return;
                  }
                  $.getJSON( "/search_index", request, function( data, status, xhr ) {
                      cache[ term ] = data.term;
                      response(data.term.slice(0,20));
                      console.log(data.term);
                  });
                }
           });
       });
    </script>

</head>
<body>
<div class="container"><h2>Connex.us</h2> 
<ul class="nav nav-tabs">
<li><a href="/Manage">Manage</a></li> 
<li><a href="/Create">Create</a></li>  
<li><a href="/View">View</a></li>  
<li class="active"><a href="/Search">Search</a></li>  
<li><a href="/Trending">Trending</a></li>  
<li><a href="social.html">Social</a></li>
</ul><br><br><br>
<form method="post">
<div style = "height:20px";></div>
<table><td><input type="text" id="keyword" name="searchStreamId" placeholder="Search Streams" style="width:160px"></td>
<td><input type="submit" name="btnSearchStream" class="btn btn-primary start" value="Search"></td>
<td><input type="submit" name="btnSearchStream" class="btn btn-primary start" value="Rebuild completion index"></td></table>
</form>
</body></html>
"""

trendhtml="""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Trending Page</title>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <style>
h2 {
font-size: 3em;
color: #00CED1;
font-style: italic;}
</style>
</head>

<body>
<div class="container"><h2>Connex.us</h2> 
<ul class="nav nav-tabs">
<li><a href="/Manage">Manage</a></li> 
<li><a href="/Create">Create</a></li>  
<li><a href="/View">View</a></li>  
<li><a href="/Search">Search</a></li>  
<li class="active"><a href="/Trending">Trending</a></li>  
<li><a href="social.html">Social</a></li>
</ul>
<h3>Top 3 Trending Streams</h3>
<div class="update_rate">
	<form method="post" id="email_freq_set" name="email_freq_set"><table>
	
	<tr><td><input type="radio" name="email_freq" value="0" >No reports</td></tr>
	<tr><td><input type="radio" name="email_freq" value="1">Every 5 minutes</td></tr>
	<tr><td><input type="radio" name="email_freq" value="2">Every 1 hour</td></tr>
	<tr><td><input type="radio" name="email_freq" value="3">Every day</td></tr>
	<tr><td>Email trending report</td></tr>
	<tr><td><input class="button" type="submit" value="Update rate"></td></tr>
	</table></form>
	</div>
</body>
</html>
"""
maphtml = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Geo View Page</title>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<style>

h2 {
font-size: 3em;
color: #00CED1;
font-style: italic;}

#map_canvas{

	margin-left:10px;
	width:800px;
	height:400px;
	border:2px solid;


}
#slider-range{
	width:840px;
}

</style>
</head>

<body>
<div class="container"><h2>Connex.us</h2> 
<ul class="nav nav-tabs">
<li><a href="/Manage">Manage</a></li> 
<li><a href="/Create">Create</a></li>  
<li class="active"><a href="/View">View</a></li>  
<li><a href="/Search">Search</a></li>  
<li><a href="/Trending">Trending</a></li>  
<li><a href="social.html">Social</a></li>
</ul><br><br>

<link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.min.js" type="text/javascript"></script>
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<script src="http://maps.google.com/maps/api/js?sensor=true" type="text/javascript"></script>
<script src="http://jquery-ui-map.googlecode.com/svn/trunk/ui/min/jquery.ui.map.full.min.js" type="text/javascript"></script>
<script src="https://google-maps-utility-library-v3.googlecode.com/svn/trunk/markerclustererplus/src/markerclusterer.js" type="text/javascript"></script>
<script type="text/javascript">
$(function(){
                $('#map_canvas').gmap({'zoom': 1, 'disableDefaultUI':true}).bind('init', function(evt, map) {
                var request_url = window.location.href;
                console.log(request_url);

                $.ajax({
                url:request_url,
                type:"POST",
                dataType:"json",
                success: function(data){
                   $.each(data.markers, function(i,m){
                      $('#map_canvas').gmap('addMarker', { 'position': new google.maps.LatLng(m.latitude, m.longitude)})
                      .mouseover(function(){
                       //console.log("inside mouseover")
                       $('#map_canvas').gmap('openInfoWindow',{'content': '<img width="100px" height ="100px" src="/img?key='+ m.url + '">'},this);
                      });
                   });
                $('#map_canvas').gmap('set', 'MarkerClusterer', new MarkerClusterer($('#map_canvas').gmap('get', 'map'), $('#map_canvas').gmap('get', 'markers')));
               }
          });
        });
});


</script>
<script type="text/javascript">
$(function() {
    var maxdate = new Date();
    var mindate = new Date(maxdate.getFullYear()-1, maxdate.getMonth(), maxdate.getDate(), maxdate.getHours(),maxdate.getMinutes(),maxdate.getSeconds(), maxdate.getMilliseconds());

    $('#slider-range').slider({
      range: true,
      min: mindate.getTime(),
      max: maxdate.getTime(),
      step: 39600000,
      values: [mindate.getTime(), maxdate.getTime()],
      slide: function( event, ui ) {
        var max = new Date(ui.values[1]);
        var min = new Date(ui.values[0]);

        var minyear = min.getFullYear();
        var minmonth = min.getMonth();
        var minday = min.getDate();
        var maxyear = max.getFullYear();
        var maxmonth = max.getMonth();
        var maxday = max.getDate();

        var m_names = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        $( "#amount" ).val(m_names[minmonth] + " " + minday + "," + " " + minyear + ' to ' + m_names[maxmonth] + " " + maxday + "," + " " + maxyear);
        $('#map_canvas').gmap('closeInfoWindow');
        $('#map_canvas').gmap('get','MarkerClusterer').setMap(null);
        $('#map_canvas').gmap('clear', 'markers');

        var request_url = window.location.href;
        $.ajax({
                url:request_url,
                type:"POST",
                dataType:"json",
                async: false,
                success: function(data){
                   $.each(data.markers, function(i,m){
                      get_time = m.year*10000 + m.month*100 + m.day;
                      max_time = maxyear*10000 + (maxmonth+1)*100 + maxday;
                      min_time = minyear*10000 + (minmonth+1)*100 + minday;
                      if((get_time >= min_time) && (get_time <= max_time)){
                          $('#map_canvas').gmap('addMarker', { 'position': new google.maps.LatLng(m.latitude, m.longitude)})
                           .mouseover(function(){
                          $('#map_canvas').gmap('openInfoWindow',{'content': '<img width="100px" height ="100px" src="/img?key='+ m.url + '">'},this);
                          });
                      }
                   });
                  $('#map_canvas').gmap('get','MarkerClusterer').setMap(null);
                  $('#map_canvas').gmap('set', 'MarkerClusterer', new MarkerClusterer($('#map_canvas').gmap('get', 'map'), $('#map_canvas').gmap('get', 'markers')));
                  $('#map_canvas').gmap('refresh');
               }
        });
      }
    });

    var max = new Date($("#slider-range").slider("values",1));
    var min = new Date($("#slider-range").slider("values",0));

    var minyear = min.getFullYear();
    var minmonth = min.getMonth();
    var minday = min.getDate();
    var maxyear = max.getFullYear();
    var maxmonth = max.getMonth();
    var maxday = max.getDate();
    var m_names = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    $( "#amount" ).val(m_names[minmonth] + " " + minday + "," + " " + minyear + ' to ' + m_names[maxmonth] + " " + maxday + "," + " " + maxyear);
  });
  </script>

<div id="map_canvas" ></div>
   </br>
   <p>
     <label for="amount">Date Range:</label>
     <input type="text" id="amount" readonly style="border:0; color:red; width:400px">
   </p>
<!--slide range -->
   <div id="slider-range"></div>

</body>
</html>
"""

class Image(ndb.Model):
     img = ndb.BlobProperty()
     comments = ndb.StringProperty()
     date = ndb.DateTimeProperty(auto_now_add=True)
     latitude = ndb.StringProperty()
     longitude = ndb.StringProperty()
class Stream(ndb.Model):
     name = ndb.StringProperty()
     tag = ndb.StringProperty(repeated=True)
     subscribers = ndb.StringProperty(repeated=True)
     invite = ndb.StringProperty()
     cover = ndb.BlobProperty()
     userID = ndb.StringProperty()
     views = ndb.IntegerProperty(default=0)
     viewtimelist = ndb.GenericProperty(repeated=True)
     numpics = ndb.IntegerProperty(default=0)
     pics = ndb.StructuredProperty(Image, repeated=True)
     datetime = ndb.DateTimeProperty(auto_now_add=True)
     keys = ndb.KeyProperty(repeated=True)
class MyUser(ndb.Model):
     mail = ndb.StringProperty()
     rate = ndb.StringProperty()
     
class LoginPage(webapp2.RequestHandler):

    def get(self):
        self.response.write(loginhtml)

    def post(self):
        user = users.get_current_user()

        if user:
            self.redirect('/Manage')
        else:
            self.redirect(users.create_login_url(self.request.uri))

class MainPage(webapp2.RequestHandler):

    def get(self):
        user = users.get_current_user()

        if user:
            pass
        else:
            self.redirect(users.create_login_url(self.request.uri))

        self.response.write(html)

    def post(self):
        user = users.get_current_user()
        stream = Stream()
        stream.name = self.request.get("streamNameId")
        tags = self.request.get("streamTagId").split(',')
        for each_tag in tags:
            (stream.tag).append(each_tag.strip("#"))
        subscribers = self.request.get("streamSubscriberId").split(',')
        for each_subscriber in subscribers:
            (stream.subscribers).append(each_subscriber)
        stream.invite = self.request.get("optionalMessageId")
        cover = self.request.get('urlId')
        # cover = images.resize(cover, 150, 150)
        stream.cover = cover
        stream.userID = user.user_id()
        existing_values = Stream.query(Stream.userID != "").fetch()
        flag=0
        for value in existing_values:
            if((not stream.name) or ((value.name).upper() == (stream.name).upper())):
                flag=1
                break

        if(flag == 1):
            self.redirect('/Error')
        else:
            stream_key = stream.put()
            self.redirect('/Manage?')

class ManagePage(webapp2.RequestHandler):
    def get(self):
        
        self.response.write(managehtml)
        q = Stream.query(Stream.userID != "").fetch()
        self.response.write('<html><body><div class="container"><table><thead><th>Name</th><th>Last New Picture</th><th>Number of Pictures</th><th>Delete</th></thead>')
        for stream in q:
            self.response.write('<tr><td><a href="/view?s=%s">%s</a></td><td>%s</td><td>%s</td><td><input type="checkbox" value=%s name="del_checked"/></td></tr>' % (stream.name,stream.name,stream.datetime.strftime('%m/%d/%Y'),stream.numpics,stream.name))
        self.response.write('</table><br>')
        self.response.write('<input type="submit" value="Delete Checked" class="btn btn-primary start" id="del_stream"/>')
        self.response.write('<h4>Streams I subscribe to</h4><table><thead><th>Name</th><th>Last New Picture</th><th>Number of Pictures</th><th>Views</th><th>Unsubscribe</th></thead>')
        user_to_remove = users.get_current_user().email()
        for subscribe_list in q:
            for emails in subscribe_list.subscribers:
                if(user_to_remove == emails):
                    self.response.write('<tr><td><a href="/view?s=%s">%s</a></td><td>%s</td><td>%s</td><td>%s</td><td><input type="checkbox" value=%s name="unsubscribe_checked"/></td></tr>' % (subscribe_list.name,subscribe_list.name,subscribe_list.datetime.strftime('%m/%d/%Y'),subscribe_list.numpics,subscribe_list.views,subscribe_list.name))
        self.response.write('</table><br>')
        self.response.write('<input type="submit" value="Unsubscribe Checked Streams" class="btn btn-primary start" id="unsub_stream"/>')
        self.response.write('</div></body></html>')
        
class Deletesublist(webapp2.RequestHandler):
    def post(self):
        sdata = json.loads(cgi.escape(self.request.body))
        for slist in sdata:
            stream_to_subdelete =  Stream.query(Stream.name == slist).get()
            stream_to_subdelete.subscribers = [x.encode('utf-8') for x in stream_to_subdelete.subscribers]
            user = users.get_current_user()
            for email_entry in stream_to_subdelete.subscribers:
                if(email_entry == user.email()):
                    stream_to_subdelete.subscribers.remove(email_entry)
                    stream_to_subdelete.subscribers = [x.decode('utf-8') for x in stream_to_subdelete.subscribers]
                    stream_to_subdelete.put()
                    

class DeleteStream(webapp2.RequestHandler):
    def post(self):
        jdata = json.loads(cgi.escape(self.request.body))
        for checked_field in jdata:
            user_to_delete = Stream.query(Stream.name == checked_field).get()
            user_to_delete.key.delete()
            
class ErrorPage(webapp2.RequestHandler):
    def get(self):
        self.response.write(errorhtml)

class SearchStream(webapp2.RequestHandler):
    def get(self):
        self.response.write(searchhtml)

    def post(self):
        act = self.request.get("btnSearchStream")
        if act == "Search":
          search_string = cgi.escape(self.request.get('searchStreamId'))
          s = Stream.query(Stream.userID != "").fetch()
          self.response.write(searchhtml)
          count = 0
          cnt1 = 0
          ct1 = 0
          for st in s:
            if (search_string != "") and (search_string.lower() in st.name.lower()):
               htmlimg = '<p style="float: left; font-size: 9pt; text-align: center; width: 200px; margin-right: 30px; margin-bottom: 0.5em;"><a href = "/view?s=%s"><img src="/image?key=%s" style="width: 100px"><br>%s</a></p>' % (st.name, st.key.urlsafe(), st.name)
               self.response.out.write(htmlimg)
               count = count + 1
            if (search_string != "") and (search_string.lower() not in st.name.lower()):
               cnt = 0
               for tag in st.tag:
                 if search_string.lower() in tag.lower():
                    cnt = cnt + 1
                    cnt1 = cnt1 + 1
               if cnt > 0:
                 htmlimg = '<p style="float: left; font-size: 9pt; text-align: center; width: 200px; margin-right: 30px; margin-bottom: 0.5em;"><a href = "/view?s=%s"><img src="/image?key=%s" style="width: 100px"><br>%s</a></p>' % (st.name, st.key.urlsafe(), st.name)
                 self.response.out.write(htmlimg)
               elif cnt == 0:
                 ct = 0
                 for k in st.keys:
                    image_key = ndb.Key(k.kind(),k.id())
                    image = image_key.get()
                    if search_string.lower() in image.comments.lower():
                       ct = ct + 1
                       ct1 = ct1 + 1
                 if ct > 0:
                        htmlimg = '<p style="float: left; font-size: 9pt; text-align: center; width: 200px; margin-right: 30px; margin-bottom: 0.5em;"><a href = "/view?s=%s"><img src="/image?key=%s" style="width: 100px"><br>%s</a></p>' % (st.name, st.key.urlsafe(), st.name)
                        self.response.out.write(htmlimg)
          if (count == 0) and (cnt1 == 0) and (ct1 == 0):
            self.response.out.write("No matching Stream found!")
            #if re.search(search_string,stream_data.name,re.IGNORECASE):
                #self.response.out.write(stream_data.name)
        else:
            self.response.write(searchhtml)
            global completion_index
            completion_index = list()
            streams = Stream.query().fetch()
            for stream in streams:
               if(str(stream.name)) not in completion_index:
                  completion_index.append(str(stream.name))
               for tag in stream.tag:
                  if((str(tag)) not in completion_index) and (str(tag) != ""):
                     completion_index.append(str(tag))
               for k in stream.keys:
                     image_key = ndb.Key(k.kind(),k.id())
                     image = image_key.get()
                     if((str(image.comments)) not in completion_index) and (str(image.comments) != ""):
                        completion_index.append(str(image.comments))
            #self.response.out.write(completion_index)

global completion_index

class SearchIndex(webapp2.RequestHandler):

    def get(self):
        global completion_index
        completion_index.sort()
        print completion_index
        term = self.request.get('term')
        response = dict()
        response['term'] = list()
        for data in completion_index:
           if term.lower() in data.lower():
              response['term'].append(data)
        self.response.write(json.dumps(response))
        print response

class SearchUpdate(webapp2.RequestHandler):

    def get(self):
        global completion_index
        completion_index = list()
        streams = Stream.query().fetch()
        for stream in streams:
           if(str(stream.name)) not in completion_index:
              completion_index.append(str(stream.name))
           for tag in stream.tag:
              if(str(tag)) not in completion_index:
                 completion_index.append(str(tag))
           for k in stream.keys:
              image_key = ndb.Key(k.kind(),k.id())
              image = image_key.get()
              if(str(image.comments)) not in completion_index:
                  completion_index.append(str(image.comments))
        #self.response.out.write(completion_index)
           
class ViewallStreams(webapp2.RequestHandler):

    def get(self):
        self.response.write(viewall) 
        stream = Stream.query().order(Stream.datetime).fetch()
        for st in stream:
           #htmlimg = '<img src="/image?key=%s" />' % st.key.urlsafe()
           #htmlimg = '<img src="/image?key=%s" style="float: left; width: 200px; margin-right: 30px; margin-bottom: 0.5em;">' % st.key.urlsafe()
           htmlimg = '<p style="float: left; font-size: 9pt; text-align: center; width: 200px; margin-right: 30px; margin-bottom: 0.5em;"><a href = "/view?s=%s"><img src="/image?key=%s" style="width: 100px"><br>%s</a></p>' % (st.name, st.key.urlsafe(), st.name)
           self.response.out.write(htmlimg)   
                      
    
class ServeImage(webapp2.RequestHandler):
     def get(self):
        stkey = ndb.Key(urlsafe=self.request.get('key'))
        image = stkey.get()
        if image.cover:
             self.response.headers['Content-Type'] = 'image/png'
             self.response.out.write(image.cover)
        else:
            self.abort(404)
            
class Serveimage(webapp2.RequestHandler):
     def get(self):
        stkey = ndb.Key(urlsafe=self.request.get('key'))
        stream = stkey.get()
        indx = self.request.get('ind')
        indx = int(indx)
        k = stream.keys[indx]
        image_key = ndb.Key(k.kind(),k.id())
        #self.response.out.write(image_key)
        image = image_key.get()
        if image.img:
             self.response.headers['Content-Type'] = 'image/png'
             self.response.out.write(image.img)
        else:
            self.abort(404)

class MapImage(webapp2.RequestHandler):
     def get(self):
         img_key = ndb.Key(urlsafe=self.request.get('key'))
         image = img_key.get()
         if image.img:
             self.response.headers['Content-Type'] = 'image/png'
             self.response.out.write(image.img)
            
class ViewStream(webapp2.RequestHandler):

    def get(self):
        self.response.write(viewstreamhtml) 
        st = self.request.get('s')
        stream = Stream.query(Stream.name == st).get()
        if stream is not None:
           stream.views = stream.views + 1
           stream.viewtimelist.append(datetime.now())
           viewlist = stream.viewtimelist
           for time in viewlist:
               if (datetime.now() - time).seconds >= 3600:
                   viewlist.remove(time)
                   stream.views = stream.views - 1
               else:
                   break
           stream.viewtimelist = viewlist
           n = stream.name
           stream.put()
        else:
           st = self.request.get('ss')
           stream = Stream.query(Stream.name == st).get()
           n = stream.name
        self.response.out.write('<div style="margin-left:10px;"><table><tr>')
        for pic in stream.pics:
            htmlimg = '<td><table><tr><td><img src="/imag?key=%s&ind=%s" style="float: left; width: 200px; margin-right: 30px; margin-bottom: 0.5em;"></td></tr></table></td>' % (stream.key.urlsafe(), stream.pics.index(pic))
            self.response.out.write(htmlimg)
        self.response.out.write('</tr></table></div></body></html>')
            
        l = len(stream.pics)    
        self.response.write("""
 <html><head>
 <script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
 <script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
 <script src="https://cdnjs.cloudflare.com/ajax/libs/dropzone/4.0.1/min/dropzone.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
 <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/dropzone/4.0.1/min/dropzone.min.css">
 <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
 <script type="text/javascript">
$(document).ready(function () {
Dropzone.autoDiscover = false;
    $("#dZUpload").dropzone({
        url: "/view?st1=%s",
        addRemoveLinks: true,
        paramName: "files",
        autoProcessQueue: false,
        uploadMultiple: true,
        init: function() {
            $(".start").click(function() {
                var dz = Dropzone.forElement("#dZUpload");
                dz.processQueue();
            });
            this.on("success", function (file) {
                window.location.reload();
            });
            $(".cancel").click(function() {
                var dz1 = Dropzone.forElement("#dZUpload");
                for (var i=0; i < dz1.files.length; i++){
                dz1.removeAllFiles(dz1.files[i]);
                }
            });
        },
        sending: function(file, xhr, formData) {
        var dz1 = Dropzone.forElement("#dZUpload");
        var file_length = dz1.files.length;
        formData.append("flength", file_length);
        }
    });
    $("#dZUpload").on("addedfile", function(file) {
        file.previewTemplate.click(function () {
        $("div#dZUpload").removeFile(file);
        });
    });
});
  </script>
 </head>
<body>
<form method="post">
<input type="hidden" name="st" value = "%s">
<input type="hidden" name="ind" value = "%s">
<div style = "height:80px;"><br>
<input type="submit" name="action" value="Subscribe" class="btn btn-danger delete" style="margin-left:10px;">
<input type="submit" name="action" value="Geo view" class="btn btn-danger delete">
</div>
</form>
<div style="height:210px;"></div>
<div id="dZUpload" class="dropzone">
      <div class="dz-message data-dz-message"><p style="font-size:25px;font-style:italic;">Drop Files here or click to Upload</p></div>
</div><br>
<div>
<button class="btn btn-primary start">
<i class="glyphicon glyphicon-upload"></i>
<span>Start Upload</span>
</button>
<button data-dz-remove class="btn btn-warning cancel">
<i class="glyphicon glyphicon-ban-circle"></i>
<span>Cancel Upload</span>
</button>
</div>
</body></html>
""" % (stream.name, n, l))
        

    def post(self):
        act = self.request.get("action")
        if act == "Subscribe":
           stream = Stream.query(Stream.name == self.request.get('st')).get()
           user = users.get_current_user()
           if user:
              stream.subs = [x.encode('utf-8') for x in stream.subscribers]
              stream.subs.append(user.email())
              stream.subscribers = [x.decode('utf-8') for x in stream.subs]
              stream.put()
           else:
              self.redirect(users.create_login_url(self.request.uri))
           self.redirect('/view?' + urllib.urlencode(
              {'ss': stream.name}))

        elif act == "Geo view":
            stream = Stream.query(Stream.name == self.request.get('st')).get()
            self.redirect('/maps?' + urllib.urlencode(
              {'ss': stream.name}))

        else:
           stream = Stream.query(Stream.name == self.request.get('st1')).get()
           ln = self.request.get('ind')
           pre_fc = self.request.get('flength')
           filecount = int(pre_fc)
           for i in range(0,filecount):
               f_list = self.request.get('files[%d]'%i)
               comment = self.request.get('commentsId')
               lati = str(random.uniform(-90, 90))
               longi = str(random.uniform(-180, 180))
               newimage = Image(img=f_list,comments=comment,latitude=lati,longitude=longi)
               stream.pics.append(newimage)
               stream.numpics = stream.numpics + 1
               img_key = newimage.put()
               stream.keys.append(img_key)
               stream.keys = list(reversed(stream.keys))
               stream.pics = list(reversed(stream.pics))
               stream.put()


class MapView(webapp2.RequestHandler):
    def get(self):
        self.response.write(maphtml)
    def post(self):
        st = self.request.get('ss')
        stream = Stream.query(Stream.name == st).get()
        result = dict()
        result['markers']=list()
        for k in stream.keys:
            image_key = ndb.Key(k.kind(),k.id())
            image = image_key.get()
            marker = dict()
            if image.latitude:
                marker['latitude'] = float(image.latitude)
                marker['longitude'] = float(image.longitude)
                #marker['url'] = images.get_serving_url(image_key.urlsafe())
                marker['url'] = str(image_key.urlsafe())
                image_date = image.date + timedelta(hours = -5)
                marker['year'] = image_date.year
                marker['month'] = image_date.month
                marker['day'] = image_date.day
                result['markers'].append(marker)
        self.response.headers['Content-Type'] = "application/json"
        self.response.headers['Accept'] = "text/plain"
        self.response.write(json.dumps(result))

class Trending(webapp2.RequestHandler):
    def get(self):
        self.response.write(trendhtml)
    def post(self):    
        self.response.write(trendhtml)
        frequency = self.request.get("email_freq")
        new_user = MyUser()
        user = users.get_current_user()
        if user:
              my_user = MyUser.query(MyUser.mail == str(user.email())).get()
              if my_user is not None:
                   my_user.rate = frequency
                   my_user.put()
              else:
                   new_user.mail = str(user.email())
                   new_user.rate = frequency
                   new_user.put()
        else:
              self.redirect(users.create_login_url(self.request.uri))
        list = topthree()
        for l in list:
            nm = l["id"]
            st = Stream.query(Stream.name == nm).get()
            htmlimg = '<p style="float: left; font-size: 9pt; text-align: center; width: 200px; margin-right: 30px; margin-bottom: 0.5em;"><img src="/image?key=%s" style="width: 100px"><br>%s<br>%d views</p>' % (st.key.urlsafe(), st.name, st.views)
            self.response.out.write(htmlimg)

def topthree():
        stream = Stream.query().fetch()
        liststr = []
        for st in stream:
            dict = {"id": st.name}
            viewlist = st.viewtimelist
            for time in viewlist:
               if (datetime.now() - time).seconds >= 3600:
                   viewlist.remove(time)
               else:
                   break
            st.viewtimelist = viewlist
            st.put()
            dict["length"] = len(viewlist)
            liststr.append(dict)
        list = sorted(liststr, key=lambda x:x['length'], reverse=True)
        return list[0:3]

class Fivemins(webapp2.RequestHandler):
    def get(self):
        list = topthree()
        count = len(list)
        my_user = MyUser.query(MyUser.rate == "1").fetch()
        if my_user is not None:
          for eachuser in my_user:
            message = mail.EmailMessage(sender="sushmahampapur@gmail.com",
                                subject="Trending streams")
            message.to = eachuser.mail
            if len == 0:
                message.body = "No streams found"
            elif len == 1:
                message.body = """
                Top 3 Trending Streams:
                Stream1: %s views:%d
                Stream2: None
                Stream3: None
                """ %((list[0])["id"],(list[0])["length"])
            elif len == 2:
                message.body = """
                Top 3 Trending Streams:
                Stream1: %s views:%d
                Stream2: %s views:%d
                Stream3: None
                """ %((list[0])["id"],(list[0])["length"],
	                  (list[1])["id"],(list[1])["length"],)
            elif len == 3:
                message.body = """
                Top 3 Trending Streams:
                Stream1: %s views:%d
                Stream2: %s views:%d
                Stream3: %s views:%d
                """ %((list[0])["id"],(list[0])["length"],
	                  (list[1])["id"],(list[1])["length"],
	                  (list[2])["id"],(list[2])["length"])
            message.send()

class Hourly(webapp2.RequestHandler):
    def get(self):
        list = topthree()
        count = len(list)
        my_user = MyUser.query(MyUser.rate == "2").fetch()
        if my_user is not None:
          for eachuser in my_user:
            message = mail.EmailMessage(sender="sushmahampapur@gmail.com",
                                subject="Trending streams")
            message.to = eachuser.mail
            if len == 0:
                message.body = "No streams found"
            elif len == 1:
                message.body = """
                Top 3 Trending Streams:
                Stream1: %s views:%d
                Stream2: None
                Stream3: None
                """ %((list[0])["id"],(list[0])["length"])
            elif len == 2:
                message.body = """
                Top 3 Trending Streams:
                Stream1: %s views:%d
                Stream2: %s views:%d
                Stream3: None
                """ %((list[0])["id"],(list[0])["length"],
	                  (list[1])["id"],(list[1])["length"],)
            elif len == 3:
                message.body = """
                Top 3 Trending Streams:
                Stream1: %s views:%d
                Stream2: %s views:%d
                Stream3: %s views:%d
                """ %((list[0])["id"],(list[0])["length"],
	                  (list[1])["id"],(list[1])["length"],
	                  (list[2])["id"],(list[2])["length"])
            message.send()

class Daily(webapp2.RequestHandler):
    def get(self):
        list = topthree()
        count = len(list)
        my_user = MyUser.query(MyUser.rate == "3").fetch()
        if my_user is not None:
          for eachuser in my_user:
            message = mail.EmailMessage(sender="sushmahampapur@gmail.com",
                                subject="Trending streams")
            message.to = eachuser.mail
            if len == 0:
                message.body = "No streams found"
            elif len == 1:
                message.body = """
                Top 3 Trending Streams:
                Stream1: %s views:%d
                Stream2: None
                Stream3: None
                """ %((list[0])["id"],(list[0])["length"])
            elif len == 2:
                message.body = """
                Top 3 Trending Streams:
                Stream1: %s views:%d
                Stream2: %s views:%d
                Stream3: None
                """ %((list[0])["id"],(list[0])["length"],
	                  (list[1])["id"],(list[1])["length"],)
            elif len == 3:
                message.body = """
                Top 3 Trending Streams:
                Stream1: %s views:%d
                Stream2: %s views:%d
                Stream3: %s views:%d
                """ %((list[0])["id"],(list[0])["length"],
	                  (list[1])["id"],(list[1])["length"],
	                  (list[2])["id"],(list[2])["length"])
            message.send()
            
app = webapp2.WSGIApplication([
    ('/', LoginPage),
    ('/Create', MainPage),
    ('/Manage', ManagePage),
    ('/delete_checked', DeleteStream),
    ('/delete_unsub', Deletesublist),
    ('/Search', SearchStream),
    ('/Error', ErrorPage),
    ('/view', ViewStream),
    ('/View', ViewallStreams),
    ('/image', ServeImage),
    ('/imag', Serveimage),
    ('/Trending', Trending),
    ('/fivemins', Fivemins),
    ('/hourly', Hourly),
    ('/daily', Daily),
    ('/search_update', SearchUpdate),
    ('/search_index', SearchIndex),
    ('/maps', MapView),
    ('/img', MapImage)
], debug=True)
